package ClearStartManager;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class GsonManagerTest {
    @Test
    public void getCustomerListFromJson() throws Exception {
        String json = "{\"code\":\"300\",\"status\":\"success\",\"data\":{" +
                "\"Customer1\":{\"host\":\"192.168.0.1\",\"user\":\"user1\"}," +
                "\"Customer2\":{\"host\":\"192.168.0.2\",\"user\":\"user2\",\"language\":\"en\"}}}";


        CustomerList customerList = createCustomerListWithTestData();
        CustomerList customerListFromJson = GsonManager.getCustomerListFromJson(json);

        String cust1User = customerList.getCustomerByIndex(0).getSettingByIndex(1).getValue();
        String cust1UserFromJson = customerListFromJson.getCustomerByIndex(0).getSettingByIndex(1).getValue();
        String cust2Host = customerList.getCustomerByIndex(0).getSettingByIndex(0).getValue();
        String cust1HostFromJson = customerListFromJson.getCustomerByIndex(0).getSettingByIndex(0).getValue();

        assertTrue(cust1User.equals(cust1UserFromJson));
        assertFalse(cust2Host.equals(cust1HostFromJson));
    }

    public static CustomerList createCustomerListWithTestData() {
        List<Setting> cust1Settings = Arrays.asList(
                new Setting("host", "192.168.0.1"),
                new Setting("user", "user1")
        );
        Customer cust1 = new Customer(
                "Customer1",
                cust1Settings
        );

        List<Setting> cust2Settings = Arrays.asList(
                new Setting("host", "192.168.0.2"),
                new Setting("user", "user2"),
                new Setting("language", "en")
        );
        Customer cust2 = new Customer(
                "Customer2",
                cust2Settings
        );

        CustomerList customerList = new CustomerList(Arrays.asList(cust1, cust2));

        return customerList;
    }

}