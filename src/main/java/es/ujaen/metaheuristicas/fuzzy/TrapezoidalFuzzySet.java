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

package es.ujaen.metaheuristicas.fuzzy;

import java.util.ArrayList;
import es.ujaen.metaheuristicas.exceptions.InvalidFuzzySetException;

/**
 * Class that defines a trapezoidal fuzzy set

 * @author agvico
 */
public class TrapezoidalFuzzySet extends FuzzySet {

    /**
     * Constructor with the three points defining the fuzzy set within an arraylist
     * @param values The list of points defining the set
     * @param y The maximum belonging degree
     * @throws es.ujaen.metaheuristicas.exceptions.InvalidFuzzySetException  if the ranges are not valid
     */
    public TrapezoidalFuzzySet(ArrayList<Double> values, double y) throws InvalidFuzzySetException{
        super(values, y);
        if(values.size() != 4){
            throw new InvalidFuzzySetException(this);
        }
        if(values.get(0) > values.get(1) || values.get(0) > values.get(2) || values.get(1) > values.get(2)){
            throw new InvalidFuzzySetException(this);
        }
    }



    @Override
    public double getBelongingDegree(double x) {
      double x0 = getValue(0);
      double x1 = getValue(1);
      double x2 = getValue(2);
      double x3 = getValue(3);

        if( (x < x0) || (x > x3) ) return 0;

        // Between 'midLow' and 'midHigh' => 1
        if( (x >= x1) && (x <= x2) ) return 1;

        // Between 'min' and 'midLow'
        if( x < x1 ) return ((x - x0) / (x1 - x0));

        // Between 'midHigh' and 'max'
        return 1 - ((x - x2) / (x3 - x2));

    }

    @Override
    public String toString() {
        return "Trapezoidal( " + getValue(0) + ", " + getValue(1) + ", " + getValue(2) + ", " + getValue(3) + ")";
    }
}
