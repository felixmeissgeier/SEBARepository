package dto;

import models.Course;

import org.joda.time.DateTime;

public class CourseDTO {

	private String title;
	private String remarks;
	private double difficulty;
	private double priority;
	private DateTime deadline;

	private boolean periodicExercisesNeeded;
	private double exerciseHoursPerWeek;

	private CourseMaterialDTO courseMaterial;

	public CourseDTO() {
	}

	public CourseDTO(String title, String remarks, double difficulty,
			double priority, DateTime deadline, CourseMaterialDTO courseMaterial) {
		this.title = title;
		this.remarks = remarks;
		this.difficulty = difficulty;
		this.priority = priority;
		this.deadline = deadline;
		this.courseMaterial = courseMaterial;
	}

	public CourseDTO(Course cource) {
		this(cource.title, cource.remarks, cource.difficulty, cource.priority,
				new DateTime(cource.deadline.getTime()), new CourseMaterialDTO(
						cource.courseMaterial));
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

}
