package ClearStartManager;


import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;


import com.google.gson.*;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

class Response {
    String code;
    String status;
    JsonElement data;
}
class Request {
    private String baseUrl = GsonManager.remoteServerUrl;
    String queryString;
    private String clientType = "agent";
    private String action;

    Request(String action) {
        this.action = action;
        this.queryString = "?type=" + this.clientType + "&action=" + this.action;
    }

    String sendGetRequest() throws IOException {
        BufferedInputStream inputStream = null;
        try {
            URL url = new URL(this.baseUrl + this.queryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            inputStream = new BufferedInputStream(connection.getInputStream());
            byte[] contents = new byte[1024];
            int bytesRead;
            StringBuilder responseBody = new StringBuilder();
            while ((bytesRead = inputStream.read(contents)) != -1) {
                responseBody.append(new String(contents, 0, bytesRead));
            }
            return responseBody.toString();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    String sendPostRequest(String dataBody) throws IOException {
        BufferedInputStream inputStream = null;
        try {
            URL url = new URL(this.baseUrl + this.queryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");

            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(dataBody);
            outputStream.flush();
            outputStream.close();

            inputStream = new BufferedInputStream(connection.getInputStream());
            byte[] contents = new byte[1024];
            int bytesRead;
            StringBuilder responseBody = new StringBuilder();
            while ((bytesRead = inputStream.read(contents)) != -1) {
                responseBody.append(new String(contents, 0, bytesRead));
            }
            return responseBody.toString();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
class GetRequest extends Request {
    GetRequest() {
        super("get");
    }
    GetRequest(String customerName) {
        super("get");
        this.queryString += "&name=" + customerName;
    }
}
class ModifyRequest extends Request {
    private String installationParameters;
    ModifyRequest(String customerName, String installationParameters) {
        super("modify");
        this.installationParameters = installationParameters;
        this.queryString += "&name=" + customerName;
    }
    String sendModifyRequest() throws IOException {
        return sendPostRequest(this.installationParameters);
    }
}
class DeleteRequest extends Request {
    DeleteRequest(String customerName) {
        super("delete");
        this.queryString += "&name=" + customerName;
    }
    String sendDeleteRequest() throws IOException {
        return sendGetRequest();
    }
}



class GsonManager {
    static String remoteServerUrl = "http://clearstart.clearit.se/"; //TODO: Set dynamically

    private GsonManager() {
    }

    static CustomerList getRemoteCustomers() throws Exception {
        String json = new GetRequest().sendGetRequest();

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
        String settingsJson = getSettingsJsonFromCustomer(customer);
        ModifyRequest modifyRequest = new ModifyRequest(customer.getName(), settingsJson);
        try {
            String returnString = modifyRequest.sendModifyRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void deleteRemoteCustomer(Customer customer) {
        String settingsJson = getSettingsJsonFromCustomer(customer);
        DeleteRequest deleteRequest = new DeleteRequest(customer.getName());
        try {
            String returnString = deleteRequest.sendDeleteRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getSettingsJsonFromCustomer(Customer customer) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(List.class, new SettingsSerializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        return gson.toJson(customer.getSettings(), List.class);

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
