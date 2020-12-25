/* 
 * Copyright (C) 2020 agvico
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ujaen.metaheuristicas;

import es.ujaen.metaheuristicas.attributes.TablaContingencia;
import es.ujaen.metaheuristicas.evaluator.EvaluatorIndDNF;
import es.ujaen.metaheuristicas.fuzzy.FuzzySet;
import es.ujaen.metaheuristicas.qualitymeasures.ContingencyTable;
import es.ujaen.metaheuristicas.qualitymeasures.QualityMeasure;
import es.ujaen.metaheuristicas.qualitymeasures.WRAcc;
import es.ujaen.metaheuristicas.qualitymeasures.WRAccNorm;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;

/**
 * Class that represents the local search algorithm for the improvement of the
 * fuzzy sets definitions.
 * 
 * @author agvico
 */
public class LocalSearch {
    
    /**
     * The problem of the local search
     */
    private Problema problem;
    
    /**
     * Max iterations BL will do
     */
    private int maxIteraciones;
    
    /**
     * Número de vecinos que se generarán
     */
    private int vecinosAGenerar;
    
    /**
     * 
     */
    private double probabilidadMutacion = 0.1;
    
    private List<List<FuzzySet>> initialSolution;
    
    private List<BinarySolution> currentPopulation;
    
    private EvaluatorIndDNF evaluador;
    
    /**
     * Guardará la tenencia de cada BinarySet, de manera que se inicialice al
     * 20% del total de variables del problema y, cuando el valor llegue a 0,
     * se eliminará de la listaTabu
     */
    private Hashtable<BinarySet, Integer> listaTabu;
    
    /**
     * Guarda las veces que sale un BinarySet
     */
    private Hashtable<BinarySet, Integer> memoriaLargoPlazo;
    
    /**
     * Default constructor with an associated problem
     * @param problem 
     */
    public LocalSearch(Problema problem){
        this.problem = problem;
        this.maxIteraciones = 15000;
        this.vecinosAGenerar = 10;
        this.listaTabu = new Hashtable<>();
        this.memoriaLargoPlazo = new Hashtable<>();
        
        this.initialSolution = null;
        this.evaluador = null;
    }
    
    
    
    /**
     * Method that performs the local search over the given set of fuzzy lablels.
     * 
     * @param _initialSolution       The initial solution of the local search
     * @param _currentPopulation     The current population of patterns
     * @param _evaluator             The patterns' evaluator
     * @return     A new set of LLs with new fuzzy definitions.
     */
    public List<BinarySolution> doLocalSearch(List<List<FuzzySet>> initialSolution, List<BinarySolution> currentPopulation, EvaluatorIndDNF evaluator) {
        
        this.initialSolution = initialSolution;
        this.evaluador = evaluator;
        
        /**
         * Duda 1 --> ¿Cómo evaluar a un único agente si el 'evaluate' que tenemos es para
         *            un conjunto de agentes? Hemos pensando en hacer un List<> donde meteríamos
         *            un único agente dentro.
         * 
         * Duda 2 --> A la hora de aplicar la búsqueda tabú a un agente, si no mejora: ¿Lo 
         *            cambiamos aún así (es decir, reemplazamos aún así) ? Internamente
         *            siempre se mueve, pero a la hora de acabar podemos elegir.
         * 
         * Duda 3 --> Si escogemos el mejor de la población para hacerle búsqueda tabú, ¿por que
         *            le pasamos un subconjunto y no el conjunto entero?
         * 
         */
        
        
        /**
         * AQUI ES DONDE DEBE DE IR VUESTRO CÓDIGO RELATIVO A LA CREACIÓN DE UNA BÚSQUEDA LOCAL.
         * En la búsqueda tabú global:
         * 
         * 1. Por cada 'agente' de la lista de currentPopulation
         * 1.1. Aplicamos la *BUSQUEDA TABU INDIVIDUAL* al 'agente'
         * 1.2. DUDA 2
         * 
         * 
         * 
         * En *BUSQUEDA TABU INDIVIDUAL*:
         * 
         * Por cada 'iteracion' en 'maxIteraciones':
         *      Generamos los 'vecinos' del 'agenteActual'
         *      Evaluamos los 'vecinos' y nos quedamos con el 'mejorVecino'
         *      Sustituimos el 'agenteActual' por el 'mejorVecino' (teniendo en cuenta, que puede ser peor)
         *      Actualizar la 'memoriaLargoPlazo' con los elementos de 'agenteActual' (teniendo en cuenta de que se acaba de sustituir)
         *      Comprobar que el 'agenteActual' es mejor o no que el 'agenteElite' (cambiandolo en caso de mejora)
         *          Si es mejor, intentoMejora = 0
         *          Si no es mejor:
         *              IntentoMejora++
         *              Si han pasado X iteraciones sin mejora:
         *                  *REINICIALIZACION*
         *                  intentoMejora = 0
         * 
         * 
         * EN *REINICIALIZACION*
         * 
         * DUDA 3
         * Si la 'estrategia' es 'intensificacion':
         *      Escogemos el 25% de los pares más repetidos
         * 
         * 
         */
        
        
        BinarySolution referenciaAlMejor = null;
        double calidadMejor = Double.MIN_VALUE;
        
        // Buscamos el mejor de la población
        for (int i = 0; i < currentPopulation.size(); i++) {
            
            // Creamos una lista unitaria conformada por el agente actual
            List<BinarySolution> listaUnitariaAgente = new ArrayList<>();
            listaUnitariaAgente.add(currentPopulation.get(i));
            
            // Calculamos la calidad del agente actual
            double calidad = evaluate(initialSolution, listaUnitariaAgente, evaluator, new WRAccNorm());
            
            // Guardamos el mejor
            if (calidad > calidadMejor) {
                calidadMejor = calidad;
                referenciaAlMejor = currentPopulation.get(i);
            }
            
        }
        
        System.out.println("Mejor individuo --> " + referenciaAlMejor.toString());
        
        busquedaLocal(referenciaAlMejor, calidadMejor);
        
        // Firstly, evaluate de initial population
        //double initialQuality = evaluate(initialSolution, currentPopulation, evaluator, new WRAccNorm());
        
        //System.out.println("\n\n\n\n\nEvaluación:" + initialQuality + "\n\n\n\n\n");
        
        // IMPORTANTE: CLONAR INITIAL SOLUTION PARA QUE NO OCURRAN COSAS EXTRAÑAS.
        List<BinarySolution> currentSolution = new ArrayList<>(currentPopulation);
        
        
        
        
        // Return
        return currentSolution;
    }
 
    
    /**
     * it performs the mutation operator in order to modify the given fuzzy set.
     * 
     * @param c
     * @return 
     */
    public BinarySet mutate(BinarySet c){
        BinarySet f = (BinarySet) c.clone();
        // TODO: AQUI DEBÉIS DE CREAR EL NUEVO VECINO PARA INCLUIRLO EN LA SOLUCIÓN
        
        for (int j = 0; j < f.getBinarySetLength(); j++) {
            if (Math.random() < probabilidadMutacion) {
                f.flip(j);
            }
        }

        return f;
    }
    
    
    /**
     * It evaluates a solution (i.e. a fuzzy sets definitions) on the current population of patterns.
     * 
     * It returns the average value of the selected quality measure on this population of patterns.
     * 
     * @param solution              The fuzzy sets definitions
     * @param currentPopulation     The current patterns
     * @param evaluator             The evaluator of the patterns
     * @param measure               The quality measure to compute
     * @return 
     */
    private double evaluate(List<List<FuzzySet>> solution, List<BinarySolution> currentPopulation, EvaluatorIndDNF evaluator, QualityMeasure measure) {
        //TODO: La búsqueda tabú es sobre un individuo y el evaluate es sobre una población entera.
        return currentPopulation.parallelStream()
                .mapToDouble((BinarySolution individual) -> {
                    evaluator.doEvaluation(individual, solution, problem.getDataset());
                    ContingencyTable table = (ContingencyTable) individual.getAttribute(ContingencyTable.class);
                    return measure.calculateValue(table);
                }).sum() / (double) currentPopulation.size();
                
    }
    
    private BinarySolution busquedaLocal(BinarySolution origen, double calidadOrigen){
        
        
        /* Por cada 'iteracion' en 'maxIteraciones':
         *      Generamos los 'vecinos' del 'agenteActual'
         *      Evaluamos los 'vecinos' y nos quedamos con el 'mejorVecino'
         *      Sustituimos el 'agenteActual' por el 'mejorVecino' (teniendo en cuenta, que puede ser peor)
         *      Actualizar la 'memoriaLargoPlazo' con los elementos de 'agenteActual' (teniendo en cuenta de que se acaba de sustituir)
         *      Comprobar que el 'agenteActual' es mejor o no que el 'agenteElite' (cambiandolo en caso de mejora)
         *          Si es mejor, intentoMejora = 0
         *          Si no es mejor:
         *              IntentoMejora++
         *              Si han pasado X iteraciones sin mejora:
         *                  *REINICIALIZACION*
         *                  intentoMejora = 0
         */
        
        BinarySolution agenteActual = (BinarySolution) origen.copy();
        BinarySolution agenteElite = (BinarySolution) origen.copy();
        
        for (int iteracion = 0; iteracion < maxIteraciones; iteracion++) {
            // Generamos los vecinos del 'agenteActual'
            
            //Método que saque N vecinos y se obtenga el mejor de ellos (sea mejor o no)
            List<BinarySolution> neighbors = calculateNeighbors(agenteActual);
        }
        
        
//        double bestQuality = 0.0;
//        int bestIndexNeighbour = -1;
//        for (int i = 0; i < neighbors.size(); i++) {
//            List<BinarySolution> prueba = new ArrayList<>();
//            prueba.add(neighbors.get(i));
//            double quality = evaluate(initialSolution, prueba, evaluator,new WRAccNorm()); //Valoramos la calidad de ese vecino en concreto.
//            
//            if (quality > bestQuality){
//                bestQuality = quality; //Guardamos la mayor calidad obtenida;
//                bestIndexNeighbour = i;
//            } 
//        }
//        
//        //Sustituir sí o sí al actual
//        destino = neighbors.get(bestIndexNeighbour);

        //Comprueba si se está mejorando a la MEJOR ENCONTRADA
            //NO: Se aplica reinicialización
        
        return agenteElite;
    }
    
    /**
     * Calcula los vecinos y los retorna. Ademas incluye en la listaTabu los binaryset prohibidos
     * @param currentBinarySolution Vecino de referencia
     * @return Una lista unitaria con el mejor vecino
     */
    private List<BinarySolution> calculateNeighbors(BinarySolution currentBinarySolution){
        List<BinarySolution> ret = new ArrayList<BinarySolution>();
        
        double mejorCalidad = Double.MIN_VALUE;
        
        /**
         * Generados --> Guardará los BinarySet generados en el ambito local (para no generarlos de nuevo)
         */
        List<BinarySet> generados = new ArrayList<>();
        for (int i = 0; i < currentBinarySolution.getNumberOfVariables(); i++)
            generados.add(currentBinarySolution.getVariableValue(i));
        
        for (int i = 0; i < vecinosAGenerar; i++) {
            
            // Inicializamos al vecino
            BinarySolution vecino = (BinarySolution) currentBinarySolution.copy();
            
            // Elegimos de forma aleatoria la variable a modificar
            int posicionAleatoria = (int) (Math.random() * currentBinarySolution.getNumberOfVariables());
            
            // Lo mutamos
            //for (int j = 0; j < vecino.getNumberOfVariables(); j++) {
            BinarySet aux = null;
            do{
                aux = mutate(vecino.getVariableValue(posicionAleatoria));
                // TODO --> Comprobar que todos los vecinos sean distintos
                System.out.println(aux.toString() + "\n");
            }while (listaTabuContiene(aux)); // TODO --> Comprobar que no está en la lista tabú ni en la lista de generados

            vecino.setVariableValue(posicionAleatoria, aux);
            //}
            
            // Evaluamos al vecino recien mutado
            List<BinarySolution> listaCalidadVecino = new ArrayList<>();
            listaCalidadVecino.add(vecino);
            double calidadVecino = evaluate(this.initialSolution, listaCalidadVecino, this.evaluador, new WRAccNorm());
            
            // Verificamos que es valido (que no se haya generado ya)
            if (calidadVecino > mejorCalidad) {
                mejorCalidad = calidadVecino;
                ret.clear();
                ret.add(vecino);
            }
            
        }
        
        for (int i = 0; i < ret.get(0).getNumberOfVariables(); i++) {
            
            BinarySet set = ret.get(0).getVariableValue(i);
            
            // Solo metemos el BinarySet si no existe.
            if (!listaTabuContiene(set))
                listaTabu.put(set, 1); // TODO CAMBIAR
            
        }
        
        
        return ret;
    }
    
    public boolean listaTabuContiene(BinarySet s) {
        
        for (Enumeration<BinarySet> e = listaTabu.keys(); e.hasMoreElements();) {
            if (equalBinarySet(s, e.nextElement())) {
                return true;
            }
        }
        
        return false;
    }
    
    
    public boolean equalBinarySet(BinarySet set1, BinarySet set2) {
        
        if (set1.getBinarySetLength() != set2.getBinarySetLength())
            return false;
        
        for (int i = 0; i < set1.getBinarySetLength(); i++)
            if (set1.get(i) != set2.get(i))
                return false;
        
        return true;
    }
    
}

