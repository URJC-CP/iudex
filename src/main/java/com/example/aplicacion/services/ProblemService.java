package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


@Service
public class ProblemService {
    @Autowired
    private ProblemRepository problemRepository;

    private static final int BUFFER_SIZE = 4096;


    public void addProblem(String nombre, List<String> entrada, List<String>  salidaCorrecta, List<String> codigoCorrecto, List<String>  entradaVisible, List<String>  salidaVisible ){
        problemRepository.save(new Problem(nombre, entrada, salidaCorrecta, codigoCorrecto, entradaVisible, salidaVisible));
    }

    public void addProblemZip(){
        //save the zip
        File zipFile =null;
        zipFile = new File("DOCKERS/PROBLEMA.zip");


    }


    public List<Problem> getNProblemas(int n){
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);

        return problemRepository.findAll();
    }
    public List<Problem> getAllProblemas(){
        return problemRepository.findAll();
    }

}
