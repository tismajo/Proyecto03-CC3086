package com.db1.pry3.Repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.db1.pry3.Model.ProgramacionMantenimientoModel;

public interface ProgramacionMantenimientoRepository extends JpaRepository<ProgramacionMantenimientoModel, Long> {
    @Query("SELECT p FROM ProgramacionMantenimientoModel p " +
       "WHERE (:fechaInicio IS NULL OR p.fechaProgramada >= :fechaInicio) " +
       "AND (:fechaFin IS NULL OR p.fechaProgramada <= :fechaFin) " +
       "AND (:tipoMaquinariaId IS NULL OR p.maquinaria.tipo.id = :tipoMaquinariaId)")
List<ProgramacionMantenimientoModel> findByFechasAndTipo(
    @Param("fechaInicio") LocalDate fechaInicio,
    @Param("fechaFin") LocalDate fechaFin,
    @Param("tipoMaquinariaId") Long tipoMaquinariaId
);
}
