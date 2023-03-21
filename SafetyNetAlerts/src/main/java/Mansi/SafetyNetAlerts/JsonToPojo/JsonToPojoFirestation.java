package Mansi.SafetyNetAlerts.JsonToPojo;

import Mansi.SafetyNetAlerts.Model.Firestation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonToPojoFirestation {


    public static List<Firestation> returnFirestationsList() throws IOException {


        // Turn firstations.json to Array of json objects
        ObjectMapper objectMapper = new ObjectMapper();
        Resource resource = new ClassPathResource("firestations.json");
        File file = resource.getFile();

        Firestation[] firestations = objectMapper.readValue(file,
                Firestation[].class);

        // Turn Array of json objects to an Array
        List<Firestation> firestationArrayList = new ArrayList<Firestation>(Arrays.asList(firestations));

        return firestationArrayList;

    }
//        File file = new File("./SafetyNetAlerts/src/main/resources");
//        for (String fileNames : file.list()) System.out.println(fileNames);

}
