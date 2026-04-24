package com.luxlav.backend.service;

import com.luxlav.backend.dto.*;
import com.luxlav.backend.exception.ClienteInativoException;
import com.luxlav.backend.model.*;
import com.luxlav.backend.repository.ClientePecaRepository;
import com.luxlav.backend.repository.ClienteRepository;
import com.luxlav.backend.repository.PecasRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClientePecaRepository clientePecaRepository;

    @Autowired
    private PecasRepository pecasRepository;

    // =========================
    // UPDATE CLIENTE
    // =========================
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

                        List<ClientePecaRequestDTO> pecas =
                                dto.getPecas() != null ? dto.getPecas() : List.of();

                        atualizarPecasCliente(cliente.getId(), pecas);
                    }

                    ClientesModel salvo = clienteRepository.save(cliente);

                    return converterParaDTO(salvo);
                });
    }

    // =========================
    // CONVERTER DTO
    // =========================
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

    // =========================
    // UPDATE PEÇAS DO CLIENTE
    // =========================
    public void atualizarPecasCliente(
            UUID clienteId,
            List<ClientePecaRequestDTO> novasPecas
    ) {

        List<ClientePecaModel> atuais =
                clientePecaRepository.listarPorCliente(clienteId);

        Map<UUID, ClientePecaModel> atuaisMap = atuais.stream()
                .collect(Collectors.toMap(
                        cp -> cp.getPecasModel().getId(),
                        cp -> cp
                ));

        Set<UUID> novasIds = novasPecas.stream()
                .map(ClientePecaRequestDTO::getPecaId)
                .collect(Collectors.toSet());

        // 1. inativa removidos
        for (ClientePecaModel cp : atuais) {
            if (!novasIds.contains(cp.getPecasModel().getId())) {
                cp.setAtivo(false);
            }
        }

        // 2. ativa ou cria novos
        for (ClientePecaRequestDTO dto : novasPecas) {

            UUID pecaId = dto.getPecaId();
            BigDecimal preco = dto.getPrecoCliente();

            ClientePecaModel existente = atuaisMap.get(pecaId);

            if (existente != null) {

                existente.setAtivo(true);
                existente.setPrecoCliente(preco);

            } else {

                ClientePecaModel novo = new ClientePecaModel();

                ClientesModel cliente = new ClientesModel();
                cliente.setId(clienteId);

                PecasModel peca = new PecasModel();
                peca.setId(pecaId);

                novo.setClientesModel(cliente);
                novo.setPecasModel(peca);
                novo.setPrecoCliente(preco);
                novo.setAtivo(true);

                atuais.add(novo);
            }
        }

        clientePecaRepository.saveAll(atuais);
    }

    // =========================
    // BUSCAR CLIENTE
    // =========================
    public Optional<ClienteDTO> buscarClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .filter(ClientesModel::getAtivo)
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

    // =========================
    // STATUS
    // =========================
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

    // =========================
    // LISTA RESUMO
    // =========================
    public List<ClienteResumoDTO> listarClientesResumidos() {
        return clienteRepository.findAll().stream()
                .map(p -> new ClienteResumoDTO(
                        p.getId(),
                        p.getNome(),
                        p.getAtivo() != null && p.getAtivo()
                ))
                .toList();
    }


    public List<ClienteResumoDTO> listarClientesResumidos(Boolean ativo) {
        List<ClientesModel> clientes = (ativo == null)
                ? clienteRepository.findAll()
                : clienteRepository.findByAtivo(ativo);

        return clientes.stream()
                .map(p -> new ClienteResumoDTO(
                        p.getId(),
                        p.getNome(),
                        p.getAtivo() != null && p.getAtivo()
                ))
                .toList();
    }

    // =========================
    // LISTAR PEÇAS
    // =========================
    public List<PecasDTO> listarPecasAtivas() {

        return pecasRepository.findByAtivoTrue()
                .stream()
                .map(p -> new PecasDTO(
                        p.getId(),
                        p.getNome(),
                        true
                ))
                .toList();
    }

    // =========================
    // CRIAR CLIENTE
    // =========================
    public ClienteDTO criarCliente(ClienteDTO clienteDTO) {

        ClientesModel cliente = new ClientesModel();

        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setTipoCliente(clienteDTO.getTipoCliente());
        cliente.setAtivo(true);

        if (clienteDTO.getTipoCliente() == TipoCliente.PESO) {
            cliente.setValorKg(clienteDTO.getValorKg());
        } else {
            cliente.setValorKg(null);
        }

        ClientesModel salvo = clienteRepository.save(cliente);

        if (clienteDTO.getTipoCliente() == TipoCliente.PECA) {

            clienteDTO.getPecas().forEach(p -> {

                PecasModel peca = pecasRepository.findById(p.getPecaId())
                        .orElseThrow(() -> new RuntimeException("Peça não encontrada"));

                ClientePecaModel cp = new ClientePecaModel();

                cp.setClientesModel(salvo);
                cp.setPecasModel(peca);
                cp.setPrecoCliente(p.getPrecoCliente());
                cp.setAtivo(true);

                clientePecaRepository.save(cp);
            });
        }

        ClienteDTO response = new ClienteDTO();
        response.setNome(salvo.getNome());
        response.setEmail(salvo.getEmail());
        response.setTelefone(salvo.getTelefone());
        response.setTipoCliente(salvo.getTipoCliente());
        response.setValorKg(salvo.getValorKg());

        return response;
    }
}