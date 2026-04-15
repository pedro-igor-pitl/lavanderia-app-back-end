package com.luxlav.backend.repository;

import com.luxlav.backend.model.PecasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PecasRepository extends JpaRepository<PecasModel, UUID> {
}
