package mansi.safetynetalerts.controller;


import mansi.safetynetalerts.jsontopojo.EmptyJsonBody;
import mansi.safetynetalerts.jsontopojo.ReadJson;
import mansi.safetynetalerts.model.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class MedicalRecordController {

    private final List<MedicalRecord> medicalRecordsList;

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);


    public MedicalRecordController(ReadJson readJson) throws IOException {
        this.medicalRecordsList = ReadJson.returnMedicalRecordsList();
    }


    /**
     * Get all medical records
     *
     * @return medical records
     */
    @GetMapping("/medicalRecord")
    public List<MedicalRecord> getMedicalRecords() {

        logger.info("HTTP GET request received at /medicalRecord URL");

        logger.info("MedicalRecords List = " + medicalRecordsList);

        return medicalRecordsList;

    }


    /**
     * Post new MedicalRecord
     *
     * @param newMedicalRecord medical record to be added
     * @return added medical record
     */
    @PostMapping("/medicalRecord")
    @ResponseBody
    public MedicalRecord addMedicalRecord(@RequestBody MedicalRecord newMedicalRecord) {

        logger.info("HTTP POST request received at /medicalRecord URL");

        medicalRecordsList.add(newMedicalRecord);

        logger.info("New Medical Record = " + newMedicalRecord);
        return newMedicalRecord;

    }


    /**
     * Update MedicalRecord
     *
     * @param firstName
     * @param lastName
     * @param record
     * @return
     * @throws IOException
     */
    @PutMapping("/medicalRecord/{firstName}/{lastName}")
    @ResponseBody
    public ResponseEntity<Object> replaceMedicalRecord(@PathVariable String firstName, @PathVariable String lastName, @RequestBody MedicalRecord record) throws IOException {

        logger.info("HTTP PUT request received at /medicalRecord URL");

        List<MedicalRecord> filteredStream = medicalRecordsList.stream()
                .filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName))
                .filter(medicalRecord -> medicalRecord.getLastName().equals(lastName)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        MedicalRecord medicalRecordObject = filteredStream.get(0);

        medicalRecordObject.setBirthdate(record.getBirthdate());
        medicalRecordObject.setMedications(record.getMedications());
        medicalRecordObject.setAllergies(record.getAllergies());

        logger.info("Updated Medical Record = " + medicalRecordObject);

        return ResponseEntity.of(Optional.of(medicalRecordObject));

    }


    /**
     * Delete Medical Record
     *
     * @param firstName
     * @param lastName
     * @return
     * @throws IOException
     */
    @DeleteMapping("/medicalRecord/{firstName}/{lastName}")
    @ResponseBody
    public ResponseEntity<Object> deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) throws IOException {

        logger.info("HTTP DELETE request received at /medicalRecord URL");

        List<MedicalRecord> filteredStream = medicalRecordsList.stream()
                .filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName))
                .filter(medicalRecord -> medicalRecord.getLastName().equals(lastName)).toList();

        if (filteredStream.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmptyJsonBody());
        }

        MedicalRecord medicalRecordObject = filteredStream.get(0);

        medicalRecordsList.remove(medicalRecordObject);

        logger.info("Deleted Medical Record = " + medicalRecordObject);

        return ResponseEntity.of(Optional.of(medicalRecordObject));
    }


}
