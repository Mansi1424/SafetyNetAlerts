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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(mansi.safetynetalerts.controller.FloodController.class)
@Import({ReadJson.class, HelperMethods.class})
public class FloodControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetFloodURLReturns404EmptyJsonWhenNoChildren() throws Exception {
        List<String> stations = new ArrayList<>();
        mockMvc.perform(get("/flood").param("stations", String.valueOf(stations))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testFloodUrlReturnsResponse() throws Exception {

        List<String> stations = Arrays.asList("1", "2");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("stations", stations);
        String expectedResult = ReadJsonFileForTests.readJsonFile("src/test/resources/GetFloodUrlResult.json");
        mockMvc.perform(get("/flood").params(params)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));

    }
}
