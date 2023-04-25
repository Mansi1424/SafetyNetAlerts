package Mansi.SafetyNetAlerts.Controller;

import Mansi.SafetyNetAlerts.JsonToPojo.EmptyJsonBody;
import Mansi.SafetyNetAlerts.JsonToPojo.ReadJson;
import Mansi.SafetyNetAlerts.Model.Firestation;
import Mansi.SafetyNetAlerts.Model.Person;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class InfoController {

    private final List<Firestation> firestationList = ReadJson.returnFirestationsList();
    private final List<Person> personList = ReadJson.returnPersonsList();

    public InfoController() throws IOException {
    }


    @GetMapping("/firestation/{stationNumber}")
    @ResponseBody
    public ResponseEntity<List<Person>> getPerson(@PathVariable("stationNumber") String stationNumber) {

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getStation().equals(stationNumber)).toList();

        if (filteredStream.isEmpty()) {
            List<Object> emptyList = new ArrayList<>();
            new ResponseEntity<>(emptyList, HttpStatus.NOT_FOUND);
            System.out.println("Empty");
        }

        Firestation matchingStation = filteredStream.get(0);
        String stationAddress = matchingStation.getAddress();

        System.out.println(matchingStation);
        List<Person> matchingPersons = new ArrayList<>();
        for (Person person : personList) {
            if (Objects.equals(person.getAddress(), stationAddress)) {
                matchingPersons.add(person);
            }
        }

        return ResponseEntity.of(Optional.of(matchingPersons));

        }




    }

