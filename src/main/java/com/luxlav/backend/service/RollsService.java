package com.luxlav.backend.service;

import com.luxlav.backend.dto.ClienteDTO;
import com.luxlav.backend.dto.RollsDTO;
import com.luxlav.backend.dto.RollsItensDTO;
import com.luxlav.backend.model.*;
import com.luxlav.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
                item.setPeca(peca); // ✔ agora correto
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