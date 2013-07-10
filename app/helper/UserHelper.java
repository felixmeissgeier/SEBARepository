package helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.subtypes.ServiceSubscriptionPeriod;

/**
 * Helper class providing auxiliary methods to manage users.
 * 
 */
public final class UserHelper {
	
	public static final int FREE_VALUE = 0;
	public static final int ONE_MONTH_VALUE = 1;
	public static final int THREE_MONTH_VALUE = 3;
	public static final int SIX_MONTH_VALUE = 6;
	public static final int TWELWE_MONTH_VALUE = 12;

	private UserHelper() {

	}

	public static String subscriptionPeriodToText(ServiceSubscriptionPeriod serviceSubscriptionPeriod) {
		switch (serviceSubscriptionPeriod) {
		case FREE:
			return "Free";
		case ONE_MONTH:
			return "1 month";
		case THREE_MONTHS:
			return "3 months";
		case SIX_MONTHS:
			return "6 months";
		case TWELWE_MONTHS:
			return "12 months";
		default:
			return "";
		}
	}

	public static int subscriptionPeriodToMonths(ServiceSubscriptionPeriod serviceSubscriptionPeriod) {
		switch (serviceSubscriptionPeriod) {
		case FREE:
			return FREE_VALUE;
		case ONE_MONTH:
			return ONE_MONTH_VALUE;
		case THREE_MONTHS:
			return THREE_MONTH_VALUE;
		case SIX_MONTHS:
			return SIX_MONTH_VALUE;
		case TWELWE_MONTHS:
			return TWELWE_MONTH_VALUE;
		default:
			return FREE_VALUE;
		}
	}

	public static ServiceSubscriptionPeriod monthsToSubscriptionPeriod(int months) {
		switch (months) {
		case FREE_VALUE:
			return ServiceSubscriptionPeriod.FREE;
		case ONE_MONTH_VALUE:
			return ServiceSubscriptionPeriod.ONE_MONTH;
		case THREE_MONTH_VALUE:
			return ServiceSubscriptionPeriod.THREE_MONTHS;
		case SIX_MONTH_VALUE:
			return ServiceSubscriptionPeriod.SIX_MONTHS;
		case TWELWE_MONTH_VALUE:
			return ServiceSubscriptionPeriod.TWELWE_MONTHS;
		default:
			return ServiceSubscriptionPeriod.FREE;
		}
	}

	public static String expirationDateToString(Date expirationDate) {
		if (expirationDate == null || expirationDate.before(new Date())) {
			return "forever";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return sdf.format(expirationDate);
	}

}
