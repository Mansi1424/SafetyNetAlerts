package mansi.safetynetalerts.controller;

import mansi.safetynetalerts.model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetPersonsIsNotNull() throws Exception {
        ResponseEntity<List<Person>> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/person",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Person>>() {
                        }
                );
        List<Person> persons = responseEntity.getBody();
        assertThat(persons).isNotNull();
    }

    @Test
    public void testGetPersonsHasSize() throws Exception {
        ResponseEntity<List<Person>> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/person",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Person>>() {
                        }
                );
        List<Person> persons = responseEntity.getBody();
        assertThat(persons).size().isEqualTo(23);

    }

    @Test
    public void testPostPerson() throws Exception {
        HttpEntity<Person> request = new HttpEntity<>(new Person("Mansi", "Patel",
                "6007 Magnolia Lane", "Tampa", "33510", "813-461-2500", "mansi2001.edu@gmail.com"));

        ResponseEntity<Person> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/person",
                        HttpMethod.POST,
                        request,
                        Person.class
                );
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        Person newPerson = responseEntity.getBody();

        Assertions.assertNotNull(newPerson);
        Assertions.assertEquals(newPerson.getAddress(), "6007 Magnolia Lane");
        Assertions.assertEquals(newPerson.getFirstName(), "Mansi");

    }

    @Test
    public void testPutPerson() throws Exception {
        String firstName = "John";
        String lastName = "Boyd";
        HttpEntity<Person> request = new HttpEntity<Person>(new Person("Mansi", "Patel",
                "6007 Magnolia Lane", "Tampa", "33510", "813-461-2500", "mansi2001.edu@gmail.com"));
        ResponseEntity<Person> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/person/" + firstName + "/" + lastName,
                        HttpMethod.PUT,
                        request,
                        Person.class
                );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Person updatedPerson = responseEntity.getBody();

        Assertions.assertNotNull(updatedPerson);
        Assertions.assertEquals(updatedPerson.getFirstName(), "John");
        Assertions.assertEquals(updatedPerson.getAddress(), "6007 Magnolia Lane");
        Assertions.assertEquals(updatedPerson.getCity(), "Tampa");
    }

    @Test
    public void testDeletePerson() throws Exception {
        String firstName = "John";
        String lastName = "Boyd";
        ResponseEntity<Person> responseEntity =
                this.restTemplate.exchange(
                        "http://localhost:" + port + "/person/" + firstName + "/" + lastName,
                        HttpMethod.DELETE,
                        null,
                        Person.class
                );
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        Person deletedPerson = responseEntity.getBody();

        Assertions.assertNotNull(deletedPerson);

    }



}
