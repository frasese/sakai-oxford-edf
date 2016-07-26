package org.sakaiproject.user.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.CandidateDetailProvider;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.api.ValueEncryptionService;

public class FallbackDetailProvider implements CandidateDetailProvider {
	
	private static Log M_log = LogFactory.getLog(FallbackDetailProvider.class);
	
	private ServerConfigurationService serverConfigurationService;
	private SiteService siteService;
	private ToolManager toolManager;
	private ValueEncryptionService valueEncryptionService;
	
	public void init() {
		if(valueEncryptionService == null){
			valueEncryptionService = (ValueEncryptionService) ComponentManager.get(ValueEncryptionService.class);
		}
		if(siteService == null){
			siteService = (SiteService) ComponentManager.get(SiteService.class);
		}
		if(toolManager == null){
			toolManager = (ToolManager) ComponentManager.get(ToolManager.class);
		}
		if(serverConfigurationService == null){
			serverConfigurationService = (ServerConfigurationService) ComponentManager.get(ServerConfigurationService.class);
		}
	}

	public Optional<String> getCandidateID(User user) {
		return getCandidateID(user, null);
	}
	
	public Optional<String> getCandidateID(User user, Site site) {
		M_log.debug("Getting candidate id from fallback provider");
		try {
			//check if we should use the institutional anonymous id (system-wide or site-based)
			if(user != null && useInstitutionalAnonymousId(site)) {
				String candidateID = "no-candidate-id:"+user.getId();
				return Optional.ofNullable(candidateID);
			}
		} catch(Exception e) {
			M_log.warn("Error getting fallback candidateID for "+((user != null) ? user.getId() : "-null-"), e);
		}
		return Optional.empty();
	}
	
	public boolean useInstitutionalAnonymousId(Site site) {
		M_log.debug("useInstitutionalAnonymousId from fallback provider");
		try {
			return (serverConfigurationService.getBoolean(SYSTEM_PROP_USE_INSTITUTIONAL_ANONYMOUS_ID, false) || (site != null && site.getProperties() != null && Boolean.parseBoolean(site.getProperties().getProperty(SYSTEM_PROP_USE_INSTITUTIONAL_ANONYMOUS_ID))));
		} catch(Exception ignore) {}
		return false;
	}
	
	public Optional<List<String>> getAdditionalNotes(User user) {
		return Optional.empty();
	}
	
	public Optional<List<String>> getAdditionalNotes(User user, Site site) {
		return Optional.empty();
	}
	
	public boolean isAdditionalNotesEnabled(Site site) {
		try {
			return (serverConfigurationService.getBoolean(SYSTEM_PROP_DISPLAY_ADDITIONAL_INFORMATION, false) || (site != null && site.getProperties() != null && Boolean.parseBoolean(site.getProperties().getProperty(SITE_PROP_DISPLAY_ADDITIONAL_INFORMATION))));
		} catch(Exception ignore) {}
		return false;
	}
	

	public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}
	
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	public void setValueEncryptionService(ValueEncryptionService valueEncryptionService) {
		this.valueEncryptionService = valueEncryptionService;
	}

	
	
}
