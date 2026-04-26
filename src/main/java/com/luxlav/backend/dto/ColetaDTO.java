package com.luxlav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColetaDTO {
    private UUID id;
    private String codigoManual;
    private LocalDate dataColeta;

    private UUID clienteId;
    private String clienteNome;

    private BigDecimal peso;

    private BigDecimal valorKg;

    private List<ColetaItemDTO> itens;
}
