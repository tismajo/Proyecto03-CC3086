package com.db1.pry3.Model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "programacion_mantenimiento")
public class ProgramacionMantenimientoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maquinaria_id", nullable = false)
    private MaquinariaModel maquinaria;

    @ManyToOne
    @JoinColumn(name = "tipo_mantenimiento_id", nullable = false)
    private TipoMantenimientoModel tipoMantenimiento;

    @Column(name = "fecha_programada")
    private LocalDate fechaProgramada;

    @Column(name = "frecuencia_dias")
    private Integer frecuenciaDias;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaquinariaModel getMaquinaria() {
        return maquinaria;
    }

    public void setMaquinaria(MaquinariaModel maquinaria) {
        this.maquinaria = maquinaria;
    }

    public TipoMantenimientoModel getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(TipoMantenimientoModel tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Integer getFrecuenciaDias() {
        return frecuenciaDias;
    }

    public void setFrecuenciaDias(Integer frecuenciaDias) {
        this.frecuenciaDias = frecuenciaDias;
    }
}
