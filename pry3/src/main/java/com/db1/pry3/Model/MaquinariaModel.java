package com.db1.pry3.Model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "maquinaria")
public class MaquinariaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String modelo;

    private String serie;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    private String ubicacion;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoMaquinariaModel tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public TipoMaquinariaModel getTipo() {
        return tipo;
    }

    public void setTipo(TipoMaquinariaModel tipo) {
        this.tipo = tipo;
    }
}
