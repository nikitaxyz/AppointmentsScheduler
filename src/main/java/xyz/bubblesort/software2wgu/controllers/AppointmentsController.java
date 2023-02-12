package xyz.bubblesort.software2wgu.controllers;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import xyz.bubblesort.software2wgu.db.Queries;
import xyz.bubblesort.software2wgu.models.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * JavaFX controller for appointments scene (appointments-view.fxml).
 */
public class AppointmentsController {
    private final TextField filter = new TextField();
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private ComboBox<User> userComboBox;
    @FXML
    private GridPane form;
    @FXML
    private Label output;
    @FXML
    private TableView<Appointment> table;
    @FXML
    private TableColumn<Appointment, String> appIDColumn;
    @FXML
    private TableColumn<Appointment, String> appTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appLocationColumn;
    @FXML
    private TableColumn<Appointment, String> appContactColumn;
    @FXML
    private TableColumn<Appointment, String> appTypeColumn;
    @FXML
    private TableColumn<Appointment, String> appStartDateColumn;
    @FXML
    private TableColumn<Appointment, String> appEndDateColumn;
    @FXML
    private TableColumn<Appointment, String> appCustomerIDColumn;
    @FXML
    private TableColumn<Appointment, String> appUserIDColumn;
    @FXML
    private TableColumn<Appointment, String> appStartTimeColumn;
    @FXML
    private TableColumn<Appointment, String> appEndTimeColumn;
    @FXML
    private TextField titleField;
    @FXML
    private TextField idField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<Contact> contactComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField startTimeFieldH;
    @FXML
    private TextField startTimeFieldM;
    @FXML
    private ChoiceBox<String> startTimeComboBox;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeFieldH;
    @FXML
    private TextField endTimeFieldM;
    @FXML
    private ChoiceBox<String> endTimeComboBox;
    @FXML
    private RadioButton radioAll;
    @FXML
    private RadioButton radioWeek;
    @FXML
    private RadioButton radioMonth;
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
     * Switch filter value for a tableview based on the radiobutton clicked.
     *
     * @param actionEvent helps to determine the source of the event (radiobutton clicked)
     */
    public void onRadioClick(ActionEvent actionEvent) {
        String id = ((Node) actionEvent.getSource()).getId();
        if (id.equals("radioWeek")) {
            filter.setText("week");
        } else if (id.equals("radioMonth")) {
            filter.setText("month");
        } else {
            filter.setText("all");
        }
    }


    /**
     * Initializes appointments controller and fxml view with data.
     */
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        radioAll.setToggleGroup(group);
        radioWeek.setToggleGroup(group);
        radioMonth.setToggleGroup(group);
        radioAll.setSelected(true);
        radioAll.setSelected(true);

        contactComboBox.setItems(Repository.getContacts());
        contactComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Contact contact) {
                if (contact != null) {
                    return contact.getContactName();
                } else {
                    return "";
                }

            }

            @Override
            public Contact fromString(String string) {
                return null;
            }
        });
        customerComboBox.setItems(Repository.getCustomers());
        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                return customer.getId() + " - " + customer.getName();
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
        endTimeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList("AM", "PM")));
        startTimeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList("AM", "PM")));
        userComboBox.setItems(Repository.getUsers());
        userComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user.getId() + " - " + user.getUserName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        appIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        appTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDateCut"));
        appStartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDateCut"));
        appEndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        FilteredList<Appointment> filteredAppointments = new FilteredList<>(Repository.getAppointments(), p -> true);
        filter.textProperty().addListener((observable, oldFilter, newFilter) -> filteredAppointments.setPredicate(appointment -> {
            if (newFilter == null || newFilter.equals("all") || newFilter.isEmpty()) {
                return true;
            }
            if (newFilter.equals("week")) {
                ZonedDateTime startDateTime = appointment.getStartDate();
                ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
                return startDateTime.isAfter(currentDateTime) && startDateTime.isBefore(currentDateTime.plusWeeks(1));
            }
            if (newFilter.equals("month")) {
                ZonedDateTime startDateTime = appointment.getStartDate();
                ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
                return startDateTime.isAfter(currentDateTime) && startDateTime.isBefore(currentDateTime.plusMonths(1));
            }

            return false;
        }));

        SortedList<Appointment> sortedAppointments = new SortedList<>(filteredAppointments);
        sortedAppointments.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedAppointments);

    }

    /**
     * Sets scene back to customers view.
     */
    public void onBack() {
        scenesAndControllers.setStageToCustomers();
    }

    /**
     * Prepares the form for adding a new appointment.
     */
    public void onAdd() {
        clear();
        form.setDisable(false);
        editMode = 0;
        idField.setText("Auto generated");
        startTimeComboBox.setValue("AM");
        endTimeComboBox.setValue("AM");
    }

    /**
     * Clears the form from previous values.
     */
    private void clear() {
        output.setText("");
        idField.setText("");
        descriptionField.setText("");
        typeField.setText("");
        locationField.setText("");
        titleField.setText("");

        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startTimeComboBox.setValue(null);
        endTimeComboBox.setValue(null);
        startTimeFieldH.setText("");
        startTimeFieldM.setText("");
        endTimeFieldH.setText("");
        endTimeFieldM.setText("");

        customerComboBox.setValue(null);
        contactComboBox.setValue(null);
        userComboBox.setValue(null);
    }

    /**
     * Prepares the form for modifying an appointment.
     */
    public void onModify() throws ParseException {
        clear();
        Appointment appointment = table.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            form.setDisable(false);
            editMode = 1;
            idField.setText(String.valueOf(appointment.getId()));
            titleField.setText(appointment.getTitle());
            descriptionField.setText(appointment.getDescription());
            typeField.setText(appointment.getType());
            locationField.setText(appointment.getLocation());

            startDatePicker.setValue(LocalDate.from(appointment.getStartDate()));
            endDatePicker.setValue(LocalDate.from(appointment.getEndDate()));

            LocalTime startTime = appointment.getStartTime();
            setTimeFields(startTime, startTimeFieldH, startTimeFieldM, startTimeComboBox);

            LocalTime endTime = appointment.getEndTime();
            setTimeFields(endTime, endTimeFieldH, endTimeFieldM, endTimeComboBox);

            contactComboBox.setValue(appointment.getContact());
            customerComboBox.setValue(appointment.getCustomer());
            userComboBox.setValue(appointment.getUser());

        }
    }

    private void setTimeFields(LocalTime endTime, TextField endTimeFieldH, TextField endTimeFieldM, ChoiceBox<String> endTimeComboBox) throws ParseException {
        Date endDateTime = displayFormat.parse(endTime.getHour() + ":" + endTime.getMinute());
        String formattedEndTime = parseFormat.format(endDateTime);
        endTimeFieldH.setText(formattedEndTime.substring(0, 2));
        endTimeFieldM.setText(formattedEndTime.substring(3, 5));
        endTimeComboBox.setValue(formattedEndTime.substring(6, 8));
    }


    /**
     * Deletes the selected appointment from a database and outputs success message.
     */
    public void onDelete() {
        clear();
        Appointment appointment = table.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            output.setText("An appointment " + appointment.getTitle() + " with an ID - " + appointment.getId() + " and type - " + appointment.getType() + " has been canceled/deleted");
            Queries.deleteAppointment(appointment);
            Repository.getAppointments().remove(appointment);
        }
        table.refresh();
    }

    /**
     * Clears and disables the form if Cancel button was pressed.
     */
    public void appOnCancel() {
        clear();
        form.setDisable(true);
    }

    /**
     * Adds or modify an appointments with data from the form.
     */
    public void appOnSave() {
        if (editMode == 0) {
            addNewAppointment();
        } else {
            modifyAppointment();
        }
    }

    /**
     * Adds a new appointment to the database if dates and times are valid.
     */
    public void addNewAppointment() {
        int contactID;
        int userID;
        int customerID;
        try {
            contactID = contactComboBox.getValue().getId();
            userID = userComboBox.getValue().getId();
            customerID = customerComboBox.getValue().getId();
        } catch (Exception e) {
            output.setText("Contact ID, User ID or Customer ID is/are not selected");
            return;
        }

        ZonedDateTime startTime = createDate(startDatePicker, startTimeFieldH, startTimeFieldM, startTimeComboBox);
        ZonedDateTime endTime = createDate(endDatePicker, endTimeFieldH, endTimeFieldM, endTimeComboBox);
        if (startTime == null || endTime == null || isInvalidDateAndTime(startTime, endTime, customerComboBox.getValue().getId(), -1)) {
            return;
        }
        String title = titleField.getText();
        String description = descriptionField.getText();
        String type = typeField.getText();
        String location = locationField.getText();

        int appointmentID = Queries.addAppointment(title, description, location, type, startTime, endTime, customerID, userID, contactID);
        if (appointmentID != -1) {
            Appointment appointment = new Appointment(appointmentID, title, description, location, contactID, type, startTime, endTime, customerID, userID);
            Repository.getAppointments().add(appointment);
        }
        clear();
        form.setDisable(true);
        table.refresh();
    }

    /**
     * Creates a ZonedDateTime object with user's default system locale from values in the form
     *
     * @param datePicker   date value selected in the datePicker field
     * @param timeFieldH   hours value form the left part of the time field
     * @param timeFieldM   minutes value form the right part of the time field
     * @param timeComboBox PM or AM value selected from the combobox
     * @return a date with user's default system locale
     */
    public ZonedDateTime createDate(DatePicker datePicker, TextField timeFieldH, TextField timeFieldM, ChoiceBox<String> timeComboBox) {
        try {
            ZonedDateTime date = ZonedDateTime.from(datePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()));
            long H = Long.parseLong(timeFieldH.getText());
            long M = Long.parseLong(timeFieldM.getText());
            if (H > 12 || H < 0) throw new RuntimeException();
            if (M > 59 || M < 0) throw new RuntimeException();
            String am = timeComboBox.getValue();
            Date dateTime = parseFormat.parse(H + ":" + M + " " + am);
            String df = displayFormat.format(dateTime);
            date = date.plusHours(Long.parseLong(df.substring(0,2)));
            date = date.plusMinutes(Long.parseLong(df.substring(3,5)));
            return date;
        } catch (Exception e) {
            output.setText("Incorrect time values. Please fix start time and/or end time");
        }
        return null;
    }

    /**
     * Modifies the selected appointment in the database if dates and times are valid.
     */
    public void modifyAppointment() {
        Appointment appointment = table.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            ZonedDateTime startTime = createDate(startDatePicker, startTimeFieldH, startTimeFieldM, startTimeComboBox);
            ZonedDateTime endTime = createDate(endDatePicker, endTimeFieldH, endTimeFieldM, endTimeComboBox);
            if (startTime == null || endTime == null || isInvalidDateAndTime(startTime, endTime, customerComboBox.getValue().getId(), appointment.getId())) {
                return;
            }
            appointment.setStartDate(startTime);
            appointment.setEndDate(endTime);
            appointment.setTitle(titleField.getText());
            appointment.setDescription(descriptionField.getText());
            appointment.setType(typeField.getText());
            appointment.setLocation(locationField.getText());
            appointment.setContactID(contactComboBox.getValue().getId());
            appointment.setUserID(userComboBox.getValue().getId());
            appointment.setCustomerID(customerComboBox.getValue().getId());
            Queries.updateAppointment(appointment);
            clear();
            form.setDisable(true);
            table.refresh();
        }
    }

    /**
     * Checks if the date meets business requirements.
     *
     * @param startTime     start data/time of the appointment
     * @param endTime       end date/time of the appointment
     * @param customerID    an appointment's customer
     * @param appointmentID an appointment's id
     * @return true if the date is valid and false otherwise
     */
    private boolean isInvalidDateAndTime(ZonedDateTime startTime, ZonedDateTime endTime, int customerID, int appointmentID) {
        ZonedDateTime startTimeEST = startTime.withZoneSameInstant(ZoneId.of("EST", ZoneId.SHORT_IDS));
        ZonedDateTime endTimeEST = endTime.withZoneSameInstant(ZoneId.of("EST", ZoneId.SHORT_IDS));
        if (startTimeEST.getDayOfWeek().equals(DayOfWeek.SUNDAY) ||
                startTimeEST.getDayOfWeek().equals(DayOfWeek.SATURDAY) ||
                endTimeEST.getDayOfWeek().equals(DayOfWeek.SUNDAY) ||
                endTimeEST.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            output.setText("Invalid start and end dates: Business does not operate on Saturday and Sunday. Please correct dates");
            return true;
        }
        if (startTimeEST.isAfter(endTimeEST) || startTimeEST.getHour() > endTimeEST.getHour()) {
            output.setText("Invalid start and end dates: Start date and time of an appointment can not be after end date and time. Please correct dates");
            return true;
        }
        if (startTimeEST.getHour() < 8 || startTimeEST.getHour() > 22 || endTimeEST.getHour() > 22) {
            output.setText("Invalid start and end dates: Business hours are 8:00 am to 10:00 pm EST. Please correct dates");
            return true;
        }
        for (Appointment a : Repository.getAppointments()) {
            if (a.getId() != appointmentID && a.getCustomerID() == customerID) {
                if (startTime.isBefore(a.getEndDate()) && a.getStartDate().isBefore(endTime)) {
                    output.setText("Customer " + customerID + " already has an appointment between start and end dates/time. Please change date/time");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generates and display a report with the total number of customer appointments by type and month.
     * Uses LAMBDA expression to easily add text about each group of appointments to the view
     */
    public void onTypeAndMonthReport() {
        final Stage reportStage = new Stage();
        reportStage.initModality(Modality.APPLICATION_MODAL);
        reportStage.initOwner(scenesAndControllers.getStage());
        VBox mainNode = new VBox();
        Scene reportScene = new Scene(mainNode);
        mainNode.getChildren().add(new Text("The total number of customer appointments by type and month"));
        mainNode.getChildren().add(new Text(""));
        List<String[]> appointmentsGroups = Queries.getAppointmentsGroups();
        appointmentsGroups.forEach(group -> mainNode.getChildren().add(new Text(group[0] + " in " + group[1] + ": " + group[2])));
        reportStage.setTitle("Report");
        reportStage.setScene(reportScene);
        reportStage.show();
    }

    /**
     * Generates and display a report with a schedule for each contact.
     */
    public void onContactsReport() {
        HashMap<String, List<Appointment>> contactToAppointments = new HashMap<>();
        for (Appointment appointment : Repository.getAppointments()) {
            String contactName = "";
            for (Contact contact : Repository.getContacts()) {
                if (contact.getId() == appointment.getContactID()) {
                    contactName = contact.getContactName();
                    break;
                }
            }
            if (!contactToAppointments.containsKey(contactName))
                contactToAppointments.put(contactName, new ArrayList<>());
            contactToAppointments.get(contactName).add(appointment);
        }

        final Stage reportStage = new Stage();
        reportStage.initModality(Modality.APPLICATION_MODAL);
        reportStage.initOwner(scenesAndControllers.getStage());
        VBox mainNode = new VBox();
        Scene reportScene = new Scene(mainNode);
        mainNode.getChildren().add(new Text("A schedule for each contact"));
        mainNode.getChildren().add(new Text(""));
        for (String contactName : contactToAppointments.keySet()) {
            mainNode.getChildren().add(new Text(contactName.toUpperCase() + " schedule:"));
            for (Appointment a : contactToAppointments.get(contactName)) {
                String text = String.format("Appointment ID: %s, Title: %s, Type: %s, Description: %s, Start: %s %s, End: %s %s, Customer ID: %s", a.getId(), a.getTitle(), a.getType(), a.getDescription(), a.getStartDateCut(), a.getStartTime(), a.getEndDateCut(), a.getEndTime(), a.getCustomerID());
                mainNode.getChildren().add(new Text(text));
            }
            mainNode.getChildren().add(new Text(""));
        }
        reportStage.setTitle("Report");
        reportStage.setScene(reportScene);
        reportStage.show();

    }

    /**
     * Generates and display a report with all appointments for each contact.
     */
    public void onCustomersReport() {
        HashMap<String, List<Appointment>> customerToAppointments = new HashMap<>();
        for (Appointment appointment : Repository.getAppointments()) {
            String customerName = "";
            for (Customer customer : Repository.getCustomers()) {
                if (customer.getId() == appointment.getCustomerID()) {
                    customerName = customer.getName();
                    break;
                }
            }
            if (!customerToAppointments.containsKey(customerName))
                customerToAppointments.put(customerName, new ArrayList<>());
            customerToAppointments.get(customerName).add(appointment);
        }

        final Stage reportStage = new Stage();
        reportStage.initModality(Modality.APPLICATION_MODAL);
        reportStage.initOwner(scenesAndControllers.getStage());
        VBox mainNode = new VBox();
        Scene reportScene = new Scene(mainNode);
        mainNode.getChildren().add(new Text("Appointments for each customer"));
        mainNode.getChildren().add(new Text(""));
        for (String customerName : customerToAppointments.keySet()) {
            mainNode.getChildren().add(new Text("Customer " + customerName.toUpperCase() + " has following appointments:"));
            for (Appointment a : customerToAppointments.get(customerName)) {
                String text = String.format("Appointment ID: %s, Location: %s, Type: %s, Description: %s, Start: %s %s, End: %s %s", a.getId(), a.getLocation(), a.getType(), a.getDescription(), a.getStartDateCut(), a.getStartTime(), a.getEndDateCut(), a.getEndTime());
                mainNode.getChildren().add(new Text(text));
            }
            mainNode.getChildren().add(new Text(""));
        }
        reportStage.setTitle("Report");
        reportStage.setScene(reportScene);
        reportStage.show();

    }
}
