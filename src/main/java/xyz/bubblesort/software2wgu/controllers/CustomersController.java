package xyz.bubblesort.software2wgu.controllers;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import xyz.bubblesort.software2wgu.db.Queries;
import xyz.bubblesort.software2wgu.models.*;

/**
 * JavaFX controller for customers scene (main-view.fxml).
 */
public class CustomersController {
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, String> customersIDColumn;
    @FXML
    private TableColumn<Customer, String> customersNameColumn;
    @FXML
    private TableColumn<Customer, String> customersAddressColumn;
    @FXML
    private TableColumn<Customer, String> customersPostalCodeColumn;
    @FXML
    private TableColumn<Customer, String> customersPhoneColumn;
    @FXML
    private TableColumn<Customer, String> customersStateColumn;
    @FXML
    private TableColumn<Customer, String> customersCountryColumn;
    @FXML
    private ComboBox<Country> customerCountryBox;
    @FXML
    private ComboBox<String> customerStateBox;
    @FXML
    private TextField customerIDField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private TextField customerPostalCodeField;
    @FXML
    private TextField customerAddressField;
    @FXML
    private GridPane customerForm;
    @FXML
    private Label output;
    @FXML
    private Label upcomingAppointment;
    private int editMode;
    private ScenesAndControllers scenesAndControllers;

    /**
     * Setter for the scenesAndControllers.
     *
     * @param scenesAndControllers object with all controllers, scenes and a stage
     */
    public void setScenesAndControllers(ScenesAndControllers scenesAndControllers) {
        this.scenesAndControllers = scenesAndControllers;
    }

    /**
     * Initializes customers controller and fxml view with data.
     */
    public void initialize() {
        Property<ObservableList<Customer>> propertyCustomers = new SimpleObjectProperty<>(Repository.getCustomers());
        customersTable.itemsProperty().bind(propertyCustomers);
        customersIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customersNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customersAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customersPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customersPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customersStateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        customersCountryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        customerCountryBox.setItems(Repository.getCountries());
        customerCountryBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Country country) {
                return country.getCountry();
            }

            @Override
            public Country fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Clears and disables the form if Cancel button was pressed.
     */
    public void customerOnCancel() {
        clear();
        customerForm.setDisable(true);
    }

    /**
     * Adds or modify a customer with data from the form.
     */
    public void customerOnSave() {
        if (editMode == 0) {
            addNewCustomer();
        } else {
            modifyCustomer();
        }
        clear();
        customerForm.setDisable(true);
        customersTable.refresh();
    }

    /**
     * Adds a new customer to the database.
     */
    private void addNewCustomer() {
        String name = customerNameField.getText();
        String address = customerAddressField.getText();
        String phone = customerPhoneField.getText();
        String postalCode = customerPostalCodeField.getText();
        int divisionID = -1;
        String stateBox = customerStateBox.getValue();
        for (Division division : Repository.getDivisions()) {
            if (division.getDivision().equals(stateBox)) {
                divisionID = division.getId();
                break;
            }
        }
        int customerID = Queries.addCustomer(name, address, postalCode, phone, divisionID);
        if (customerID != -1) {
            Customer customer = new Customer(customerID, name, address, postalCode, phone, divisionID);
            Repository.getCustomers().add(customer);
        }
    }

    /**
     * Modifies the selected customer in the database.
     */
    private void modifyCustomer() {
        Customer customer = customersTable.getSelectionModel().getSelectedItem();
        if (customer != null) {
            customer.setName(customerNameField.getText());
            customer.setAddress(customerAddressField.getText());
            customer.setPhone(customerPhoneField.getText());
            customer.setPostalCode(customerPostalCodeField.getText());
            String stateBox = customerStateBox.getValue();
            for (Division division : Repository.getDivisions()) {
                if (division.getDivision().equals(stateBox)) {
                    customer.setDivision(division.getId());
                    break;
                }
            }
            Queries.updateCustomer(customer);
        }
    }

    /**
     * Prepares the form for adding a new customer.
     */
    public void onAdd() {
        clear();
        customerForm.setDisable(false);
        editMode = 0;
        customerIDField.setText("Auto generated");
    }

    /**
     * Prepares the form for modifying a customer.
     */
    public void onModify() {
        clear();
        Customer customer = customersTable.getSelectionModel().getSelectedItem();
        if (customer != null) {
            customerForm.setDisable(false);
            editMode = 1;
            customerIDField.setText(String.valueOf(customer.getId()));
            customerNameField.setText(customer.getName());
            customerPhoneField.setText(customer.getPhone());
            customerPostalCodeField.setText(customer.getPostalCode());
            customerAddressField.setText(customer.getAddress());
            customerCountryBox.setValue(customer.getCountry());
            customerStateBox.setValue(customer.getState());
        }
    }

    /**
     * Deletes the selected customer from a database and outputs success message.
     * Uses LAMBDA expression to easily remove an appointment based on the customer id
     */
    public void onDelete() {
        clear();
        Customer customer = customersTable.getSelectionModel().getSelectedItem();
        if (customer != null) {
            Queries.deleteCustomer(customer);
            Queries.deleteCustomerAppointments(customer);
            Repository.getAppointments().removeIf(a -> a.getCustomerID() == customer.getId());
            Repository.getCustomers().remove(customer);
            output.setText("A customer " + customer.getName() + " record has been deleted");
        }
        customersTable.refresh();
    }

    /**
     * Sets states options based on the country selected
     */
    public void onCountrySelect() {
        Country country = customerCountryBox.getValue();
        if (country != null) {
            customerStateBox.setItems(FXCollections.observableArrayList(Repository.getDivisions().stream().filter(d -> d.getCountryID() == country.getId()).map(Division::getDivision).toList()));
        }
    }

    /**
     * Clears the form from previous values.
     */
    private void clear() {
        output.setText("");
        customerIDField.setText("");
        customerNameField.setText("");
        customerStateBox.setValue(null);
        customerCountryBox.setValue(null);
        customerPhoneField.setText("");
        customerPostalCodeField.setText("");
        customerAddressField.setText("");
    }

    /**
     * Sets scene to appointments view.
     */
    public void onEditAppointments() {
        clear();
        scenesAndControllers.setStageToAppointment();
    }

    /**
     * Displays an alert if there is an appointment within 15 minutes from user's log in.
     *
     * @param a an upcoming appointment
     */
    public void setAlert(Appointment a) {
        upcomingAppointment.setText("Within 15 minutes there is an appointment with ID " + a.getId() + " and starting at " + a.getStartDate());
    }
}
