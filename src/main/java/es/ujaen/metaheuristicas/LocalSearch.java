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
import es.ujaen.metaheuristicas.qualitymeasures.WRAccNorm;
import java.util.ArrayList;
import java.util.Hashtable;
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
    
    private int maxIteraciones;
    
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
        this.listaTabu = new Hashtable<>();
        this.memoriaLargoPlazo = new Hashtable<>();
    }
    
    
    
    /**
     * Method that performs the local search over the given set of fuzzy lablels.
     * 
     * @param initialSolution       The initial solution of the local search
     * @param currentPopulation     The current population of patterns
     * @param evaluator             The patterns' evaluator
     * @return     A new set of LLs with new fuzzy definitions.
     */
    public List<BinarySolution> doLocalSearch(List<List<FuzzySet>> initialSolution, List<BinarySolution> currentPopulation, EvaluatorIndDNF evaluator) {
        
        int n = 0;
        for (BinarySolution binarySolution : currentPopulation) {
            System.out.println(n + binarySolution.toString());
            n++;
        }
        
        
        /**
         * AQUI ES DONDE DEBE DE IR VUESTRO CÓDIGO RELATIVO A LA CREACIÓN DE UNA BÚSQUEDA LOCAL.
         * 
         */
        // Firstly, evaluate de initial population
        double initialQuality = evaluate(initialSolution, currentPopulation, evaluator, new WRAccNorm());
        
        // IMPORTANTE: CLONAR INITIAL SOLUTION PARA QUE NO OCURRAN COSAS EXTRAÑAS.
        List<BinarySolution> currentSolution = new ArrayList<>(currentPopulation);
        
        
        int numIteracionesSinMejora = 0;
        
        
        // BL
        
        
        
        // Return
        return currentSolution;
    }
 
    
    /**
     * it performs the mutation operator in order to modify the given fuzzy set.
     * 
     * @param f
     * @return 
     */
    public BinarySolution mutate(BinarySolution f){
        // TODO: AQUI DEBÉIS DE CREAR EL NUEVO VECINO PARA INCLUIRLO EN LA SOLUCIÓN
        double probabilidad = 0.4;
        for (int i = 0; i < f.getNumberOfVariables(); i++) {
            for (int j = 0; j < f.getNumberOfBits(i); j++) {
                if (Math.random() < probabilidad) {
                    f.getVariableValue(i).flip(j);
                }
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
        
        return currentPopulation.parallelStream()
                .mapToDouble((BinarySolution individual) -> {
                    evaluator.doEvaluation(individual, solution, problem.getDataset());
                    ContingencyTable table = (ContingencyTable) individual.getAttribute(TablaContingencia.class);
                    return measure.calculateValue(table);
                }).sum() / (double) currentPopulation.size();
                
    }
    
}
