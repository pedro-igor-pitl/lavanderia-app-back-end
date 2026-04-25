package com.luxlav.backend.repository;

import com.luxlav.backend.model.RollsItensModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RollsItensRepository extends JpaRepository <RollsItensModel, UUID> {}
