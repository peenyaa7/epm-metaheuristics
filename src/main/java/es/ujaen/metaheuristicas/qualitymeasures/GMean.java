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

import es.ujaen.metaheuristicas.exceptions.InvalidMeasureComparisonException;
import es.ujaen.metaheuristicas.exceptions.InvalidRangeInMeasureException;

/**
 *
 * @author angel
 */
public final class GMean extends QualityMeasure {

    public GMean() {
        super.name = "Geometric Mean";
        super.short_name = "G-Mean";
        super.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        try {
            TPR tpr = new TPR();
            tpr.calculateValue(t);
            tpr.validate();

            TNR tnr = new TNR();
            tnr.calculateValue(t);
            tnr.validate();
            
            setValue(Math.sqrt(tpr.value * tnr.value));

        } catch (InvalidRangeInMeasureException ex) {
           ex.showAndExit(this);
        }
        return value;

    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (!(isGreaterTharOrEqualZero(value) && value <= 1.0) || Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }


    @Override
    public QualityMeasure clone() {
        GMean a = new GMean();
        a.name = this.name;
        a.setValue(this.value);

        return a;
    }



    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof GMean)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            return Double.compare(this.value, o.value);
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;    }

}
