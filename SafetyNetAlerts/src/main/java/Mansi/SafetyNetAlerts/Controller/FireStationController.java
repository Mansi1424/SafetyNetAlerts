package Mansi.SafetyNetAlerts.Controller;


import Mansi.SafetyNetAlerts.JsonToPojo.JsonToPojoFirestation;
import Mansi.SafetyNetAlerts.Model.Firestation;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FireStationController {

    @GetMapping("/firestation/get")
    public List<Firestation> getFirestations() throws IOException {

        return JsonToPojoFirestation.returnFirestationsList();

    }


    @PostMapping("/firestation/post")
    @ResponseBody
    public List<Firestation> addFirestation(@RequestBody Firestation newFirestation) throws IOException {

        List<Firestation> firestationsArrayList = JsonToPojoFirestation.returnFirestationsList();
        System.out.println("Before adding " + firestationsArrayList);
        System.out.println("New Firestation is " + newFirestation);

        firestationsArrayList.add(newFirestation);
        System.out.println("After adding new firestation " + firestationsArrayList);

        return firestationsArrayList;

    }



}
