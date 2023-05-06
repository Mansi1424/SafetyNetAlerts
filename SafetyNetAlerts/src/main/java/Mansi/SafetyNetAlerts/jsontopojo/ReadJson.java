package mansi.safetynetalerts.jsontopojo;

import mansi.safetynetalerts.model.Firestation;
import mansi.safetynetalerts.model.MedicalRecord;
import mansi.safetynetalerts.model.Person;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ReadJson {
    private static final Gson gson = new Gson();
    private static JsonObject entireFile;

    static {
        try {
            entireFile = returnEntireJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(returnEntireJsonObject());
        returnFirestationsList();
    }

    public static JsonObject returnEntireJsonObject() throws IOException {
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

    public static List<Firestation> returnFirestationsList() throws IOException {
        JsonElement firestationsObject = entireFile.get("firestations");
        Type type = new TypeToken<List<Firestation>>() {
        }.getType();

        List<Firestation> firestations = gson.fromJson(firestationsObject, type);

        return firestations;
    }

    public static List<Person> returnPersonsList() throws IOException {
        JsonElement personsObject = entireFile.get("persons");
        Type type = new TypeToken<List<Person>>() {
        }.getType();

        List<Person> persons = gson.fromJson(personsObject, type);

        return persons;
    }

    public static List<MedicalRecord> returnMedicalRecordsList() throws IOException {
        JsonElement medicalRecordsObject = entireFile.get("medicalrecords");
        Type type = new TypeToken<List<MedicalRecord>>() {
        }.getType();

        List<MedicalRecord> medicalRecords = gson.fromJson(medicalRecordsObject, type);

        return medicalRecords;
    }

    public static String readJsonFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }



}

//        File file = new File("./SafetyNetAlerts/src/main/resources");
//        for (String fileNames : file.list()) System.out.println(fileNames);

//    import static com.jsoniter.JsonIterator.parse;
//    var rawData = Files.readAllBytes(new File(PATH_DATA).toPath());
//    var any = parse(rawData).readAny();

//    any.get(KEY_PERSONS).forEach(p -> people.add( //
//            Person.builder() //
//            .firstName(p.get(KEY_FIRST_NAME).toString()) //
//            .lastName(p.get(KEY_LAST_NAME).toString()) //
//            .address(p.get(KEY_ADDRESS).toString()) //
//            .city(p.get(KEY_CITY).toString()) //
//            .zip(p.get(KEY_ZIP).toString()) //
//            .phone(p.get(KEY_PHONE).toString()) //
//            .email(p.get(KEY_EMAIL).toString()) //
//            .build() //
//        ));


