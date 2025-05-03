package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.TecnicoModel;
import com.db1.pry3.Repository.TecnicoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @GetMapping
    public List<TecnicoModel> getAllTecnicos() {
        return tecnicoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TecnicoModel> getTecnicoById(@PathVariable Long id) {
        return tecnicoRepository.findById(id)
            .map(tecnico -> ResponseEntity.ok(tecnico))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public TecnicoModel createTecnico(@RequestBody TecnicoModel tecnico) {
        return tecnicoRepository.save(tecnico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TecnicoModel> updateTecnico(@PathVariable Long id, @RequestBody TecnicoModel tecnico) {
        return tecnicoRepository.findById(id).map(existingTecnico -> {
            tecnico.setId(id);
            return ResponseEntity.ok(tecnicoRepository.save(tecnico));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteTecnico(@PathVariable Long id) {
        tecnicoRepository.deleteById(id);
    }
}
