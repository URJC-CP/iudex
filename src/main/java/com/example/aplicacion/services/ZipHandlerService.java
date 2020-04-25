package com.example.aplicacion.services;

import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipHandlerService {

    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private InNOutRepository inNOutRepository;


    public Problem generateProblemFromZIP(Problem problem, String problemName, InputStream inputStream) throws IOException {

        //Mapa que se encarga de revisar que toda entrada tenga salida y no haya ninguna con nombre repetido
        Map<String, List<String>> mapaRevisionEntradas = new HashMap<>();

        //Guardamos el nombre del problema
        problem.setNombreEjercicio(problemName.split("\\.")[0]);

        //Empezamos a descomprimir
        ZipInputStream zipFile = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipFile.getNextEntry();

        //Patron que parsea los apth de los archivos
        Pattern p = Pattern.compile("(.+)/(.+)/(.+)/(.+)\\.(.+)$");
        Pattern p2 = Pattern.compile("(.+)/(.+)\\.(.+)$");

        while (zipEntry != null) {
            String nombreZip = zipEntry.getName();

            //ER para gestionar los path
            Matcher m = p.matcher(nombreZip);
            //Si entra significa q es de la categoria /algo/algo/fichero
            if (m.matches()){
                //Objetemos el primer gruo data
                String path1 = m.group(2);
                String path2 = m.group(3);
                String filename = m.group(4);
                String extension = m.group(5);



                //Si es ES de ejemplo
                if(path2.equals("sample")){
                    if(extension.equals("ans")){
                        //Cuando entra un ans SIEMPRE tiene que estar la pila vacia, si no lanza error
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        //Leemos el archivo zip a string
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        inNOutRepository.save(inNOut);
                        problem.addSalidaVisible(inNOut);
                    }else if(extension.equals("in")){
                        //revisamos q el zip este bien
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        inNOutRepository.save(inNOut);
                        problem.addEntradaVisible(inNOut);
                    }
                }
                //Si es ES secreta
                else if(path2.equals("secret")){
                    if(extension.equals("ans")){
                        //Cuando entra un ans SIEMPRE tiene que estar la pila vacia, si no lanza error
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        //Leemos el archivo zip a string
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        inNOutRepository.save(inNOut);
                        problem.addSalidaOculta(inNOut);
                    }else if(extension.equals("in")){
                        //revisamos q el zip este bien
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        inNOutRepository.save(inNOut);
                        problem.addEntradaOculta(inNOut);
                    }
                }
            }
            //Si no es de esa carpeta siginfica q esta en la carpeta base
            else {
                Matcher m2 =p2.matcher(nombreZip);
                if(m2.matches()){
                    String name = m2.group(2);
                    String extension = m2.group(3);
                    //SI es el archivo de configuracion jaml
                    if(extension.equals("yaml")&&name.equals("problem")){
                        Yaml yaml = new Yaml();
                        String aux = convertZipToString(zipFile);
                        Map<String, Object> obj = yaml.load(aux);
                        rellenaElYuml(obj, problem);
                    }
                }
            }

            zipEntry = zipFile.getNextEntry();
        }
        zipFile.closeEntry();
        zipFile.close();

        try {
            checkMap(mapaRevisionEntradas);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return problem;
    }

    public Problem generateProblemFromZIP(Problem problem, MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(convFile);
        return generateProblemFromZIP(problem, multipartFile.getOriginalFilename(), multipartFile.getInputStream());
    }

    private void addStringToMap(Map<String, List<String>> mapa, String nombre, String extension){
        if (mapa.containsKey(nombre)){
            List<String> laux = mapa.get(nombre);
            laux.add(nombre+"."+extension);
            mapa.put(nombre, laux);
        }else {
            List<String> laux = new ArrayList<>();
            laux.add(nombre+"."+extension);
            mapa.put(nombre, laux);
        }
    }


    //Funcion que checkea que el mapa se haya completado y por lo tanto la entrada sea correcta
    private void checkMap(Map<String, List<String>> mapa) throws Exception {
        Collection<List<String>> laux = mapa.values();
        for (List<String> aux : laux) {
            if (aux.size() != 2) {
                //Significa que algo ha fallado
                if (aux.get(0).endsWith(".in")) {
                    throw new Exception("El ZIP tiene fallos, la entrada " + aux.get(0) + " no tiene salida");
                } else if (aux.get(0).endsWith(".ans")) {
                    throw new Exception("El ZIP tiene fallos, la salida " + aux.get(0) + " no tiene entrada");

                }
            }
        }
    }

    //clase que coge un zipInput y lo convierte en string a traves del zipentry
    private String convertZipToString( ZipInputStream zipFile) throws IOException {
        StringBuilder salida = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile));

        String linea;
        while ((linea = reader.readLine())!=null){
            salida.append(linea+"\n");
        }
        //borramos el ultimo salto de linea
        if(salida.length()>=1){
            salida.deleteCharAt(salida.length()-1);
        }
        return salida.toString();
    }
    private  void rellenaElYuml(Map<String, Object> mapa, Problem problem){
        Object aux;
        //Anyadido extra
        if((aux =mapa.get("name")) !=null){
            problem.setNombreEjercicio((aux.toString()));
        }

        if((aux =mapa.get("uuid")) !=null){
            problem.setId(Long.parseLong(aux.toString()));
        }

        if((aux =mapa.get("author")) !=null){
            problem.setAutor(aux.toString());
        }
        if((aux =mapa.get("source")) !=null){
            problem.setSource(aux.toString());
        }
        if((aux =mapa.get("source_url")) !=null){
            problem.setSource_url(aux.toString());
        }
        if((aux =mapa.get("license")) !=null){
            problem.setLicense(aux.toString());
        }
        if((aux =mapa.get("rights_owner")) !=null){
            problem.setRights_owner(aux.toString());
        }
        //Es un map
        if((aux =mapa.get("limits")) !=null){
            Map<String, Object> limitsMap = (LinkedHashMap<String, Object>) aux;
            if((aux= limitsMap.get("time_multiplier"))!=null){
                problem.setLimit_time_multiplier(aux.toString());
            }
            if((aux= limitsMap.get("time_safety_margin"))!=null){
                problem.setLimit_time_safety_margin(aux.toString());
            }
            if((aux= limitsMap.get("memory"))!=null){
                problem.setLimit_memory(aux.toString());
            }
            if((aux= limitsMap.get("output"))!=null){
                problem.setLimit_output(aux.toString());
            }
            if((aux= limitsMap.get("code"))!=null){
                problem.setLimit_code(aux.toString());
            }
            if((aux= limitsMap.get("compilation_time"))!=null){
                problem.setLimit_compilation_time(aux.toString());
            }
            if((aux= limitsMap.get("compilation_memory"))!=null){
                problem.setLimit_compilation_memory(aux.toString());
            }
            if((aux= limitsMap.get("validation_time"))!=null){
                problem.setLimit_validation_time(aux.toString());
            }
            if((aux= limitsMap.get("validation_memory"))!=null){
                problem.setLimit_validation_memory(aux.toString());
            }
            if((aux= limitsMap.get("validation_output"))!=null){
                problem.setLimit_validation_output(aux.toString());
            }
        }
        if((aux =mapa.get("validation")) !=null){
            problem.setValidation(aux.toString());
        }
        if((aux =mapa.get("validator_flags")) !=null){
            problem.setValidation_flags(aux.toString());
        }


    }
}
