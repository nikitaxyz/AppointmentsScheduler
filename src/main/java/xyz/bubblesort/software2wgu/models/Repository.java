package xyz.bubblesort.software2wgu.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import xyz.bubblesort.software2wgu.db.Queries;

/**
 * Stores and provide access to all data
 */
public class Repository {
    private static final ObservableList<Contact> contacts = FXCollections.observableArrayList();
    private static final ObservableList<Country> countries = FXCollections.observableArrayList();
    private static final ObservableList<Division> divisions = FXCollections.observableArrayList();
    private static final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private static final ObservableList<User> users = FXCollections.observableArrayList();
    private static final User user = new User();

    /**
     * Queries database to get all the information required for the application
     */
    public static void fetchData() {
        Repository.getUsers().addAll(Queries.getUsers());
        Repository.getCustomers().addAll(Queries.getCustomers());
        Repository.getContacts().addAll(Queries.getContacts());
        Repository.getAppointments().addAll(Queries.getAppointments());
        Repository.getCountries().addAll(Queries.getCountries());
        Repository.getDivisions().addAll(Queries.getDivisions());
    }

    /**
     * Getter for Contact objects
     * @return all Contact objects
     */
    public static ObservableList<Contact> getContacts() {
        return contacts;
    }

    /**
     * Getter for Division objects
     * @return all Division objects
     */
    public static ObservableList<Division> getDivisions() {
        return divisions;
    }

    /**
     * Getter for Country objects
     * @return all Country objects
     */
    public static ObservableList<Country> getCountries() {
        return countries;
    }

    /**
     * Getter for Appointment objects
     * @return all Appointment objects
     */
    public static ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Getter for User objects
     * @return all User objects
     */
    public static ObservableList<User> getUsers() {
        return users;
    }

    /**
     * Getter for Customer objects
     * @return all Customer objects
     */
    public static ObservableList<Customer> getCustomers() {
        return customers;
    }

    /**
     * Getter for logged user
     * @return logged User object
     */
    public static User getUser() {
        return user;
    }

    /**
     * Sets logged user
     * @param user log in user
     */
    public static void setUser(User user) {
        getUser().setId(user.getId());
        getUser().setUserName(user.getUserName());
    }
}
