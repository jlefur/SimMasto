package simmasto0.util;

import cern.jet.random.engine.RandomEngine;
import repast.simphony.random.RandomHelper;

/** This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>. Mark N.
 * Read, 2016. Represents a Levy distributed random variable. This is calculated using the method outlined in Jacobs's "Stochastic
 * Processes for Physicists". 2010, Cambridge University Press. Makes use of MASON's MersenneTwisterFast random number generator.
 * The MASon simulation framework can be obtained for free from http://cs.gmu.edu/~eclab/projects/mason/
 * @see http://markread.info/2016/08/code-to-generate-a-levy-distribution/
 * @author Mark N. Read, adapted J.Le Fur 04.2018 */
public class LevyDistribution {
	public static RandomEngine randomGeneratorForLevyFlight = RandomHelper.registerGenerator("", 254983425);
	private static double bounded_uniform(double low, double high) {
		// returns a double in inverval (0,1). IE, neither zero nor one will be returned.
		double x = randomGeneratorForLevyFlight.nextDouble();

		double range = high - low;
		x *= range; // scale onto the required range of values
		x += low; // translate x onto the values requested

		return x;
	}

	/** Samples a Levy distribution wherein the power law decay can be adjusted between 1/x and 1/x^3. This method is based on
	 * that found in section 9.2.2 of Jacobs's "Stochastic Processes for Physicists". 2010, Cambridge University Press. Note that
	 * this sampling method can return negative values. Values are symmetrical around zero.
	 * @param mu must lie between 1 and 3. Correspods to 1/x and 1/x^3
	 * @return */
	public static double sample(double mu) {
		double X = bounded_uniform(-Math.PI / 2.0, Math.PI / 2.0);
		// Retrieve a value between (0,1) (does not include 0 or 1 themselves)
		double Y = -Math.log(randomGeneratorForLevyFlight.nextDouble());
		double alpha = mu - 1.0;
		// there's a lot going on here, written over several lines to aid clarity.
		/*
		 * a = math.sin( (mu - 1.0) * x ) / (math.pow(math.cos(x), (1.0 / (mu - 1.0)))) b = math.pow( (math.cos((2.0 - mu) * x) /
		 * y), ((2.0 - mu) / (mu - 1.0)) ) z = a * b
		 */
		double a = Math.sin(alpha * X) / Math.pow(Math.cos(X), 1.0 / alpha);
		double b = Math.pow(Math.cos((2.0 - mu) * X) / Y, (2.0 - mu) / alpha);
		// double b = Math.pow(Math.cos((1.0 - alpha) * X) / Y, (1.0 - alpha) / alpha);
		return a * b;
	}

	/** Same as above, but ensures all values are positive. Negative values are simply negated, as the Levy distribution
	 * represented is symmetrical around zero.
	 * @param mu
	 * @return */
	public static double sample_positive(double mu, double scale) {
		double l = sample(mu) * scale;
		if (l < 0.0) { return -1.0 * l; }
		return l;
	}

	/** Default value case, scale=1 */
	public static double sample_positive(double mu) {
		return sample_positive(mu, 1.0);
	}
	public static void main(String[] args) {
		System.out.println(sample_positive(5.));
	}
}
