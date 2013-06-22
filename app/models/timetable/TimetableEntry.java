package models.timetable;

import java.awt.Color;

import org.joda.time.DateTime;

/**
 * Single slot in the time table. Stores all the information required to render
 * the slot.
 * 
 */
public class TimetableEntry implements Comparable<TimetableEntry> {

    /**
     * Type of the timetable entry.
     * 
     */
    public enum TimetableEntryType {
	EXTERNAL, SCHEDULED_LEARNINGSLOT, SCHEDULED_EXERCISESLOT, BLOCKED
    }

    private String title;
    private String description;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private TimetableEntryType entryType;
    private Color entryColor;

    public TimetableEntry() {
    }

    public TimetableEntry(String title, String description, DateTime startDateTime, DateTime endDateTime,
	    TimetableEntryType entryType, Color color) {
	this.title = title;
	this.description = description;
	this.startDateTime = startDateTime;
	this.endDateTime = endDateTime;
	this.entryType = entryType;
	if (color != null) {
	    this.entryColor = color;
	} else {
	    this.entryColor = new Color(120, 130, 140);
	}
    }

    public void setColor(int r, int g, int b) {
	entryColor = new Color(r, g, b);
    }

    public void setColor(Color color) {
	entryColor = color;
    }

    public Color getColor() {
	return entryColor;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public DateTime getStartDateTime() {
	return startDateTime;
    }

    public void setStartDateTime(DateTime startDateTime) {
	this.startDateTime = startDateTime;
    }

    public DateTime getEndDateTime() {
	return endDateTime;
    }

    public void setEndDateTime(DateTime endDateTime) {
	this.endDateTime = endDateTime;
    }

    public TimetableEntryType getEntryType() {
	return entryType;
    }

    public void setEntryType(TimetableEntryType entryType) {
	this.entryType = entryType;
    }

    @Override
    public int compareTo(TimetableEntry o) {
	if (o.getStartDateTime().isAfter(this.startDateTime)) {
	    return -1;
	} else if (o.getStartDateTime().isBefore(this.startDateTime)) {
	    return 1;
	}

	return 0;
    }
}
