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
    
    private List<List<FuzzySet>> initialSolution;
    
    private List<BinarySolution> currentPopulation;
    
    private EvaluatorIndDNF evaluator;
    
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
        
        this.initialSolution = new ArrayList<List<FuzzySet>>();
        this.currentPopulation = new ArrayList<>();
        evaluator = null;
    }
    
    
    
    /**
     * Method that performs the local search over the given set of fuzzy lablels.
     * 
     * @param _initialSolution       The initial solution of the local search
     * @param _currentPopulation     The current population of patterns
     * @param _evaluator             The patterns' evaluator
     * @return     A new set of LLs with new fuzzy definitions.
     */
    public List<BinarySolution> doLocalSearch(List<List<FuzzySet>> _initialSolution, List<BinarySolution> _currentPopulation, EvaluatorIndDNF _evaluator) {
        this.initialSolution = _initialSolution;
        this.currentPopulation = _currentPopulation;
        this.evaluator = _evaluator;
        
        /**
         * AQUI ES DONDE DEBE DE IR VUESTRO CÓDIGO RELATIVO A LA CREACIÓN DE UNA BÚSQUEDA LOCAL.
         * 
         */
        // Firstly, evaluate de initial population
        double initialQuality = evaluate(initialSolution, currentPopulation, evaluator, new WRAccNorm());
        
        // IMPORTANTE: CLONAR INITIAL SOLUTION PARA QUE NO OCURRAN COSAS EXTRAÑAS.
        List<BinarySolution> currentSolution = new ArrayList<>(currentPopulation);
        
        
        // BL
        currentPopulation.forEach((binarySolution) -> {
            binarySolution = busquedaLocal(binarySolution);
        });
        
        
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
        double probabilidad = 0.4;
        for (int j = 0; j < f.getBinarySetLength(); j++) {
            if (Math.random() < probabilidad) {
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
                    ContingencyTable table = (ContingencyTable) individual.getAttribute(TablaContingencia.class);
                    return measure.calculateValue(table);
                }).sum() / (double) currentPopulation.size();
                
    }
    
    private BinarySolution busquedaLocal(BinarySolution origen){
        
        BinarySolution bestBinarySolution = (BinarySolution) origen.copy();
        
        BinarySolution destino = (BinarySolution) origen.copy();
        
        //Método que saque N vecinos y se obtenga el mejor de ellos (sea mejor o no)
        List<BinarySolution> neighbors = calculateNeighbors(destino);
        
        double bestQuality = 0.0;
        int bestIndexNeighbour = -1;
        for (int i = 0; i < neighbors.size(); i++) {
            List<BinarySolution> prueba = new ArrayList<>();
            prueba.add(neighbors.get(i));
            double quality = evaluate(initialSolution, prueba, evaluator,new WRAccNorm()); //Valoramos la calidad de ese vecino en concreto.
            
            if (quality > bestQuality){
                bestQuality = quality; //Guardamos la mayor calidad obtenida;
                bestIndexNeighbour = i;
            } 
        }
        
        //Sustituir sí o sí al actual
        destino = neighbors.get(bestIndexNeighbour);

        //Comprueba si se está mejorando a la MEJOR ENCONTRADA
            //NO: Se aplica reinicialización
        
        return destino;
    }
    
    private List<BinarySolution> calculateNeighbors(BinarySolution currentBinarySolution){
        
        int num_vecinos = 10;
        List<BinarySolution> ret = new ArrayList<BinarySolution>(num_vecinos);
        
        for (BinarySolution vecino : ret){
            System.out.println("NO ME MUESTRAS NA");
            vecino = (BinarySolution) currentBinarySolution.copy();
            for (int i = 0; i < vecino.getNumberOfVariables(); i++) {
                BinarySet aux = null;
                do{
                    aux = mutate(vecino.getVariableValue(i));
                    System.out.println(aux.toString()+"\n");
                }while (listaTabu.containsKey(aux));
                
                vecino.setVariableValue(i, aux);
            }
        }
        
        return ret;
    }
    
}

