package com.luxlav.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "tipo_cliente_enum_novo")
    private TipoCliente tipoCliente;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorKg;

    @Column(nullable = false)
    private Boolean ativo = true;
}