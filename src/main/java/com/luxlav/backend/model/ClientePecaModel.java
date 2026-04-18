package com.luxlav.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.font.NumericShaper;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
public class ClientePecaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClientesModel clientesModel;

    @ManyToOne
    @JoinColumn(name = "peca_id", nullable = false)
    private PecasModel pecasModel;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoCliente;
}
