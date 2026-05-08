package com.luxlav.backend.service;

import com.luxlav.backend.dto.*;
import com.luxlav.backend.model.*;
import com.luxlav.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RollsService {

    @Autowired
    private RollsRepository rollsRepository;

    @Autowired
    private PecasRepository pecasRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RollsItensRepository rollsItensRepository;

    @Autowired
    private ClientePecaRepository clientePecaRepository;

    @Transactional
    public RollsModel atualizarStatus(UUID id, StatusRollDTO dto) {

        RollsModel roll = rollsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roll não encontrado"));

        roll.setAtivo(dto.getAtivo());

        return rollsRepository.save(roll);
    }

    @Transactional
    public RollsDTO atualizarRoll(RollsDTO dto) {

        RollsModel roll = rollsRepository
                .findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Roll não encontrado"));

        if (!roll.getAtivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Coleta inativa"
            );
        }

        roll.setCodigoManual(dto.getCodigo_manual());
        roll.setDataColeta(dto.getData_coleta());

        rollsRepository.save(roll);

        List<RollsItensModel> atuais =
                rollsItensRepository.findByRollId(roll.getId());

        Map<UUID, RollsItensModel> atuaisMap = atuais.stream()
                .filter(item -> item.getPeca() != null)
                .collect(Collectors.toMap(
                        item -> item.getPeca().getId(),
                        item -> item
                ));

        if (dto.getItens() == null) {
            dto.setItens(new ArrayList<>());
        }

        Set<UUID> novosIds = dto.getItens().stream()
                .map(RollsItensDTO::getPeca_id)
                .collect(Collectors.toSet());

        for (RollsItensModel item : atuais) {
            if (item.getPeca() != null &&
                    !novosIds.contains(item.getPeca().getId())) {

                item.setAtivo(false);
            }
        }

        for (RollsItensDTO novo : dto.getItens()) {

            RollsItensModel existente = atuaisMap.get(novo.getPeca_id());

            if (existente != null) {
                existente.setAtivo(true);
                existente.setQuantidade(novo.getQuantidade());
                existente.setPrecoUnitario(novo.getPreco_unitario());
            } else {

                PecasModel peca = pecasRepository.findById(novo.getPeca_id())
                        .orElseThrow(() -> new RuntimeException("Peça não encontrada"));

                RollsItensModel novoItem = new RollsItensModel();
                novoItem.setRoll(roll);
                novoItem.setPeca(peca);
                novoItem.setQuantidade(novo.getQuantidade());
                novoItem.setPrecoUnitario(novo.getPreco_unitario());
                novoItem.setAtivo(true);

                atuais.add(novoItem);
            }
        }

        if (dto.getPeso() != null) {
            for (RollsItensModel item : atuais) {
                if (item.getPeca() == null) {
                    item.setPeso(dto.getPeso());
                    item.setPrecoUnitario(item.getValorKg());
                }
            }
        }

        rollsItensRepository.saveAll(atuais);

        return RollsDTO.fromModel(roll);
    }

    public List<ColetaResumoDTO> buscarPorIntervaloRoll(
            UUID clienteId,
            LocalDate inicio,
            LocalDate fim) {

        List<RollsModel> rolls = rollsRepository
                .findByCliente_IdAndDataColetaBetween(clienteId, inicio, fim);

        return rolls.stream()
                .map(roll -> ColetaResumoDTO.builder()
                        .id(roll.getId())
                        .clienteId(clienteId)
                        .codigoManual(roll.getCodigoManual())
                        .dataColeta(roll.getDataColeta())
                        .clienteNome(roll.getCliente().getNome())
                        .ativo(roll.getAtivo())
                        .build()
                )
                .toList();
    }

    @Transactional
    public ColetaDTO buscarRoll(UUID clienteId, UUID rollId) {

        RollsModel roll = rollsRepository
                .findByIdAndCliente_Id(rollId, clienteId)
                .orElseThrow(() -> new RuntimeException("Roll não encontrado"));

        if (!roll.getAtivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Coleta inativa"
            );
        }

        List<RollsItensModel> itens = roll.getItens();

        return ColetaDTO.builder()
                .id(roll.getId())
                .codigoManual(roll.getCodigoManual())
                .dataColeta(roll.getDataColeta())
                .clienteId(roll.getCliente().getId())
                .clienteNome(roll.getCliente().getNome())
                .valorKg(
                        itens.stream()
                                .map(RollsItensModel::getPrecoUnitario)
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(BigDecimal.ZERO)
                )
                .peso(
                        itens.stream()
                                .map(item -> item.getPeso())
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .itens(
                        itens.stream()
                                .filter(item ->
                                        item.getPeca() != null &&
                                                Boolean.TRUE.equals(item.getAtivo())
                                )
                                .map(item ->
                                        ColetaItemDTO.builder()
                                                .pecaId(item.getPeca().getId())
                                                .nomePeca(item.getPeca().getNome())
                                                .quantidade(item.getQuantidade())
                                                .precoUnitario(item.getPrecoUnitario())
                                                .build()
                                ).toList()
                )
                .build();
    }

    @Transactional
    public RollsDTO criarRoll(RollsDTO rollsDTO) {

        ClientesModel cliente = clienteRepository.findById(rollsDTO.getCliente_id())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        RollsModel rollsModel = new RollsModel();

        rollsModel.setCodigoManual(rollsDTO.getCodigo_manual());
        rollsModel.setCliente(cliente);
        rollsModel.setDataColeta(rollsDTO.getData_coleta());
        rollsModel.setAtivo(true);

        RollsModel salvo = rollsRepository.save(rollsModel);

        TipoCliente tipo = cliente.getTipoCliente();

        if (tipo == TipoCliente.PESO) {

            if (rollsDTO.getPeso() == null || rollsDTO.getPeso().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Peso deve ser maior que zero");
            }

            if (cliente.getValorKg() == null) {
                throw new RuntimeException("Cliente não possui preço por kg definido");
            }

            RollsItensModel item = new RollsItensModel();
            item.setRoll(salvo);
            item.setPeso(rollsDTO.getPeso());
            item.setQuantidade(null);
            item.setPeca(null);
            item.setPrecoUnitario(cliente.getValorKg());

            rollsItensRepository.save(item);
        } else if (tipo == TipoCliente.PECA) {

            if (rollsDTO.getItens() == null || rollsDTO.getItens().isEmpty()) {
                throw new RuntimeException("Lista de peças obrigatória");
            }

            List<RollsItensModel> itens = new ArrayList<>();

            for (RollsItensDTO itensDTO : rollsDTO.getItens()) {
                if (itensDTO.getQuantidade() == null || itensDTO.getQuantidade() == 0) {
                    continue;
                }

                PecasModel peca = pecasRepository.findById(itensDTO.getPeca_id())
                        .orElseThrow(() -> new RuntimeException("Peça não encontrada"));

                RollsItensModel item = new RollsItensModel();
                item.setRoll(salvo);
                item.setPeca(peca);
                item.setQuantidade(itensDTO.getQuantidade());
                item.setPeso(null);
                ClientePecaModel clientePeca = clientePecaRepository
                        .findByClienteAndPeca(cliente, peca)
                        .orElseThrow(() -> new RuntimeException("Preço não definido para essa peça"));

                item.setPrecoUnitario(clientePeca.getPrecoCliente());

                itens.add(item);
            }

            rollsItensRepository.saveAll(itens);
        }
        return RollsDTO.fromModel(salvo);
    }
}