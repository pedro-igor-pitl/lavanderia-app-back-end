package com.luxlav.backend.controller;


import com.luxlav.backend.dto.ClienteDTO;
import com.luxlav.backend.dto.ClienteResumoDTO;
import com.luxlav.backend.dto.PecasDTO;
import com.luxlav.backend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/cadastrar")
    public ResponseEntity<ClienteDTO> cadastrar(@RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteCriado = clienteService.criarCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
    }

    @GetMapping("/listarPecasAtivas")
    public List<PecasDTO> listar(@RequestParam(required = false) Boolean ativo) {
        return clienteService.listarPecas(ativo);
    }

    @GetMapping("/listarClientesResumido")
    public List<ClienteResumoDTO> listarClientesResumido() {return clienteService.listarClientesResumidos();}
}
