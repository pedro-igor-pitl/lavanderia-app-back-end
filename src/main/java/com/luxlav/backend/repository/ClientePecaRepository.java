package com.luxlav.backend.repository;

import com.luxlav.backend.model.ClientePecaModel;
import com.luxlav.backend.model.ClientesModel;
import com.luxlav.backend.model.PecasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientePecaRepository extends JpaRepository<ClientePecaModel, UUID> {

    @Query("SELECT cp FROM ClientePecaModel cp WHERE cp.clientesModel.id = :id")
    List<ClientePecaModel> listarPorCliente(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE ClientePecaModel cp SET cp.ativo = :ativo WHERE cp.clientesModel.id = :id")
    void atualizarStatusPorCliente(@Param("id") UUID id,
                                   @Param("ativo") Boolean ativo);

    @Query("SELECT cp FROM ClientePecaModel cp " +
            "WHERE cp.clientesModel = :cliente " +
            "AND cp.pecasModel = :peca " +
            "AND cp.ativo = true")
    Optional<ClientePecaModel> findByClienteAndPeca(
            @Param("cliente") ClientesModel cliente,
            @Param("peca") PecasModel peca
    );
}