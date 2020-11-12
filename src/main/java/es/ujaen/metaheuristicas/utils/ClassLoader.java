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
package es.ujaen.metaheuristicas.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import es.ujaen.metaheuristicas.qualitymeasures.QualityMeasure;

/**
 * A Class to load the classes of the quality measures
 *
 * @author Angel Miguel Garcia Vico <agvico at ujaen.es>
 */
public class ClassLoader {

    /**
     * The names of the class of each quality measure that should be used.
     *
     * If you want to add new measures, add them to the
     * moa.subgroupdiscovery.qualitymeasures package and after that, add the
     * name of the class here in order to be used by the algorithm
     */
    private static String[] measureClassNames = {"AUC",
        "Accuracy",
        "Confidence",
        "Coverage",
        "FPR",
        "GMean",
        "GrowthRate",
        "IsGrowthRate",
        "Jaccard",
        "SuppDiff",
        "Support",
        "TNR",
        "TPR",
        "WRAcc",
        "WRAccNorm"};

    /**
     * Returns the classes that represents the quality measures that are
     * available on the framework.
     *
     * This measures are found on the src/moa/subgroupdiscovery/qualitymeasures
     * folder under the package "qualitymeasures".
     *
     * @return An ArrayList, with all the QualityMeasure classes of the
     * measures.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static ArrayList<QualityMeasure> getClasses() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // Sort the array of class names 
        Arrays.sort(measureClassNames, String.CASE_INSENSITIVE_ORDER);
        ArrayList<QualityMeasure> measures = new ArrayList<>();
        for (String i : measureClassNames) {
            Class a = Class.forName(QualityMeasure.class.getPackage().getName() + "." + i);
            Object instance = a.newInstance();
            measures.add((QualityMeasure) instance);
        }
        return measures;
    }
}
