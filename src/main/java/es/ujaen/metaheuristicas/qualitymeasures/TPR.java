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
public final class TPR extends QualityMeasure {

    public TPR() {
        super.name = "True Positive Rate";
        super.short_name = "TPR";
        super.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        if (t.getTp() + t.getFn() == 0) {
            setValue(0.0);
        } else {
            setValue((double) t.getTp() / (double) (t.getTp() + t.getFn()));
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

        TPR a = new TPR();
        a.name = this.name;
        a.setValue(this.value);

        return a;

    }


    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof TPR)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            return Double.compare(this.value, o.value);
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
