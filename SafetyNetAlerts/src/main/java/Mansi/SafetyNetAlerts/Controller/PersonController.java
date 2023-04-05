package Mansi.SafetyNetAlerts.Controller;

import Mansi.SafetyNetAlerts.JsonToPojo.EmptyJsonBody;
import Mansi.SafetyNetAlerts.JsonToPojo.ReadJson;
import Mansi.SafetyNetAlerts.Model.Firestation;
import Mansi.SafetyNetAlerts.Model.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class PersonController {

    private final List<Person> personList;

    public PersonController(List<Person> personsList) throws IOException {

        this.personList = ReadJson.returnPersonsList();
    }

    @GetMapping("/person")
    public List<Person> getPersons() throws IOException {

        return personList;

    }

    @PostMapping("/person")
    @ResponseBody
    public Person addPerson(@RequestBody Person newPerson) throws IOException {

        personList.add(newPerson);

        return newPerson;

    }

    @PutMapping("/person/{firstName}/{lastName}/{phone}")
    @ResponseBody
    public ResponseEntity<Object> replaceFirestation(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,  @PathVariable("phone") String phone, @RequestBody Person personEntered) {


        List<Person> filteredStream = personList.stream()
                .filter(person -> person.getFirstName().equals(firstName))
                .filter(person -> person.getLastName().equals(lastName))
                .filter(person -> person.getPhone().equals(phone)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        Person updatedPerson = filteredStream.get(0);

        updatedPerson.setAddress(personEntered.getAddress());
        updatedPerson.setCity(personEntered.getCity());
        updatedPerson.setZip(personEntered.getZip());
        updatedPerson.setPhone(personEntered.getPhone());
        updatedPerson.setEmail(personEntered.getEmail());

        return ResponseEntity.of(Optional.of(updatedPerson));

    }

    @DeleteMapping("/person/{firstName}/{lastName}/{phone}")
    public ResponseEntity<Object> deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,  @PathVariable("phone") String phone) {

        List<Person> filteredStream = personList.stream()
                .filter(person -> person.getFirstName().equals(firstName))
                .filter(person -> person.getLastName().equals(lastName))
                .filter(person -> person.getPhone().equals(phone)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        Person person = filteredStream.get(0);

        personList.remove(person);

        return ResponseEntity.of(Optional.of(person));
    }
}
