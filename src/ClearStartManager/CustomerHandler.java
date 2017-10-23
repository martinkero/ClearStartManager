package ClearStartManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class CustomerHandler {
    static String clientType = "agent";
    static String customerType = "remote";
    static CustomerList customerList;
    private static String localAgentConfigFile = "/Users/mark/Dropbox/ClearIt/CustomClearMac/clearStart/agent/clearStartCustomers.json";
    private static String localCoachConfigFile = "/Users/mark/Dropbox/ClearIt/CustomClearMac/clearStart/coach/clearStartCustomers.json";

    static void resetCustomerList() {
        try {
            if ("remote".equals(customerType)) {
                customerList = getRemoteCustomers();
                customerList.sort();
                return;
            }
            customerList = getLocalCustomers();
            customerList.sort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CustomerList getLocalCustomers() throws IOException {
        if ("agent".equals(clientType)) {
            return GsonHandler.getCustomerListFromJsonFile(localAgentConfigFile);
        }
        return GsonHandler.getCustomerListFromJsonFile(localCoachConfigFile);
    }

    static void modifyCustomer(Customer customer) throws IOException {
        if ("remote".equals(customerType)) {
            modifyRemoteCustomer(customer);
            return;
        }
        if ("agent".equals(clientType)) {
            GsonHandler.writeCustomerListToJsonFile(customerList, localAgentConfigFile);
            return;
        }
        GsonHandler.writeCustomerListToJsonFile(customerList, localCoachConfigFile);
    }

    private static CustomerList getRemoteCustomers() throws IOException {
        String json = new GetRequest().sendGetRequest();

        return GsonHandler.getCustomerListFromResponseString(json);
    }

    static void createCustomer(String name) throws IOException {
        List<Setting> emptySettings = new ArrayList<Setting>();
        Customer customer = new Customer(name, emptySettings);
        if ("remote".equals(customerType)) {
            createRemoteCustomer(customer);
            return;
        }
        customerList.addCustomer(customer);
        if ("agent".equals(clientType)) {
            GsonHandler.writeCustomerListToJsonFile(customerList, localAgentConfigFile);
            return;
        }
        GsonHandler.writeCustomerListToJsonFile(customerList, localCoachConfigFile);
    }

    static void deleteCustomer(Customer customer) throws IOException {
        if ("remote".equals(customerType)) {
            deleteRemoteCustomer(customer.getName());
            return;
        }
        customerList.deleteCustomer(customer);
        if ("agent".equals(clientType)) {
            GsonHandler.writeCustomerListToJsonFile(customerList, localAgentConfigFile);
            return;
        }
        GsonHandler.writeCustomerListToJsonFile(customerList, localCoachConfigFile);
    }

    private static void createRemoteCustomer(Customer customer) {
        String settingsJson = GsonHandler.getSettingsJsonStringFromCustomer(customer).toString();
        CreateRequest createRequest = new CreateRequest(customer.getName(), settingsJson);
        try {
            String returnString = createRequest.sendCreateRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void modifyRemoteCustomer(Customer customer) {
        String settingsJson = GsonHandler.getSettingsJsonStringFromCustomer(customer).toString();
        ModifyRequest modifyRequest = new ModifyRequest(customer.getName(), settingsJson);
        try {
            String returnString = modifyRequest.sendModifyRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteRemoteCustomer(String name) {
        DeleteRequest deleteRequest = new DeleteRequest(name);
        try {
            String returnString = deleteRequest.sendDeleteRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
