package com.luxlav.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface FinanceiroDTO {

    UUID getClienteId();
    String getClienteNome();
    String getEmail();
    String getTelefone();
    String getTipoCliente();
    Boolean getClienteAtivo();

    UUID getRollId();
    String getCodigoManual();
    LocalDate getDataColeta();
    Boolean getRollAtivo();
    BigDecimal getRollValorKg();

    UUID getRollItemId();
    Integer getQuantidade();
    BigDecimal getPeso();
    BigDecimal getPrecoUnitario();
    BigDecimal getRollItemValorKg();
    Boolean getRollItemAtivo();

    UUID getPecaId();
    String getPecaNome();
    Boolean getPecaAtivo();
}