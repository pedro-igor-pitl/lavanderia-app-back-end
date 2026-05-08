package com.luxlav.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class ColetaResumoDTO {
    private UUID id;
    private String codigoManual;
    private LocalDate dataColeta;
    private String clienteNome;
    private UUID clienteId;
    private Boolean ativo;
}