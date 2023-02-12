package xyz.bubblesort.software2wgu.models;

/**
 * Representation of the first_level_divisions table entity
 */
public class Division {
    private int id;
    private String division;
    private int countryID;

    public int getId() {
        return id;
    }

    public String getDivision() {
        return division;
    }

    public int getCountryID() {
        return countryID;
    }

    public Division(int id, String division, int countryID) {
        this.id = id;
        this.division = division;
        this.countryID = countryID;
    }
}
