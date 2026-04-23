package com.cognizant.greengov.controller.program_controller;
//import java.util.List;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramDTO;
//import com.cognizant.greengov.service.sustainability_service.EnergyProgramService;
//
//@RestController
//@RequestMapping("/api/programs")
//public class EnergyProgramController {
//
//    private final EnergyProgramService service;
//
//    public EnergyProgramController(EnergyProgramService service) {
//        this.service = service;
//    }
//
//    @GetMapping
//    public List<EnergyProgramDTO> getAllPrograms() {
//        return service.getAllPrograms();
//    }
//    @PostMapping("/create")
//    public EnergyProgramDTO createProgram(@RequestBody EnergyProgramDTO EnPr) {
//    	return service.createprogram(EnPr);
//    }
//}


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramDTO;
import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramResponseDTO;
import com.cognizant.greengov.exception.ProjectNotFound;
import com.cognizant.greengov.service.sustainability_service.EnergyProgramService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@RestController

@RequestMapping("/api/programs")

@Slf4j

@AllArgsConstructor

public class EnergyProgramController {
 
    private final EnergyProgramService service;
 
    /**

     * Endpoint to retrieve all energy programs.

     * @return List of EnergyProgramResponseDTO

     */

    @GetMapping

    public List<EnergyProgramResponseDTO> getAllPrograms() {

    	log.info("REST request to fetch all energy programs");

        return service.getAllPrograms();

    }

    /**

     * Endpoint to create a new energy program.

     * @param EnPr DTO containing program details.

     * @return The created EnergyProgramDTO.

     */

    @PostMapping("/create")

    public EnergyProgramDTO createProgram(@RequestBody EnergyProgramDTO EnPr) {

    	log.info("REST request to create a new Energy Program: {}", EnPr.getTitle());

    	return service.createprogram(EnPr);

    }

    /**

     * Endpoint to fetch a specific program by its unique ID.

     * @param id The ID of the program.

     * @return ResponseEntity containing the program details.

     * @throws ProjectNotFound if the ID does not exist.

     */

    @GetMapping("/fetch/{id}")

    public ResponseEntity<EnergyProgramResponseDTO> fetchById(@PathVariable Long id) throws ProjectNotFound {

    	log.debug("REST request to fetch program by ID: {}", id);

        return ResponseEntity.ok(service.getProgramById(id));

    }

    /**

     * Endpoint for full update of an existing energy program.

     * @param programId ID of the program to update.

     * @param request The updated data.

     * @return ResponseEntity containing the updated program.

     */

    @PutMapping("/{programId}")

    public ResponseEntity<EnergyProgramResponseDTO> update(@PathVariable Long programId, @RequestBody EnergyProgramDTO request) throws ProjectNotFound {

    	log.info("REST request to update program ID: {}", programId);

        return ResponseEntity.ok(service.updateProgram(programId, request));

    }
 
    /**

     * Endpoint for partial update of program status.

     * @param programId ID of the program.

     * @param status The new status string.

     * @return ResponseEntity containing the updated program.

     */

    @PatchMapping("/{programId}/{status}")

    public ResponseEntity<EnergyProgramResponseDTO> updateStatus(@PathVariable Long programId, @RequestParam String status) throws ProjectNotFound {

    	log.info("REST request to update status of program ID: {} to {}", programId, status);

        return ResponseEntity.ok(service.updateProgramStatus(programId, status));

    }
 
    /**

     * Endpoint to delete a program record.

     * @param id ID of the program to be deleted.

     * @return ResponseEntity with success message.

     */

    @DeleteMapping("/delete/{id}")

    public ResponseEntity<String> delete(@PathVariable Long id) throws ProjectNotFound {

    	log.warn("REST request to delete Energy Program ID: {}", id);

        return ResponseEntity.ok(service.deleteProgram(id));

    }

}

 