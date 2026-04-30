package com.luxlav.backend.repository;

import com.luxlav.backend.model.RollsModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RollsRepository extends JpaRepository<RollsModel, UUID> {
    Optional<RollsModel> findByIdAndCliente_Id(
            UUID id,
            UUID clienteId
    );

    List<RollsModel> findByCliente_IdAndDataColetaBetween(
            UUID clienteId,
            LocalDate inicio,
            LocalDate fim
    );
}

