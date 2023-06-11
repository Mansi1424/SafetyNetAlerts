package mansi.safetynetalerts.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mansi.safetynetalerts.helper.HelperMethods;
import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import mansi.safetynetalerts.model.MedicalRecord;
import mansi.safetynetalerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles Person Endpoints
 */
@RestController
public class PersonController {

    private final List<Person> personList;
    private final HelperMethods helperMethods;

    private static Logger logger = LoggerFactory.getLogger(PersonController.class);

    public PersonController(ReadJson readJson, HelperMethods helperMethods) throws IOException {

        this.personList = readJson.returnPersonsList();
        this.helperMethods = helperMethods;
    }


    /**
     * Get Persons List
     *
     * @return PersonsList
     */
    @GetMapping("/person")
    public List<Person> getPersons() {

        logger.info("HTTP GET request received at /person URL");

        logger.info("Person List = " + personList);

        return personList;

    }


    /**
     * Add a new a Person
     *
     * @param newPerson personBeingAddes
     * @return newPerson
     */
    @PostMapping("/person")
    @ResponseBody
    public Person addPerson(@RequestBody Person newPerson) throws IOException {

        logger.info("HTTP POST request received at /person URL");

        personList.add(newPerson);

        logger.info("New Person added = " + newPerson);

        return newPerson;

    }


    /**
     * Update a person's info
     *
     * @param firstName     personFirstName
     * @param lastName      personLastName
     * @param personEntered fieldsBeingUpdated
     * @return updatedPerson
     */
    @PutMapping("/person/{firstName}/{lastName}")
    @ResponseBody
    public ResponseEntity<Object> replaceFirestation(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName, @RequestBody Person personEntered) {

        logger.info("HTTP PUT request received at /person URL");

        List<Person> filteredStream = personList.stream()
                .filter(person -> person.getFirstName().equals(firstName))
                .filter(person -> person.getLastName().equals(lastName)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        Person updatedPerson = filteredStream.get(0);

        updatedPerson.setAddress(personEntered.getAddress());
        updatedPerson.setCity(personEntered.getCity());
        updatedPerson.setZip(personEntered.getZip());
        updatedPerson.setPhone(personEntered.getPhone());
        updatedPerson.setEmail(personEntered.getEmail());

        logger.info("Person Updated = " + updatedPerson);

        return ResponseEntity.of(Optional.of(updatedPerson));

    }


    /**
     * Delete a Person
     *
     * @param firstName personFirstName
     * @param lastName  personLastName
     * @return deletedPerson
     */
    @DeleteMapping("/person/{firstName}/{lastName}")
    @ResponseBody
    public ResponseEntity<Object> deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {

        logger.info("HTTP DELETE request received at /person URL");

        List<Person> filteredStream = personList.stream()
                .filter(person -> person.getFirstName().equals(firstName))
                .filter(person -> person.getLastName().equals(lastName)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        Person person = filteredStream.get(0);

        logger.info("Deleted Person = " + person);

        personList.remove(person);

        return ResponseEntity.of(Optional.of(person));
    }


    /**
     * 6th Url /personInfo
     *
     * @param firstName personFirstName
     * @param lastName  personLastName
     * @return Persons Info By Their Name
     */
    @GetMapping("/personInfo")
    @ResponseBody
    public ResponseEntity<Object> getPersonInfo(@RequestParam String firstName, @RequestParam String lastName) throws ParseException {
        logger.info("HTTP GET request received at /personInfo URL");

        JsonObject responseJsonObject = new JsonObject();

        List<Person> filteredPersonStream = new ArrayList<>();
        for (Person person : personList) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                filteredPersonStream.add(person);
            }
        }

        // Return Not Found if Name is not there in personList
        if (filteredPersonStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }


        List<JsonObject> filteredPersonList = new ArrayList<>();
        for (Person person : filteredPersonStream) {
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
            personJson.addProperty("address", person.getAddress());
            personJson.addProperty("age", personAge);
            personJson.addProperty("email", person.getEmail());
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


    @GetMapping("/communityEmail")
    public ResponseEntity<Object> getEmailsByCity(@RequestParam String city) throws IOException {

        logger.info("HTTP GET request received at /phoneAlert URL");


        List<Person> filteredStream = new ArrayList<>();
        for (Person person : personList) {
            if (person.getCity().equals(city)) {
                filteredStream.add(person);
            }
        }

        logger.info(String.valueOf(filteredStream));


        // Return 404 if city is not found
        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }


        List<String> emailAddresses = new ArrayList<>();
        for (Person person : filteredStream) {
            emailAddresses.add(person.getEmail());
        }

        logger.info("Email Addresses List" + emailAddresses);

        Gson gson = new Gson();
        gson.toJson(emailAddresses);

        return ResponseEntity.of(Optional.of(emailAddresses));

    }
}
