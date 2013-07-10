package models.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * Implementation of an {@link CalendarConnector} for Google Calendar.
 * 
 */
public class GoogleCalendarConnector implements CalendarConnector {

	private static final int MAX_CONNECT_TIMEOUT = 10000;
	private URL privateCalendarURL = null;
	private HttpURLConnection httpConnection = null;
	private CalendarFeed latestCalendarFeed = null;

	public GoogleCalendarConnector(URL privateCalendarURL) {
		this.privateCalendarURL = privateCalendarURL;
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
	}

	@Override
	public void receiveCalendarFeed() throws IOException, ParserException {

		httpConnection = (HttpURLConnection) privateCalendarURL.openConnection();
		httpConnection.setRequestMethod("GET");
		httpConnection.setDoOutput(true);
		httpConnection.setReadTimeout(MAX_CONNECT_TIMEOUT);
		httpConnection.connect();

		BufferedReader rd = null;
		rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = rd.readLine()) != null) {
			sb.append(line + '\n');
		}

		String responseICS = sb.toString();

		// System.out.println(responseICS);
		StringReader sin = new StringReader(responseICS);
		CalendarBuilder builder = new CalendarBuilder();
		Calendar receivedGoogleCalendar = null;
		receivedGoogleCalendar = builder.build(sin);
		latestCalendarFeed = new CalendarFeed(receivedGoogleCalendar);
	}

	@Override
	public CalendarFeed getCalendarFeed() {
		return latestCalendarFeed;
	}

}
