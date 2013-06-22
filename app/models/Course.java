package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import play.data.binding.As;
import play.db.jpa.Model;

/**
 * Database model of a course object. 
 *
 */
@Entity
public class Course extends Model {

	// @ManyToOne
	// public User user;
	
	public String title;
	public String remarks;
	//hours per DIFFICULTY_RELATION_PAGES_PER_X_HOURS script-pages
	public double difficulty;
	public double priority;
	
	@NotNull
	@As(lang={"*"}, value={"yyyy-MM-dd HH:mm"}) 
	public Date deadline;
	
	public boolean periodicExercisesNeeded;
	public double exerciseHoursPerWeek;

	@NotNull
	@OneToOne
	public CourseMaterial courseMaterial;
}
