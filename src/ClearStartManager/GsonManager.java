package ClearStartManager;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.*;

class Response {
    String code;
    String status;
    JsonElement data;
}


class GsonManager {
    static String remoteServerUrl = ""; //TODO: Set dynamically

    private GsonManager() {
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
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

    public static CustomerList getRemoteCustomers() throws Exception {
        String json = readUrl(remoteServerUrl);

        return getCustomerListFromJson(json);
    }

    public static CustomerList getCustomerListFromJson(String json) throws Exception {

        Response response = new Gson().fromJson(json, Response.class);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CustomerList.class, new CustomerListDeserializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        return gson.fromJson(response.data, CustomerList.class);
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


}
