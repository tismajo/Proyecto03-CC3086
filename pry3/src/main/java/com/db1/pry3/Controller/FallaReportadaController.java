package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.FallaReportadaModel;
import com.db1.pry3.Repository.FallaReportadaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/falla-reportadas")
public class FallaReportadaController {

    @Autowired
    private FallaReportadaRepository fallaReportadaRepository;

    @GetMapping
    public List<FallaReportadaModel> getAllFallas() {
        return fallaReportadaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FallaReportadaModel> getFallaById(@PathVariable Long id) {
        return fallaReportadaRepository.findById(id)
            .map(falla -> ResponseEntity.ok(falla))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public FallaReportadaModel createFalla(@RequestBody FallaReportadaModel falla) {
        return fallaReportadaRepository.save(falla);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FallaReportadaModel> updateFalla(@PathVariable Long id, @RequestBody FallaReportadaModel falla) {
        return fallaReportadaRepository.findById(id).map(existingFalla -> {
            falla.setId(id);
            return ResponseEntity.ok(fallaReportadaRepository.save(falla));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteFalla(@PathVariable Long id) {
        fallaReportadaRepository.deleteById(id);
    }
}
