package models;

public class CourseMaterial {
	private int scriptPagesDone = 0;
	private int scriptPagesToDo = 0;
	private float workloadHoursExpected = 0;
	private boolean considerExpectedHours = false;
	
	public CourseMaterial(int scriptPagesToDo, int scriptPagesDone){
		this.scriptPagesDone = scriptPagesDone; 
		this.scriptPagesToDo = scriptPagesToDo;
		this.considerExpectedHours = false;
	}
	
	public CourseMaterial(float workloadHoursExpected){
		this.workloadHoursExpected = workloadHoursExpected;
		this.considerExpectedHours = true;
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
	public float getWorkloadHoursExpected() {
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
