package Mansi.SafetyNetAlerts.Controller;


import Mansi.SafetyNetAlerts.Model.Firestation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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


}
