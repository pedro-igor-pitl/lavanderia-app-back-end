package com.luxlav.backend.service;

import com.luxlav.backend.dto.PecasDTO;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.repository.PecasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PecasService {

    @Autowired
    private PecasRepository pecasRepository;

    public PecasModel criarPeca(PecasDTO pecasDTO) {
        PecasModel pecasModel = new PecasModel();
        pecasModel.setNome(pecasDTO.getNome());
        pecasModel.setAtivo(pecasDTO.getAtivo());
        return pecasRepository.save(pecasModel);
    }
}