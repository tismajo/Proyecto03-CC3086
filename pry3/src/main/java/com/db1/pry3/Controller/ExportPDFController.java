package com.db1.pry3.Controller;

import com.db1.pry3.Model.FallaReportadaModel;
import com.db1.pry3.Repository.FallaReportadaRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ExportPDFController {

    @Autowired
    private FallaReportadaRepository fallaRepo;

    @GetMapping("/reportes/fallas/export/pdf")
    public ResponseEntity<InputStreamResource> exportPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long departamentoId
    ) throws IOException, DocumentException {

        if (prioridad != null && prioridad.trim().isEmpty()) prioridad = null;
        if (estado != null && estado.trim().isEmpty()) estado = null;

        List<FallaReportadaModel> fallas = fallaRepo.findFallasByFilters(
                fechaInicio, fechaFin, prioridad, estado, departamentoId
        );

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        document.add(new Paragraph("Reporte de Fallas Reportadas"));
        document.add(new Paragraph("\n"));

        for (FallaReportadaModel falla : fallas) {
            document.add(new Paragraph("ID: " + falla.getId()));
            document.add(new Paragraph("Descripción: " + falla.getDescripcion()));
            document.add(new Paragraph("Fecha Reporte: " + falla.getFechaReporte()));
            document.add(new Paragraph("Prioridad: " + falla.getPrioridad()));
            document.add(new Paragraph("Estado: " + falla.getEstado()));
            document.add(new Paragraph("Máquina: " + falla.getMaquinaria().getNombre()));
            document.add(new Paragraph("\n"));
        }

        document.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=falla_reportada.pdf");

        return new ResponseEntity<>(new InputStreamResource(byteArrayInputStream), headers, HttpStatus.OK);
    }
}
