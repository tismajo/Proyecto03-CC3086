package com.db1.pry3.Controller;

import com.db1.pry3.Model.*;
import com.db1.pry3.Repository.*;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private FallaReportadaRepository fallaRepo;

    @GetMapping("/fallas")
    public String mostrarFormulario(Model model) {
        model.addAttribute("fallas", null);
        return "reporte-fallas";
    }

    @PostMapping("/fallas")
public String filtrarFallas(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
        @RequestParam(required = false) String prioridad,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) Long departamentoId,
        Model model) {

    if (prioridad != null && prioridad.trim().isEmpty()) prioridad = null;
    if (estado != null && estado.trim().isEmpty()) estado = null;

    List<FallaReportadaModel> fallas = fallaRepo.findFallasByFilters(
            fechaInicio, fechaFin, prioridad, estado, departamentoId);

    model.addAttribute("fallas", fallas);

    // Agrupar por prioridad
    Map<String, Long> conteo = fallas.stream()
        .collect(Collectors.groupingBy(FallaReportadaModel::getPrioridad, Collectors.counting()));

    // Asegurar que estén en orden: Alta, Media, Baja
    List<Long> conteoPrioridades = List.of(
        conteo.getOrDefault("Alta", 0L),
        conteo.getOrDefault("Media", 0L),
        conteo.getOrDefault("Baja", 0L)
    );

    model.addAttribute("conteoPrioridades", conteoPrioridades);

    return "reporte-fallas";
}
}
