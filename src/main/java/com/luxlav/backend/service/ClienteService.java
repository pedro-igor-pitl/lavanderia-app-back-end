package com.luxlav.backend.service;

import com.luxlav.backend.dto.ClienteDTO;
import com.luxlav.backend.model.ClientesModel;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteService clienteService;

    public PecasModel criarCliente(ClienteDTO clienteDTO, ClientePe) {
        ClientesModel clientesModel = new ClientesModel();
        clientesModel.setNome(clienteDTO.getNome());
        clientesModel.setEmail(clienteDTO.getEmail());
        clientesModel.setTelefone(clienteDTO.getTelefone());
        clientesModel.setTipoCliente(clienteDTO.getTipoCliente());
        clientesModel.setAtivo(clienteDTO.getAtivo());
        clie
        return
    }
}
