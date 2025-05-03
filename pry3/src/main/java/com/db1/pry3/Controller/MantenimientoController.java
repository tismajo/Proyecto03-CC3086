package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.MantenimientoModel;
import com.db1.pry3.Repository.MantenimientoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @GetMapping
    public List<MantenimientoModel> getAllMantenimientos() {
        return mantenimientoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoModel> getMantenimientoById(@PathVariable Long id) {
        return mantenimientoRepository.findById(id)
            .map(mantenimiento -> ResponseEntity.ok(mantenimiento))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public MantenimientoModel createMantenimiento(@RequestBody MantenimientoModel mantenimiento) {
        return mantenimientoRepository.save(mantenimiento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoModel> updateMantenimiento(@PathVariable Long id, @RequestBody MantenimientoModel mantenimiento) {
        return mantenimientoRepository.findById(id).map(existingMantenimiento -> {
            mantenimiento.setId(id);
            return ResponseEntity.ok(mantenimientoRepository.save(mantenimiento));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteMantenimiento(@PathVariable Long id) {
        mantenimientoRepository.deleteById(id);
    }
}
