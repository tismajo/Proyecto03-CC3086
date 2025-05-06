package com.db1.pry3.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.db1.pry3.Model.FallaReportadaModel;
import com.db1.pry3.Model.MantenimientoModel;
import com.db1.pry3.Model.ProgramacionMantenimientoModel;
import com.db1.pry3.Repository.FallaReportadaRepository;
import com.db1.pry3.Repository.MantenimientoRepository;
import com.db1.pry3.Repository.ProgramacionMantenimientoRepository;
import com.db1.pry3.Repository.TipoMaquinariaRepository;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private FallaReportadaRepository fallaRepo;

    @Autowired
    private TipoMaquinariaRepository tipoMaquinariaRepository;

    @Autowired
    private ProgramacionMantenimientoRepository programacionRepo;

    @Autowired
    private MantenimientoRepository mantenimientoRepo;

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

    if (prioridad != null && prioridad.trim().isEmpty())
        prioridad = null;
    if (estado != null && estado.trim().isEmpty())
        estado = null;

    List<FallaReportadaModel> fallas = fallaRepo.findFallasByFilters(
            fechaInicio, fechaFin, prioridad, estado, departamentoId);

    model.addAttribute("fallas", fallas);

    // Agrega nuevamente los filtros al modelo para que se mantengan en el HTML
    model.addAttribute("fechaInicio", fechaInicio);
    model.addAttribute("fechaFin", fechaFin);
    model.addAttribute("prioridad", prioridad);
    model.addAttribute("estado", estado);
    model.addAttribute("departamentoId", departamentoId);

    // Conteo para gráfico
    Map<String, Long> conteo = fallas.stream()
            .collect(Collectors.groupingBy(FallaReportadaModel::getPrioridad, Collectors.counting()));

    List<Long> conteoPrioridades = List.of(
            conteo.getOrDefault("Alta", 0L),
            conteo.getOrDefault("Media", 0L),
            conteo.getOrDefault("Baja", 0L));

    model.addAttribute("conteoPrioridades", conteoPrioridades);

    return "reporte-fallas";
}

@GetMapping("/programados-vs-realizados")
public String mostrarReporteProgramadosVsRealizados(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
        @RequestParam(required = false) Long tipoMaquinariaId,
        Model model) {

    // Datos para los selectores
    model.addAttribute("tiposMaquinaria", tipoMaquinariaRepository.findAll());

    // Lógica para comparar programados vs. realizados
    List<ProgramacionMantenimientoModel> programados = programacionRepo.findByFechasAndTipo(fechaInicio, fechaFin, tipoMaquinariaId);
    List<MantenimientoModel> realizados = mantenimientoRepo.findByFechasAndTipo(fechaInicio, fechaFin, tipoMaquinariaId);

    // Datos para el gráfico
    Map<String, Long> datosGrafico = Map.of(
        "Programados", (long) programados.size(),
        "Realizados", (long) realizados.size()
    );

    model.addAttribute("datosGrafico", datosGrafico);
    return "reporte-programados-vs-realizados";
}
}
