package com.db1.pry3.Controller;

import com.db1.pry3.Model.TecnicoModel;
import com.db1.pry3.Repository.TecnicoRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
public class ExportTecnicoPDFController {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @GetMapping("/tecnicos/export/pdf")
    public ResponseEntity<InputStreamResource> exportPDF() throws DocumentException {
        List<TecnicoModel> tecnicos = tecnicoRepository.findAll();

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        document.add(new Paragraph("Reporte de Técnicos"));
        document.add(new Paragraph("\n"));

        for (TecnicoModel t : tecnicos) {
            document.add(new Paragraph("ID: " + t.getId()));
            document.add(new Paragraph("Nombre: " + t.getNombre()));
            document.add(new Paragraph("Especialidad: " + t.getEspecialidad()));
            document.add(new Paragraph("Correo: " + t.getCorreo()));
            document.add(new Paragraph("Teléfono: " + t.getTelefono()));
            document.add(new Paragraph("\n"));
        }

        document.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tecnicos.pdf");

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }
}
