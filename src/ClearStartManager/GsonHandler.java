package ClearStartManager;


import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class GsonHandler {

    private GsonHandler() {
    }

    static CustomerList getCustomerListFromJsonFile(String file) throws FileNotFoundException {
        FileReader fileReader = new FileReader(file);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject) parser.parse(fileReader);
        return getCustomerListFromJson((JsonObject) jsonObject.get(CustomerHandler.clientType));
    }

    static void writeCustomerListToJsonFile(CustomerList customerList, String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        JsonObject jsonObject = new JsonObject();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(List.class, new CustomerListSerializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        JsonObject customerListJson = (JsonObject) gson.toJsonTree(customerList.getCustomers(), List.class);

        jsonObject.add(CustomerHandler.clientType, customerListJson);

        Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
        gsonWriter.toJson(jsonObject, fileWriter);
        fileWriter.close();

    }

    static CustomerList getCustomerListFromResponseString(String responseString) {
        Response response = new Gson().fromJson(responseString, Response.class);
        return getCustomerListFromJson(response.data);
    }

    private static CustomerList getCustomerListFromJson(JsonObject json) {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CustomerList.class, new CustomerListDeserializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        return gson.fromJson(json, CustomerList.class);
    }


    static JsonObject getSettingsJsonStringFromCustomer(Customer customer) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(List.class, new SettingsSerializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        return (JsonObject) gson.toJsonTree(customer.getSettings(), List.class);

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
                if (!"".equals(setting.getKey())) {
                    jsonObject.addProperty(setting.getKey(), setting.getValue());
                }
            }
            return jsonObject;
        }
    }

    private static class CustomerListSerializer implements JsonSerializer<List<Customer>> {
        @Override
        public JsonElement serialize(List<Customer> customers, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            for (Customer customer : customers) {
                JsonObject settingsJsonString = getSettingsJsonStringFromCustomer(customer);
                jsonObject.add(customer.getName(), settingsJsonString);
            }
            return jsonObject;
        }
    }
}
