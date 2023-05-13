package mansi.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
public class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetChildAlertURLReturns404EmptyJsonWhenNoChildren() throws Exception {
        EmptyJsonBody empty = new EmptyJsonBody();
        String emptyBody = empty.toString();
        String stationNum = "112 Steppes Pl";
        mockMvc.perform(MockMvcRequestBuilders.get("/childAlert/{address}", stationNum)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetChildAlertURLReturnsResponse() throws Exception {
        String expectedResult = ReadJson.readJsonFile("src/test/resources/GetChildAlertTestResult.json");
        String address = "947 E. Rose Dr";
        mockMvc.perform(MockMvcRequestBuilders.get("/childAlert/{address}", address)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }


}
