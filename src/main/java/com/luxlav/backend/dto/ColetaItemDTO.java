package com.luxlav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColetaItemDTO {
    private UUID pecaId;
    private String nomePeca;

    private Integer quantidade;
    private BigDecimal precoUnitario;
}
