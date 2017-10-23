package ClearStartManager;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldListCell;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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
    @FXML
    private JFXButton toggleRemoteButton;
    @FXML
    private JFXButton toggleLocalButton;
    @FXML
    private JFXButton createCustomerButton;
    @FXML
    private JFXButton deleteCustomerButton;


    private ObservableList<String> observableCustomerList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingKeyList = FXCollections.observableArrayList();
    private ObservableList<String> observableSettingValueList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetButton.setOnMouseClicked(event -> resetButtonClicked());
        resetButton.setDisable(true);

        saveButton.setOnMouseClicked(event -> saveButtonClicked());
        saveButton.setDisable(true);

        toggleAgentButton.getStyleClass().add("toggled");
        toggleAgentButton.setOnMouseClicked(event -> toggleAgentButtonClicked());

        toggleCoachButton.setOnMouseClicked(event -> toggleCoachButtonClicked());

        toggleRemoteButton.getStyleClass().add("toggled");
        toggleRemoteButton.setOnMouseClicked(event -> toggleRemoteButtonClicked());

        toggleLocalButton.setOnMouseClicked(event -> toggleLocalButtonClicked());

        createCustomerButton.setOnMouseClicked(event -> createCustomerButtonClicked());

        deleteCustomerButton.setOnMouseClicked(event -> deleteButtonClicked());

        settingKeyListBox.setEditable(true);
        settingKeyListBox.setCellFactory(TextFieldListCell.forListView());
        settingKeyListBox.setOnEditCommit(this::settingKeyEdited);
        settingKeyListBox.setOnEditStart(event -> cancelEditing(settingValueListBox));

        settingValueListBox.setEditable(true);
        settingValueListBox.setCellFactory(TextFieldListCell.forListView());
        settingValueListBox.setOnEditCommit(this::settingValueEdited);
        settingValueListBox.setOnEditStart(event -> cancelEditing(settingKeyListBox));

        customerListBox.setOnMouseClicked(event -> customerListClicked());

        CustomerHandler.resetCustomerList();
        refreshGui();
    }


    private void cancelEditing(ListView listView) {
        listView.edit(-1);
    }


    private void refreshGui() {
        Customer selectedCustomer;
        try {
            selectedCustomer = getSelectedCustomer();
        } catch (IndexOutOfBoundsException e) {
            selectedCustomer = getTopmostCustomer();
        }

        rebuildObservableCustomerList();

        showCustomer(selectedCustomer);
    }

    private void rebuildObservableCustomerList() {
        observableCustomerList.clear();
        for (Customer customer : CustomerHandler.customerList.getCustomers()) {
            observableCustomerList.add(customer.getName());
        }

        customerListBox.setItems(observableCustomerList);
    }

    private void customerListClicked() {
        CustomerHandler.resetCustomerList();
        Customer clickedCustomer;
        try {
            clickedCustomer = getSelectedCustomer();
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        showCustomer(clickedCustomer);
    }

    private void toggleAgentButtonClicked() {
        CustomerHandler.clientType = "agent";
        toggleButton(toggleAgentButton, toggleCoachButton);
    }

    private void toggleCoachButtonClicked() {
        CustomerHandler.clientType = "coach";
        toggleButton(toggleCoachButton, toggleAgentButton);
    }

    private void toggleRemoteButtonClicked() {
        CustomerHandler.customerType = "remote";
        toggleButton(toggleRemoteButton, toggleLocalButton);
    }

    private void toggleLocalButtonClicked() {
        CustomerHandler.customerType = "local";
        toggleButton(toggleLocalButton, toggleRemoteButton);
    }

    private void toggleButton(JFXButton toggledButton, JFXButton unToggledButton) {
        toggledButton.getStyleClass().add("toggled");
        unToggledButton.getStyleClass().removeAll("toggled");
        CustomerHandler.resetCustomerList();
        refreshGui();
        customerListBox.getSelectionModel().selectFirst();
    }

    private void resetButtonClicked() {
        resetButton.setDisable(true);
        saveButton.setDisable(true);
        CustomerHandler.resetCustomerList();
        refreshGui();
    }

    private void saveButtonClicked() {
        resetButton.setDisable(true);
        saveButton.setDisable(true);
        Customer customer = getSelectedCustomer();
        try {
            CustomerHandler.modifyCustomer(customer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CustomerHandler.resetCustomerList();
        refreshGui();
    }

    private void createCustomerButtonClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Customer");
        dialog.setContentText("Name: ");
        dialog.setHeaderText("");
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent() || "".equals(result.get())) {
            return;
        }
        String customerName = result.get();

        try {
            CustomerHandler.createCustomer(customerName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CustomerHandler.resetCustomerList();
        refreshGui();

        customerListBox.getSelectionModel().select(customerName);
        customerListBox.scrollTo(customerName);

        showCustomer(getSelectedCustomer());
    }


    private void deleteButtonClicked() {
        Customer customer = getSelectedCustomer();
        try {
            CustomerHandler.deleteCustomer(customer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CustomerHandler.resetCustomerList();
        refreshGui();
    }

    private void showCustomer(Customer customer) {
        customerListBox.getSelectionModel().select(CustomerHandler.customerList.getCustomers().indexOf(customer));

        settingKeyListBox.getItems().clear();
        settingValueListBox.getItems().clear();

        customer.addEmptySettingIfNone();

        for (Setting setting : customer.getSettings()) {
            observableSettingKeyList.add(setting.getKey());
            observableSettingValueList.add(setting.getValue());
        }

        settingKeyListBox.setItems(observableSettingKeyList);
        settingValueListBox.setItems(observableSettingValueList);

        settingKeyListBox.edit(settingKeyListBox.getItems().size() - 1);
    }

    private Customer getSelectedCustomer() throws IndexOutOfBoundsException {
        int selectedIndex = customerListBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) throw new IndexOutOfBoundsException();

        return CustomerHandler.customerList.getCustomerByIndex(selectedIndex);
    }

    private Customer getTopmostCustomer() {
        return CustomerHandler.customerList.getCustomerByIndex(0);
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
