package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


//Clase que lee el zip y genera el problema adecuado
@Service
public class ZipHandlerService {
    Logger logger = LoggerFactory.getLogger(ZipHandlerService.class);

    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private InNOutRepository inNOutRepository;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private SubmissionProblemValidatorService submissionProblemValidatorService;

    public ProblemString generateProblemFromZIP(Problem problem, String problemName, InputStream inputStream, String contestId, String teamId) throws Exception {
        ProblemString problemString = new ProblemString();

        //Mapa que se encarga de revisar que toda entrada tenga salida y no haya ninguna con nombre repetido
        Map<String, List<String>> mapaRevisionEntradas = new HashMap<>();

        //Guardamos el nombre del problema
        if(problem.getNombreEjercicio()==null){
            problem.setNombreEjercicio(problemName.split("\\.")[0]);
        }
        logger.info("ZIPUNCOMRESS: Se ha anyadido un nuevo zip para descomprimir con el nombre: "+ problem.getNombreEjercicio());

        //Empezamos a descomprimir
        ZipInputStream zipFile = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipFile.getNextEntry();


        //Patron que parsea los apth de los archivos
        Pattern p = Pattern.compile("(.+)/(.+)/(.+)\\.(.+)");
        //Pattern p2 = Pattern.compile("(.+)/(.+)\\.(.+)$");
        Pattern p2 = Pattern.compile("(.+)\\.(.+)$");

        while (zipEntry != null) {
            String nombreZip = zipEntry.getName();

            //ER para gestionar los path
            Matcher m = p.matcher(nombreZip);
            //Si entra significa q es de la categoria /algo/algo/fichero
            if (m.find()){
                //Objetemos el primer gruo data
                //String path = m.group(1);
                String path1 = m.group(1);
                String path2 = m.group(2);
                String filename = m.group(3);
                String extension = m.group(4);

                //Hay que quitar parte del path que no necesitamos CHUCHES/data
                path1 = path1.replaceAll(".+/", "");

                //Si es ES de ejemplo
                if(path1.equals("data") && path2.equals("sample")){
                    if(extension.equals("ans")){
                        //Cuando entra un ans SIEMPRE tiene que estar la pila vacia, si no lanza error
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        //Leemos el archivo zip a string
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        //inNOut.setProblem(problem);
                        //SEBORRATEMPORALMENTEinNOutRepository.save(inNOut);
                        problem.addSalidaVisible(inNOut);
                    }else if(extension.equals("in")){
                        //revisamos q el zip este bien
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        //inNOut.setProblem(problem);
                        //SEBORRATEMPORALMENTEinNOutRepository.save(inNOut);
                        problem.addEntradaVisible(inNOut);
                    }
                }
                //Si es ES secreta
                else if(path1.equals("data") && path2.equals("secret")){
                    if(extension.equals("ans")){
                        //Cuando entra un ans SIEMPRE tiene que estar la pila vacia, si no lanza error
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        //Leemos el archivo zip a string
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        //inNOut.setProblem(problem);
                        //SEBORRATEMPORALMENTEinNOutRepository.save(inNOut);
                        problem.addSalidaOculta(inNOut);
                    }else if(extension.equals("in")){
                        //revisamos q el zip este bien
                        addStringToMap(mapaRevisionEntradas, path2+"/"+filename, extension);
                        String aux = convertZipToString( zipFile);
                        InNOut inNOut = new InNOut(filename, aux);
                        //inNOut.setProblem(problem);
                        //SEBORRATEMPORALMENTEinNOutRepository.save(inNOut);
                        problem.addEntradaOculta(inNOut);
                        logger.info("ZIPUNCOMRESS: anyadido una nueva entrada de datos de prueba para el problema "+ problemName);
                    }
                }
                //Buscamos ahora las submission
                if ("submissions".equals(path1)){
                    String resultadoEsperado ="";

                    if( path2.equals("accepted")){
                        resultadoEsperado = "accepted";

                    }
                    else if (path2.equals("wrong_answer")){
                        resultadoEsperado = "wrong_answer";
                    }
                    else if (path2.equals("run_time_error")){
                        resultadoEsperado = "run_time_error";
                    }
                    else {
                        logger.warn("ZIPHANDLER: Se ha introducido una carpeta dentro de submissions con un nombre no compatible");
                        break;
                    }

                    //Buscamos el lenguaje que es analizando la extension
                    String lenguaje = selectLenguaje(extension);
                    if(lenguaje==null){
                        //throw new Exception("ZIPHANDLER: " +extension+ "NO es un lenguaje soportado");
                        logger.error("ZIPHANDLER: " +extension+ "NO es un lenguaje soportado");

                    }
                    else {
                        logger.info("ZIPCOMPRESS: Detectado el lenguaje "+lenguaje);
                        //obtenemos el string del codigo
                        String aux = convertZipToString( zipFile);
                        //Tendremos que crear una submission y comprobar que el resultado de esta sea correcta
                        SubmissionProblemValidator submissionProblemValidator = submissionProblemValidatorService.createSubmissionNoExecute(aux, problem, lenguaje, filename,  resultadoEsperado, contestId, teamId);
                        //Anyadimos el submissionproblemvalidator al problema
                        problem.getSubmissionProblemValidators().add(submissionProblemValidator);
                        logger.info("ZIPCOMPRESS: Anyadido una nueva submission para el problema "+ problemName);
                    }


                }
            }

            //Si no es de esa carpeta siginfica q esta en la carpeta base
            else {
                Matcher m2 =p2.matcher(nombreZip);
                if(m2.matches()){
                    String name = m2.group(1);
                    String extension = m2.group(2);
                    //SI es el archivo de configuracion jaml
                    if(extension.equals("yaml")&&name.equals("problem")){
                        Yaml yaml = new Yaml();
                        String aux = convertZipToString(zipFile);
                        Map<String, Object> obj = yaml.load(aux);
                        rellenaElYuml(obj, problem);
                    }
                    if(extension.equals("ini")){
                        String aux = convertZipToString(zipFile);
                        rellenaElYumlConIni(aux, problem);
                    }
                    if (extension.equals("pdf")){
                        problem.setDocumento(convertZipToByte(zipFile));
                    }
                }
            }

            zipEntry = zipFile.getNextEntry();
        }
        zipFile.closeEntry();
        zipFile.close();


        //Creamos los RESULTs
        for(Submission submission:problem.getSubmissions()){
            submissionService.creaResults(submission, problem, submission.getCodigo(),submission.getLanguage());

        }
        //generamos el hash
        String hash = problem.generaHash();
        rellenaSubmissionConProblemHash(problem);
        //Comprobamos que todas las entradas tienen su salida etcetc. SI HAY error, nos lo comunicara el string y cortamos la subida.
        String checkMap = checkMap(mapaRevisionEntradas);
        if (!checkMap.equals("OK")){
            //Si hay algun fallo borramos todos los inNOut que hemos creado
            //borraInNOut(problem);
            problemString.setSalida(checkMap);
        }




        problemString.setProblem(problem);
        return problemString;
    }


    public String selectLenguaje(String lenguaje){
        if(lenguaje.equals("java")){
            return "java";
        }
        else if(lenguaje.equals("py")){
            return "python3";
        }
        else if(lenguaje.equals("cpp") || lenguaje.equals("c++")){
            return "cpp";
        }
        else {
            return null;
        }
    }

    /*
    public Problem generateProblemFromZIP(Problem problem, MultipartFile multipartFile) throws Exception {
        File convFile = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(convFile);
        return generateProblemFromZIP(problem, multipartFile.getOriginalFilename(), multipartFile.getInputStream());
    }
     */
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
    private String checkMap(Map<String, List<String>> mapa) throws Exception {
        Collection<List<String>> laux = mapa.values();
        for (List<String> aux : laux) {
            if (aux.size() != 2) {
                //Significa que algo ha fallado
                if (aux.get(0).endsWith(".in")) {
                    //throw new Exception("El ZIP tiene fallos, la entrada " + aux.get(0) + " no tiene salida");
                    return ("El ZIP tiene fallos, la entrada " + aux.get(0) + " no tiene salida");
                } else if (aux.get(0).endsWith(".ans")) {
                    //throw new Exception("El ZIP tiene fallos, la salida " + aux.get(0) + " no tiene entrada");
                    return ("El ZIP tiene fallos, la salida " + aux.get(0) + " no tiene entrada");
                }
            }
        }
        return "OK";
    }

    private void borraInNOut(Problem problem){
        for(InNOut aux:problem.getEntradaVisible()){
            inNOutRepository.delete(aux);
            //problem.removeEntradaVisible(aux);
        }
        for(InNOut aux:problem.getSalidaVisible()){
            //problem.removeSalidaVisible(aux);
            inNOutRepository.delete(aux);
        }
        for(InNOut aux:problem.getEntradaOculta()){
            //problem.removeEntradaOculta(aux);
            inNOutRepository.delete(aux);
        }
        for(InNOut aux:problem.getSalidaOculta()){
            //problem.removeSalidaOculta(aux);
            inNOutRepository.delete(aux);
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
    private byte[] convertZipToByte(ZipInputStream zipInputStream) throws IOException {
        byte[] bits = zipInputStream.readAllBytes();
        return bits;
    }
    private void rellenaSubmissionConProblemHash(Problem problem){
        for(Submission submission: problem.getSubmissions()){
            submission.generaHashProblema();
        }
    }

    private  void rellenaElYumlConIni(String configuracion, Problem problem){
        String split[] = configuracion.split("\\r?\\n");
        Pattern pattern = Pattern.compile("(.+)=(.+)");
        String propiedad, valor;
        for(String aux :split){
            Matcher m = pattern.matcher(aux);
            if(m.find()){
                propiedad = m.group(1);
                valor = m.group(2);
                valor = valor.replace("\'", ""); //quitamos las comillas
                valor = valor.replace("\"", "");

                if(propiedad.equals("name")){
                    problem.setNombreEjercicio(valor);
                }
                else if(propiedad.equals("timelimit")){
                    //Si entra a timelimit actualizamos los valores de los results
                    problem.setTimeout(valor);
                    for (Submission submission: problem.getSubmissions()){
                        for (Result result: submission.getResults()){
                            result.setMaxTimeout(valor);
                        }
                    }
                }
                else if(propiedad.equals("color")){
                    problem.setColor(valor);
                }

            }
        }
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
