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

import es.ujaen.metaheuristicas.evaluator.EvaluatorIndDNF;
import es.ujaen.metaheuristicas.fuzzy.FuzzySet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * CLASE DE EJEMPLO PARA LA CREACION DE UN ALGORITMO MEMETICO PARA EL PROBLEMA DE EPM
 * 
 * BASADO EN EL ALGORITMO GENETICO NSGA-II (Para otro algoritmo, simplemente cambiar la clase de la que se herede)
 * 
 * SI OS FIJAIS, AL HEREDAR DE NSGA-II TODA SU FUNCIONALIDAD PERMANECE INTACTA. PARA MODIFICARLO CON NUESTRO CÓDIGO
 * PARA FORMAR UN MEMÉTICO, DEBEMOS DE SOBRECARGAR EL METODO replacement() que es la función de reemplazo.
 * 
 * 
 * En nuestro caso debemos aplicar el algoritmo de BL en la población offspring y tras esto, llamar al método super()
 * para que no se pierda el proceso de reemplazo original.
 * 
 * @author agvico
 */
public class MyMemeticAlgorithm extends NSGAII<BinarySolution>{
    
    int evaluacion;
    
    public MyMemeticAlgorithm(
            Problem<BinarySolution> problem,
            int maxEvaluations,
            int populationSize,
            int matingPoolSize,
            int offspringPopulationSize,
            CrossoverOperator<BinarySolution> crossoverOperator,
            MutationOperator<BinarySolution> mutationOperator,
            SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator,
            SolutionListEvaluator<BinarySolution> evaluator
        ) {
        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        evaluacion = 0;
    }

    @Override
    protected List<BinarySolution> replacement(List<BinarySolution> population, List<BinarySolution> offspringPopulation) {
        
        // AQUI DEBEMOS JUGAR CON offspringPopulation aplicando la BL.
        // Entramos cada 20% de evaluaciones
        
        if ( evaluacion % Math.round(0.2*maxEvaluations) == 0) {
            
            // Extraemos el 25% del offspringPopulation
            //List<BinarySolution> choosen = new  ArrayList<>(); //<-- EEDD que mantiene las posiciones escogidas
            Problema problema = (Problema) problem;
            LocalSearch bl = new LocalSearch(problema);
            
//            int individuoBLSize = (int) Math.round(0.25*offspringPopulationSize);
//            while (choosen.size() < individuoBLSize) {
//                
//                // Obtenemos el individuo
//                BinarySolution individuo = offspringPopulation.remove((int)Math.random()*(offspringPopulation.size() - 1));
//                
//                
//                // Lo metemos en la EEDD
//                choosen.add(individuo);
//                
//            }
            
            // Aplicamos la BL al 25% de individuos
            System.out.println("Comenzando BL en la evaluación "+evaluacion);
            offspringPopulation = bl.doLocalSearch(problema.getFuzzySets(), offspringPopulation, (EvaluatorIndDNF)evaluator);
            System.out.println("Terminada BL: "+evaluacion);
            //offspringPopulation.addAll(choosen);
        }
        evaluacion+=this.maxPopulationSize;
        
        // Finalmente, debemos llamar SIEMPRE a super() para realizar el proceso de reemplazo original
        return super.replacement(population, offspringPopulation); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}
