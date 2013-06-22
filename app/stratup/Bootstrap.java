package stratup;

import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

/**
 * Bootstrap class to perform initialisation of the applications. Implements a
 * job called on startup and performing database initialisation.
 * 
 */
@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
	// Check if the database is empty
	if (User.count() == 0) {
	    Fixtures.loadModels("initial-data.yml");
	}
    }
}
