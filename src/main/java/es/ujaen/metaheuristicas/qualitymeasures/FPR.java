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
public final class FPR extends QualityMeasure {

    public FPR() {
        super.name = "False Positive Rate";
        super.short_name = "FPR";
        super.value = 1.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        if (t.getFp() + t.getTn() == 0) {
            setValue(1.0);
        } else {
            setValue((double) t.getFp() / (double) (t.getFp() + t.getTn()));
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
        FPR a = new FPR();
        a.setValue(this.value);

        return a;
    }


    @Override
    public int compareTo(QualityMeasure o) {
        
        // THIS MEASURES MUST BE MINIMISED !!!!
        try {
            if (!(o instanceof FPR)) {
                throw new InvalidMeasureComparisonException(this, o);
            }
            return Double.compare(o.value, this.value);

        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
