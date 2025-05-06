package com.db1.pry3.Repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.db1.pry3.Model.MantenimientoModel;

public interface MantenimientoRepository extends JpaRepository<MantenimientoModel, Long> {
    @Query("SELECT m FROM MantenimientoModel m " +
       "WHERE (:fechaInicio IS NULL OR m.fecha >= :fechaInicio) " +
       "AND (:fechaFin IS NULL OR m.fecha <= :fechaFin) " +
       "AND (:tipoMaquinariaId IS NULL OR m.maquinaria.tipo.id = :tipoMaquinariaId)")
List<MantenimientoModel> findByFechasAndTipo(
    @Param("fechaInicio") LocalDate fechaInicio,
    @Param("fechaFin") LocalDate fechaFin,
    @Param("tipoMaquinariaId") Long tipoMaquinariaId
);
}
