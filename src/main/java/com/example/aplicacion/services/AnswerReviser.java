package com.example.aplicacion.services;


import com.example.aplicacion.Entities.Answer;
import com.example.aplicacion.Entities.Exercise;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AnswerReviser {

    public void revisar(Answer respuesta, Exercise ejercicio){
        //Si existe errores en la salida del compliador
        if(!Objects.equals(respuesta.getSalidaCompilador(), "")){   //Comprobar salida compilador
            respuesta.setResultado("FAILED IN COMPILER"+"\n"+respuesta.getSalidaCompilador());

        }else if(!Objects.equals(respuesta.getSalidaError(), "")){  //Comprobar salida Error
            respuesta.setResultado("FAILED IN EXECUTION"+"\n"+respuesta.getSalidaError());
        }else {                                                         //Comprobar salida Estandar
            if(Objects.equals(respuesta.getSalidaEstandar(), ejercicio.getSalidaCorrecta() )){
                respuesta.setResultado("GOOD ANSWER");
            }else if(compareIgnoreExpressions(respuesta.getSalidaEstandar(), ejercicio.getSalidaCorrecta())){
                respuesta.setResultado("GOOD ANSWER despues de revisar");

            }
            else{
                respuesta.setResultado("WRONG ANSWER");
            }

        }


        respuesta.setCorrejido(true);

    }

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
