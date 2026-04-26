package com.luxlav.backend.service;

import com.luxlav.backend.dto.*;
import com.luxlav.backend.model.*;
import com.luxlav.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
                        .build()
                )
                .toList();
    }

    public ColetaDTO buscarRoll(UUID clienteId, String codigoManual) {

        RollsModel roll = rollsRepository
                .findByCodigoManualAndCliente_Id(codigoManual, clienteId)
                .orElseThrow(() -> new RuntimeException("Roll não encontrado"));

        List<RollsItensModel> itens = rollsItensRepository.findByRollId(roll.getId());

        return ColetaDTO.builder()
                .id(roll.getId())
                .codigoManual(roll.getCodigoManual())
                .dataColeta(roll.getDataColeta())
                .clienteId(roll.getCliente().getId())
                .clienteNome(roll.getCliente().getNome())
                .peso(
                        itens.stream()
                                .map(item -> item.getPeso())
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .itens(
                        itens.stream()
                                .filter(item -> item.getPeca() != null)
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