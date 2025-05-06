package com.db1.pry3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.db1.pry3.Model.TecnicoModel;
import com.db1.pry3.Repository.TecnicoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes/tecnicos")
public class TecnicoController {
    @Autowired
    private TecnicoRepository tecnicoRepository;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("tecnicos", null); // no mostrar nada al inicio
        return "reporte-tecnicos";
    }

    @GetMapping("/filtrar")
    public String filtrarTecnicos(@RequestParam(required = false) String nombre,
                                  @RequestParam(required = false) String especialidad,
                                  @RequestParam(required = false) String correo,
                                  @RequestParam(required = false) String telefono,
                                  Model model) {

        List<TecnicoModel> tecnicos = tecnicoRepository.findAll().stream()
            .filter(t -> (nombre == null || t.getNombre().toLowerCase().contains(nombre.toLowerCase())))
            .filter(t -> (especialidad == null || t.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase())))
            .filter(t -> (correo == null || t.getCorreo().toLowerCase().contains(correo.toLowerCase())))
            .filter(t -> (telefono == null || t.getTelefono().toLowerCase().contains(telefono.toLowerCase())))
            .collect(Collectors.toList());

        model.addAttribute("tecnicos", tecnicos);
        model.addAttribute("nombre", nombre);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("correo", correo);
        model.addAttribute("telefono", telefono);

        return "reporte-tecnicos";
}
}