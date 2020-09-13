package com.example.aplicacion.services;

import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Service
public class ProblemService {
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private InNOutRepository inNOutRepository;
    @Autowired
    private ZipHandlerService zipHandlerService;
    @Autowired
    private ProblemValidatorService problemValidatorService;
    @Autowired
    private TeamRepository teamRepository;


    public void addProblem(String nombre, List<InNOut> entrada, List<InNOut>  salidaCorrecta, List<InNOut> codigoCorrecto, List<InNOut>  entradaVisible, List<InNOut>  salidaVisible ){
        problemRepository.save(new Problem(nombre, entrada, salidaCorrecta, codigoCorrecto, entradaVisible, salidaVisible));
    }


    public String addProblemFromZip(String nombreFichero, InputStream inputStream, String nombreEquipo, String nombreProblema) throws Exception {
            Problem problem = new Problem();
            if(!nombreProblema.equals("")){
                problem.setNombreEjercicio(nombreProblema);
            }
            zipHandlerService.generateProblemFromZIP(problem, nombreFichero, inputStream);

            Team team =teamRepository.findByNombreEquipo(nombreEquipo);
            if (team == null) {
                return "TEAM NOT FOUND";
            }
            problem.setEquipoPropietario(team);
            problemRepository.save(problem);

            problemValidatorService.validateProblem(problem);
            return "OK";

    }

    private boolean problemDuplicated(String nombre){
        return problemRepository.existsByNombreEjercicio(nombre);
    }


    public List<Problem> getNProblemas(int n){
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);

        return problemRepository.findAll();
    }

    public List<Problem> getAllProblemas(){
        return problemRepository.findAll();
    }

}
