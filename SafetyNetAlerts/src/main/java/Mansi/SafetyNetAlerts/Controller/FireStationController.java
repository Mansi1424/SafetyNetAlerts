package Mansi.SafetyNetAlerts.Controller;


import Mansi.SafetyNetAlerts.helper.FirestationMethods;
import Mansi.SafetyNetAlerts.helper.FirestationMethods.*;
import Mansi.SafetyNetAlerts.JsonToPojo.EmptyJsonBody;
import Mansi.SafetyNetAlerts.JsonToPojo.ReadJson;
import Mansi.SafetyNetAlerts.Model.Firestation;
import Mansi.SafetyNetAlerts.Model.MedicalRecord;
import Mansi.SafetyNetAlerts.Model.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
 * Handle Firestation Endpoints
 */
@RestController
public class FireStationController {


    private List<Firestation> firestationList = ReadJson.returnFirestationsList();
    private final List<Person> personList = ReadJson.returnPersonsList();
    private final List<MedicalRecord> medicalRecordList = ReadJson.returnMedicalRecordsList();


    private static Logger logger = LoggerFactory.getLogger(FireStationController.class);

    public FireStationController(List<Firestation> firestationList) throws IOException {

        this.firestationList = ReadJson.returnFirestationsList();
    }


    /**
     * Get all firestations
     * @return
     * @throws IOException
     */
    @GetMapping("/firestation")
    public List<Firestation> getFirestations() throws IOException {

        logger.info("HTTP GET request received at /firestation URL");

        logger.info("Firestations List = " +  firestationList);

        return firestationList;

    }

    /**
     * Post new Firestation
     * @param newFirestation
     * @return
     * @throws IOException
     */
    @PostMapping("/firestation")
    @ResponseBody
    public Firestation addFirestation(@RequestBody Firestation newFirestation) throws IOException {

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
     * @param address
     * @param station
     * @return
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

        logger.info("deleted Firestation = " +  response);

        return response;
    }

    @GetMapping("/firestation/{stationNumber}")
    @ResponseBody
    public ResponseEntity<Object> getPerson(@PathVariable("stationNumber") String stationNumber) throws ParseException {

        JsonObject responseJsonObject = new JsonObject();

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getStation().equals(stationNumber)).toList();

        // If stationNumber not found return empty jsonObject
        if (filteredStream.isEmpty()) {
            List<JsonObject> emptyList = new ArrayList<>();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        //Find list of people associated to the stationNumber
        List<Person> matchingPersons = new ArrayList<>();
        for (Firestation firestation : filteredStream) {
            String stationAddress = firestation.getAddress();
            for (Person person : personList) {
                if (Objects.equals(person.getAddress(), stationAddress) && !FirestationMethods.containsPersonWithName(matchingPersons, person.getFirstName())) {
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
                    age = FirestationMethods.getAge(personDob);

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


}

