package xyz.bubblesort.software2wgu.db;

import xyz.bubblesort.software2wgu.models.*;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for querying a database
 */
public abstract class Queries {

    /**
     * Gets current UTC OffsetDateTime object for Timestamp MySQL field.
     *
     * @return current UTC OffsetDateTime
     */
    public static OffsetDateTime getOffsetDateTimeUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Gets all customers from the database.
     *
     * @return all customers from the customers table
     */
    public static List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (Statement stat = DBManager.getConnection().createStatement()) {
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String address = rs.getString(3);
                String postalCode = rs.getString(4);
                String phone = rs.getString(5);
                int division = rs.getInt(10);
                Customer customer = new Customer(id, name, address, postalCode, phone, division);
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return customers;
    }

    /**
     * Adds a new customer to the database.
     *
     * @param name       user's name
     * @param address    user's address
     * @param postalCode user's postal code
     * @param phone      user's phone
     * @param divisionID users division id
     * @return id of a new customer
     */
    public static int addCustomer(String name, String address, String postalCode, String phone, int divisionID) {
        int customerID = -1;
        String query = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stat.setString(1, name);
            stat.setString(2, address);
            stat.setString(3, postalCode);
            stat.setString(4, phone);
            stat.setObject(5, getOffsetDateTimeUTC());
            stat.setString(6, Repository.getUser().getUserName());
            stat.setObject(7, getOffsetDateTimeUTC());
            stat.setString(8, Repository.getUser().getUserName());
            stat.setInt(9, divisionID);
            stat.execute();
            try (ResultSet generatedKeys = stat.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customerID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return customerID;
    }

    /**
     * Updates a customer in the database.
     *
     * @return number of rows updated
     */
    public static int updateCustomer(Customer customer) {
        String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query)) {
            stat.setString(1, customer.getName());
            stat.setString(2, customer.getAddress());
            stat.setString(3, customer.getPostalCode());
            stat.setString(4, customer.getPhone());
            stat.setObject(5, getOffsetDateTimeUTC());
            stat.setString(6, Repository.getUser().getUserName());
            stat.setInt(7, customer.getDivision());
            stat.setInt(8, customer.getId());
            return stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return 0;
    }

    /**
     * Deletes a customer from the database.
     *
     * @param customer a customer to be deleted
     * @return number of rows deleted
     */
    public static int deleteCustomer(Customer customer) {
        String query = "DELETE FROM customers WHERE Customer_ID = ?";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query)) {
            stat.setInt(1, customer.getId());
            return stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets all appointments from the database.
     *
     * @return all appointments from the appointments table
     */
    public static List<Appointment> getAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments";
        try (Statement stat = DBManager.getConnection().createStatement()) {
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                appointments.add(parseAppointment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return appointments;
    }

    /**
     * Counts all groups of appointments grouped by type and month.
     *
     * @return groups of appointments from the appointments table
     */
    public static List<String[]> getAppointmentsGroups() {
        List<String[]> appointmentsGroups = new ArrayList<>();
        String query = "SELECT Type, MONTH(Start), COUNT(*) FROM appointments GROUP BY Type, MONTH(Start)";
        try (Statement stat = DBManager.getConnection().createStatement()) {
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                appointmentsGroups.add(new String[]{rs.getString(1), Month.of(rs.getInt(2)).toString(), rs.getString(3)});
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return appointmentsGroups;
    }

    /**
     * Adds a new appointment to the database.
     *
     * @param title       appointment's title
     * @param description appointment's description
     * @param location    appointment's location
     * @param type        appointment's type
     * @param start       appointment's start date
     * @param end         appointment's end date
     * @param customerID  appointment's customer id
     * @param userID      appointment's user id
     * @param contactID   appointment's contact id
     * @return id of the new appointment
     */
    public static int addAppointment(String title, String description, String location, String type, ZonedDateTime start, ZonedDateTime end, int customerID, int userID, int contactID) {
        int appointmentID = -1;
        String query = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stat.setString(1, title);
            stat.setString(2, description);
            stat.setString(3, location);
            stat.setString(4, type);
            stat.setObject(5, start);
            stat.setObject(6, end);
            stat.setObject(7, getOffsetDateTimeUTC());
            stat.setString(8, Repository.getUser().getUserName());
            stat.setObject(9, getOffsetDateTimeUTC());
            stat.setString(10, Repository.getUser().getUserName());
            stat.setInt(11, customerID);
            stat.setInt(12, userID);
            stat.setInt(13, contactID);
            stat.execute();
            try (ResultSet generatedKeys = stat.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    appointmentID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return appointmentID;
    }

    /**
     * Updates an appointment in the database.
     *
     * @param appointment to be updated
     * @return number of rows updated
     */
    public static int updateAppointment(Appointment appointment) {
        String query = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query)) {
            stat.setString(1, appointment.getTitle());
            stat.setString(2, appointment.getDescription());
            stat.setString(3, appointment.getLocation());
            stat.setString(4, appointment.getType());
            stat.setObject(5, appointment.getStartDate());
            stat.setObject(6, appointment.getEndDate());
            stat.setObject(7, getOffsetDateTimeUTC());
            stat.setString(8, Repository.getUser().getUserName());
            stat.setInt(9, appointment.getCustomerID());
            stat.setInt(10, appointment.getUserID());
            stat.setInt(11, appointment.getContactID());
            stat.setInt(12, appointment.getId());
            return stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return 0;
    }

    /**
     * Makes a query to check if there is an appointments within 15 minutes from
     *
     * @param userDateTime user date and time to check
     * @return an appointment within 15 minutes if found and null otherwise
     */
    public static Appointment appointmentWithin15Minutes(ZonedDateTime userDateTime) {
        String query = "SELECT * FROM appointments WHERE Start BETWEEN ? AND ?";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query)) {
            stat.setObject(1, userDateTime);
            stat.setObject(2, userDateTime.plusMinutes(15));
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                return parseAppointment(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return null;
    }

    private static Appointment parseAppointment(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String title = rs.getString(2);
        String description = rs.getString(3);
        String location = rs.getString(4);
        String type = rs.getString(5);

        LocalDateTime startTime = rs.getObject(6, LocalDateTime.class);
        ZonedDateTime zonedStartTime = startTime.atZone(ZoneId.of("UTC"));
        zonedStartTime = zonedStartTime.withZoneSameInstant(ZoneId.systemDefault());

        LocalDateTime endTime = rs.getObject(7, LocalDateTime.class);
        ZonedDateTime zonedEndTime = endTime.atZone(ZoneId.of("UTC"));
        zonedEndTime = zonedEndTime.withZoneSameInstant(ZoneId.systemDefault());

        int customerID = rs.getInt(12);
        int userID = rs.getInt(13);
        int contactID = rs.getInt(14);
        return new Appointment(id, title, description, location, contactID, type, zonedStartTime, zonedEndTime, customerID, userID);
    }

    /**
     * Deletes an appointment from the database.
     *
     * @param appointment an appointment to be deleted
     * @return number of rows deleted
     */
    public static int deleteAppointment(Appointment appointment) {
        String query = "DELETE FROM appointments WHERE Appointment_ID = ?";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query)) {
            stat.setInt(1, appointment.getId());
            return stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return 0;
    }

    /**
     * Deletes all appointments associated with a customer.
     * @param customer customer whose appointments will be deleted
     * @return number of deleted appointments
     */
    public static int deleteCustomerAppointments(Customer customer) {
        String query = "DELETE FROM appointments WHERE Customer_ID = ?";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query)) {
            stat.setInt(1, customer.getId());
            return stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return 0;
    }

    /**
     * Selects user from users table
     * @param userID userID of the user to be selected
     * @param password password of the user to be selected
     * @return User object if user is found and null otherwise
     */
    public static User getUser(int userID, String password) {
        User user = null;
        String query = "SELECT * FROM users WHERE User_ID = ? AND Password = ? ";
        try (PreparedStatement stat = DBManager.getConnection().prepareStatement(query)) {
            stat.setInt(1, userID);
            stat.setString(2, password);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String userName = rs.getString(2);
                user = new User(id, userName);
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return user;
    }

    /**
     * Gets all users from the database.
     *
     * @return all users from the users table
     */
    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Statement stat = DBManager.getConnection().createStatement()) {
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String userName = rs.getString(2);
                users.add(new User(id, userName));
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return users;
    }

    /**
     * Gets all contacts from the database.
     *
     * @return all contacts from the contacts table
     */
    public static List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM contacts";
        try (Statement stat = DBManager.getConnection().createStatement()) {
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String contactName = rs.getString(2);
                String email = rs.getString(3);
                Contact contact = new Contact(id, contactName, email);
                contacts.add(contact);
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return contacts;
    }

    /**
     * Gets all countries from the database.
     *
     * @return all countries from the countries table
     */
    public static List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();
        String query = "SELECT * FROM countries";
        try (Statement stat = DBManager.getConnection().createStatement()) {
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String country = rs.getString(2);
                countries.add(new Country(id, country));
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return countries;
    }

    /**
     * Gets all divisions from the database.
     *
     * @return all divisions from the first_level_divisions table
     */
    public static List<Division> getDivisions() {
        List<Division> divisions = new ArrayList<>();
        String query = "SELECT * FROM first_level_divisions";
        try (Statement stat = DBManager.getConnection().createStatement()) {
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String division = rs.getString(2);
                int countryID = rs.getInt(7);
                divisions.add(new Division(id, division, countryID));
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return divisions;
    }
}
