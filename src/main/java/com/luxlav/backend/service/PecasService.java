package com.luxlav.backend.service;

import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.repository.PecasRepository;
import org.springframework.stereotype.Service;

@Service
public class PecasService {

    private final PecasRepository pecasRepository;

    public PecasService(PecasRepository pecasRepository) {
        this.pecasRepository = pecasRepository;
    }

    public PecasModel criarPeca(PecasModel pecasModel) {
        return pecasRepository.save(pecasModel);
    }
}