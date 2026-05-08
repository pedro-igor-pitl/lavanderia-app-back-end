package com.luxlav.backend.repository;

import com.luxlav.backend.model.RollsItensModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RollsItensRepository extends JpaRepository <RollsItensModel, UUID> {
    List<RollsItensModel> findByRollId(UUID rollId);
    List<RollsItensModel> findByRollIdAndAtivoTrue(UUID rollId);
}
