package com.db1.pry3.Model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "historial_falla")
public class HistorialFallaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "falla_id", nullable = false)
    private FallaReportadaModel falla;

    @ManyToOne
    @JoinColumn(name = "mantenimiento_id", nullable = false)
    private MantenimientoModel mantenimiento;

    @Column(name = "fecha_solucion")
    private LocalDate fechaSolucion;

    private String solucion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FallaReportadaModel getFalla() {
        return falla;
    }

    public void setFalla(FallaReportadaModel falla) {
        this.falla = falla;
    }

    public MantenimientoModel getMantenimiento() {
        return mantenimiento;
    }

    public void setMantenimiento(MantenimientoModel mantenimiento) {
        this.mantenimiento = mantenimiento;
    }

    public LocalDate getFechaSolucion() {
        return fechaSolucion;
    }

    public void setFechaSolucion(LocalDate fechaSolucion) {
        this.fechaSolucion = fechaSolucion;
    }

    public String getSolucion() {
        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

}
