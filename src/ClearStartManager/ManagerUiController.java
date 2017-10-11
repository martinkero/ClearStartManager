package ClearStartManager;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;

import java.beans.EventHandler;
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

    @FXML JFXNodesList testnodelist;


    private ObservableList<String> observableCustomerList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingKeyList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingValueList = FXCollections.observableArrayList();
    private CustomerList customerList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        resetCustomerList();
        refreshGui();

        resetButton.setOnMouseClicked(event -> resetButtonClicked());
        resetButton.setDisable(true);
        saveButton.setOnMouseClicked(event -> saveButtonClicked());
        saveButton.setDisable(true);
        toggleAgentButton.setDisable(true);
        toggleCoachButton.setDisable(true);

        settingKeyListBox.setEditable(true);
        settingKeyListBox.setCellFactory(TextFieldListCell.forListView());
        settingKeyListBox.setOnEditCommit(this::settingKeyEdited);
        settingKeyListBox.setOnEditStart(event -> cancelEditing(settingValueListBox));

        settingValueListBox.setEditable(true);
        settingValueListBox.setCellFactory(TextFieldListCell.forListView());
        settingValueListBox.setOnEditCommit(this::settingValueEdited);
        settingValueListBox.setOnEditStart(event -> cancelEditing(settingKeyListBox));

        customerListBox.setOnMouseClicked(event -> customerListClicked());



    }

    private void cancelEditing(ListView listView) {
        listView.edit(-1);
    }

    private void resetCustomerList() {

        try {
            customerList = GsonManager.getRemoteCustomers();
            //customerList = GsonManagerTest.createCustomerListWithTestData();
        } catch (Exception e) {
            //TODO: Proper exception handling
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void refreshGui() {
        Customer selectedCustomer;
        try {
            selectedCustomer = getSelectedCustomer();
        } catch (IndexOutOfBoundsException e) {
            selectedCustomer = getTopmostCustomer();
            customerListBox.getSelectionModel().selectFirst();
        }

        observableCustomerList.clear();
        for (Customer customer : customerList.getCustomers()) {
            observableCustomerList.add(customer.getName());
        }

        customerListBox.setItems(observableCustomerList);

        showCustomer(selectedCustomer);

    }

    private void customerListClicked() {
        resetCustomerList();
        Customer clickedCustomer;
        try {
            clickedCustomer = getSelectedCustomer();
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        showCustomer(clickedCustomer);
    }

    private void resetButtonClicked() {
        resetButton.setDisable(true);
        resetCustomerList();
        refreshGui();
    }

    private void saveButtonClicked() {
        saveButton.setDisable(true);
        Customer customer = getSelectedCustomer();
        GsonManager.modifyRemoteCustomer(customer);
        resetCustomerList();
        refreshGui();
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

    private Customer getSelectedCustomer() throws IndexOutOfBoundsException {
        int selectedIndex = customerListBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) throw new IndexOutOfBoundsException();

        return customerList.getCustomerByIndex(selectedIndex);
    }

    private Customer getTopmostCustomer() {
        return customerList.getCustomerByIndex(0);
    }


    private void settingKeyEdited(ListView.EditEvent event) {
        resetButton.setDisable(false);
        saveButton.setDisable(false);
        Customer selectedCustomer = getSelectedCustomer();
        Integer settingKeyIndex = event.getIndex();
        String newValue = event.getNewValue().toString();

        selectedCustomer.setSettingKeyByIndex(settingKeyIndex, newValue);
        showCustomer(getSelectedCustomer());
    }

    private void settingValueEdited(ListView.EditEvent event) {
        resetButton.setDisable(false);
        saveButton.setDisable(false);
        Customer selectedCustomer = getSelectedCustomer();
        Integer settingValueIndex = event.getIndex();
        String newValue = event.getNewValue().toString();

        selectedCustomer.setSettingValueByIndex(settingValueIndex, newValue);
        showCustomer(getSelectedCustomer());
    }

}
