package com.luxlav.backend.repository;

import com.luxlav.backend.model.ClientePecaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientePecaRepository extends JpaRepository<ClientePecaModel, UUID> {}
