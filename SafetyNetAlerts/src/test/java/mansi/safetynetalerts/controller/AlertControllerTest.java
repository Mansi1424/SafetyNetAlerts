package mansi.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mansi.safetynetalerts.helper.HelperMethods;
import mansi.safetynetalerts.helper.ReadJsonFileForTests;
import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
@Import({ReadJson.class, HelperMethods.class})
public class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testGetChildAlertURLReturns404EmptyJsonWhenNoChildren() throws Exception {
        EmptyJsonBody empty = new EmptyJsonBody();
        String emptyBody = empty.toString();
        String address = "112 Steppes Pl";
        mockMvc.perform(get("/childAlert").param("address", address)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetChildAlertURLReturnsResponse() throws Exception {
        String expectedResult = ReadJsonFileForTests.readJsonFile("src/test/resources/GetChildAlertTestResult.json");
        String address = "947 E. Rose Dr";
        mockMvc.perform(get("/childAlert").param("address", address)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }


}
