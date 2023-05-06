package mansi.safetynetalerts.model;

/**
 * Firestation Model
 */
public class Firestation {

    private String address;
    private String station;

    public Firestation() {
        super();
    }

    public Firestation(String address, String station) {
        this.address = address;
        this.station = station;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "Firestation{" +
                "address='" + address + '\'' +
                ", station='" + station + '\'' +
                '}';
    }
}
