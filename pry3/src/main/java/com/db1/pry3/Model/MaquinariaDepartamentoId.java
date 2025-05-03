package com.db1.pry3.Model;

import java.io.Serializable;

import jakarta.persistence.*;

@Embeddable
public class MaquinariaDepartamentoId implements Serializable{
    @Column(name = "maquinaria_id")
    private Long maquinariaId;

    @Column(name = "departamento_id")
    private Long departamentoId;

    public Long getMaquinariaId() {
        return maquinariaId;
    }

    public void setMaquinariaId(Long maquinariaId) {
        this.maquinariaId = maquinariaId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }
}
