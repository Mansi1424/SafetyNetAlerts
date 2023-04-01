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

    @PutMapping("/person/{phone}")
    @ResponseBody
    public ResponseEntity<Object> replaceFirestation(@PathVariable String phone, @RequestBody Person address, ) {

        Firestation firestationObject = firestationList.stream().filter(firestation -> address.equals(firestation.getAddress())).findFirst().orElse(null);

        if (firestationObject == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        firestationObject.setStation(stationNumber);

        return ResponseEntity.of(Optional.of(firestationObject));

    }
}
