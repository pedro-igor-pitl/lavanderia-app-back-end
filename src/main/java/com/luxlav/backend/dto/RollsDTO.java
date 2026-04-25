package com.luxlav.backend.dto;

import com.luxlav.backend.model.RollsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RollsDTO {

    private String codigo_manual;
    private UUID cliente_id;
    private LocalDate data_coleta;
    private BigDecimal peso;
    private List<RollsItensDTO> itens;

    public static RollsDTO fromModel(RollsModel model) {
        RollsDTO dto = new RollsDTO();

        dto.setCodigo_manual(model.getCodigoManual());
        dto.setCliente_id(model.getCliente().getId());
        dto.setData_coleta(model.getDataColeta());

        return dto;
    }
}