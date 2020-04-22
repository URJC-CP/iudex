package com.example.aplicacion.services;

import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private static final int BUFFER_SIZE = 4096;


    public void addProblem(String nombre, List<InNOut> entrada, List<InNOut>  salidaCorrecta, List<InNOut> codigoCorrecto, List<InNOut>  entradaVisible, List<InNOut>  salidaVisible ){
        problemRepository.save(new Problem(nombre, entrada, salidaCorrecta, codigoCorrecto, entradaVisible, salidaVisible));
    }

    @PostConstruct
    public void addProblemZip() throws IOException {
        Problem problem = new Problem();

        generateProblemformZip(problem);

        problemRepository.save(problem);

    }

    private void generateProblemFromZip2(Problem problem) throws IOException {
        ZipFile zipFile = new ZipFile(new File("DOCKERS/PROBLEMA.zip"));

        Enumeration<ZipArchiveEntry> it = zipFile.getEntriesInPhysicalOrder();

    }
    private void generateProblemformZip(Problem problem) throws IOException {


        //Mapa que se encarga de revisar que toda entrada tenga salida y no haya ninguna con nombre repetido
        Map<String, List<String>> mapaRevisionEntradas = new HashMap<>();
        File file = new File("DOCKERS/PROBLEMA.zip");
        //Guardamos el nombre del problema
        problem.setNombreEjercicio(file.getName().split("\\.")[0]);

        //Empezamos a descomprimir
        ZipInputStream zipFile = new ZipInputStream(new FileInputStream(file));
        ZipEntry zipEntry = zipFile.getNextEntry();

        Pattern p = Pattern.compile("(.+)/(.+)/(.+)\\.(.+)$");

        while (zipEntry != null) {
            String nombreZip = zipEntry.getName();

            //ER para gestionar los path
            Matcher m = p.matcher(nombreZip);
            if (m.matches()){
                //Objetemos el primer gruo data
                String path1 = m.group(1);
                String path2 = m.group(2);
                String filename = m.group(3);
                String extension = m.group(4);

                //Guardamos los archivos de prueba
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

            zipEntry = zipFile.getNextEntry();
        }
        zipFile.closeEntry();
        zipFile.close();

        try {
            checkMap(mapaRevisionEntradas);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        for (List<String> aux:      laux ) {
            if(aux.size()!=2){
                //Significa que algo ha fallado
                if(aux.get(0).endsWith(".in")){
                    throw new Exception("El ZIP tiene fallos, la entrada " + aux.get(0) +  " no tiene salida");
                }else if(aux.get(0).endsWith(".ans")) {
                    throw new Exception("El ZIP tiene fallos, la salida " + aux.get(0) +  " no tiene entrada");

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
        salida.deleteCharAt(salida.length()-1);
        return salida.toString();
    }


    private void checkPila(Stack<String> pila, boolean esAns, String path, String nombre ) throws Exception {
        if(esAns){
            if(!pila.empty()){
                throw new Exception("El ZIP tiene fallos, la salida " + pila.peek()+ " no tiene entrada");
            }//si esta bien
            else {
                pila.push(path+"/"+nombre);
            }
        }
        //Si es in
        else{
            if(pila.empty()){
                throw new Exception("El ZIP tiene fallos, la entrada " +path+"/"+nombre+ " no tiene salida");
            }
            else {
                String aux = pila.pop();
                if(aux.equals(path+"/"+nombre)){
                    //todo perfecto continua la ejecucion
                }
                else{
                    throw new Exception("El ZIP tiene fallos, la entrada " +path+"/"+nombre+ " no corresponde con la salida " +aux);
                }
            }
        }

    }
    public List<Problem> getNProblemas(int n){
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);

        return problemRepository.findAll();
    }
    public List<Problem> getAllProblemas(){
        return problemRepository.findAll();
    }

}
