package com.db1.pry3.Controller;

import com.db1.pry3.Model.TecnicoModel;
import com.db1.pry3.Repository.TecnicoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.util.List;

@Controller
public class ExportTecnicoController {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @GetMapping("/tecnicos/export/csv")
    public ResponseEntity<InputStreamResource> exportTecnicosCSV() throws IOException {
        List<TecnicoModel> tecnicos = tecnicoRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        writer.write("ID,Nombre,Especialidad,Correo,Teléfono");
        writer.newLine();

        for (TecnicoModel tecnico : tecnicos) {
            writer.write(tecnico.getId() + "," +
                         tecnico.getNombre() + "," +
                         tecnico.getEspecialidad() + "," +
                         tecnico.getCorreo() + "," +
                         tecnico.getTelefono());
            writer.newLine();
        }

        writer.flush();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tecnicos.csv");

        return new ResponseEntity<>(new InputStreamResource(byteArrayInputStream), headers, HttpStatus.OK);
    }
}
