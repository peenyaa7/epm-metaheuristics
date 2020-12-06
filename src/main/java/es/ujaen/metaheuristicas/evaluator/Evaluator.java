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
package es.ujaen.metaheuristicas.evaluator;

import es.ujaen.metaheuristicas.exceptions.InvalidRangeInMeasureException;
import es.ujaen.metaheuristicas.fuzzy.FuzzySet;
import org.uma.jmetal.solution.Solution;
import es.ujaen.metaheuristicas.qualitymeasures.ContingencyTable;
import es.ujaen.metaheuristicas.qualitymeasures.QualityMeasure;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public abstract class Evaluator extends SequentialSolutionListEvaluator<BinarySolution>{

    public ArrayList<QualityMeasure> getObjectives() {
        return objectives;
    }

    public void setObjectives(ArrayList<QualityMeasure> objectives) {
        this.objectives = objectives;
    }

    /**
     * The objectives to be used in the evaluator.
     * These are the objectives employed for guiding the search process and they are used only for its identification.
     */
    private ArrayList<QualityMeasure>  objectives;


    /**
     * It calculates the quality measures given a contingency table
     *
     * @param confMatrix
     */
    public ArrayList<QualityMeasure> calculateMeasures(ContingencyTable confMatrix) {

        ArrayList<QualityMeasure> objs = (ArrayList) objectives.clone();

        // Calculates the value of each measure
        objs.forEach(q -> {
            try {
                q.calculateValue(confMatrix);
                q.validate();
            } catch (InvalidRangeInMeasureException ex) {
                System.err.println("Error while evaluating Individuals: ");
                ex.showAndExit(this);
            }
        });

        return objs;
    }


    /**
     * It performs the evaluation of the individuals using the labels and the dataset (if necessary)
     * @param individual
     * @param fuzzySet
     * @param dataset
     */
    public abstract void doEvaluation(Solution individual, List<List<FuzzySet>> fuzzySet, Instances dataset);


    /**
     * It return whether the individual represents the empty pattern or not.
     *
     * @param individual
     * @return
     */
    public abstract boolean isEmpty(Solution individual);

    /**
     * It returns whether a given variable of the individual participates in the pattern or not.
     *
     * @param individual
     * @param var
     * @return
     */
    public abstract boolean participates(Solution individual, int var);

}
