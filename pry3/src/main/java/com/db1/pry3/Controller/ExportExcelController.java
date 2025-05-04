package com.db1.pry3.Controller;

import com.db1.pry3.Model.FallaReportadaModel;
import com.db1.pry3.Repository.FallaReportadaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ExportExcelController {

    @Autowired
    private FallaReportadaRepository fallaReportadaRepository;

    @GetMapping("/reportes/fallas/export/excel")
public ResponseEntity<InputStreamResource> exportExcel(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
        @RequestParam(required = false) String prioridad,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) Long departamentoId) throws IOException {

    if (prioridad != null && prioridad.trim().isEmpty()) prioridad = null;
    if (estado != null && estado.trim().isEmpty()) estado = null;

    List<FallaReportadaModel> fallas = fallaReportadaRepository.findFallasByFilters(
            fechaInicio, fechaFin, prioridad, estado, departamentoId);

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Fallas Reportadas");

    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("ID");
    headerRow.createCell(1).setCellValue("Descripción");
    headerRow.createCell(2).setCellValue("Fecha Reporte");
    headerRow.createCell(3).setCellValue("Prioridad");
    headerRow.createCell(4).setCellValue("Estado");
    headerRow.createCell(5).setCellValue("Máquina");

    int rowNum = 1;
    for (FallaReportadaModel falla : fallas) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(falla.getId());
        row.createCell(1).setCellValue(falla.getDescripcion());
        row.createCell(2).setCellValue(falla.getFechaReporte().toString());
        row.createCell(3).setCellValue(falla.getPrioridad());
        row.createCell(4).setCellValue(falla.getEstado());
        row.createCell(5).setCellValue(falla.getMaquinaria().getNombre());
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=falla_reportada.xlsx");

    return new ResponseEntity<>(new InputStreamResource(byteArrayInputStream), headers, HttpStatus.OK);
}

}
