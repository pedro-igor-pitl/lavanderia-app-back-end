package com.luxlav.backend.service;

import com.luxlav.backend.dto.FinanceiroDTO;
import com.luxlav.backend.repository.RollsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FinanceiroService {

    private final RollsRepository rollsRepository;

    public FinanceiroService(RollsRepository rollsRepository) {
        this.rollsRepository = rollsRepository;
    }

    public List<FinanceiroDTO> buscarFinanceiro(UUID clienteId,
                                                LocalDate inicio,
                                                LocalDate fim) {

        return rollsRepository.buscarFinanceiro(clienteId, inicio, fim);
    }
}