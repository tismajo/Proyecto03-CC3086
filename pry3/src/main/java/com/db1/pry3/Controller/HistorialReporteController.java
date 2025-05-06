package com.db1.pry3.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.db1.pry3.Model.HistorialFallaModel;
import com.db1.pry3.Repository.HistorialFallaRepository;
import com.db1.pry3.Repository.TecnicoRepository;
import com.db1.pry3.Repository.TipoMantenimientoRepository;

@Controller
@RequestMapping("/reportes/historial")
public class HistorialReporteController {

    @Autowired
    private HistorialFallaRepository historialFallaRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;
    
    @Autowired
    private TipoMantenimientoRepository tipoMantenimientoRepository;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("tiposMantenimiento", tipoMantenimientoRepository.findAll());
        model.addAttribute("historiales", null);
        return "reporte-historial";
    }
    
    @GetMapping("/filtrar")
    public String filtrarHistorial(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long tipoMantenimientoId,
            @RequestParam(required = false) String estadoSolucion,
            Model model) {

    List<HistorialFallaModel> historiales = historialFallaRepository.findAll(); // Fetch all records from the repository
    Map<String, Long> conteoTipos = historiales.stream()
        .collect(Collectors.groupingBy(
            h -> h.getMantenimiento().getTipoMantenimiento().getNombre(), 
            Collectors.counting()
        ));

    List<String> nombresTipos = new ArrayList<>(conteoTipos.keySet());
    List<Long> valoresConteo = new ArrayList<>(conteoTipos.values());

    model.addAttribute("tiposMantenimientoNombres", nombresTipos);
    model.addAttribute("conteoTiposMantenimiento", valoresConteo);

        model.addAttribute("tiposMantenimientoNombres", conteoTipos.keySet());
        model.addAttribute("conteoTiposMantenimiento", conteoTipos.values());

        // Mantener los datos de los selectores tras filtrar
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("tiposMantenimiento", tipoMantenimientoRepository.findAll());
        model.addAttribute("historiales", historiales);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("tecnicoId", tecnicoId);
        model.addAttribute("tipoMantenimientoId", tipoMantenimientoId);
        model.addAttribute("estadoSolucion", estadoSolucion);

        return "reporte-historial";
    }
}