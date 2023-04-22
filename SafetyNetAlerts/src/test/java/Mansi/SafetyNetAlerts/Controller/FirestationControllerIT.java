package Mansi.SafetyNetAlerts.Controller;


import Mansi.SafetyNetAlerts.Model.Firestation;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FirestationControllerIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetFirestationsIsNotNull() throws Exception {
        ResponseEntity<List<Firestation>> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/firestation",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Firestation>>() {
                        }
                );
        List<Firestation> firestations = responseEntity.getBody();
        assertThat(firestations).isNotNull();

    }

    @Test
    public void testGetFirestationsHasSize() throws Exception {
        ResponseEntity<List<Firestation>> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/firestation",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Firestation>>() {
                        }
                );
        List<Firestation> firestations = responseEntity.getBody();
        assertThat(firestations).size().isEqualTo(13);

    }

    @Test
    public void testPostFirestation() throws Exception {
        HttpEntity<Firestation> request = new HttpEntity<>(new Firestation("6007 Magnolia Park", "8"));
        ResponseEntity<Firestation> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/firestation",
                        HttpMethod.POST,
                        request,
                        Firestation.class
                );
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        Firestation newFirestation = responseEntity.getBody();

        Assertions.assertNotNull(newFirestation);
        Assertions.assertEquals(newFirestation.getAddress(), "6007 Magnolia Park");
        Assertions.assertEquals(newFirestation.getStation(), "8");

    }


    @Test
    public void testPutFirestation() throws Exception {
        String address = "1509 Culver St";
        String stationNumber = "3";
        HttpEntity<String> station = new HttpEntity<String>("8");
        ResponseEntity<Firestation> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/firestation/" + address + "/" + stationNumber,
                        HttpMethod.PUT,
                        station,
                        Firestation.class
                );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Firestation updatedFirestation = responseEntity.getBody();

        Assertions.assertNotNull(updatedFirestation);
        Assertions.assertEquals(updatedFirestation.getStation(), "8");

    }

    @Test
    public void testDeleteFirestation() throws Exception {
        String address = "951 LoneTree Rd";
        String station = "2";
        ResponseEntity<Firestation> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/firestation/" + address + "/" + station,
                        HttpMethod.DELETE,
                        null,
                        Firestation.class
                );
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        Firestation deletedFirestation = responseEntity.getBody();

        Assertions.assertNotNull(deletedFirestation);

    }
}
