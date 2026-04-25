package com.luxlav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RollsItensDTO {
    private UUID peca_id;
    private Integer quantidade;
    private BigDecimal preco_unitario;
}