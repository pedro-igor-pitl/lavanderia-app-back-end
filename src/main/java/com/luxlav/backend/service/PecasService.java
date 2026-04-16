package com.luxlav.backend.service;

import com.luxlav.backend.dto.PecasDTO;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.repository.PecasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<PecasDTO> listarPecas() {
        return pecasRepository.findAll().stream()
                .map(p -> new PecasDTO(
                        p.getId(),
                        p.getNome(),
                        p.getAtivo() != null && p.getAtivo()
                ))
                .collect(Collectors.toList());
    }

    public Optional<PecasModel> atualizarPecas(UUID id, PecasDTO pecasDTO) {
        return pecasRepository.findById(id)
                .map(pecasModel -> {
                   pecasModel.setNome(pecasDTO.getNome());
                   return pecasRepository.save(pecasModel);
                });
    }

    public Optional<PecasDTO> buscarPecaPorId(UUID id) {
        return pecasRepository.findById(id)
                .map(p -> new PecasDTO(
                        p.getId(),
                        p.getNome(),
                        p.getAtivo()!= null && p.getAtivo()
                ));
    }

    public Optional<PecasModel> atualizarStatus(UUID id, PecasDTO pecasDTO) {
        return pecasRepository.findById(id)
                .map(pecasModel -> {
                   pecasModel.setAtivo(pecasDTO.getAtivo());
                   return pecasRepository.save(pecasModel);
                });
    }
}