package com.itg.plugins.confluence.itg.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;

@Component
public class itgTemplateListener implements InitializingBean, DisposableBean {

	private static final ModuleCompleteKey MY_BLUEPRINT_KEY = new ModuleCompleteKey("com.itg.plugins.confluence.itg.itg-documents:itg-blueprint");
	private static final Logger log = LoggerFactory.getLogger(itgTemplateListener.class);

	@Nonnull
	@ComponentImport
    private final EventPublisher eventPublisher;
	@Nonnull
	@ComponentImport
	private final UserAccessor userAccessor;
    @Nonnull 
    @ComponentImport
    private final LocaleManager localeManager; 
	@Nonnull
    @ComponentImport
    private final ApplicationProperties applicationProperties;
	@Nonnull
    @ComponentImport
    private final EditorFormatService editorFormatService;
	@Nonnull
    @ComponentImport
	private final I18NBeanFactory i18NBeanFactory;
	@Nonnull
    @ComponentImport
	private final AttachmentManager attachmentManager;
	
    @Autowired
    public itgTemplateListener(@ComponentImport final EventPublisher eventPublisher,
    								    		final ApplicationProperties applicationProperties,
    								    		final UserAccessor userAccessor,
    								    		final LocaleManager localeManager,
    								    		final EditorFormatService editorFormatService,
    								    		final I18NBeanFactory i18NBeanFactory,
    								    		final AttachmentManager attachmentManager
    											) {
        this.eventPublisher = eventPublisher;
    	this.userAccessor = userAccessor;	
		this.localeManager = localeManager;
        this.applicationProperties = applicationProperties;
        this.editorFormatService = editorFormatService;
        this.i18NBeanFactory = i18NBeanFactory;
        this.attachmentManager = attachmentManager;
    }

    @EventListener
    public void onBlueprintCreateEvent(BlueprintPageCreateEvent event){
    	
        if (MY_BLUEPRINT_KEY.equals(event.getBlueprintKey())){
        		
        	DefaultConversionContext converter = new DefaultConversionContext(event.getPage().toPageContext());
        	
        	// set the page body from the template collection
        	//event.getPage().setBodyContent(event.);
    		// set page title
    		event.getPage().setTitle(event.getContext().get("templatekey").toString().concat(": ").concat(event.getContext().get("varTitle").toString()));
    		
    		
    		//add client logo
    		// TODO: this need to be configurable
//    		ClassLoader clsLoader = getClass().getClassLoader();
//    		//BufferedImage img = ImageIO.read(clsLoader.getResource(event.getContext().get("clientkey").toString().replace("client-id-","").toLowerCase().concat("-logo.png")));
//    		URL resourcePath = clsLoader.getResource(getClientLogoPath(event.getContext().get("clientkey").toString()));
//    		File logo = new File(resourcePath.getFile());
//    		Attachment att = new Attachment(logo.getAbsolutePath(), "image/png", logo.getTotalSpace(), "");
//    		att.setCreator(getCurrentUser());
//    		att.setCreationDate(new Date());
//    		att.setLastModificationDate(att.getCreationDate());
//    		
//    		try {
//				this.attachmentManager.saveAttachment(att, null, new FileInputStream(logo));
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				log.warn("File not Found", e);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				log.warn("File cannot be read", e);
//			}
//    		catch (Exception e) {
//    			log.warn("Unhandled Exception", e);
//    		}
//    		event.getPage().addAttachment(att);
    		
    		// first we're going to substitute all the "Wiki" entries to their placeholders in the template
            try {
            	for(Entry<String, Object> kvp: event.getContext().entrySet()) {

    	    		//We're only looking for "wiki" items here
    	    		if(kvp.getKey().substring(0, 4).equals("wiki")) {
    	    			kvp.setValue(editorFormatService.convertWikiToEdit(event.getContext().get(kvp.getKey()).toString(), converter));
    	    		}
            	}
            	
        		ContentEntityObject pCeo = event.getPage().getEntity();
        		BodyContent pBc = pCeo.getBodyContent();
        		String pBody = pBc.getBody().toString();
        		
	    		// inject defaults values to specific keys
	    		pBody = pBody.replace("$varCreatedDate$", getFriendlyDate());
	    		
	    		// if email address is null use the page creators else let the contextObject use the input value
	    		if(event.getContext().get("varEmailAddress").equals("")) {
	    			pBody = pBody.replace("varEmailAddress", getCurrentUser().getEmail());
	    		}
        		//Create Status macro
        		HashMap <String, String> params = new HashMap<String, String>();
        		params.put("title", event.getContext().get("varDocumentStatus").toString().toUpperCase());
        		params.put("colour", getDocumentStatusColour(event.getContext().get("varDocumentStatus").toString()));
        		pBody = pBody.replace("$varDocumentStatus$", createStatusMacro(params));
        		
        		// update the pageBody with modified values
        		pBc.setBody(pBody);
        		pCeo.setBodyContent(pBc);
        		
    	    }
            catch (XhtmlException ignored) {
            	log.warn("Xhtml Parsing failed", ignored);
    	    }
            catch (Exception ignored) {
            	log.warn("Unhandle Exception", ignored);
            	
            }
            
        }
    }

    @Override
    public void destroy() {
        this.eventPublisher.unregister(this);
    }

    @Override
    public void afterPropertiesSet() {
        this.eventPublisher.register(this);
    }
    
	private String getFriendlyDate() {
		
		return DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
	}

	private String getFriendlyDateTime() {
		
		return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date());
	}

	private ConfluenceUser getUser(String userName) {
		
		return userAccessor.getUserByName(userName);
	}

	private ConfluenceUser getCurrentUser() {
		
		return AuthenticatedUserThreadLocal.get(); 
	}
	
	private Locale getCurrentLocale() {
		
		return getCurrentUser() != null ? (Locale)localeManager.getLocale(getCurrentUser()) : null;
	}
	

	// TODO: This needs to be made configurable
	private String getClientLogoPath(String clientId) {

		return applicationProperties.getBaseUrl().concat("resources/images/").concat(clientId).replace("client-id-","").concat("-logo.png");
	}

	// TODO: This needs to be made configurable
	private String getRequiredSpaceKey(String clientId) {
		
		if(clientId.equals("TUI")) {
			return "TUI";
		}
		else if(clientId.equals("LGI")) {
			return "LGI";
		}
		else if(clientId.equals("MSD")) {
			return "MSD";
		}
		else if(clientId.equals("ITG")) {
			return "ITG";
		}
		else if(clientId.equals("VT")) {
			return "SME";
		}
		else if(clientId.equals("VW")) {
			return "SME";
		}
		else if(clientId.equals("Audi")) {
			return "SME";
		}
		else if(clientId.equals("Wacoal")) {
			return "SME";
		}
		return null;
	}
	
	private String getDocumentStatusColour(String documentStatus) {
		
		if(documentStatus.toLowerCase().equals("confidential")){return "Red";}
		else if(documentStatus.toLowerCase().equals("internal")){return "Yellow";}
		else if(documentStatus.toLowerCase().equals("public")){return "Green";}
		else {return null;}
	}
	
	// TODO: there's probably a better way to do this using MacroManager...
	private String createStatusMacro(HashMap<String, String> params) {
		
		String macro = "<ac:structured-macro ac:name=\"status\" ac:schema-version=\"1\">";
		
		for(Entry<String, String> kvp: params.entrySet()) {
			macro += "<ac:parameter ac:name=\"" + kvp.getKey() + "\">" + kvp.getValue() + "</ac:parameter>";
		}
		macro += "</ac:structured-macro>";
		
		return macro;
	}
	
	private String createJIRALink (HashMap<String, String> params) {
		
		String macro = "<ac:structured-macro ac:name=\"jira\" ac:schema-version=\"1\">";
			for(Entry<String, String> kvp: params.entrySet()) {
				macro += "<ac:parameter ac:name=\"" + kvp.getKey() + "\">" + kvp.getValue() + "</ac:parameter>";
			}
			macro += "</ac:structured-macro>";
			
			return macro;
	}
}
