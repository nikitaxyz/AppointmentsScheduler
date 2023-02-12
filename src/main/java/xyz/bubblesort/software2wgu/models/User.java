package xyz.bubblesort.software2wgu.models;

/**
 * Representation of the users table entity
 */
public class User {
    private int id;
    private String userName;

    public User() {
        id = -1;
        userName = "Undefined";
    }

    public User(int id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
