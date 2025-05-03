package com.db1.pry3.Model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "falla_reportada")
public class FallaReportadaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maquinaria_id", nullable = false)
    private MaquinariaModel maquinaria;

    private String descripcion;

    @Column(name = "fecha_reporte")
    private LocalDate fechaReporte;

    private String prioridad;

    private String estado;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDate fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}
