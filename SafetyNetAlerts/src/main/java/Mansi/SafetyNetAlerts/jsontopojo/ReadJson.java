package mansi.safetynetalerts.jsontopojo;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import mansi.safetynetalerts.model.Firestation;
import mansi.safetynetalerts.model.MedicalRecord;
import mansi.safetynetalerts.model.Person;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


@Service
public class ReadJson {
    private final Gson gson = new Gson();
    private final JsonObject entireFile;

    public ReadJson() {
        try {
            entireFile = returnEntireJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void main(String[] args) throws IOException {
//        System.out.println(returnEntireJsonObject());
        returnFirestationsList();
    }

    public JsonObject returnEntireJsonObject() throws IOException {
        String url = "https://s3-eu-west-1.amazonaws.com/course.oc-static.com/projects/DA+Java+EN/P5+/data.json";
        List<String> dataArray;


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        String responseAsString = response.toString();


        JsonObject body = gson.fromJson(responseAsString, JsonObject.class);

        return body;
    }

    public List<Firestation> returnFirestationsList() throws IOException {
        JsonElement firestationsObject = entireFile.get("firestations");
        Type type = new TypeToken<List<Firestation>>() {
        }.getType();

        List<Firestation> firestations = gson.fromJson(firestationsObject, type);


        return firestations;
    }

    public List<Person> returnPersonsList() throws IOException {
        JsonElement personsObject = entireFile.get("persons");
        Type type = new TypeToken<List<Person>>() {
        }.getType();

        List<Person> persons = gson.fromJson(personsObject, type);

        return persons;
    }

    public List<MedicalRecord> returnMedicalRecordsList() throws IOException {
        JsonElement medicalRecordsObject = entireFile.get("medicalrecords");
        Type type = new TypeToken<List<MedicalRecord>>() {
        }.getType();

        List<MedicalRecord> medicalRecords = gson.fromJson(medicalRecordsObject, type);

        return medicalRecords;
    }

}
