package com.luxlav.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "clientes")
public class ClientesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String email;

    @Column(nullable = false)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente;

    private Boolean ativo = true;
}
