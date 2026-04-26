package com.luxlav.backend.controller;

import com.luxlav.backend.dto.ColetaDTO;
import com.luxlav.backend.dto.RollsDTO;
import com.luxlav.backend.service.RollsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/vizualizarColeta")
    public ResponseEntity<ColetaDTO> visualizarColeta(
            @RequestParam UUID clienteId,
            @RequestParam  String codigoManual
    ) {
        ColetaDTO coleta = rollsService.buscarRoll(clienteId, codigoManual);
        return ResponseEntity.ok(coleta);
    }
}
