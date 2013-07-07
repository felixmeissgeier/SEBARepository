package helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.subtypes.ServiceSubscriptionPeriod;

/**
 * Helper class providing auxiliary methods to manage users.
 * 
 */
public final class UserHelper {

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
	    return 0;
	case ONE_MONTH:
	    return 1;
	case THREE_MONTHS:
	    return 3;
	case SIX_MONTHS:
	    return 6;
	case TWELWE_MONTHS:
	    return 12;
	default:
	    return 0;
	}
    }

    public static ServiceSubscriptionPeriod monthsToSubscriptionPeriod(int months) {
	switch (months) {
	case 0:
	    return ServiceSubscriptionPeriod.FREE;
	case 1:
	    return ServiceSubscriptionPeriod.ONE_MONTH;
	case 3:
	    return ServiceSubscriptionPeriod.THREE_MONTHS;
	case 6:
	    return ServiceSubscriptionPeriod.SIX_MONTHS;
	case 12:
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
