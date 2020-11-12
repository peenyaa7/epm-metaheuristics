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
package es.ujaen.metaheuristicas.qualitymeasures;

import es.ujaen.metaheuristicas.exceptions.InvalidRangeInMeasureException;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Abstract class that represents an statistical quality measure
 *
 * @author Angel Miguel Garcia Vico <agvico at ujaen.es>
 */
public abstract class QualityMeasure implements Cloneable, Serializable, Comparable<QualityMeasure> {

    /**
     * Threshold to check if a value is greater than or equal zero.
     */
    protected double THRESHOLD = 1E-13;
    
    /**
     * @return the short_name
     */
    public String getShort_name() {
        return short_name;
    }

    /**
     * @return the table
     */
    public ContingencyTable getTable() {
        return table;
    }

    /**
     * The value of the quality measure
     */
    protected double value;

    /**
     * The name of the quality measure
     */
    protected String name;

    /**
     * The acronim of the quality measure
     */
    protected String short_name;

    /**
     * The contingencyTable from the values are calculated
     */
    protected ContingencyTable table;

    /**
     * It calculates the value of the given quality measure by means of the
     * given contingency table
     *
     * @param t
     * @return
     */
    public abstract double calculateValue(ContingencyTable t);

    /**
     * Return the last calculated value of the measure
     *
     * @return
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * It checks that the value of the measure is within the domain of the
     * measure
     *
     * @return
     */
    public abstract void validate() throws InvalidRangeInMeasureException;

    /**
     * Returns a copy of this object
     *
     * @return
     */
    @Override
    public abstract QualityMeasure clone();

    @Override
    public String toString() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat sixDecimals = new DecimalFormat("0.000000", symbols);
        return short_name + " = " + sixDecimals.format(value);
    }

    /**
     * Returns the full name of the quality measure
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return short_name;
    }

    @Override
    public abstract int compareTo(QualityMeasure o);

    
    /**
     * Method to check whether a double is zero or not due to the error produced in the double precision.
     * 
     * @param value The value to check if it is 
     * @return 
     */
    public boolean isZero(double value) {
        return value >= -THRESHOLD && value <= THRESHOLD;
    }
    
      /**
     * Method to check whether a double is greater than or equal zero with an error threshold for values near zero
     * 
     * @param value The value to check if it is zero
     * @return 
     */
    public boolean isGreaterTharOrEqualZero(double value) {
        return value >= -THRESHOLD;
    }

}
