package com.db1.pry3.Controller;

import com.db1.pry3.Model.FallaReportadaModel;
import com.db1.pry3.Repository.FallaReportadaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class ExportExcelController {

    @Autowired
    private FallaReportadaRepository fallaReportadaRepository;

    @GetMapping("/reportes/fallas/export/excel")
    public ResponseEntity<InputStreamResource> exportExcel() throws IOException {
        // Obtener las fallas desde el repositorio
        List<FallaReportadaModel> fallas = fallaReportadaRepository.findAll();
        
        // Crear un libro de trabajo de Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Fallas Reportadas");

        // Crear encabezado
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Descripción");
        headerRow.createCell(2).setCellValue("Fecha Reporte");
        headerRow.createCell(3).setCellValue("Prioridad");
        headerRow.createCell(4).setCellValue("Estado");
        headerRow.createCell(5).setCellValue("Máquina");

        // Llenar las filas con los datos de las fallas
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

        // Convertir a ByteArrayInputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());

        // Configuración para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=falla_reportada.xlsx");

        return new ResponseEntity<>(new InputStreamResource(byteArrayInputStream), headers, HttpStatus.OK);
    }
}
