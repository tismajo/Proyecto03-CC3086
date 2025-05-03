package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.MaquinariaModel;
import com.db1.pry3.Repository.MaquinariaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/maquinarias")
public class MaquinariaController {

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @GetMapping
    public List<MaquinariaModel> getAllMaquinarias() {
        return maquinariaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinariaModel> getMaquinariaById(@PathVariable Long id) {
        return maquinariaRepository.findById(id)
            .map(maquinaria -> ResponseEntity.ok(maquinaria))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public MaquinariaModel createMaquinaria(@RequestBody MaquinariaModel maquinaria) {
        return maquinariaRepository.save(maquinaria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaquinariaModel> updateMaquinaria(@PathVariable Long id, @RequestBody MaquinariaModel maquinaria) {
        return maquinariaRepository.findById(id).map(existingMaquinaria -> {
            maquinaria.setId(id);
            return ResponseEntity.ok(maquinariaRepository.save(maquinaria));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteMaquinaria(@PathVariable Long id) {
        maquinariaRepository.deleteById(id);
    }
}
