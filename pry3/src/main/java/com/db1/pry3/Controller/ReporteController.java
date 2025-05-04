package com.db1.pry3.Controller;

import com.db1.pry3.Model.*;
import com.db1.pry3.Repository.*;

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

        List<FallaReportadaModel> fallas = fallaRepo.findFallasByFilters(fechaInicio, fechaFin, prioridad, estado, departamentoId);
        model.addAttribute("fallas", fallas);
        return "reporte-fallas";
    }
}
