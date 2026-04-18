package com.luxlav.backend.service;

import com.luxlav.backend.dto.ClienteDTO;
import com.luxlav.backend.model.ClientePecaModel;
import com.luxlav.backend.model.ClientesModel;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.model.TipoCliente;
import com.luxlav.backend.repository.ClientePecaRepository;
import com.luxlav.backend.repository.ClienteRepository;
import com.luxlav.backend.repository.PecasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClientePecaRepository clientePecaRepository;

    @Autowired
    private PecasRepository pecasRepository;

    public ClientesModel criarCliente(ClienteDTO clienteDTO) {

        ClientesModel clientesModel = new ClientesModel();

        clientesModel.setNome(clienteDTO.getNome());
        clientesModel.setEmail(clienteDTO.getEmail());
        clientesModel.setTelefone(clienteDTO.getTelefone());
        clientesModel.setTipoCliente(clienteDTO.getTipoCliente());
        clientesModel.setAtivo(true);

        // REGRA: PESO
        if (clienteDTO.getTipoCliente() == TipoCliente.PESO) {
            clientesModel.setValorKg(clienteDTO.getValorKg());
        } else {
            clientesModel.setValorKg(null);
        }

        ClientesModel cliente = clienteRepository.save(clientesModel);

        // REGRA: PEÇA
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

        return cliente;
    }
}
