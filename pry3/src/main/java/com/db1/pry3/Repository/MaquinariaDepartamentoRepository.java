package com.db1.pry3.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.db1.pry3.Model.MaquinariaDepartamentoId;
import com.db1.pry3.Model.MaquinariaDepartamentoModel;

public interface MaquinariaDepartamentoRepository extends JpaRepository<MaquinariaDepartamentoModel, MaquinariaDepartamentoId> {}
