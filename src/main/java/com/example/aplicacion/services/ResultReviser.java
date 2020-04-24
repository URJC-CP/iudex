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
        //SIGNAL: el primer valor es la salida del compilador y el segundo es la salida de la ejecucion
        String signalAux[] = res.getSignalFile().split("\n");

        //si hay errores en compilacion no miramos mas
        if(!compareIgnoreExpressions(signalAux[0],"0")){
            res.setResultadoRevision("FAILED IN COMPILER"+"\n"+res.getSalidaCompilador());
        }
        //Si hay errores en la ejecucion buscamos cual es
        else if(!compareIgnoreExpressions(signalAux[1], "0")){
            //Si el codigo es el 143 significa que ha dado timeout
            if(compareIgnoreExpressions(signalAux[1], "143")){
                res.setResultadoRevision("FAILED TIMEOUT");
            }
            else {
                res.setResultadoRevision("FAILED IN EXECUTION"+"\n"+res.getSalidaError()+ "\n El codigo de salida de error es " + signalAux[1]);
            }

        }
        //Si no ejecuta ninguno significa que su ejecucion es correcta
        else {
            //Comprobar salida Estandar
            String salidaCorrecta = res.getSalidaEstandarCorrectaInO().getText();
            if(compareIgnoreExpressions(res.getSalidaEstandar(), salidaCorrecta)){
                res.setResultadoRevision("GOOD ANSWER");

            }
            else{
                res.setResultadoRevision("WRONG ANSWER");
            }

        }
        String[] time =getTime(res.getSalidaTime());
        res.setExecTime(Float.parseFloat(time[0]));
        res.setExecMemory(Float.parseFloat(time[1]));

        res.setRevisado(true);

    }

    //Funciones que modifican los string haciendolos iguales quitando los caracteres espciales como /n /t etcetc
    private boolean compareIgnoreExpressions(String salida, String ejer){
        String salidaAux =salida.replaceAll("\\s+", " ");
        String ejerAux =ejer.replaceAll("\\s+", " ");

        salidaAux = removeWhitespace(salidaAux);
        ejerAux = removeWhitespace(ejerAux);

        boolean aux = ejerAux.equals(salidaAux);
        return aux;
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
