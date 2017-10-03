package ClearStartManager;

import com.jfoenix.controls.*;
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
    private JFXListView<String> customerListBox;
    @FXML
    private JFXListView<String> settingKeyListBox;
    @FXML
    private JFXListView<String> settingValueListBox;
    @FXML
    private JFXButton resetButton;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton toggleAgentButton;
    @FXML
    private JFXButton toggleCoachButton;


    private ObservableList<String> observableCustomerList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingKeyList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingValueList = FXCollections.observableArrayList();
    private CustomerList customerList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        refreshCustomerList();

        resetButton.setOnMouseClicked(event -> refreshCustomerList());
        saveButton.setDisable(true);
        toggleAgentButton.setDisable(true);
        toggleCoachButton.setDisable(true);

        settingKeyListBox.setEditable(true);
        settingKeyListBox.setCellFactory(TextFieldListCell.forListView());
        settingKeyListBox.setOnEditCommit(this::settingKeyEdited);

        settingValueListBox.setEditable(true);

        settingValueListBox.setCellFactory(TextFieldListCell.forListView());
        settingValueListBox.setOnEditCommit(this::settingValueEdited);

        customerListBox.setOnMouseClicked(event -> customerListClicked());
    }

    private void refreshCustomerList() {
        observableCustomerList.clear();
        try {
            //customerList = GsonManager.refreshCustomers();
            customerList = GsonManagerTest.createCustomerListWithTestData();
        } catch (Exception e) {
            //TODO: Proper exception handling
            e.printStackTrace();
            System.exit(0);
        }
        for (Customer customer : customerList.getCustomers()) {
            observableCustomerList.add(customer.getName());
        }

        customerListBox.setItems(observableCustomerList);


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
        int selectedIndex = customerListBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) throw new NoSuchElementException();

        return customerList.getCustomerByIndex(selectedIndex);
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
