package com.luxlav.backend.repository;

import com.luxlav.backend.model.RollsModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RollsRepository extends JpaRepository<RollsModel, UUID> {
    Optional<RollsModel> findByCodigoManualAndCliente_Id(
            String codigoManual,
            UUID clienteId
    );
}

