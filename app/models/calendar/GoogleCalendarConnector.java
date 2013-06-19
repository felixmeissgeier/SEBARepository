package models.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import models.timetable.TimetableEntry;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class GoogleCalendarConnector implements CalendarConnector{
	
	private URL privateCalendarURL = null;
	private HttpURLConnection httpConnection = null;
	private CalendarFeed latestCalendarFeed = null;
	
	public GoogleCalendarConnector(URL privateCalendarURL){
		this.privateCalendarURL = privateCalendarURL;
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
	}

	@Override
	public void receiveCalendarFeed() {

		try {
			httpConnection = (HttpURLConnection) privateCalendarURL.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoOutput(true);
			httpConnection.setReadTimeout(10000);
			httpConnection.connect();
			
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));			
	        StringBuilder sb = new StringBuilder();	        
	        String line = null;			
	        while ((line = rd.readLine()) != null)
			{
			    sb.append(line + '\n');
			}
			
	        String responseICS = sb.toString();
	     
	        //System.out.println(responseICS);
	        StringReader sin = new StringReader(responseICS);
	        CalendarBuilder builder = new CalendarBuilder();
	        Calendar receivedGoogleCalendar = null;
	        try {
	        	receivedGoogleCalendar = builder.build(sin);
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        latestCalendarFeed = new CalendarFeed(receivedGoogleCalendar);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public CalendarFeed getCalendarFeed() {
		return latestCalendarFeed;
	}

}
