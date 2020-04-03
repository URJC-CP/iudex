package com.example.aplicacion.services;


import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ResultReviser {

    public void revisar(Result res){

        //Si existe errores en la salida del compliador
        if(!Objects.equals(res.getSalidaCompilador(), "")){   //Comprobar salida compilador
            res.setResultadoRevision("FAILED IN COMPILER"+"\n"+res.getSalidaCompilador());

        }else if(!Objects.equals(res.getSalidaError(), "")){  //Comprobar salida Error
            res.setResultadoRevision("FAILED IN EXECUTION"+"\n"+res.getSalidaError());
        }else {
             //Comprobar salida Estandar
            if(compareIgnoreExpressions(res.getSalidaEstandar(), res.getSalidaEstandarCorrecta())){
                res.setResultadoRevision("GOOD ANSWER despues de revisar");

            }
            else{
                res.setResultadoRevision("WRONG ANSWER");
            }

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

}
