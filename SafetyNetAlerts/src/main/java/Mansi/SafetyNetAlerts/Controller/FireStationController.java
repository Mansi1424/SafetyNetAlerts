package Mansi.SafetyNetAlerts.Controller;


import Mansi.SafetyNetAlerts.JsonToPojo.JsonToPojoFirestation;
import Mansi.SafetyNetAlerts.Model.Firestation;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class FireStationController {

    private final List<Firestation> firestationList;

    public FireStationController(List<Firestation> firestationList) throws IOException {

        this.firestationList = JsonToPojoFirestation.returnFirestationsList();
    }

    @GetMapping("/firestation")
    public List<Firestation> getFirestations() throws IOException {

        return firestationList;

    }


    @PostMapping("/firestation")
    @ResponseBody
    public List<Firestation> addFirestation(@RequestBody Firestation newFirestation) throws IOException {

        System.out.println("Before adding " + firestationList);
        System.out.println("New Firestation is " + newFirestation);

        firestationList.add(newFirestation);
        System.out.println("After adding new firestation " + firestationList);

        return firestationList;

    }

//    @PutMapping("/firestation/put{existingFirestation}")
//    @ResponseBody
//    public Firestation replaceFirestation(@PathVariable Firestation existingFirestation, Firestation replacementFirestation) {
//
//        return firestationList.set(firestationList.indexOf(existingFirestation), replacementFirestation);
//
//    }

    @DeleteMapping("/firestation/{address}")
    public Firestation deleteFirestation(@PathVariable String address) {

        Firestation firestationObject = firestationList.stream().filter(firestation -> address.equals(firestation.getAddress())).findFirst().orElse(null);
        firestationList.remove(firestationObject);

        return firestationObject;
    }



}
