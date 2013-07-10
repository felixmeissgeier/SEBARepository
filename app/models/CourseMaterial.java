package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * Database object for course material.
 * 
 */
@Entity
public class CourseMaterial extends Model {

    // CSOFF: VisibilityModifierCheck
    public int scriptPagesDone = 0;
    public int scriptPagesToDo = 0;
    public double workloadHoursExpected = 0;
    public boolean considerExpectedHours = false;
    // CSON: VisibilityModifierCheck

}
