package com.db1.pry3.Model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "maquinaria_departamento")
public class MaquinariaDepartamentoModel {
    @EmbeddedId
    private MaquinariaDepartamentoId id;

    @ManyToOne
    @MapsId("maquinariaId")
    @JoinColumn(name = "maquinaria_id")
    private MaquinariaModel maquinaria;

    @ManyToOne
    @MapsId("departamentoId")
    @JoinColumn(name = "departamento_id")
    private DepartamentoModel departamento;

    @Column(name = "fecha_asignacion")
    private LocalDate fechaAsignacion;

}
