package com.db1.pry3.Repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.db1.pry3.Model.HistorialFallaModel;

public interface HistorialFallaRepository extends JpaRepository<HistorialFallaModel, Long> {
        @Query("SELECT h FROM HistorialFallaModel h " +
           "JOIN h.mantenimiento m " +
           "JOIN m.tecnico t " +
           "WHERE (:fechaInicio IS NULL OR h.fechaSolucion >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR h.fechaSolucion <= :fechaFin) " +
           "AND (:tecnicoId IS NULL OR t.id = :tecnicoId) " +
           "AND (:tipoMantenimientoId IS NULL OR m.tipoMantenimiento.id = :tipoMantenimientoId) " +
           "AND (:estadoSolucion IS NULL OR h.solucion LIKE %:estadoSolucion%)")
    List<HistorialFallaModel> findHistorialByFilters(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("tecnicoId") Long tecnicoId,
            @Param("tipoMantenimientoId") Long tipoMantenimientoId,
            @Param("estadoSolucion") String estadoSolucion
    );
}
