package com.luxlav.backend.service;

import com.luxlav.backend.dto.*;
import com.luxlav.backend.exception.ClienteInativoException;
import com.luxlav.backend.model.ClientePecaModel;
import com.luxlav.backend.model.ClientesModel;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.model.TipoCliente;
import com.luxlav.backend.repository.ClientePecaRepository;
import com.luxlav.backend.repository.ClienteRepository;
import com.luxlav.backend.repository.PecasRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClientePecaRepository clientePecaRepository;

    @Autowired
    private PecasRepository pecasRepository;

    @Transactional
    public Optional<ClienteDTO> atualizarCliente(UUID id, ClienteRequestDTO dto) {

        return clienteRepository.findById(id)
                .map(cliente -> {

                    if (!cliente.getAtivo()) {
                        throw new ClienteInativoException("Cliente inativo não pode ser alterado");
                    }

                    cliente.setNome(dto.getNome());
                    cliente.setEmail(dto.getEmail());
                    cliente.setTelefone(dto.getTelefone());
                    cliente.setTipoCliente(dto.getTipoCliente());

                    if (dto.getTipoCliente() == TipoCliente.PESO) {

                        cliente.setValorKg(dto.getValorKg());

                        if (cliente.getPecas() != null) {
                            cliente.getPecas().clear();
                        }

                    } else if (dto.getTipoCliente() == TipoCliente.PECA) {

                        cliente.setValorKg(null);

                        atualizarPecasCliente(
                                cliente.getId(),
                                dto.getPecas() != null ? dto.getPecas() : List.of()
                        );
                    }

                    ClientesModel salvo = clienteRepository.save(cliente);

                    // 🔥 CONVERSÃO AQUI
                    return converterParaDTO(salvo);
                });
    }

    private ClienteDTO converterParaDTO(ClientesModel c) {

        List<ClientePecaResponseDTO> pecas = null;

        if (c.getTipoCliente() == TipoCliente.PECA && c.getPecas() != null) {
            pecas = c.getPecas().stream()
                    .filter(ClientePecaModel::getAtivo)
                    .map(p -> new ClientePecaResponseDTO(
                            p.getPecasModel().getId(),
                            p.getPecasModel().getNome(),
                            p.getPrecoCliente()
                    ))
                    .toList();
        }

        return new ClienteDTO(
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getTelefone(),
                c.getTipoCliente(),
                c.getValorKg(),
                c.getAtivo(),
                pecas
        );
    }

    public void atualizarPecasCliente(UUID clienteId, List<UUID> novasPecasIds) {

        List<ClientePecaModel> atuais =
                clientePecaRepository.listarPorCliente(clienteId);

        // mapa das relações atuais por peça
        Map<UUID, ClientePecaModel> atuaisMap = atuais.stream()
                .collect(Collectors.toMap(
                        cp -> cp.getPecasModel().getId(),
                        cp -> cp
                ));

        // 1. inativa o que não veio
        for (ClientePecaModel cp : atuais) {
            if (!novasPecasIds.contains(cp.getPecasModel().getId())) {
                cp.setAtivo(false);
            }
        }

        // 2. ativa ou cria o que veio
        for (UUID pecaId : novasPecasIds) {

            ClientePecaModel existente = atuaisMap.get(pecaId);

            if (existente != null) {
                // já existe → só reativa
                existente.setAtivo(true);

            } else {
                // não existe → cria nova relação
                ClientePecaModel novo = new ClientePecaModel();

                ClientesModel cliente = new ClientesModel();
                cliente.setId(clienteId);

                PecasModel peca = new PecasModel();
                peca.setId(pecaId);

                novo.setClientesModel(cliente);
                novo.setPecasModel(peca);
                novo.setAtivo(true);

                atuais.add(novo);
            }
        }

        clientePecaRepository.saveAll(atuais);
    }

    public Optional<ClienteDTO> buscarClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .map(c -> {

                    List<ClientePecaResponseDTO> pecas = null;

                    if (c.getTipoCliente() == TipoCliente.PECA) {
                        pecas = c.getPecas().stream()
                                .filter(ClientePecaModel::getAtivo)
                                .map(p -> new ClientePecaResponseDTO(
                                        p.getPecasModel().getId(),
                                        p.getPecasModel().getNome(),
                                        p.getPrecoCliente()
                                ))
                                .toList();
                    }

                    return new ClienteDTO(
                            c.getId(),
                            c.getNome(),
                            c.getEmail(),
                            c.getTelefone(),
                            c.getTipoCliente(),
                            c.getValorKg(),
                            c.getAtivo(),
                            pecas
                    );
                });
    }

    @Transactional
    public Optional<ClientesModel> atualizarStatus(UUID id, ClienteDTO dto) {
        return clienteRepository.findById(id)
                .map(cliente -> {

                    cliente.setAtivo(dto.getAtivo());
                    clienteRepository.save(cliente);

                    clientePecaRepository.atualizarStatusPorCliente(id, dto.getAtivo());

                    return cliente;
                });
    }

    public List<ClienteResumoDTO> listarClientesResumidos() {
        return clienteRepository.findAll().stream()
                .map(p -> new ClienteResumoDTO(
                        p.getId(),
                        p.getNome(),
                        p.getAtivo() != null && p.getAtivo()
                ))
                .toList();
    }

    public List<PecasDTO> listarPecas(Boolean ativo) {

        List<PecasModel> pecas;

        if (ativo == null) {
            pecas = pecasRepository.findAll();
        } else {
            pecas = pecasRepository.findByAtivo(ativo);
        }

        return pecas.stream()
                .map(p -> new PecasDTO(
                        p.getId(),
                        p.getNome(),
                        p.getAtivo() != null && p.getAtivo()
                ))
                .toList();
    }

    public ClienteDTO criarCliente(ClienteDTO clienteDTO) {

        ClientesModel clientesModel = new ClientesModel();

        clientesModel.setNome(clienteDTO.getNome());
        clientesModel.setEmail(clienteDTO.getEmail());
        clientesModel.setTelefone(clienteDTO.getTelefone());
        clientesModel.setTipoCliente(clienteDTO.getTipoCliente());
        clientesModel.setAtivo(true);

        if (clienteDTO.getTipoCliente() == TipoCliente.PESO) {
            clientesModel.setValorKg(clienteDTO.getValorKg());
        } else {
            clientesModel.setValorKg(null);
        }

        ClientesModel cliente = clienteRepository.save(clientesModel);

        if (clienteDTO.getTipoCliente() == TipoCliente.PECA) {
            clienteDTO.getPecas().forEach(p -> {

                PecasModel peca = pecasRepository.findById(p.getPecaId())
                        .orElseThrow(() -> new RuntimeException("Peça não encontrada"));

                ClientePecaModel clientePecaModel = new ClientePecaModel();

                clientePecaModel.setClientesModel(cliente);
                clientePecaModel.setPecasModel(peca);
                clientePecaModel.setPrecoCliente(p.getPrecoCliente());

                clientePecaRepository.save(clientePecaModel);
            });
        }

        ClienteDTO response = new ClienteDTO();
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());
        response.setTipoCliente(cliente.getTipoCliente());
        response.setValorKg(cliente.getValorKg());

        return response;
    }
}
