package Mansi.SafetyNetAlerts.Controller;

import Mansi.SafetyNetAlerts.JsonToPojo.ReadJson;
import Mansi.SafetyNetAlerts.helper.FirestationMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FireStationController.class)
public class FirestationUrlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    ;
    private final String endpointPath = "/firestation";


    @Test
    public void testGetPeopleByFirestationURLReturns404NotFound() throws Exception {

        String stationNum = "10";
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation/{stationNumber}", stationNum)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());

    }

    @Test
    public void testGetPeopleByFirestationURLReturnsCorrectResponse() throws Exception {
        String expectedResult = ReadJson.readJsonFile("src/test/java/Mansi/SafetyNetAlerts/resources/GetPeopleByFirestationResult.json");
        String stationNum = "1";
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation/{stationNumber}", stationNum)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    }





