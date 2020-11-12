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
 * Confidence Quality Measure. It measures the precision of a pattern with
 * respect to the examples it covers.
 * <p>
 * Ref.: U. M. Fayyad, G. Piatetsky-Shapiro, and P. Smyth. From data mining to
 * knowledge discovery: an overview. In Advances in knowledge discovery and data
 * mining, pages 1–34. AAAI/MIT Press, 1996.
 * </p>
 *
 * @author Angel Miguel Garcia Vico <agvico at ujaen.es>
 */
public final class Confidence extends QualityMeasure {

    public Confidence() {
        super.name = "Confidence";
        super.short_name = "CONF";
        super.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        if (t.getTp() + t.getFp() == 0) {
            setValue(0.0);
        } else {
            setValue((double) t.getTp() / (double) (t.getTp() + t.getFp()));
        }
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (!(value <= 1.0 && isGreaterTharOrEqualZero(value)) || Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }


    @Override
    public QualityMeasure clone() {
        Confidence a = new Confidence();
        a.setValue(this.value);

        return a;
    }

    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof Confidence)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            return Double.compare(this.value, o.value);
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
