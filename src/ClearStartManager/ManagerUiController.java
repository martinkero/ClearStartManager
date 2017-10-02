package ClearStartManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;

import java.net.URL;
import java.util.*;

public class ManagerUiController implements Initializable {

    @FXML
    private ListView<String> customerListBox;
    @FXML
    private ListView<String> settingKeyListBox;
    @FXML
    private ListView<String> settingValueListBox;


    private ObservableList<String> observableCustomerList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingKeyList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingValueList = FXCollections.observableArrayList();
    private CustomerList customerList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerList = new CustomerList(generateTestData());

        for (Customer customer : customerList.getCustomers()) {
            observableCustomerList.add(customer.getName());
        }

        customerListBox.setItems(observableCustomerList);

        settingKeyListBox.setEditable(true);
        settingKeyListBox.setCellFactory(TextFieldListCell.forListView());
        settingKeyListBox.setOnEditCommit(this::settingKeyEdited);
        settingValueListBox.setEditable(true);
        settingValueListBox.setCellFactory(TextFieldListCell.forListView());
        settingValueListBox.setOnEditCommit(this::settingValueEdited);


        customerListBox.setOnMouseClicked(event -> customerListClicked());
    }

    private List<Customer> generateTestData() {
        List<Setting> cust1Settings = Arrays.asList(
                new Setting("ip", "192.168.1.2"),
                new Setting("phone", "4088"),
                new Setting("user", "user1")
        );
        Customer cust1 = new Customer(
                "Customer1",
                cust1Settings
        );

        List<Setting> cust2Settings = Arrays.asList(
                new Setting("ip", "10.0.0.1"),
                new Setting("phone", "4033")
        );
        Customer cust2 = new Customer(
                "Customer2",
                cust2Settings
        );
        List<Customer> customers = new ArrayList<Customer>();

        customers.add(cust1);
        customers.add(cust2);

        return customers;
    }

    private void customerListClicked() {
        Customer clickedCustomer;
        try {
            clickedCustomer = getSelectedCustomer();
        } catch (NoSuchElementException e) {
            return;
        }

        showCustomer(clickedCustomer);
    }


    private void showCustomer(Customer customer) {
        settingKeyListBox.getItems().clear();
        settingValueListBox.getItems().clear();

        for (Setting setting : customer.getSettings()) {
            observableSettingKeyList.add(setting.getKey());
            observableSettingValueList.add(setting.getValue());
        }
        settingKeyListBox.setItems(observableSettingKeyList);
        settingValueListBox.setItems(observableSettingValueList);
    }

    private Customer getSelectedCustomer() throws NoSuchElementException {
        String selectedName = customerListBox.getSelectionModel().getSelectedItems().get(0);
        if (selectedName == null) throw new NoSuchElementException();

        return customerList.getCustomerByName(selectedName);
    }

    private void settingKeyEdited(ListView.EditEvent event) {
        Customer selectedCustomer = getSelectedCustomer();
        Integer settingKeyIndex = event.getIndex();
        String newValue = event.getNewValue().toString();

        selectedCustomer.setSettingKeyByIndex(settingKeyIndex, newValue);
        showCustomer(getSelectedCustomer());
    }

    private void settingValueEdited(ListView.EditEvent event) {
        Customer selectedCustomer = getSelectedCustomer();
        Integer settingValueIndex = event.getIndex();
        String newValue = event.getNewValue().toString();

        selectedCustomer.setSettingValueByIndex(settingValueIndex, newValue);
        showCustomer(getSelectedCustomer());
    }

}
