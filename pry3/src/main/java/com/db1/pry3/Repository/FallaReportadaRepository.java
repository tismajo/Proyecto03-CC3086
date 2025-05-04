package com.db1.pry3.Repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.db1.pry3.Model.FallaReportadaModel;

@Repository
public interface FallaReportadaRepository extends JpaRepository<FallaReportadaModel, Long> {

    @Query("SELECT f FROM FallaReportadaModel f " +
           "JOIN f.maquinaria m JOIN MaquinariaDepartamentoModel md ON m.id = md.maquinaria.id " +
           "WHERE (:fechaInicio IS NULL OR f.fechaReporte >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR f.fechaReporte <= :fechaFin) " +
           "AND (:prioridad IS NULL OR f.prioridad = :prioridad) " +
           "AND (:estado IS NULL OR f.estado = :estado) " +
           "AND (:departamentoId IS NULL OR md.departamento.id = :departamentoId)")
    List<FallaReportadaModel> findFallasByFilters(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("prioridad") String prioridad,
            @Param("estado") String estado,
            @Param("departamentoId") Long departamentoId
    );
}
