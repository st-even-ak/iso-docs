package com.itg.plugins.confluence.iso.impl;

import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class IsoTemplateListener implements InitializingBean, DisposableBean {

	private static final ModuleCompleteKey MY_BLUEPRINT_KEY = new ModuleCompleteKey("com.itg.plugins.confluence.iso.iso-documents:create-iso-document");
	private static final Logger log = LoggerFactory.getLogger(IsoTemplateListener.class);
    private final EventPublisher eventPublisher;

    @Autowired
    public IsoTemplateListener(@ComponentImport final EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        log.warn("WARN: Listener Class Registered.");
    }

    @EventListener
    public void onBlueprintCreateEvent(final BlueprintPageCreateEvent event){
        final ContentBlueprint blueprint = event.getBlueprint();
        
        ModuleCompleteKey moduleCompleteKey = event.getBlueprintKey();

        log.warn("WARN: Listener fired");
        if (MY_BLUEPRINT_KEY.equals(moduleCompleteKey)){
                //Take some action when 
                log.warn("WARN: Created a blueprint.");
        }
    }

    @Override
    public void destroy() {
        this.eventPublisher.unregister(this);
        log.warn("WARN: Listener Destroyed.");
    }

    @Override
    public void afterPropertiesSet() {
        this.eventPublisher.register(this);
        log.warn("WARN: Listener afterPropertiesSet.");
    }
}