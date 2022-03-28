package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResultReviser {
    private static final Logger logger = LoggerFactory.getLogger(ResultReviser.class);

    public void revisar(Result res) {
        //SIGNAL: el primer valor es la salida del compilador y el segundo es la salida de la ejecucion
        logger.debug("Review result {}", res.getId());
        //si hay errores en compilacion no miramos mas
        if (!compareIgnoreExpressions(res.getSignalCompilador(), "0")) {
            logger.info("Result {} failed in compiler {}", res.getId(), res.getSalidaCompilador());
            res.setResultadoRevision("FAILED IN COMPILER with " + res.getSalidaCompilador());
        }
        //Si hay errores en la ejecucion buscamos cual es
        else if (!compareIgnoreExpressions(res.getSignalEjecutor(), "0")) {
            //Si el codigo es el 143 significa que ha dado timeout
            if (compareIgnoreExpressions(res.getSignalEjecutor(), "143")) {
                logger.info("Time limit exceeded for result {}", res.getId());
                res.setResultadoRevision("time_limit_exceeded");
            } else {
                logger.info("Result {} failed in runtime {} with {}", res.getId(), res.getSalidaError(), res.getSignalEjecutor());
                res.setResultadoRevision("run_time_error " + res.getSalidaError() + " with " + res.getSignalEjecutor());
            }
        }
        //Si no ejecuta ninguno significa que su ejecucion es correcta
        else {
            //Comprobar salida Estandar
            String salidaCorrecta = res.getSample().getOutputText();
            if (compareIgnoreExpressions(res.getSalidaEstandar(), salidaCorrecta)) {
                res.setResultadoRevision("accepted");
            } else {
                res.setResultadoRevision("wrong_answer");
            }
        }
        String[] time = getTime(res.getSalidaTime());
        if (time.length != 2) {
            res.setExecTime(0.0f);
            res.setExecMemory(0.0f);
        } else {
            res.setExecTime(Float.parseFloat(time[0]));
            res.setExecMemory(Float.parseFloat(time[1]));
        }

        logger.debug("Finish review result {} with {}", res.getId(), res.getResultadoRevision());
        res.setRevisado(true);

    }

    //Funciones que modifican los string haciendolos iguales quitando los caracteres espciales como /n /t etcetc
    private boolean compareIgnoreExpressions(String salida, String ejer) {
        String salidaAux = salida.replaceAll("\\s+", " ");
        String ejerAux = ejer.replaceAll("\\s+", " ");

        salidaAux = removeWhitespace(salidaAux);
        ejerAux = removeWhitespace(ejerAux);

        return ejerAux.equals(salidaAux);
    }

    private String removeWhitespace(String aux) {
        aux = aux.trim();
        return aux;
    }

    private String[] getTime(String aux) {
        return aux.split(" ");
    }
}
