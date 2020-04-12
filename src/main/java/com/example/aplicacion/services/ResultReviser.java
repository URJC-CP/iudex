package com.example.aplicacion.services;


import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ResultReviser {

    public void revisar(Result res){
        //En caso de que haya dado timeout, se pone como wrong answer y no se compara nada
        if(compareIgnoreExpressions(res.getTimeout(), "TIMEOUT")){
            res.setResultadoRevision("FAILED TIMEOUT");
        }
        else{

            //Si existe errores en la salida del compliador
            if(!Objects.equals(res.getSalidaCompilador(), "")){   //Comprobar salida compilador
                res.setResultadoRevision("FAILED IN COMPILER"+"\n"+res.getSalidaCompilador());

            }else if(!Objects.equals(res.getSalidaError(), "")){  //Comprobar salida Error
                res.setResultadoRevision("FAILED IN EXECUTION"+"\n"+res.getSalidaError());
            }else {
                //Comprobar salida Estandar
                if(compareIgnoreExpressions(res.getSalidaEstandar(), res.getSalidaEstandarCorrecta())){
                    res.setResultadoRevision("GOOD ANSWER");

                }
                else{
                    res.setResultadoRevision("WRONG ANSWER");
                }

            }

            String[] time =getTime(res.getSalidaTime());
            res.setExecTime(Float.parseFloat(time[0]));
            res.setExecMemory(Float.parseFloat(time[1]));
        }



        res.setRevisado(true);

    }

    //Funciones que modifican los string haciendolos iguales quitando los caracteres espciales como /n /t etcetc
    private boolean compareIgnoreExpressions(String salida, String ejer){
        String salidaAux =salida.replaceAll("\\s+", " ");
        String ejerAux =ejer.replaceAll("\\s+", " ");

        salidaAux = removeWhitespace(salidaAux);
        ejerAux = removeWhitespace(ejerAux);
        
        return Objects.equals(salidaAux, ejerAux);
    }

    private String removeWhitespace(String aux){
        aux= aux.startsWith(" ") ? aux.substring(1) : aux;
        aux= aux.endsWith(" ") ? aux.substring(0, aux.length()-1) : aux;
        return aux;
    }
    private String[] getTime(String aux){
        String[] salida = aux.split(" ");
        return salida;
    }


}
