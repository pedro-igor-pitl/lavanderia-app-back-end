package com.luxlav.backend.controller;

import com.luxlav.backend.dto.PecasDTO;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.service.PecasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pecas")
public class PecasController {

    @Autowired
    private PecasService pecasService;

    @PostMapping("/cadastrar")
    public ResponseEntity<PecasModel> cadastrar(@RequestBody PecasDTO pecasDTO) {
        PecasModel pecasModel = pecasService.criarPeca(pecasDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pecasModel);
    }

    @GetMapping("/listar")
    public List<PecasDTO>listarPecas() { return pecasService.listarPecas(); }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PecasModel> atualizar(@PathVariable UUID id, @RequestBody PecasDTO dto) {

        return pecasService.atualizarPecas(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/atualizarStatus/{id}")
    public ResponseEntity<PecasModel> atualizarStatus(@PathVariable UUID id, @RequestBody PecasDTO dto) {

        return pecasService.atualizarStatus(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
