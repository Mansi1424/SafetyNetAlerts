package mansi.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mansi.safetynetalerts.helper.HelperMethods;
import mansi.safetynetalerts.helper.ReadJsonFileForTests;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
@Import({ReadJson.class, HelperMethods.class})
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetPersonInfoURLTestReturnsNull() throws Exception {
        mockMvc.perform(get("/personInfo").param("firstName", "John")
                        .param("lastName", "Cena")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());

    }

    @Test
    public void testGetPersonInfoURLTest() throws Exception {
        String expectedResult = ReadJsonFileForTests.readJsonFile("src/test/resources/GetPersonInfoURLResult.json");
        mockMvc.perform(get("/personInfo")
                        .param("firstName", "John")
                        .param("lastName", "Boyd")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));

    }

    @Test
    public void testGetCommunityEmailURLReturnsResponse() throws Exception {
        String expectedResult = ReadJsonFileForTests.readJsonFile("src/test/resources/GetCommunityEmailURLResult.json");
        String city = "Culver";
        mockMvc.perform(get("/communityEmail").param("city", city)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void testGetCommunityEmailURLReturnsNotFound() throws Exception {
        String city = "NOTACITY";
        mockMvc.perform(get("/communityEmail").param("city", city)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
