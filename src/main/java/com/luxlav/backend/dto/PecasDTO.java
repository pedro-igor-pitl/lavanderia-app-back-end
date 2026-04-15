package com.luxlav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PecasDTO {
    UUID id;
    String nome;
    Boolean ativo;
}
