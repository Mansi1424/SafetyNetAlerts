package Mansi.SafetyNetAlerts.Controller;


import Mansi.SafetyNetAlerts.JsonToPojo.EmptyJsonBody;
import Mansi.SafetyNetAlerts.JsonToPojo.ReadJson;
import Mansi.SafetyNetAlerts.Model.Firestation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Handle Firestation Endpoints
 */
@RestController
public class FireStationController {


    private final List<Firestation> firestationList;

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


}

