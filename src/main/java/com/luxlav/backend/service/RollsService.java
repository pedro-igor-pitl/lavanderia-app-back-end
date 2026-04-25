package com.luxlav.backend.service;

import com.luxlav.backend.dto.ClienteDTO;
import com.luxlav.backend.dto.RollsDTO;
import com.luxlav.backend.dto.RollsItensDTO;
import com.luxlav.backend.model.ClientesModel;
import com.luxlav.backend.model.RollsItensModel;
import com.luxlav.backend.model.RollsModel;
import com.luxlav.backend.model.TipoCliente;
import com.luxlav.backend.repository.ClienteRepository;
import com.luxlav.backend.repository.RollsItensRepository;
import com.luxlav.backend.repository.RollsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RollsService {

    @Autowired
    private RollsRepository rollsRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RollsItensRepository rollsItensRepository;

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

            if (rollsDTO.getPeso() == null) {
                throw new RuntimeException("Peso obrigatório");
            }

            RollsItensModel item = new RollsItensModel();
            item.setRoll(salvo);
            item.setPeso(rollsDTO.getPeso());
            item.setQuantidade(null);
            item.setPeca(null);

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

                RollsItensModel item = new RollsItensModel();
                item.setRoll(salvo);
                item.setQuantidade(itensDTO.getQuantidade());
                item.setPeso(null);
                item.setPrecoUnitario(itensDTO.getPreco_unitario());

                itens.add(item);
            }

            rollsItensRepository.saveAll(itens);
        }
        return RollsDTO.fromModel(salvo);
    }
}
