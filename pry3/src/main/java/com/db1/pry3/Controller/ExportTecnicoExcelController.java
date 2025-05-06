package com.db1.pry3.Controller;

import com.db1.pry3.Model.TecnicoModel;
import com.db1.pry3.Repository.TecnicoRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
public class ExportTecnicoExcelController {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @GetMapping("/tecnicos/export/excel")
    public ResponseEntity<InputStreamResource> exportExcel() throws Exception {
        List<TecnicoModel> tecnicos = tecnicoRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Técnicos");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Especialidad");
        header.createCell(3).setCellValue("Correo");
        header.createCell(4).setCellValue("Teléfono");

        int rowNum = 1;
        for (TecnicoModel t : tecnicos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getNombre());
            row.createCell(2).setCellValue(t.getEspecialidad());
            row.createCell(3).setCellValue(t.getCorreo());
            row.createCell(4).setCellValue(t.getTelefono());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tecnicos.xlsx");

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }
}
