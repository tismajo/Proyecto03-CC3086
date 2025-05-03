package com.db1.pry3.Model;

import java.io.Serializable;

import jakarta.persistence.*;

@Embeddable
public class MantenimientoRepuestoIdModel implements Serializable {
    @Column(name = "mantenimiento_id")
    private Long mantenimientoId;

    @Column(name = "repuesto_id")
    private Long repuestoId;

    public Long getMantenimientoId() {
        return mantenimientoId;
    }

    public void setMantenimientoId(Long mantenimientoId) {
        this.mantenimientoId = mantenimientoId;
    }

    public Long getRepuestoId() {
        return repuestoId;
    }

    public void setRepuestoId(Long repuestoId) {
        this.repuestoId = repuestoId;
    }

}
