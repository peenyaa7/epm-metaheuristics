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
 * @author agvico
 */
public final class WRAccNorm extends QualityMeasure {

    public WRAccNorm() {
        super.value = 0.0;
        super.name = "Weighter Relative Accuracy (Normalised)";
        super.short_name = "WRAcc_Norm";
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        double classPct = 0.0;
        if (t.getTotalExamples() != 0) {
            classPct = (double) (t.getTp() + t.getFn()) / (double) t.getTotalExamples();
        }

        double minUnus = (1.0 - classPct) * (0.0 - classPct);
        double maxUnus = classPct * (1.0 - classPct);

        if (maxUnus - minUnus != 0) {
            try {
                WRAcc unus = new WRAcc();
                unus.calculateValue(t);
                unus.validate();
                setValue((unus.value - minUnus) / (maxUnus - minUnus));
            } catch (InvalidRangeInMeasureException ex) {
                ex.showAndExit(this);
            }
        } else {
            setValue(0.0);
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
        WRAccNorm a = new WRAccNorm();
        a.name = this.name;
        a.setValue(this.value);

        return a;
    }


    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof WRAccNorm)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            return Double.compare(this.value, o.value);
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
