package com.luxlav.backend.service;

import com.luxlav.backend.dto.ClienteDTO;
import com.luxlav.backend.dto.ClienteResumoDTO;
import com.luxlav.backend.dto.PecasDTO;
import com.luxlav.backend.model.ClientePecaModel;
import com.luxlav.backend.model.ClientesModel;
import com.luxlav.backend.model.PecasModel;
import com.luxlav.backend.model.TipoCliente;
import com.luxlav.backend.repository.ClientePecaRepository;
import com.luxlav.backend.repository.ClienteRepository;
import com.luxlav.backend.repository.PecasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
