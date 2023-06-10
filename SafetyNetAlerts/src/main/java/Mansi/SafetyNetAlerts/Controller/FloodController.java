package mansi.safetynetalerts.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mansi.safetynetalerts.helper.HelperMethods;
import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import mansi.safetynetalerts.model.Firestation;
import mansi.safetynetalerts.model.MedicalRecord;
import mansi.safetynetalerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Handles /Flood URL
 */
@RestController
public class FloodController {

    private final List<Firestation> firestationList;
    private final List<Person> personList;
    private final List<MedicalRecord> medicalRecordList;
    private final HelperMethods helperMethods;

    @Autowired
    public FloodController(ReadJson readJson, HelperMethods helperMethods) throws IOException {
        this.firestationList = readJson.returnFirestationsList();
        this.personList = readJson.returnPersonsList();
        this.medicalRecordList = readJson.returnMedicalRecordsList();
        this.helperMethods = helperMethods;
    }

    /**
     * 5th URL /flood/stations
     *
     * @param stations listOfStations
     * @return People By Household
     */
    @GetMapping("/flood")
    @ResponseBody
    public ResponseEntity<Object> getFloodInfo(@RequestParam List<String> stations) throws ParseException {

        JsonObject responseJsonObject = new JsonObject();

        List<Firestation> firestationsFilteredStream = new ArrayList<>();
        for (String station : stations) {
            List<Firestation> matchingStations = firestationList.stream()
                    .filter(firestation -> firestation.getStation().equals(station)).toList();
            firestationsFilteredStream.addAll(matchingStations);
        }

        if (firestationsFilteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        List<String> stationAddresses = new ArrayList<>();
        List<Person> matchingPersons = new ArrayList<>();
        for (Firestation firestation : firestationsFilteredStream) {
            String stationAddress = firestation.getAddress();
            stationAddresses.add(stationAddress);
            for (Person person : personList) {
                if (Objects.equals(person.getAddress(), stationAddress) && helperMethods.containsPersonWithName(matchingPersons, person.getFirstName())) {
                    Person matchedPerson = new Person(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone());
                    matchingPersons.add(matchedPerson);

                }
            }
        }

        // Make map from matching persons
        Map<String, Person> peopleMap = matchingPersons.stream()
                .collect(Collectors.toMap(Person::getFirstName, person -> person));


        for (String stationAddress : stationAddresses) {
            List<JsonObject> filteredPersonList = new ArrayList<>();
            // Loop through map and get wanted fields in PersonObject then save to filteredPersonList
            for (Map.Entry<String, Person> entry : peopleMap.entrySet()) {
                Person person = entry.getValue();
                if (stationAddress.equals(person.getAddress())) {
                    MedicalRecord record = helperMethods.findRecordByName(person.getFirstName(), person.getLastName());
                    String dob = record.getBirthdate();
                    String personAge = String.valueOf(helperMethods.getAge(dob));
                    List<String> medications = record.getMedications();
                    List<String> allergies = record.getAllergies();
                    JsonArray medicationsArray = new JsonArray();
                    JsonArray allergiesArray = new JsonArray();
                    //Turn medications to JsonArray
                    for (String medication : medications) {
                        medicationsArray.add(medication);
                    }
                    for (String allergy : allergies) {
                        allergiesArray.add(allergy);
                    }
                    JsonObject personJson = new JsonObject();
                    personJson.addProperty("firstName", person.getFirstName());
                    personJson.addProperty("lastName", person.getLastName());
                    personJson.addProperty("phoneNum", person.getPhone());
                    personJson.addProperty("age", personAge);
                    personJson.add("medications", medicationsArray);
                    personJson.add("allergies", allergiesArray);
                    filteredPersonList.add(personJson);
                    // Turn list of persons to JsonArray to be a part of a JsonObject
                    JsonArray personArray = new JsonArray();
                    for (JsonObject personInList : filteredPersonList) {
                        personArray.add(personInList);
                    }
                    responseJsonObject.add(stationAddress, personArray);


                }

            }

        }
        return ResponseEntity.of(Optional.of(responseJsonObject));
    }


}
