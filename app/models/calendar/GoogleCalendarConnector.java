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

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class GoogleCalendarConnector implements CalendarConnector{
	
	private URL privateCalendarURL = null;
	private HttpURLConnection httpConnection = null;
	
	public GoogleCalendarConnector(URL privateCalendarURL){
		this.privateCalendarURL = privateCalendarURL;
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
	}

	@Override
	public CalendarFeed receiveCalendarFeed() {
		CalendarFeed feed = null;

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
	        
	        feed = new CalendarFeed(receivedGoogleCalendar);
	        
//	        Serializer serializer = new Persister();
//	        try {
//				feed = serializer.read(GoogleCalendarFeed.class, responseXML);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return feed;
	}	
}
