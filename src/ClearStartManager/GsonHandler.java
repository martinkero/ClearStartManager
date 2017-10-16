package ClearStartManager;


import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class GsonHandler {

    private GsonHandler() {
    }



    static CustomerList getCustomerListFromJson(String json) throws Exception {

        Response response = new Gson().fromJson(json, Response.class);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CustomerList.class, new CustomerListDeserializer());
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        return gson.fromJson(response.data, CustomerList.class);
    }


    static String getSettingsJsonFromCustomer(Customer customer) {
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
