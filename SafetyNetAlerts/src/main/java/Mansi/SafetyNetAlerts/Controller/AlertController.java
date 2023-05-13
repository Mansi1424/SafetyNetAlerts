package mansi.safetynetalerts.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mansi.safetynetalerts.helper.FirestationMethods;
import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import mansi.safetynetalerts.model.Firestation;
import mansi.safetynetalerts.model.MedicalRecord;
import mansi.safetynetalerts.model.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AlertController {

    private final List<Person> personList = ReadJson.returnPersonsList();
    private final List<MedicalRecord> medicalRecordList = ReadJson.returnMedicalRecordsList();
    private List<Firestation> firestationList = ReadJson.returnFirestationsList();

    public AlertController() throws IOException {
    }

    @GetMapping("/childAlert/{address}")
    @ResponseBody
    public ResponseEntity<Object> getChildAlert(@PathVariable("address") String address) throws ParseException {

        JsonObject responseJsonObject = new JsonObject();

        List<Person> filteredStream = personList.stream()
                .filter(person -> person.getAddress().equals(address)).toList();


        List<MedicalRecord> children = new ArrayList<>();
        List<Person> nonChildrenList = new ArrayList<>();
        int age = 0;
        for (Person person : filteredStream) {
            for (MedicalRecord matchedRecord : medicalRecordList) {
                if (Objects.equals(matchedRecord.getFirstName(), person.getFirstName())) {
                    String personDob = matchedRecord.getBirthdate();
                    age = FirestationMethods.getAge(personDob);

                    if (age < 18) {
                        children.add(matchedRecord);
                    } else {
                        nonChildrenList.add(person);
                    }
                }
            }
        }

        // Get Children
        Map<String, MedicalRecord> childrenMap = children.stream()
                .collect(Collectors.toMap(MedicalRecord::getFirstName, person -> person));

        List<JsonObject> filteredChildrenList = new ArrayList<>();

        for (Map.Entry<String, MedicalRecord> entry : childrenMap.entrySet()) {
            MedicalRecord record = entry.getValue();
            JsonObject medRecordJson = new JsonObject();
            medRecordJson.addProperty("firstName", record.getFirstName());
            medRecordJson.addProperty("lastName", record.getLastName());
            medRecordJson.addProperty("age", FirestationMethods.getAge(record.getBirthdate()));
            filteredChildrenList.add(medRecordJson);
        }

        JsonArray childArray = new JsonArray();
        for (JsonObject child : filteredChildrenList) {
            childArray.add(child);
        }


        // Get Non Children
        Map<String, Person> adultsMap = nonChildrenList.stream()
                .collect(Collectors.toMap(Person::getFirstName, person -> person));

        List<JsonObject> filteredAdultList = new ArrayList<>();

        for (Map.Entry<String, Person> entry : adultsMap.entrySet()) {
            Person person = entry.getValue();
            JsonObject adult = new JsonObject();
            adult.addProperty("firstName", person.getFirstName());
            adult.addProperty("lastName", person.getLastName());
            adult.addProperty("address", person.getAddress());
            filteredAdultList.add(adult);

        }

        JsonArray adultsArray = new JsonArray();
        for (JsonObject adult : filteredAdultList) {
            adultsArray.add(adult);
        }


        // Add children array and non children array to responseObject else return empty json body
        if (!childArray.isEmpty()) {
            responseJsonObject.add("children", childArray);
            responseJsonObject.add("nonChildren", adultsArray);
            return ResponseEntity.of(Optional.of(responseJsonObject));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }


    }

    @GetMapping("/phoneAlert/{stationNumber}")
    public ResponseEntity<Object> getPhoneAlert(@PathVariable("stationNumber") String stationNumber) {
        JsonObject responseJsonObject = new JsonObject();

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getStation().equals(stationNumber)).toList();


        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        List<String> phoneNumbers = new ArrayList<>();
        for (Firestation firestation : filteredStream) {
            String stationAddress = firestation.getAddress();
            for (Person person : personList) {
                if (Objects.equals(person.getAddress(), stationAddress)) {
                    Person matchedPerson = new Person(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone());
                    if (!phoneNumbers.contains(matchedPerson.getPhone())) {
                        phoneNumbers.add(matchedPerson.getPhone());
                    }


                }
            }
        }

        Gson gson = new Gson();
        gson.toJson(phoneNumbers);

        return ResponseEntity.of(Optional.of(phoneNumbers));


    }

}