package mansi.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mansi.safetynetalerts.helper.HelperMethods;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(mansi.safetynetalerts.controller.MedicalRecordController.class)
@Import({ReadJson.class, HelperMethods.class})
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetMedicalRecords() throws Exception {
        List<String> stations = new ArrayList<>();
        mockMvc.perform(get("/medicalRecord")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void testDeletingMedicalRecord() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/medicalRecord/John/Boyd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
