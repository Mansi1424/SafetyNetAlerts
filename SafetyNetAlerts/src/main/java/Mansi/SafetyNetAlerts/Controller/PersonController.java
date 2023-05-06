package mansi.safetynetalerts.controller;

import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import mansi.safetynetalerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {

    private final List<Person> personList;

    private static Logger logger = LoggerFactory.getLogger(PersonController.class);


    public PersonController(List<Person> personsList) throws IOException {

        this.personList = ReadJson.returnPersonsList();
    }




    /**
     * Get Persons List
     * @return
     * @throws IOException
     */
    @GetMapping("/person")
    public List<Person> getPersons() throws IOException {

        logger.info("HTTP GET request received at /person URL");

        logger.info("Person List = " +  personList);

        return personList;

    }


    /**
     * Delete a Person
     * @param newPerson
     * @return
     * @throws IOException
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
     * @param firstName
     * @param lastName
     * @param personEntered
     * @return
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
     * @param firstName
     * @param lastName
     * @return
     */
    @DeleteMapping("/person/{firstName}/{lastName}")
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
}
