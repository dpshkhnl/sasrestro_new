package sasrestro.misc;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class to perform BigDecimal divisions and multiplications.
 */
public class BigDecimalArithmetics {
	public double getProduct(double dbl1, double dbl2) {
		BigDecimal bd1 = new BigDecimal(dbl1);
		BigDecimal bd2 = new BigDecimal(dbl2);

		return bd1.multiply(bd2).setScale(2, RoundingMode.HALF_EVEN)
				.doubleValue();
	}

	public double getDivision(double number, double divisor) {
		BigDecimal bd1 = new BigDecimal(number);
		BigDecimal bd2 = new BigDecimal(divisor);

		return bd1.divide(bd2, 2, RoundingMode.HALF_EVEN).doubleValue();
	}

	public double getSum(double dbl1, double dbl2) {
		BigDecimal bd1 = new BigDecimal(dbl1);
		BigDecimal bd2 = new BigDecimal(dbl2);

		return bd1.add(bd2).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
	}

	public double getFormatted(String number) {
		if (number.equals("")) {
			return 0d;
		} else {
			BigDecimal bd = new BigDecimal(number);

			return bd.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
		}
	}

}
