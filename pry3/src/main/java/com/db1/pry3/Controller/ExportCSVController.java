package com.db1.pry3.Controller;

import com.db1.pry3.Model.FallaReportadaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.db1.pry3.Repository.FallaReportadaRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.List;

@Controller
public class ExportCSVController {

    @Autowired
    private FallaReportadaRepository fallaReportadaRepository;

    @GetMapping("/reportes/fallas/export/csv")
    public ResponseEntity<InputStreamResource> exportCSV() throws IOException {
        List<FallaReportadaModel> fallas = fallaReportadaRepository.findAll(); // O usa el método con filtros según sea necesario
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        // Escribir el encabezado
        writer.write("ID,Descripción,Fecha Reporte,Prioridad,Estado,Máquina");
        writer.newLine();

        // Escribir los datos de las fallas
        for (FallaReportadaModel falla : fallas) {
            writer.write(falla.getId() + "," + falla.getDescripcion() + "," + falla.getFechaReporte() + ","
                    + falla.getPrioridad() + "," + falla.getEstado() + "," + falla.getMaquinaria().getNombre());
            writer.newLine();
        }

        writer.flush();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());

        // Configuración para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=falla_reportada.csv");

        return new ResponseEntity<>(new InputStreamResource(byteArrayInputStream), headers, HttpStatus.OK);
    }
    
}
