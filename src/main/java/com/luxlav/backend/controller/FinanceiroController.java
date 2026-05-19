package com.luxlav.backend.controller;


import com.luxlav.backend.dto.FinanceiroDTO;
import com.luxlav.backend.service.FinanceiroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final FinanceiroService service;

    public FinanceiroController(FinanceiroService service) {
        this.service = service;
    }

    @GetMapping("/relatorioRollsPorPeriodo")
    public List<FinanceiroDTO> buscar(
            @RequestParam UUID clienteId,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {
        return service.buscarFinanceiro(clienteId, inicio, fim);
    }
}
