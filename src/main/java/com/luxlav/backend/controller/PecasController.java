package com.luxlav.backend.controller;

import com.luxlav.backend.dto.PecasDTO;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.service.PecasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
