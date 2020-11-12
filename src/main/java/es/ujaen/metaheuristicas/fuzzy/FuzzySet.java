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

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class FuzzySet {

    /**
     * The maximum belonging degree to be returned
     */
    private double y;

    /**
     * The values that defines the fuzzy set.
     */
    protected ArrayList<Double> values;


    public FuzzySet(ArrayList<Double> values, double y){
        this.values = (ArrayList) values.clone();
        this.y = y;
    }


    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public double getValue(int index){
        return values.get(index);
    }

    /**
     * Sets a value in the given fuzzy definition.
     * 
     * @param index
     * @param value 
     */
    public void setValue(int index, double value){
        values.set(index, value);
    }


    /**
     * It returns the belonging degree of the value of {@code x} with respect to this fuzzy set.
     * @param x
     * @return
     */
    public abstract double getBelongingDegree(double x);

    public abstract String toString();

}
