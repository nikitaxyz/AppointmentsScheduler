package xyz.bubblesort.software2wgu.models;

/**
 * Representation of the countries table entity
 */
public class Country {
    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    private int id;
    private String country;

    public Country(int id, String country) {
        this.id = id;
        this.country = country;
    }
}
