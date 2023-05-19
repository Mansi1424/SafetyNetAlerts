package mansi.safetynetalerts.controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mansi.safetynetalerts.helper.HelperMethods;
import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import mansi.safetynetalerts.model.Firestation;
import mansi.safetynetalerts.model.MedicalRecord;
import mansi.safetynetalerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handle Firestation Endpoints and firestation URLs
 */
@RestController
public class FireStationController {


    private final List<Firestation> firestationList;
    private final List<Person> personList;
    private final List<MedicalRecord> medicalRecordList;


    private static final Logger logger = LoggerFactory.getLogger(FireStationController.class);

    public FireStationController(ReadJson readJson) throws IOException {

        this.firestationList = ReadJson.returnFirestationsList();
        this.personList = ReadJson.returnPersonsList();
        this.medicalRecordList = ReadJson.returnMedicalRecordsList();
    }


    /**
     * Get all firestations
     */
    @GetMapping("/allFirestation")
    public List<Firestation> getFirestations() {

        logger.info("HTTP GET request received at /firestation URL");

        logger.info("Firestations List = " + firestationList);

        return firestationList;

    }

    /**
     * Post new Firestation
     *
     * @param newFirestation newFirestation
     */
    @PostMapping("/firestation")
    @ResponseBody
    public Firestation addFirestation(@RequestBody Firestation newFirestation) {

        logger.info("HTTP POST request received at /firestation URL");

        firestationList.add(newFirestation);

        logger.info("Added Firestation = " + newFirestation);
        return newFirestation;

    }

    /**
     * Update firestation station
     *
     * @param address address firestation serves
     * @param stationNumber number of firestation
     * @return firestation
     */
    @PutMapping("/firestation/{address}/{station}")
    @ResponseBody
    public ResponseEntity<Object> replaceFirestation(@PathVariable("address") String address, @PathVariable("station") String station, @RequestBody String stationNumber) {

        logger.info("HTTP PUT request received at /firestation URL");

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getAddress().contains(address))
                .filter(firestation -> firestation.getStation().equals(station)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        Firestation firestationObject = filteredStream.get(0);
        firestationObject.setStation(stationNumber);
        logger.info("Updated Firestation = " + firestationObject);

        return ResponseEntity.of(Optional.of(firestationObject));

    }


    /**
     * Delete Firestation
     *
     * @param address firestationAddress
     * @param station firestationStationNumber
     * @return deleted firestation
     */
    @DeleteMapping("/firestation/{address}/{station}")
    public ResponseEntity<Object> deleteFirestation(@PathVariable("address") String address, @PathVariable("station") String station) {

        StringBuilder respMessage = new StringBuilder();
        logger.info("HTTP DELETE request received at /firestation URL");

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getAddress().equals(address))
                .filter(firestation -> firestation.getStation().equals(station)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        Firestation firestationObject = filteredStream.get(0);
        firestationList.remove(firestationObject);

        ResponseEntity<Object> response = ResponseEntity.of(Optional.of(firestationObject));

        logger.info("deleted Firestation = " + response);

        return response;
    }


    /**
     * 1st URL - List of people by firestation
     *
     * @param stationNumber numberOfStation
     * @return list of people (children and adults) by stationNumber
     */
    @GetMapping("/firestation")
    @ResponseBody
    public ResponseEntity<Object> getPerson(@RequestParam String stationNumber) throws ParseException {

        JsonObject responseJsonObject = new JsonObject();

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getStation().equals(stationNumber)).toList();

        // If stationNumber not found return empty jsonObject
        if (filteredStream.isEmpty()) {
            List<JsonObject> emptyList = new ArrayList<>();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        //Find list of people associated to the stationNumber by matching address
        List<Person> matchingPersons = new ArrayList<>();
        for (Firestation firestation : filteredStream) {
            String stationAddress = firestation.getAddress();
            for (Person person : personList) {
                if (Objects.equals(person.getAddress(), stationAddress) && HelperMethods.containsPersonWithName(matchingPersons, person.getFirstName())) {
                    Person matchedPerson = new Person(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone());
                    matchingPersons.add(matchedPerson);

                }
            }
        }

        int age;
        int children = 0;
        int adults = 0;
        // Find ages of people located to stationNumber
        for (Person matchedPerson : matchingPersons) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (Objects.equals(medicalRecord.getFirstName(), matchedPerson.getFirstName())) {
                    String personDob = medicalRecord.getBirthdate();
                    age = HelperMethods.getAge(personDob);

                    if (age < 18) {
                        children++;
                    } else {
                        adults++;
                    }
                }
            }
        }

        // Create Map to get specific fields only
        Map<String, Person> peopleMap = matchingPersons.stream()
                .collect(Collectors.toMap(Person::getFirstName, person -> person));

        List<JsonObject> filteredPersonList = new ArrayList<>();

        // Loop through map and get wanted fields for output then save to filteredPersonList
        for (Map.Entry<String, Person> entry : peopleMap.entrySet()) {
            Person person = entry.getValue();
            JsonObject personJson = new JsonObject();
            personJson.addProperty("firstName", person.getFirstName());
            personJson.addProperty("lastName", person.getLastName());
            personJson.addProperty("address", person.getAddress());
            personJson.addProperty("phoneNum", person.getPhone());
            filteredPersonList.add(personJson);

        }

        // Add summary (num of adults and children) to filteredPersonList
        JsonObject summary = new JsonObject();
        summary.addProperty("children", children);
        summary.addProperty("adults", adults);
        filteredPersonList.add(summary);

        // Turn list of persons to JsonArray to be a part of a JsonObject
        JsonArray personArray = new JsonArray();
        for (JsonObject person : filteredPersonList) {
            personArray.add(person);
        }
        responseJsonObject.add("people", personArray);
        return ResponseEntity.of(Optional.of(responseJsonObject));

    }


    /**
     * 4th URL: /fire
     *
     * @param address firestationAddress
     * @return peopleListAndStationNum
     */
    @GetMapping("/fire")
    @ResponseBody
    public ResponseEntity<Object> getFire(@RequestParam String address) throws ParseException {
        JsonObject responseJsonObject = new JsonObject();

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getAddress().equals(address)).toList();

        // If address not found return empty jsonObject
        if (filteredStream.isEmpty()) {
            List<JsonObject> emptyList = new ArrayList<>();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        // Find person which matches those addresses
        List<Person> matchingPersons = new ArrayList<>();
        for (Firestation firestation : filteredStream) {
            String stationAddress = firestation.getAddress();
            for (Person person : personList) {
                if (Objects.equals(person.getAddress(), stationAddress) && HelperMethods.containsPersonWithName(matchingPersons, person.getFirstName())) {
                    Person matchedPerson = new Person(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone());
                    matchingPersons.add(matchedPerson);

                }
            }
        }

        // Make map from matching persons
        Map<String, Person> peopleMap = matchingPersons.stream()
                .collect(Collectors.toMap(Person::getFirstName, person -> person));

        List<JsonObject> filteredPersonList = new ArrayList<>();

        // Add stationNumber to list of Persons Objects
        JsonObject stationNumber = new JsonObject();
        stationNumber.addProperty("stationNum", filteredStream.get(0).getStation());
        filteredPersonList.add(stationNumber);

        // Loop through map and get wanted fields in PersonObject then save to filteredPersonList
        for (Map.Entry<String, Person> entry : peopleMap.entrySet()) {
            Person person = entry.getValue();
            MedicalRecord record = findRecordByName(person.getFirstName(), person.getLastName());
            String dob = record.getBirthdate();
            String personAge = String.valueOf(HelperMethods.getAge(dob));
            List<String> medications = record.getMedications();
            List<String> allergies = record.getAllergies();
            JsonArray medicationsArray = new JsonArray();
            JsonArray allergiesArray = new JsonArray();
            // Turn medications to JsonArray
            for (String medication : medications) {
                medicationsArray.add(medication);
            }
            for (String allergy : allergies) {
                allergiesArray.add(allergy);
            }
            ;
            JsonObject personJson = new JsonObject();
            personJson.addProperty("firstName", person.getFirstName());
            personJson.addProperty("lastName", person.getLastName());
            personJson.addProperty("phoneNum", person.getPhone());
            personJson.addProperty("age", personAge);
            personJson.add("medications", medicationsArray);
            personJson.add("allergies", allergiesArray);
            filteredPersonList.add(personJson);
        }



        // Turn list of persons to JsonArray to be a part of a JsonObject
        JsonArray personArray = new JsonArray();
        for (JsonObject person : filteredPersonList) {
            personArray.add(person);
        }

        responseJsonObject.add("people", personArray);
        return ResponseEntity.of(Optional.of(responseJsonObject));

    }


    /**
     * Method used by /fire url to get MedRecord by first/lastName
     *
     * @param firstName firstNameOfPerson
     * @param lastName lastNameOfPerson
     * @return MedicalRecord MedicalRecordOfPerson
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

