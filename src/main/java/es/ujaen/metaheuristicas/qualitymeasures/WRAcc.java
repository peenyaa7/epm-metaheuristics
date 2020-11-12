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
public final class WRAcc extends QualityMeasure {

    public WRAcc() {
        super.name = "Weighted Relative Accuracy";
        super.short_name = "WRAcc";
        super.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        try {
            // Calculate the coverage
            double cov = 0.0; // Change with Coverage class when it is available
            if (t.getTotalExamples() != 0) {
                cov = (double) (t.getTp() + t.getFp()) / (double) t.getTotalExamples();
            }

            // Calculate the confidence
            Confidence conf = new Confidence();
            conf.calculateValue(t);
            conf.validate();

            // Calculate the class percentage with respect to the total examples
            double class_pct = 0.0;
            if (t.getTotalExamples() != 0) {
                class_pct = (double) (t.getTp() + t.getFn()) / (double) t.getTotalExamples();
            }

            // Calculate the value
            setValue(cov * (conf.value - class_pct));
        } catch (InvalidRangeInMeasureException ex) {
            ex.showAndExit(this);
        }
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }



    @Override
    public QualityMeasure clone() {
        WRAcc a = new WRAcc();
        a.name = this.name;
        a.setValue(this.value);

        return a;
    }


    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof WRAcc)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            return Double.compare(this.value, o.value);
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
