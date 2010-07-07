package uk.ac.ox.oucs.vle;

import java.util.Date;
import java.util.List;
import java.util.Set;

import uk.ac.ox.oucs.vle.CourseSignupService.Status;

public interface CourseDAO {

	CourseGroupDAO findCourseGroupById(String courseId);
	
	CourseGroupDAO findAvailableCourseGroupById(String courseId);
	
	List<CourseGroupDAO> findCourseGroupByDept(String dept);

	List<CourseComponentDAO> findOpenComponents(String id, Date at);

	CourseGroupDAO findUpcomingComponents(String courseId, Date available);
	
	CourseComponentDAO findCourseComponent(String id);

	CourseSignupDAO newSignup(String userId, String supervisorId);

	void save(CourseSignupDAO signupDao);

	void save(CourseComponentDAO componentDao);

	CourseSignupDAO findSignupById(String signupId);

	List<CourseSignup> findSignupForUser(String userId, Set<Status> statuses);
	

}
