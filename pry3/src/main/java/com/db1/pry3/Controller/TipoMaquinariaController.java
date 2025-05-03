package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.TipoMaquinariaModel;
import com.db1.pry3.Repository.TipoMaquinariaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-maquinarias")
public class TipoMaquinariaController {

    @Autowired
    private TipoMaquinariaRepository tipoMaquinariaRepository;

    @GetMapping
    public List<TipoMaquinariaModel> getAllTipoMaquinarias() {
        return tipoMaquinariaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoMaquinariaModel> getTipoMaquinariaById(@PathVariable Long id) {
        return tipoMaquinariaRepository.findById(id)
            .map(tipoMaquinaria -> ResponseEntity.ok(tipoMaquinaria))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public TipoMaquinariaModel createTipoMaquinaria(@RequestBody TipoMaquinariaModel tipoMaquinaria) {
        return tipoMaquinariaRepository.save(tipoMaquinaria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoMaquinariaModel> updateTipoMaquinaria(@PathVariable Long id, @RequestBody TipoMaquinariaModel tipoMaquinaria) {
        return tipoMaquinariaRepository.findById(id).map(existingTipoMaquinaria -> {
            tipoMaquinaria.setId(id);
            return ResponseEntity.ok(tipoMaquinariaRepository.save(tipoMaquinaria));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteTipoMaquinaria(@PathVariable Long id) {
        tipoMaquinariaRepository.deleteById(id);
    }
}
