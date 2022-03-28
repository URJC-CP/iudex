package es.urjc.etsii.grafo.iudex.pojos;

import es.urjc.etsii.grafo.iudex.entities.Problem;

public class ProblemString {
    Problem problem;
    String salida;

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }
}
