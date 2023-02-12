package xyz.bubblesort.software2wgu.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Representation of the appointments table entity
 */
public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private int contactID;
    private String type;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int customerID;
    private int userID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        for (Contact c : Repository.getContacts()) {
            if (c.getId() == contactID) {
                return c.getContactName();
            }
        }
        return null;
    }

    public Contact getContact() {
        for (Contact c : Repository.getContacts()) {
            if (c.getId() == contactID) {
                return c;
            }
        }
        return null;
    }

    public Customer getCustomer() {
        for (Customer c : Repository.getCustomers()) {
            if (c.getId() == customerID) {
                return c;
            }
        }
        return null;
    }

    public User getUser() {
        for (User u : Repository.getUsers()) {
            if (u.getId() == userID) {
                return u;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public LocalDate getStartDateCut() {
        return startDate.toLocalDate();
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public LocalDate getEndDateCut() {
        return endDate.toLocalDate();
    }

    public LocalTime getStartTime() {
        return startDate.toLocalTime();
    }

    public LocalTime getEndTime() {
        return endDate.toLocalTime();
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Appointment(int id, String title, String description, String location, int contactID, String type, ZonedDateTime startDate, ZonedDateTime endDate, int customerID, int userID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactID = contactID;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerID = customerID;
        this.userID = userID;
    }
}
