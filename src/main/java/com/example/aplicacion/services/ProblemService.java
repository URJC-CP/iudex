package com.example.aplicacion.services;

import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


@Service
public class ProblemService {
    @Autowired
    private ProblemRepository problemRepository;


    private static final int BUFFER_SIZE = 4096;


    public void addProblem(String nombre, List<InNOut> entrada, List<InNOut>  salidaCorrecta, List<InNOut> codigoCorrecto, List<InNOut>  entradaVisible, List<InNOut>  salidaVisible ){
        problemRepository.save(new Problem(nombre, entrada, salidaCorrecta, codigoCorrecto, entradaVisible, salidaVisible));
    }

    @PostConstruct
    public void addProblemZip() throws IOException {
        Problem problem = new Problem();



    }

    private void generateProblemformZip(Problem problem) throws IOException {

        File file = new File("DOCKERS/PROBLEMA.zip");
        //Guardamos el nombre del problema
        problem.setNombreEjercicio(file.getName().split("\\.")[0]);

        //Empezamos a descomprimir
        ZipInputStream zipFile = new ZipInputStream(new FileInputStream(file));
        ZipEntry zipEntry = zipFile.getNextEntry();

        while (zipEntry != null) {
            String nombreZip = zipEntry.getName();

            //trunyo de Raul
            Pattern p = Pattern.compile("(.+)/(.+)/(.+)\\.(.+)$");
            Matcher m = p.matcher(nombreZip);
            if (m.matches()){
                //Objetemos el primer gruo data
                String path1 = m.group(1);
                String path2 = m.group(2);
                String filename = m.group(3);
                String extension = m.group(4);

                if(path2.equals("sample")){
                    if(extension.equals("ans")){
                        String aux = convertZipToString(zipEntry, zipFile);
                        problem.addEntradaVisible(new InNOut(filename, aux));
                    }else if(extension.equals("in")){
                        String aux = convertZipToString(zipEntry, zipFile);

                    }
                }
            }


            if(nombreZip.startsWith("data/sample/")){
                if(nombreZip.endsWith(".in")){
                    System.out.println(nombreZip+"Es in visible");
                }
            }


            zipEntry = zipFile.getNextEntry();
        }
        zipFile.closeEntry();
        zipFile.close();

    }

    private String convertZipToString(ZipEntry zipEntry, ZipInputStream zipFile) throws IOException {
        String salida = "";

        byte[] data = new byte[BUFFER_SIZE];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int count;
        while((count = zipFile.read(data, 0, BUFFER_SIZE)) !=-1){
            buffer.write(data);
        }
        buffer.flush();
        byte[] bytes = buffer.toByteArray();
        salida = new String(bytes, StandardCharsets.UTF_8);
        return salida;
    }


    public List<Problem> getNProblemas(int n){
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);

        return problemRepository.findAll();
    }
    public List<Problem> getAllProblemas(){
        return problemRepository.findAll();
    }

}
