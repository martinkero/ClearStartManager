package ClearStartManager;


import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;


import com.google.gson.*;

class Response {
    String code;
    String status;
    JsonElement data;
}
class PostRequest {
    String baseUrl;
    String queryString;
    String installationParameters;

    PostRequest(String customerName, String action) {
        this.baseUrl = GsonManager.remoteServerUrl;
        String clientType = "agent";
        this.queryString = "?action=" + action + "&type=" + clientType + "&name=" + customerName;
    }
}


class GsonManager {
    static String remoteServerUrl = "http://clearstart.clearit.se/"; //TODO: Set dynamically

    private GsonManager() {
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static String sendPostRequest(PostRequest postRequest) throws IOException {
        URL url = new URL(postRequest.baseUrl + postRequest.queryString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type","application/json");

        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(postRequest.installationParameters);
        outputStream.flush();
        outputStream.close();

        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        byte[] contents = new byte[1024];
        int bytesRead;
        StringBuilder responseBody = new StringBuilder();
        while ((bytesRead = inputStream.read(contents)) != -1) {
            responseBody.append(new String(contents, 0, bytesRead));
        }
        return responseBody.toString();
    }

    static CustomerList getRemoteCustomers() throws Exception {
        String json = readUrl(remoteServerUrl + "?action=get&type=agent");

        return getCustomerListFromJson(json);
    }

    static CustomerList getCustomerListFromJson(String json) throws Exception {

        Response response = new Gson().fromJson(json, Response.class);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CustomerList.class, new CustomerListDeserializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        return gson.fromJson(response.data, CustomerList.class);
    }

    static void modifyRemoteCustomer(Customer customer) {
        PostRequest postRequest = new PostRequest(customer.getName(), "modify");

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(List.class, new SettingsSerializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        postRequest.installationParameters = gson.toJson(customer.getSettings(), List.class);

        try {
            String returnString = sendPostRequest(postRequest);
            System.out.println(returnString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class CustomerListDeserializer implements JsonDeserializer<CustomerList> {
        @Override
        public CustomerList deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject customerListObject = element.getAsJsonObject();
            List<Customer> customers = new ArrayList<Customer>();
            for (Map.Entry<String, JsonElement> customerEntry : customerListObject.entrySet()) {
                JsonObject customerObject = customerEntry.getValue().getAsJsonObject();

                String customerName = customerEntry.getKey();
                JsonObject customerObjectWithName = new JsonObject();
                customerObjectWithName.addProperty("name", customerName);

                List<Setting> settings = getSettingsFromCustomerObject(customerObject);

                Customer customer = new Gson().fromJson(customerObjectWithName, Customer.class);
                customer.setSettings(settings);
                customers.add(customer);
            }
            return new CustomerList(customers);
        }


        List<Setting> getSettingsFromCustomerObject(JsonObject customerObject) {
            List<Setting> settings = new ArrayList<Setting>();

            for (Map.Entry<String, JsonElement> settingMap : customerObject.entrySet()) {
                JsonObject settingObject = new JsonObject();
                settingObject.addProperty("key", settingMap.getKey());
                settingObject.addProperty("value", settingMap.getValue().getAsString());
                Setting setting = new Gson().fromJson(settingObject, Setting.class);
                settings.add(setting);
            }
            return settings;
        }
    }

    private static class SettingsSerializer implements JsonSerializer<List<Setting>> {
        @Override
        public JsonElement serialize(List<Setting> settings, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            for (Setting setting : settings) {
                jsonObject.addProperty(setting.getKey(), setting.getValue());
            }
            return jsonObject;
        }
    }
}
