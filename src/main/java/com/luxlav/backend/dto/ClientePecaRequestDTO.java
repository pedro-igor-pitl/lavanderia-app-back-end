package com.luxlav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientePecaRequestDTO {
    private UUID pecaId;
    private BigDecimal precoCliente;
}