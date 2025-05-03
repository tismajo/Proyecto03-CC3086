package com.db1.pry3.Model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "mantenimiento")
public class MantenimientoModel {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "maquinaria_id", nullable = false)
        private MaquinariaModel maquinaria;

        @ManyToOne
        @JoinColumn(name = "tecnico_id", nullable = false)
        private TecnicoModel tecnico;

        @ManyToOne
        @JoinColumn(name = "tipo_mantenimiento_id", nullable = false)
        private TipoMantenimientoModel tipoMantenimiento;

        @Column(name = "fecha", nullable = false)
        private LocalDate fecha;

        @Column(name = "duracion_horas", nullable = false)
        private BigDecimal duracionHoras;

        @Column(name = "costo", nullable = false)
        private BigDecimal costo;

        @Column(name = "observaciones")
        private String observaciones;

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

        public TecnicoModel getTecnico() {
            return tecnico;
        }

        public void setTecnico(TecnicoModel tecnico) {
            this.tecnico = tecnico;
        }

        public TipoMantenimientoModel getTipoMantenimiento() {
            return tipoMantenimiento;
        }

        public void setTipoMantenimiento(TipoMantenimientoModel tipoMantenimiento) {
            this.tipoMantenimiento = tipoMantenimiento;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public void setFecha(LocalDate fecha) {
            this.fecha = fecha;
        }

        public BigDecimal getDuracionHoras() {
            return duracionHoras;
        }

        public void setDuracionHoras(BigDecimal duracionHoras) {
            this.duracionHoras = duracionHoras;
        }

        public BigDecimal getCosto() {
            return costo;
        }

        public void setCosto(BigDecimal costo) {
            this.costo = costo;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }
