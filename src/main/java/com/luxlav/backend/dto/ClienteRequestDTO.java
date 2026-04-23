package com.luxlav.backend.dto;

import com.luxlav.backend.model.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ClienteRequestDTO {
    private String nome;
    private String email;
    private String telefone;
    private TipoCliente tipoCliente;
    private BigDecimal valorKg;

    private List<ClientePecaRequestDTO> pecas;
}