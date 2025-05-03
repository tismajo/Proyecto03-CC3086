package com.db1.pry3.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "mantenimiento_repuesto")
public class MantenimientoRepuestoModel {
    @EmbeddedId
    private MantenimientoRepuestoIdModel id;

    @ManyToOne
    @MapsId("mantenimientoId")
    @JoinColumn(name = "mantenimiento_id")
    private MantenimientoModel mantenimiento;

    @ManyToOne
    @MapsId("repuestoId")
    @JoinColumn(name = "repuesto_id")
    private RepuestoModel repuesto;

    @Column(name = "cantidad_usada")
    private Integer cantidadUsada;

    public MantenimientoRepuestoIdModel getId() {
        return id;
    }

    public void setId(MantenimientoRepuestoIdModel id) {
        this.id = id;
    }

    public MantenimientoModel getMantenimiento() {
        return mantenimiento;
    }

    public void setMantenimiento(MantenimientoModel mantenimiento) {
        this.mantenimiento = mantenimiento;
    }

    public RepuestoModel getRepuesto() {
        return repuesto;
    }

    public void setRepuesto(RepuestoModel repuesto) {
        this.repuesto = repuesto;
    }

    public Integer getCantidadUsada() {
        return cantidadUsada;
    }

    public void setCantidadUsada(Integer cantidadUsada) {
        this.cantidadUsada = cantidadUsada;
    }


}
