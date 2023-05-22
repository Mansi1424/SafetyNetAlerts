package mansi.safetynetalerts.helper;

import mansi.safetynetalerts.jsontopojo.ReadJson;
import mansi.safetynetalerts.model.Firestation;
import mansi.safetynetalerts.model.MedicalRecord;
import mansi.safetynetalerts.model.Person;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class HelperMethods {

    private final List<Firestation> firestationList;
    private final List<Person> personList;
    private final List<MedicalRecord> medicalRecordList;

    public HelperMethods(ReadJson readJson) throws IOException {
        this.firestationList = readJson.returnFirestationsList();
        this.personList = readJson.returnPersonsList();
        this.medicalRecordList = readJson.returnMedicalRecordsList();
    }


    public int getAge(String Stringdob) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        int age;
        Date dateOfBirth = dateFormat.parse(Stringdob);
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);

        Calendar today = Calendar.getInstance();
        age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;

    }

    /**
     * Checks if List of Persons contains the specified Name
     *
     * @param list listOfPersons
     * @param name FirstName
     * @return True or False
     */
    public boolean containsPersonWithName(List<Person> list, String name) {
        for (Person person : list) {
            if (person.getFirstName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method used by /fire url to get MedRecord by first/lastName
     *
     * @param firstName firstNameOfPerson
     * @param lastName  lastNameOfPerson
     */
    public MedicalRecord findRecordByName(String firstName, String lastName) throws ParseException {

        List<Person> filteredStream = personList.stream()
                .filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)).toList();
        int age = 0;

        MedicalRecord matchingRecord = null;
        for (Person person : filteredStream) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (Objects.equals(medicalRecord.getFirstName(), person.getFirstName())
                        && Objects.equals(medicalRecord.getLastName(), person.getLastName())) {
                    matchingRecord = medicalRecord;
                }
            }
        }

        return matchingRecord;


    }


}

