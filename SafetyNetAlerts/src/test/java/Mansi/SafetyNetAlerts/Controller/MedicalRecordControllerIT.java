package Mansi.SafetyNetAlerts.Controller;

import Mansi.SafetyNetAlerts.Model.Firestation;
import Mansi.SafetyNetAlerts.Model.MedicalRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MedicalRecordControllerIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetMedicalRecordsIsNotNull() throws Exception {
        ResponseEntity<List<MedicalRecord>> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/medicalRecord",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<MedicalRecord>>() {
                        }
                );
        List<MedicalRecord> medicalRecords = responseEntity.getBody();
        assertThat(medicalRecords).isNotNull();

    }

    @Test
    public void testGetMedicalRecordsHasSize() throws Exception {
        ResponseEntity<List<MedicalRecord>> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/medicalRecord",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<MedicalRecord>>() {
                        }
                );
        List<MedicalRecord> medicalRecords = responseEntity.getBody();
        assertThat(medicalRecords).size().isEqualTo(23);

    }

    @Test
    public void testPostMedicalRecords() throws Exception {
        List<String> medications = new ArrayList<String>() {{
            add("Tylenol");
            add("Metformin");
        } };
        List<String> allergies = new ArrayList<String>() {{
            add("Pollen");
            add("Bees");
        } };
        HttpEntity<MedicalRecord> request = new HttpEntity<>(new MedicalRecord("John", "Cook", "02/20/1990", medications, allergies));
        ResponseEntity<MedicalRecord> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/medicalRecord",
                        HttpMethod.POST,
                        request,
                        MedicalRecord.class
                );
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        MedicalRecord newMedicalRecord = responseEntity.getBody();

        Assertions.assertNotNull(newMedicalRecord);
        Assertions.assertEquals(newMedicalRecord.getFirstName(), "John");
        Assertions.assertEquals(newMedicalRecord.getBirthdate(), "02/20/1990");
        Assertions.assertEquals(newMedicalRecord.getMedications(), medications);

    }

    @Test
    public void testPutMedicalRecord() throws Exception {
        String firstName = "John";
        String lastName = "Boyd";
        List<String> medications = new ArrayList<String>() {{
            add("Tylenol:100mg");
            add("Metformin:500mg");
        } };
        List<String> allergies = new ArrayList<String>() {{
            add("Pollen");
            add("Bees");
        } };
        HttpEntity<MedicalRecord> request = new HttpEntity<>(new MedicalRecord("John", "Cook", "02/20/1990", medications, allergies));
        ResponseEntity<MedicalRecord> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/medicalRecord/" + firstName + "/" + lastName,
                        HttpMethod.PUT,
                        request,
                        MedicalRecord.class
                );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        MedicalRecord updatedMedicalRecord = responseEntity.getBody();

        Assertions.assertNotNull(updatedMedicalRecord);
        Assertions.assertEquals(updatedMedicalRecord.getFirstName(), "John");
        Assertions.assertEquals(updatedMedicalRecord.getLastName(), "Boyd");
        Assertions.assertEquals(updatedMedicalRecord.getBirthdate(), "02/20/1990");

    }

    @Test
    public void testDeleteMedicalRecord() throws Exception {
        String firstName = "John";
        String lastName = "Boyd";
        ResponseEntity<MedicalRecord> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/medicalRecord/" + firstName + "/" + lastName,
                        HttpMethod.DELETE,
                        null,
                        MedicalRecord.class
                );
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        MedicalRecord deletedRecord = responseEntity.getBody();

        Assertions.assertNotNull(deletedRecord);

    }
}
