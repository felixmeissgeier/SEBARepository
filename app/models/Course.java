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

    public String title;
    public String remarks;
    public double difficulty;
    public double priority;
    public int color = -1;

    @NotNull
    @As(lang = { "*" }, value = { "yyyy-MM-dd HH:mm" })
    public Date deadline;

    public boolean periodicExercisesNeeded;
    public double exerciseHoursPerWeek;

    @NotNull
    @OneToOne
    public CourseMaterial courseMaterial;

}
