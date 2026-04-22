package com.luxlav.backend.controller;


import com.luxlav.backend.dto.ClienteDTO;
import com.luxlav.backend.dto.ClienteRequestDTO;
import com.luxlav.backend.dto.ClienteResumoDTO;
import com.luxlav.backend.dto.PecasDTO;
import com.luxlav.backend.model.ClientesModel;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PutMapping("/atualizarCliente/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(
            @PathVariable UUID id,
            @RequestBody ClienteRequestDTO request
    ) {

        return clienteService.atualizarCliente(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/buscarClienteCompleto/{id}")
    public ResponseEntity<ClienteDTO> buscarClientePorId(@PathVariable UUID id) {
        return clienteService.buscarClientePorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @PatchMapping("/atualizarStatus/{id}")
    public ResponseEntity<ClientesModel> atualizarStatus(@PathVariable UUID id, @RequestBody ClienteDTO dto) {
        return clienteService.atualizarStatus(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
