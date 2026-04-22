package com.luxlav.backend.repository;

import com.luxlav.backend.model.ClientePecaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ClientePecaRepository extends JpaRepository<ClientePecaModel, UUID> {

    @Modifying
    @Query("UPDATE ClientePecaModel cp SET cp.ativo = :ativo WHERE cp.clientesModel.id = :id")
    void atualizarStatusPorCliente(@Param("id") UUID id,
                                   @Param("ativo") Boolean ativo);
}