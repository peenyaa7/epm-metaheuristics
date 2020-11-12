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
package es.ujaen.metaheuristicas.operators.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.BinarySolution;

import java.util.Random;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class BiasedMutationDNF implements MutationOperator<BinarySolution> {

    /**
     * The mutation probability
     */
    double mutationProb;


    public BiasedMutationDNF(double mutProb){
        this.mutationProb = mutProb;
    }

    @Override
    public BinarySolution execute(BinarySolution binarySolution) {
        return doMutation(binarySolution);
    }

    private BinarySolution doMutation(BinarySolution binarySolution) {
        JMetalRandom rand = JMetalRandom.getInstance();
        if(rand.nextDouble() <= mutationProb) {
            int var = rand.nextInt(0, binarySolution.getNumberOfVariables() -1);

            if (rand.nextDouble(0.0, 1.0) <= mutationProb) {
                // remove variable
                binarySolution.getVariableValue(var).clear();
            } else {
                // random flip of variables
                for (int i = 0; i < binarySolution.getNumberOfBits(var); i++) {
                    if (rand.nextDouble(0.0, 1.0) <= 0.5) {
                        binarySolution.getVariableValue(var).flip(i);
                    }
                }
            }
        }

        return binarySolution;
    }


}
