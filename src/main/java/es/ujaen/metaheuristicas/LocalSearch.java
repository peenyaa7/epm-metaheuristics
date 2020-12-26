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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.util.Pair;
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
     * Probabilidad que tiene un bit de ser mutado en una variable
     */
    private double probabilidadMutacion;
    
    /**
     * Intentos máximos permitidos. Si se sobrepasa, se reinicializa.
     */
    private static final double porcentajeMAXIntentos = 0.25;
    private int maxIntentos;
    
    /**
     * Tenencia inicial de los elementos de la lista tabú
     */
    private static final double porcentajeTenencia = 0.2;
    private int tenenciaMaxima;
    
    /**
     * Guardará la tenencia de cada BinarySet, de manera que se inicialice al
     * 20% del total de variables del problema y, cuando el valor llegue a 0,
     * se eliminará de la listaTabu
     */
    private LinkedList<BinarySet> listaTabu;
    
    private List<List<FuzzySet>> initialSolution;
    
    private List<BinarySolution> currentPopulation;
    
    private EvaluatorIndDNF evaluador;
    
    
    
    /**
     * Guarda las veces que sale un BinarySet
     */
    private ArrayList<Pair<BinarySet,Integer>> memoriaLargoPlazo;
    
    /**
     * Default constructor with an associated problem
     * @param problem 
     */
    public LocalSearch(Problema problem){
        
        // Constantes
        this.maxIteraciones = 15000;
        this.vecinosAGenerar = 10;
        this.probabilidadMutacion = 0.1;
        this.maxIntentos = (int) Math.round(this.maxIteraciones * LocalSearch.porcentajeMAXIntentos); //25% del maximo de Iteraciones
        
        //this.tenenciaMaxima = 5;
        
        this.problem = problem;
        //this.listaTabu = new Hashtable<>();
        this.listaTabu = new LinkedList<>();
        this.memoriaLargoPlazo = new ArrayList<>();
        
        this.initialSolution = null;
        this.evaluador = null;
    }
    
    
    
    /**
     * Method that performs the local search over the given set of fuzzy lablels.
     * 
     * @param initialSolution
     * @param currentPopulation
     * @param evaluator
     * @return     A new set of LLs with new fuzzy definitions.
     */
    public List<BinarySolution> doLocalSearch(List<List<FuzzySet>> initialSolution, List<BinarySolution> currentPopulation, EvaluatorIndDNF evaluator) {
        
        this.initialSolution = initialSolution;
        this.evaluador = evaluator;
        
        /*CARGAR TENENCIA MÁXIMA Y LA LISTA TABÚ DE VARIABLES*/
        this.tenenciaMaxima = (int) Math.ceil(currentPopulation.get(0).getNumberOfVariables()* LocalSearch.porcentajeTenencia);
        for(int i = 0; i < this.tenenciaMaxima; i++)
            listaTabu.add(new BinarySet(0)); //COMO USARLO: Como la lista está siempre llena, una vez entran por el inicio (push) -> se saca por el final (pollLast)
        
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
        
        /**
         * PROCESO:
         * 
         * Por cada 'iteracion' en 'maxIteraciones':
         *      Generamos los 'vecinos' del 'agenteActual' (HECHO)
         *      Evaluamos los 'vecinos' y nos quedamos con el 'mejorVecino' (HECHO)
         *      Sustituimos el 'agenteActual' por el 'mejorVecino' (teniendo en cuenta, que puede ser peor) (HECHO)
         *      Actualizar la 'memoriaLargoPlazo' con los elementos de 'agenteActual' (teniendo en cuenta de que se acaba de sustituir) (HECHO)
         *      Comprobar que el 'agenteActual' es mejor o no que el 'agenteElite' (cambiandolo en caso de mejora) (HECHO)
         *          Si es mejor, intentoMejora = 0 (HECHO)
         *          Si no es mejor:
         *              IntentoMejora++ (HECHO)
         *              Si han pasado X iteraciones sin mejora:
         *                  *REINICIALIZACION* (DUDA A ANGEL)
         *                  intentoMejora = 0
         */
        
        BinarySolution agenteActual = (BinarySolution) origen.copy();
        BinarySolution agenteElite = (BinarySolution) origen.copy();
        double calidadElite = Double.MIN_VALUE;
        int intentoMejora = 0;
        
        
        for (int iteracion = 0; iteracion < maxIteraciones; iteracion++) {
             
            //Método que evalua N vecinos y saca el mejor de ellos por parámetro retornando su calidad (DENTRO SE HACE LA ACTUALIZACIÓN DE LA LISTA TABÚ)
            Pair<BinarySolution,Double> parVecinoCalidad  = calculateBestNeighbour((BinarySolution) agenteActual.copy());
            
            //Sustituimos al agenteActual por el mejor vecino calculado (Sea mejor o no al agenteActual)
//            List<BinarySolution> listAgenteActual = new ArrayList<>();
//            listAgenteActual.add(agenteActual);
//            double antiguaCalidad = evaluate(initialSolution, listAgenteActual, evaluador, new WRAccNorm() );
//            System.out.print(antiguaCalidad+" :: "+agenteActual.toString()+" --> "); //AGENTE ACTUAL SIN SUSTITUIR
            agenteActual = (BinarySolution) parVecinoCalidad.getKey().copy();
            double calidad = parVecinoCalidad.getValue();
//            System.out.println(calidad+" :: "+agenteActual.toString()); //AGENTE ACTUAL SUSTITUIDO
            
            //double calidad = evaluate(initialSolution, neighbors, evaluador, new WRAccNorm()); //Se está evaluando doblemente, ya que el elemento que viene ya ha sido evaluado -> devolver el double y por parametro pasar el mejor vecino (HECHO)
            
            //Actualizar la LISTA TABÚ -> Hecho dentro del método calculateBestNeighBour
            //Actualizar la MEMORIA A LARGO PLAZO con el agenteActual
                //1 opcion: Añadir las n variables del agente en la MLP (Si están ya, incrementar contador)
                //2 opcion: Añadir solo la variable que se está añadiendo como mutada
            //1 opcion:
            for (BinarySet variable : agenteActual.getVariables()) { //Bucle de las variables del agenteActual
                boolean encontrado = false;
                for (int v = 0; v < memoriaLargoPlazo.size() && !encontrado; v++) {
                    if (equalBinarySet(variable, memoriaLargoPlazo.get(v).getKey())){ //Si son iguales...
                        memoriaLargoPlazo.set(v, new Pair<BinarySet, Integer>(memoriaLargoPlazo.get(v).getKey(),memoriaLargoPlazo.get(v).getValue() + 1)); //Incrementa en 1 las apariciones de esa variable
                        encontrado = true;
                    }
                }
                
                if (!encontrado) //Si no se ha encontrado ya en la MLP...
                    memoriaLargoPlazo.add(new Pair<BinarySet,Integer> (variable,1)); //Se añade el BinarySet no encontrado y con una cantidad de apariciones = 1.
            }
            
            // Si mejora, sustituimos las variables elites y reiniciamos el contador de intentos de mejora
            if (calidad > calidadElite) {
                System.out.println("VECINO MEJORADO | Calidad anterior: " + calidadElite + " | Calidad nueva: " + calidad);
                agenteElite = (BinarySolution) agenteActual.copy();
                calidadElite = calidad;
                intentoMejora = 0;
            }
            
            // Si no mejora, aumentamos el contador de intentos de mejora
            else {
                
                intentoMejora++;
                
                // Ademas, comprobamos si no ha mejora en un número de iteraciones establecido
                if (intentoMejora >= maxIntentos) { //HECHO: El maxIntentos = 25% del maximo de iteraciones.
                    reinicializacion(agenteActual); //Pasamos como parametro el agente que queremos que ahora sea reinicializado con la MLP
                    intentoMejora = 0;
                }
                
            }
            
            // TODO --> Actualizar la memoria a largo plazo
        }
        
        
        return agenteElite;
    }
    
    
    /**
     * Reinicializa la búsqueda tabú
     */
    private void reinicializacion(BinarySolution currentBinarySolution) {
        // TODO --> Hacer el método.
        System.out.println("Reinicialización...");
        
        /*ORDENACION DE LA MEMORIA A LARGO PLAZO (MENOR A MAYOR).*/
        memoriaLargoPlazo.sort((o1,o2) -> o1.getValue().compareTo(o2.getValue())); 
        
        currentBinarySolution.getVariables().clear(); //LIMPIAMOS LAS VARIABLES
        /**
         * IF RANDOM < PORCENTAJE THEN
         *      REINICIALIZACION DE LOS DE MENOR VALOR DE CONTEO (COGIENDO N VARIABLES NECESARIAS PARA EL BINARYSOLUTION) >
         * ELSE IF PORCENTAJE < RANDOM THEN
         *      REINICIALIZACION DE LOS DE MAYOR VALOR DE CONTEO (COGIENDO N VARIABLES NECESARIAS PARA EL BINARYSOLUTION)
         *      
         */
        
        //TODO: AQUÍ FALTA ENTENDER A QUE SE REFIERE CON INTENSIFICAR USANDO 25% DE LOS MÁS USADOS O INTENSIFICAR USANDO EL 25% DE LOS MENOS USADOS
        //Mi deducción: Habla en ambos casos de intensificar porque, aunque cojamos los que menos aparecen, en la MLP únicamente estamos metiendo par variables-valor que hayan existido
        // en el proceso del algoritmo de trayectorias. Entonces, realmente se intensifica ya sea cogiendo los que más han aparecido y los que menos hayan aparecido
        // aún sabiendo que si es con los que más veces han aparecido -> se está intensificando más aún que con los que menos.
        
        System.out.println("Limpiando Estructuras de lista tabú y MLP...");
        /*LISTA TABÚ.*/
        listaTabu.clear();
        for (int i = 0; i < tenenciaMaxima; i++)
            listaTabu.push(new BinarySet(0)); //Reinicio de n BinarySet (con número de bits = 0), siendo n = tenenciaMaxima.
        
        /*MEMORIA A LARGO PLAZO*/
        //Para no perder del todo que valor par variable-valor aparecieron, en vez de hacer un clear() de la estructura vamos a reiniciar sus contadores a 0
        for (int i = 0; i < memoriaLargoPlazo.size(); i++)
            memoriaLargoPlazo.set(i, new Pair<BinarySet, Integer>(memoriaLargoPlazo.get(i).getKey(), 0 )); //Contador de esa variable a 0
    }
    
    
    /**
     * @brief Calcular el mejor VECINO
     * @post A partir de una solución pasada como parámetro, calculamos N vecimos y nos quedamos con el mejor de ellos 
     * según una evaluación de su calidad. Se devuelve el mejor vecino mediante el otro parámetro; es decir, bestNeighbour y su 
     * valor de calidad se retorna.
     * @param currentBinarySolution
     * @param bestNeighbour
     * @return 
     */
    private Pair<BinarySolution,Double> calculateBestNeighbour(BinarySolution currentBinarySolution){
        
        // ArrayList que retornaremos. Contendrá un solo vecino (el mejor)
        List<BinarySolution> ret = new ArrayList<>();
        
        // Variable tabú candidata --> Variable que se meterá en la lista tabú
        BinarySet variableTabuCandidata = null;
        
        // Inicializamos la mejor calidad al mínimo los flotantes
        double mejorCalidad = Double.MIN_VALUE;
        
        // Generados --> Guardará los BinarySet generados en el ambito local (para no generarlos de nuevo)
        List<BinarySet> generados = new ArrayList<>();
        
        // Rellenamos la lista de generados con las variables que tiene nuestro agente actual, para que los vecinos sean diferentes entre si.
        for (int i = 0; i < currentBinarySolution.getNumberOfVariables(); i++)
            generados.add(currentBinarySolution.getVariableValue(i));
        
        
        for (int i = 0; i < vecinosAGenerar; i++) {
            
            // Inicializamos al vecino actual haciendo una copia del agente
            BinarySolution vecino = (BinarySolution) currentBinarySolution.copy();
            
            // Elegimos de forma aleatoria la variable a modificar. //TODO: No deberiamos hacerlo así porque no nos generará resultados que podamos repetir.
            int posicionAleatoria = (int) (Math.random() * currentBinarySolution.getNumberOfVariables());
            
            // Mutamos dicha variable (considerando restricciones)
            BinarySet variableMutada = null;
            do{
                variableMutada = mutate(vecino.getVariableValue(posicionAleatoria));
                //System.out.println(variableMutada.toString() + "\n");
            }while (listaTabu.contains(variableMutada) && generados.contains(variableMutada));
            
            // Una vez tenemos la variable mutada (que cumple las restricciones), la sustituimos por la elegida anteriormente
            vecino.setVariableValue(posicionAleatoria, variableMutada);
            
            // Evaluamos al vecino recien mutado
            List<BinarySolution> listaCalidadVecino = new ArrayList<>();
            listaCalidadVecino.add(vecino);
            double calidadVecino = evaluate(this.initialSolution, listaCalidadVecino, this.evaluador, new WRAccNorm());
            
            // Verificamos que es el mejor vecino, y si lo es:
            if (calidadVecino > mejorCalidad) {
                // - Sustituimos la mejor calidad
                mejorCalidad = calidadVecino;
                // - Limpiamos la estructura a retornar
                ret.clear();
                // - Metemos el mejor vecino hasta ahora
                ret.add(vecino);
                // - Sustituimos la variable tabú candidata
                variableTabuCandidata = variableMutada;
            }
            
        }
        // HECHO --> Hacer la tenencia máxima
        listaTabu.push(variableTabuCandidata); // Incluimos la variable tabú candidata en la lista tabú 
        listaTabu.pollLast(); //Eliminamos la última de la lista (Ya que ha entrado 1 y debe de salir debido a la tenencia máxima (tamaño máximo de la lista)).
        
        Pair<BinarySolution,Double> mejorVecino = new Pair<>(ret.get(0),mejorCalidad);
       
        return mejorVecino; //Devolvemos su calidad ya calculada en el proceso de obtención del mejor vecino.
    }
    
    /**
     * PEÑA: EN MI CASO ESTE MÉTODO YA NO ME HARÍA FALTA, YA HA SIDO TODO HECHO EN EL calculateBestNeighbour
     * Calcula los vecinos y los retorna. Ademas incluye en la listaTabu los binaryset prohibidos
     * @param currentBinarySolution Vecino de referencia
     * @return Una lista unitaria con el mejor vecino
     */
    private List<BinarySolution> calculateNeighbors(BinarySolution currentBinarySolution){
        
        // ArrayList que retornaremos. Contendrá un solo vecino (el mejor)
        List<BinarySolution> ret = new ArrayList<>();
        
        // Variable tabú candidata --> Variable que se meterá en la lista tabú
        BinarySet variableTabuCandidata = null;
        
        // Inicializamos la mejor calidad al mínimo los flotantes
        double mejorCalidad = Double.MIN_VALUE;
        
        // Generados --> Guardará los BinarySet generados en el ambito local (para no generarlos de nuevo)
        List<BinarySet> generados = new ArrayList<>();
        
        // Rellenamos la lista de generados con las variables que tiene nuestro agente actual, para que los vecinos sean diferentes entre si.
        for (int i = 0; i < currentBinarySolution.getNumberOfVariables(); i++)
            generados.add(currentBinarySolution.getVariableValue(i));
        
        
        for (int i = 0; i < vecinosAGenerar; i++) {
            
            // Inicializamos al vecino actual haciendo una copia del agente
            BinarySolution vecino = (BinarySolution) currentBinarySolution.copy();
            
            // Elegimos de forma aleatoria la variable a modificar. //TODO: No deberiamos hacerlo así porque no nos generará resultados que podamos repetir.
            int posicionAleatoria = (int) (Math.random() * currentBinarySolution.getNumberOfVariables());
            
            // Mutamos dicha variable (considerando restricciones)
            BinarySet variableMutada = null;
            do{
                variableMutada = mutate(vecino.getVariableValue(posicionAleatoria));
                //System.out.println(variableMutada.toString() + "\n");
            }while (listaTabu.contains(variableMutada) && generados.contains(variableMutada));
            
            // Una vez tenemos la variable mutada (que cumple las restricciones), la sustituimos por la elegida anteriormente
            vecino.setVariableValue(posicionAleatoria, variableMutada);
            
            // Evaluamos al vecino recien mutado
            List<BinarySolution> listaCalidadVecino = new ArrayList<>();
            listaCalidadVecino.add(vecino);
            double calidadVecino = evaluate(this.initialSolution, listaCalidadVecino, this.evaluador, new WRAccNorm());
            
            // Verificamos que es el mejor vecino, y si lo es:
            if (calidadVecino > mejorCalidad) {
                // - Sustituimos la mejor calidad
                mejorCalidad = calidadVecino;
                // - Limpiamos la estructura a retornar
                ret.clear();
                // - Metemos el mejor vecino hasta ahora
                ret.add(vecino);
                // - Sustituimos la variable tabú candidata
                variableTabuCandidata = variableMutada;
            }
            
        }
        // HECHO --> Hacer la tenencia máxima
        listaTabu.push(variableTabuCandidata); // Incluimos la variable tabú candidata en la lista tabú 
        listaTabu.pollLast(); //Eliminamos la última de la lista (Ya que ha entrado 1 y debe de salir debido a la tenencia máxima (tamaño máximo de la lista)).
        
        return ret; //Devolvemos su calidad ya calculada en el proceso de obtención del mejor vecino.
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

    
//    public boolean listaTabuContiene(BinarySet s) {
//        
//        for (Enumeration<BinarySet> e = listaTabu.keys(); e.hasMoreElements();) {
//            if (equalBinarySet(s, e.nextElement())) {
//                return true;
//            }
//        }
//        
//        return false;
//    }

//            List<BinarySet> candidatosASerEliminados = new ArrayList<>();
//            Enumeration<BinarySet> enumeration = listaTabu.keys();
//            while (enumeration.hasMoreElements()) {
//                
//                BinarySet key = enumeration.nextElement();
//                
//                Integer oldValue = listaTabu.get(key);
//                
//                if (oldValue <= 1) {
//                    candidatosASerEliminados.add(key);
//                } else {
//                    Integer check = listaTabu.replace(key, oldValue - 1);
//                    
//                    if (check != oldValue)
//                        System.out.println("ALGO RARISIMO");
//                }
//                
//                System.out.println(oldValue);
//                System.out.println("TENENCIAS: " + listaTabu.toString() + "\n\n\n\n\n\n\n");
//                
//            }