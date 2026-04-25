package com.luxlav.backend.controller;

import com.luxlav.backend.dto.RollsDTO;
import com.luxlav.backend.service.RollsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/coleta")
public class RollsController {
    @Autowired
    private RollsService rollsService;

    @PostMapping("/cadastrarRoll")
    public ResponseEntity<RollsDTO> cadastrar(@RequestBody RollsDTO rollsDTO) {
        RollsDTO rollCriado = rollsService.criarRoll(rollsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(rollCriado);
    }
}
