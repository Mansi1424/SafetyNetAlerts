package mansi.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mansi.safetynetalerts.controller.FireStationController;
import mansi.safetynetalerts.helper.HelperMethods;
import mansi.safetynetalerts.helper.ReadJsonFileForTests;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(FireStationController.class)
@Import({ReadJson.class, HelperMethods.class})
public class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testGetAllFirestations() throws Exception {
        mockMvc.perform(get("/allFirestation")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void testDeletingAFirestation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/firestation/951 LoneTree Rd/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void testGetPeopleByFirestationURLReturns404NotFound() throws Exception {

        String stationNum = "10";
        mockMvc.perform(get("/firestation").param("stationNumber", stationNum)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());

    }

    @Test
    public void testGetPeopleByFirestationURLReturnsResponse() throws Exception {
        String expectedResult = ReadJsonFileForTests.readJsonFile("src/test/resources/GetPeopleByFirestationResult.json");
        String stationNum = "1";
        mockMvc.perform(get("/firestation").param( "stationNumber",stationNum)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void testFireUrlWhenAddressNotFoundReturns404() throws Exception {
        String address = "WrongAddress";
        mockMvc.perform(get("/fire").param("address", address)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testFireUrlReturnsResponse() throws Exception {
        String address = "1509 Culver St";
        String expectedResult = ReadJsonFileForTests.readJsonFile("src/test/resources/GetFireUrlResult.json");
        mockMvc.perform(get("/fire").param("address", address)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));

    }

}





