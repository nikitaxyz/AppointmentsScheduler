package xyz.bubblesort.software2wgu.models;

/**
 * Representation of the contacts table entity
 */
public class Contact {
    private int id;
    private String contactName;
    private String email;

    public int getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEmail() {
        return email;
    }

    public Contact(int id, String contactName, String email) {
        this.id = id;
        this.contactName = contactName;
        this.email = email;
    }
}
