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

import es.ujaen.metaheuristicas.utils.Files;
import es.ujaen.metaheuristicas.utils.ClassLoader;
import es.ujaen.metaheuristicas.attributes.Clase;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import es.ujaen.metaheuristicas.qualitymeasures.QualityMeasure;
import es.ujaen.metaheuristicas.attributes.Clase;
import es.ujaen.metaheuristicas.qualitymeasures.ContingencyTable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.solution.BinarySolution;

/**
 * Class to store the results and rules of a given population of individuals
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8
 * @version 1.0
 */
public final class ResultWriter {

    /**
     * The path where the objectives values for each individual is stored
     */
    private final String pathTra;

    /**
     * The population to get the results
     */
    private List<BinarySolution> population;

    /**
     * The problem to be used
     */
    private Problema problem;

    /**
     * The formatter of the numbers
     */
    private final DecimalFormat sixDecimals;

    /**
     * The symbols to use in the formatter
     */
    private final DecimalFormatSymbols symbols;

    /**
     * It determines if it is the first time to write the header or not
     */
    private boolean firstTime;
    
    
    private String test;

    /**
     * Default constructor, it sets the path where the files are stored.
     *
     *
     * @param tra The path for training QMs files
     * @param tst The path fot test QMs files
     * @param tstSummary The path for the summary of the test QMs file
     * @param rules The path for the rules file
     * @param population The population of individuals
     * @param header The InstancesHeader of the given problem
     * @param overwrite If previous files exists, overwrite it?
     */
    public ResultWriter(String tra, String tst, List<BinarySolution> population, Problema problem, boolean overwrite) {

        this.pathTra = tra;
            this.test = tst;
        this.population = population;
        this.problem = problem;
        symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setDecimalSeparator('.');
        symbols.setNaN("NaN");
        symbols.setInfinity("INFINITY");
        sixDecimals = new DecimalFormat("0.000000", symbols);
        if (this.population != null) {
            this.population.sort((x, y) -> Integer.compare((Integer) x.getAttribute(Clase.class), (Integer) y.getAttribute(Clase.class)));
        }
        firstTime = true;

        if (overwrite) {
            File[] a = {new File(tra), new File(tra + "_reg"), new File(tst + "_testMeasures.txt")};
            for (File f : a) {
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }

    /**
     * It only writes the results of the objectives
     */
    public void writeTrainingMeasures() {
        String content = "";
        if (firstTime) {

            // Write the header (the consequent first, and next, the objective quality measures, finaly, the diversity measure)
            content += "Rule\tID\tConsequent";
            for (QualityMeasure q : (ArrayList<QualityMeasure>) problem.getEvaluator().getObjectives()) {
                content += "\t" + q.getShortName();
            }
            content += "\n";
        }

        // Now, for each individual, writes the training measures
        double[] medias = new double[population.get(0).getObjectives().length];
        double[] p = new double[population.get(0).getObjectives().length];
        for (int i = 0; i < medias.length; i++) {
            medias[i] = 0.0;
            p[i] = 0.0;
        }
        for (int i = 0; i < population.size(); i++) {
            content += i + "\t" + population.get(i).hashCode() + "\t" + population.get(i).getAttribute(Clase.class) + "\t";
            for (int j = 0; j < population.get(i).getObjectives().length; j++) {
                double q = population.get(i).getObjective(j);
                //para evitar los infinitos
                if (q != Double.POSITIVE_INFINITY && q != Double.NEGATIVE_INFINITY) {
                    content += sixDecimals.format(q) + "\t";
                    medias[j] += q;
                    p[j]++;
                }
            }
            content += "\n";
        }
        content += "------\t------\t------";
        for (int i = 0; i < population.get(0).getObjectives().length; i++) {
            if (p[i] != 0) {
                content += "\t" + sixDecimals.format(medias[i] / p[i]);
            }
        }
        content += "\n";
        Files.addToFile(pathTra, content);

        // Guardar el fichero de los individuos:
        population.stream().filter((s) -> (s.getObjective(0) != Double.NEGATIVE_INFINITY && s.getObjective(0) != Double.POSITIVE_INFINITY)).forEachOrdered((s) -> {
            Files.addToFile(pathTra + "_reg", s.toString());
        });
        content = "";
        
        try {
            problem.readDataset(test);
            population.forEach(i -> problem.evaluate(i));   // Evaluate the population against test data
            
            //Guardar el fichero de todas las medidas de calidad. Guardar todas las clases disponibles
            ArrayList<QualityMeasure> classes = ClassLoader.getClasses();

            // Cabecera
            content += "Rule\tID\tConsequent\tTP\tFP\tTN\tFN";
            for (QualityMeasure q : classes) {
                content += "\t" + q.getShortName();
            }
            content += "\n";

            // datos
            for (int i = 0; i < population.size(); i++) {
                content += i + "\t" + population.get(i).hashCode() + "\t" + population.get(i).getAttribute(Clase.class) + "\t";
                ContingencyTable tabla = (ContingencyTable) population.get(i).getAttribute(ContingencyTable.class);
                if (tabla != null) {
                    content += tabla.getTp() + "\t";
                    content += tabla.getFp() + "\t";
                    content += tabla.getTn() + "\t";
                    content += tabla.getFn() + "\t";
                    for (QualityMeasure q : classes) {

                        q.calculateValue(tabla);
                        content += sixDecimals.format(q.getValue()) + "\t";
                    }
                    content += "\n";
                }
            }
            Files.addToFile(test + "_testMeasures.txt", content);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            Logger.getLogger(ResultWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
