package Mansi.SafetyNetAlerts.Controller;


import Mansi.SafetyNetAlerts.JsonToPojo.EmptyJsonBody;
import Mansi.SafetyNetAlerts.JsonToPojo.ReadJson;
import Mansi.SafetyNetAlerts.Model.Firestation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class FireStationController {

    private final List<Firestation> firestationList;

    public FireStationController(List<Firestation> firestationList) throws IOException {

        this.firestationList = ReadJson.returnFirestationsList();
    }


    @GetMapping("/firestation")
    public List<Firestation> getFirestations() throws IOException {

        return firestationList;

    }


    @PostMapping("/firestation")
    @ResponseBody
    public Firestation addFirestation(@RequestBody Firestation newFirestation) throws IOException {

        firestationList.add(newFirestation);

        return newFirestation;

    }

    @PutMapping("/firestation/{address}")
    @ResponseBody
    public ResponseEntity<Object> replaceFirestation(@PathVariable String address, @RequestBody String stationNumber) {

        Firestation firestationObject = firestationList.stream().filter(firestation -> address.equals(firestation.getAddress())).findFirst().orElse(null);

        if (firestationObject == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        firestationObject.setStation(stationNumber);

        return ResponseEntity.of(Optional.of(firestationObject));

    }

    @DeleteMapping("/firestation/{address}/{station}")
    public ResponseEntity<Object> deleteFirestation(@PathVariable("address") String address, @PathVariable("station") String station) {

        List<Firestation> filteredStream = firestationList.stream()
                .filter(firestation -> firestation.getAddress().equals(address))
                .filter(firestation -> firestation.getStation().equals(station)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        Firestation firestationObject = filteredStream.get(0);

        firestationList.remove(firestationObject);

        return ResponseEntity.of(Optional.of(firestationObject));
    }


}

