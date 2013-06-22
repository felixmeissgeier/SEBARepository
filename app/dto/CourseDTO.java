package dto;

import java.util.Random;

import models.Course;

import org.joda.time.DateTime;

/**
 * DTO for a course object used to transfer the data between controllers and
 * views.
 * 
 */
public class CourseDTO {

    private Long id;
    private String title;
    private String remarks;
    private double difficulty;
    private double priority;
    private DateTime deadline;
    private String hint;

    private boolean periodicExercisesNeeded;
    private double exerciseHoursPerWeek;

    private CourseMaterialDTO courseMaterial;

    public CourseDTO() {
    }

    public CourseDTO(Long id, String title, String remarks, double difficulty,
	    double priority, DateTime deadline, CourseMaterialDTO courseMaterial) {
	this.id = id;
	this.title = title;
	this.remarks = remarks;
	this.difficulty = difficulty;
	this.priority = priority;
	this.deadline = deadline;
	this.courseMaterial = courseMaterial;
    }

    public CourseDTO(Course cource) {
	this(cource.id, cource.title, cource.remarks, cource.difficulty,
		cource.priority, new DateTime(cource.deadline.getTime()),
		new CourseMaterialDTO(cource.courseMaterial));
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    public double getDifficulty() {
	return difficulty;
    }

    public void setDifficulty(double difficulty) {
	this.difficulty = difficulty;
    }

    public double getPriority() {
	return priority;
    }

    public void setPriority(double priority) {
	this.priority = priority;
    }

    public DateTime getDeadline() {
	return deadline;
    }

    public void setDeadline(DateTime deadline) {
	this.deadline = deadline;
    }

    public boolean isPeriodicExercisesNeeded() {
	return periodicExercisesNeeded;
    }

    public void setPeriodicExercisesNeeded(boolean periodicExercisesNeeded) {
	this.periodicExercisesNeeded = periodicExercisesNeeded;
    }

    public double getExerciseHoursPerWeek() {
	return exerciseHoursPerWeek;
    }

    public void setExerciseHoursPerWeek(double exerciseHoursPerWeek) {
	this.exerciseHoursPerWeek = exerciseHoursPerWeek;
    }

    public CourseMaterialDTO getCourseMaterial() {
	return courseMaterial;
    }

    public void setCourseMaterial(CourseMaterialDTO courseMaterial) {
	this.courseMaterial = courseMaterial;
    }

    public String getHint() {
	if (hint != null) {
	    return hint;
	}
	

	// CSOFF: MagicNumber
	switch (new Random().nextInt(10)) {
 	case 0:
	    return "2nd exercise deadline tomorrow";
	case 1:
	    return "You are one week ahead";
	case 2:
	    return "[!] 3 study slots missed";
	case 3:
	    return "Midterm exam in 2 weeks";
	case 4:
	    return "Spend more time on this";
	default:
	    // Empty placeholder
	    return new String(new char[25]).replaceAll("\0", "\u00A0");
	}
	// CSON: MagicNumber
    }

    public void setHint(String hint) {
	this.hint = hint;
    }
}
