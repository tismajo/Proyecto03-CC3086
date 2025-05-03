package com.db1.pry3.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.db1.pry3.Model.MantenimientoRepuestoIdModel;
import com.db1.pry3.Model.MantenimientoRepuestoModel;

public interface MantenimientoRepuestoRepository extends JpaRepository<MantenimientoRepuestoModel, MantenimientoRepuestoIdModel> {}
