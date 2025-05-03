package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.RepuestoModel;
import com.db1.pry3.Repository.RepuestoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/repuestos")
public class RepuestoController {

    @Autowired
    private RepuestoRepository repuestoRepository;

    @GetMapping
    public List<RepuestoModel> getAllRepuestos() {
        return repuestoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepuestoModel> getRepuestoById(@PathVariable Long id) {
        return repuestoRepository.findById(id)
            .map(repuesto -> ResponseEntity.ok(repuesto))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public RepuestoModel createRepuesto(@RequestBody RepuestoModel repuesto) {
        return repuestoRepository.save(repuesto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepuestoModel> updateRepuesto(@PathVariable Long id, @RequestBody RepuestoModel repuesto) {
        return repuestoRepository.findById(id).map(existingRepuesto -> {
            repuesto.setId(id);
            return ResponseEntity.ok(repuestoRepository.save(repuesto));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteRepuesto(@PathVariable Long id) {
        repuestoRepository.deleteById(id);
    }
}
