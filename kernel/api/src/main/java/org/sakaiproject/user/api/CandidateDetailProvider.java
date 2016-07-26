package org.sakaiproject.user.api;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;

import java.util.List;
import java.util.Optional;

/**
 * This is a provider interface that allows Assignments to provide addition details about candidates
 * to the interface.
 */
public interface CandidateDetailProvider {
	
	public static final String USER_PROP_CANDIDATE_ID = "candidateID";
	public static final String USER_PROP_ADDITIONAL_INFO = "additionalInfo";
	
	public final static String SITE_PROP_USE_INSTITUTIONAL_ANONYMOUS_ID = "useInstitutionalAnonymousID";
	public final static String SITE_PROP_DISPLAY_ADDITIONAL_INFORMATION = "displayAdditionalInformation";
	
	public final static String SYSTEM_PROP_USE_INSTITUTIONAL_ANONYMOUS_ID = "useInstitutionalAnonymousID";
	public final static String SYSTEM_PROP_DISPLAY_ADDITIONAL_INFORMATION = "displayAdditionalInformation";

	/**
	 * This gets an candidate ID for a user, this can be used to make candidate IDs anonymous.
	 * @param user The user for who an ID is wanted. Cannot be <code>null</code>
	 * @param site The site in which the lookup is happening. If site is null, it will try to get the current site
	 * @return An option containing the candidate ID.
	 */
	Optional<String> getCandidateID(User user, Site site);
    /**
     * This gets an candidate ID for a user in the current site, this can be used to make candidate IDs anonymous.
     * @param user The user for who an ID is wanted. Cannot be <code>null</code>
     * @return An option containing the candidate ID.
     */
    Optional<String> getCandidateID(User user);
    
    /**
     * Should the candidate id (institutional anonymous id) be used for this site.
     * @param site The site in which the lookup is happening.
     * @return If <code>true</code> then use the candidateid for this site.
     */
    boolean useInstitutionalAnonymousId(Site site);

    /**
     * This gets additional notes for a user.
     * @param user The user for who addition notes are wanted. Cannot be <code>null</code>
     * @param site The site in which the lookup is happening. If site is null, it will try to get the current site
     * @return An option containing the additional user notes.
     */
    Optional<List<String>> getAdditionalNotes(User user, Site site);
    
    /**
     * This gets additional notes for a user in the current site.
     * @param user The user for who addition notes are wanted. Cannot be <code>null</code>
     * @param site The site in which the lookup is happening. If site is null, it will try to get the current site
     * @return An option containing the additional user notes.
     */
    Optional<List<String>> getAdditionalNotes(User user);

    /**
     * Is the additional notes enabled for this site.
     * @param site The site in which the lookup is happening.
     * @return If <code>true</code> then show the additional details for this site.
     */
     boolean isAdditionalNotesEnabled(Site site);
}
