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
package es.ujaen.metaheuristicas.exceptions;

import es.ujaen.metaheuristicas.qualitymeasures.QualityMeasure;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agvico
 */
public class InvalidMeasureComparisonException extends Exception {

    public InvalidMeasureComparisonException(QualityMeasure one, QualityMeasure other) {
        super("Invalid Comparison. You are trying to compare \"" +
                one.getShort_name() + "\" against \"" + 
                other.getShort_name() + "\". You are doing: " + other.getShort_name() + 
                ".compareTo(" + one.getShortName() + ")");
    }

    /**
     * It shows the message in the estandard error and the stack trace and exit
     * the program with error code = 2.
     *
     * @param clas The class where the error ocurred, in order to be shown in
     * the stack trace
     */
    public void showAndExit(Object clas) {
        Logger.getLogger(clas.getClass().getName()).log(Level.SEVERE, null, this);
        System.err.println(super.getMessage());
        System.exit(2);
    }
}
