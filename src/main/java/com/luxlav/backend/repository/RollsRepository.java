package com.luxlav.backend.repository;

import com.luxlav.backend.dto.FinanceiroDTO;
import com.luxlav.backend.model.RollsModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(value = """
        SELECT 
            clientes.id AS clienteId,
            clientes.nome AS clienteNome,
            clientes.email AS email,
            clientes.telefone AS telefone,
            clientes.tipo_cliente AS tipoCliente,
            clientes.ativo AS clienteAtivo,

            rolls.id AS rollId,
            rolls.codigo_manual AS codigoManual,
            rolls.data_coleta AS dataColeta,
            rolls.ativo AS rollAtivo,
            rolls.valor_kg AS rollValorKg,

            roll_itens.id AS rollItemId,
            roll_itens.quantidade AS quantidade,
            roll_itens.peso AS peso,
            roll_itens.preco_unitario AS precoUnitario,
            roll_itens.valor_kg AS rollItemValorKg,
            roll_itens.ativo AS rollItemAtivo,

            pecas.id AS pecaId,
            pecas.nome AS pecaNome,
            pecas.ativo AS pecaAtivo

        FROM clientes

        INNER JOIN rolls 
            ON clientes.id = rolls.cliente_id

        INNER JOIN roll_itens 
            ON rolls.id = roll_itens.roll_id

        INNER JOIN pecas 
            ON pecas.id = roll_itens.peca_id

        WHERE 
            clientes.ativo = true
            AND rolls.ativo = true
            AND roll_itens.ativo = true
            AND pecas.ativo = true

            AND clientes.id = :clienteId

            AND rolls.data_coleta >= :dataInicio
            AND rolls.data_coleta < :dataFim
        """, nativeQuery = true)

    List<FinanceiroDTO> buscarFinanceiro(
            @Param("clienteId") UUID clienteId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );
}