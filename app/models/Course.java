package models;

import java.util.Date;

import org.joda.time.DateTime;

public class Course {
	
	private String title;
	private String remarks;
	private double difficulty;
	private double priority;
	private DateTime deadline;
	
	private boolean periodicExercisesNeeded;
	private double exerciseHoursPerWeek;
	
	private CourseMaterial courseMaterial;
	
	public Course(){
	}
	
	public Course(String title, String remarks, double difficulty, double priority, DateTime deadline, CourseMaterial courseMaterial){
		this.title = title;
		this.remarks = remarks;
		this.difficulty = difficulty;
		this.priority = priority;
		this.deadline = deadline;
		this.courseMaterial = courseMaterial;
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
	public CourseMaterial getCourseMaterial() {
		return courseMaterial;
	}
	public void setCourseMaterial(CourseMaterial courseMaterial) {
		this.courseMaterial = courseMaterial;
	}

	
	
}
