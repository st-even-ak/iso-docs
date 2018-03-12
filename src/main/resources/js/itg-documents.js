AJS.toInit(function($) {

	// Common functions
    function validateRequiredField($container, fieldId, error) {
    	
        var $field = $container.find(fieldId);

        if ($.trim($field.val()) == "") {
            $field.focus().siblings(fieldId+".error").html(error);
            return false;
        }
        return true;
    }

	// Wizard functions
    function onPageSubmit(e, state) {
    	
        return validate(state);
    }
    
	function onSelectionFormSubmit(e, state) {

		var docType = state.pageData.templatekey;

        if (docType == "Brief") {
            state.nextPageId = "briefProperties";
        } else if (docType == "Release Note") {
            state.nextPageId = "releaseNoteProperties";
        } else if (docType == "Change Request") {
        	state.nextPageId = "changeRequestProperties";
    	} else {
            //no selection
            return false;
        }
    }

    function validate(state) {
    	
    	console.clear();
    	console.log(state);
    	
        var $container = state.$container;
        var dialoguePage = $container["0"].id;
        
        console.log(dialoguePage);
        
        $container.find(".error").html(""); // clear all existing errors
        
        if(dialoguePage == "briefProperties"){

            var varBriefTitle = validateRequiredField($container, "#varTitle", AJS.I18n.getText("itg.blueprint.form.label.title.varBriefTitle.required"));
            var varBriefProposer = validateRequiredField($container, "#varProposer", AJS.I18n.getText("itg.blueprint.form.label.title.varBriefProposer.required"));
            var varBriefBusinessArea = validateRequiredField($container, "#varBusinessArea", AJS.I18n.getText("itg.blueprint.form.label.title.varBriefBusinessArea.required"));
            var varBriefAuthoriser = validateRequiredField($container, "#varAuthoriser", AJS.I18n.getText("itg.blueprint.form.label.title.varBriefAuthoriser.required"));
            
            return varBriefTitle && varBriefProposer && varBriefBusinessArea && varBriefEmailAddress && varBriefAuthoriser;
        }
        else if (dialoguePage == "briefSummary"){
        	
            return validateRequiredField($container, "#wiki-markup-briefSummary", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefSummary.required"));
    	}
        else if (dialoguePage == "briefBackground"){
        	
            return validateRequiredField($container, "#wiki-markup-briefBackground", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefBackground.required"));
    	}
        else if (dialoguePage == "briefObjectives"){
        	
            return validateRequiredField($container, "#wiki-markup-briefObjectives", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefObjectives.required"));
    	}
        else if (dialoguePage == "briefRequirements"){
        	
            return validateRequiredField($container, "#wiki-markup-briefRequirements", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefRequirements.required"));
    	}
        else if (dialoguePage == "briefScope"){
        	
            return validateRequiredField($container, "#wiki-markup-briefScope", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefScope.required"));
        }
        else if (dialoguePage == "briefBenefits"){
        	
            return validateRequiredField($container, "#wiki-markup-briefBenefits", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefBenefits.required"));
    	}
        else if (dialoguePage == "briefTimeframe"){
        	
            return validateRequiredField($container, "#wiki-markup-briefTimeframe", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefTimeframe.required"));
    	}
        else if (dialoguePage == "briefRisks"){
        	
            return validateRequiredField($container, "#wiki-markup-briefRisks", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefRisks.required"));
    	}
        else if (dialoguePage == "briefConstraints"){
        	
            return validateRequiredField($container, "#wiki-markup-briefConstraints", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefConstraints.required"));
    	}
        else if (dialoguePage == "briefStakeholders"){
        	
            return validateRequiredField($container, "#wiki-markup-briefStakeholders", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-briefStakeholders.required"));
    	}
        else if(dialoguePage == "releaseNoteProperties"){
        	
        	return validateRequiredField($container, "#wiki-markup-releaseNoteProperties", AJS.I18n.getText("itg.blueprint.form.label.title.wiki-markup-releaseNoteSummary.required"));
        }
        else
    	{
    	return true;
    	}

        
    }
	
	// Register wizard hooks
	Confluence.Blueprint.setWizard('com.itg.plugins.confluence.itg.itg-documents:create-itg-document', function(wizard) {

		wizard.on('submit.templateSelectionForm', onSelectionFormSubmit);
		
//		wizard.on('submit.briefProperties', onPageSubmit);
//		wizard.on('submit.briefSummary',onPageSubmit);
//		wizard.on('submit.briefBackground', onPageSubmit);
//		wizard.on('submit.briefObjectives', onPageSubmit);
//		wizard.on('submit.briefRequirements', onPageSubmit);
//		wizard.on('submit.briefScope', onPageSubmit);
//		wizard.on('submit.briefBenefits', onPageSubmit);
//		wizard.on('submit.briefTimeframe', onPageSubmit);
//		wizard.on('submit.briefRisks', onPageSubmit);
//		wizard.on('submit.briefConstraints', onPageSubmit);
//		wizard.on('submit.briefStakeholders', onPageSubmit);
		 
		//wizard.on('submit.releaseNoteSummary', onPageSubmit);
	});
})(AJS.$);