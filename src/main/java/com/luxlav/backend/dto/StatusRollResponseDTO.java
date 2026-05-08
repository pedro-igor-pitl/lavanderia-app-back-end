package com.luxlav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusRollResponseDTO {
    private UUID id;
    private Boolean ativo;
}
