package Mansi.SafetyNetAlerts.Model;

public class MedicalRecord {

    private String firstName;
    private String lastName;
    private String birthdate;
    private String medications;
    private String allergies;

    public MedicalRecord(String firstName, String lastName, String birthdate, String medications, String allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.medications = medications;
        this.allergies = allergies;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getMedications() {
        return medications;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
}
