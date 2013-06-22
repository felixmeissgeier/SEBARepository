package dto;

import models.CourseMaterial;

/**
 * DTO for a course material object used to transfer the data between
 * controllers and views.
 * 
 */
public class CourseMaterialDTO {
    private int scriptPagesDone = 0;
    private int scriptPagesToDo = 0;
    private double workloadHoursExpected = 0;
    private boolean considerExpectedHours = false;

    public CourseMaterialDTO(int scriptPagesToDo, int scriptPagesDone) {
	this.scriptPagesDone = scriptPagesDone;
	this.scriptPagesToDo = scriptPagesToDo;
	this.considerExpectedHours = false;
    }

    public CourseMaterialDTO(double workloadHoursExpected) {
	this.workloadHoursExpected = workloadHoursExpected;
	this.considerExpectedHours = true;
    }

    public CourseMaterialDTO(CourseMaterial courseMaterial) {
	this(courseMaterial.scriptPagesToDo, courseMaterial.scriptPagesDone);
	considerExpectedHours = courseMaterial.considerExpectedHours;
	workloadHoursExpected = courseMaterial.workloadHoursExpected;
    }

    public int getScriptPagesDone() {
	return scriptPagesDone;
    }

    public void setScriptPagesDone(int scriptPagesDone) {
	this.scriptPagesDone = scriptPagesDone;
    }

    public int getScriptPagesToDo() {
	return scriptPagesToDo;
    }

    public void setScriptPagesToDo(int scriptPagesToDo) {
	this.scriptPagesToDo = scriptPagesToDo;
    }

    public double getWorkloadHoursExpected() {
	return workloadHoursExpected;
    }

    public void setWorkloadHoursExpected(float workloadHoursExpected) {
	this.workloadHoursExpected = workloadHoursExpected;
    }

    public boolean isConsiderExpectedHours() {
	return considerExpectedHours;
    }

    public void setConsiderExpectedHours(boolean considerExpectedHours) {
	this.considerExpectedHours = considerExpectedHours;
    }
}
