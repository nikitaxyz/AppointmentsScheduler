package xyz.bubblesort.software2wgu.models;

/**
 * Representation of the customers table entity
 */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int division;

    public String getState() {
        for (Division d : Repository.getDivisions()) {
            if (d.getId() == division) {
                return d.getDivision();
            }
        }
        return null;
    }

    public Country getCountry() {
        for (Division d : Repository.getDivisions()) {
            if (d.getId() == division) {
                int countryID = d.getCountryID();
                for (Country c : Repository.getCountries()) {
                    if (c.getId() == countryID) {
                        return c;
                    }
                }
            }
        }
        return null;
    }

    public String getCountryName() {
        return getCountry().getCountry();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDivision() {
        return division;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public Customer(int id, String name, String address, String postalCode, String phone, int division) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.division = division;
    }
}
