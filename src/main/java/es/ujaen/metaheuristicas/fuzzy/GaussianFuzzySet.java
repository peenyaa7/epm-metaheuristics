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
 * Class that defines a gaussian fuzzy set

 * @author agvico
 */
public class GaussianFuzzySet extends FuzzySet {

    /**
     * Constructor with the three points defining the fuzzy set within an arraylist
     * @param values The list of points defining the set
     * @param y The maximum belonging degree
     * @throws es.ujaen.metaheuristicas.exceptions.InvalidFuzzySetException  if the ranges are not valid
     */
    public GaussianFuzzySet(ArrayList<Double> values, double y) throws InvalidFuzzySetException{
        super(values, y);
        if(values.size() != 2){
            throw new InvalidFuzzySetException(this);
        }
    }



    @Override
    public double getBelongingDegree(double x) {
      return Math.exp(-(x - getValue(0)) * (x - getValue(0)) / (2 * getValue(1) * getValue(1)));
	
    }

    @Override
    public String toString() {
        return "Gaussian( " + getValue(0) + ", " + getValue(1) +  ")";
    }
}

