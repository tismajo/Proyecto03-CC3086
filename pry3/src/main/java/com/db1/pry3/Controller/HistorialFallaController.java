package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.HistorialFallaModel;
import com.db1.pry3.Repository.HistorialFallaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/historial-fallas")
public class HistorialFallaController {

    @Autowired
    private HistorialFallaRepository historialFallaRepository;

    // Obtener todos los registros de historial de fallas
    @GetMapping
    public List<HistorialFallaModel> getAllHistorialFallas() {
        return historialFallaRepository.findAll();
    }

    // Obtener un historial de falla por ID
    @GetMapping("/{id}")
    public ResponseEntity<HistorialFallaModel> getHistorialFallaById(@PathVariable Long id) {
        return historialFallaRepository.findById(id)
                .map(historial -> ResponseEntity.ok(historial))
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo historial de falla
    @PostMapping("/add")
    public HistorialFallaModel createHistorialFalla(@RequestBody HistorialFallaModel historial) {
        return historialFallaRepository.save(historial);
    }

    // Actualizar un historial de falla existente
    @PutMapping("/{id}")
    public ResponseEntity<HistorialFallaModel> updateHistorialFalla(@PathVariable Long id, @RequestBody HistorialFallaModel historial) {
        return historialFallaRepository.findById(id).map(existingHistorial -> {
            historial.setId(id);  // Actualizar el ID para la entidad existente
            return ResponseEntity.ok(historialFallaRepository.save(historial));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar un historial de falla por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteHistorialFalla(@PathVariable Long id) {
        return historialFallaRepository.findById(id).map(existingHistorial -> {
            historialFallaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }


}
