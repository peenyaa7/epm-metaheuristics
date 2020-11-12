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

import es.ujaen.metaheuristicas.exceptions.InvalidContingencyTableException;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;

/**
 * Class to represent a contingency table.
 * 
 * @author Angel Miguel Garcia Vico <agvico at ujaen.es>
 */
public class ContingencyTable {
    
    /**
     * The true positives
     */
    private int tp;
    
    /**
     * The false positives
     */
    private int fp;
    
    /**
     * The true negatives
     */
    private int tn;
    
    /**
     * The false negatives
     */
    private int fn;
    
    /**
     * The Contingency Table constructor.
     * 
     * @param tp
     * @param fp
     * @param tn
     * @param fn 
     */
    public ContingencyTable(int tp, int fp, int tn, int fn){
        this.tp = tp;
        this.fn = fn;
        this.fp = fp;
        this.tn = tn;
    }
    

    /**
     * @return the tp
     */
    public int getTp() {
        return tp;
    }

    /**
     * @param tp the tp to set
     */
    public void setTp(int tp) {
        this.tp = tp;
    }

    /**
     * @return the fp
     */
    public int getFp() {
        return fp;
    }

    /**
     * @param fp the fp to set
     */
    public void setFp(int fp) {
        this.fp = fp;
    }

    /**
     * @return the tn
     */
    public int getTn() {
        return tn;
    }

    /**
     * @param tn the tn to set
     */
    public void setTn(int tn) {
        this.tn = tn;
    }

    /**
     * @return the fn
     */
    public int getFn() {
        return fn;
    }

    /**
     * @param fn the fn to set
     */
    public void setFn(int fn) {
        this.fn = fn;
    }
    
    /**
     * Gets the total number of observations in the contingency table
     * 
     * @return 
     */
    public double getTotalExamples(){
        return (double)(fn + tp + tn +fp);
    }
    
    
    public String toString(){
        return "TP: " + tp + "  FP: " + fp + "  TN: " + tn + "  FN: " + fn;
    }
    
    /**
     * It checks if the contingency table is correctly created.
     * @throws InvalidContingencyTableException 
     */
    public void validate() throws InvalidContingencyTableException {
        if(tp < 0 || fp < 0 || tn < 0 || fn < 0){
            throw new InvalidContingencyTableException(this);
        }
    }
}
