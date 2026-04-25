package com.luxlav.backend.model;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "rolls")
public class RollsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo_manual", nullable = false)
    private String codigoManual;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClientesModel cliente;

    @Column(name = "data_coleta", nullable = false)
    private LocalDate dataColeta;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "roll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RollsItensModel> itens;
}