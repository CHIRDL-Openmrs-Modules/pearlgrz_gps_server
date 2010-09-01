/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pearlgrlz;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.OpenmrsObject;


/**
 *
 */
public class ConceptPromptPage extends BaseOpenmrsData implements OpenmrsObject {
	
	Integer conceptId;
	String  prompt;
	String  prompt_sp;
	Integer formId;
	Date    date_created;
	Date    date_changed;
	
	
	
	
	
	

	
    public Integer getConceptId() {
    	return conceptId;
    }

	
    public void setConceptId(Integer conceptId) {
    	this.conceptId = conceptId;
    }

	
    public String getPrompt() {
    	return prompt;
    }

	
    public void setPrompt(String prompt) {
    	this.prompt = prompt;
    }

	
    public String getPrompt_sp() {
    	return prompt_sp;
    }

	
    public void setPrompt_sp(String promptSp) {
    	prompt_sp = promptSp;
    }

	
    public Integer getFormId() {
    	return formId;
    }

	
    public void setFormId(Integer formId) {
    	this.formId = formId;
    }

	/**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    public Integer getId() {
	    // TODO Auto-generated method stub
	    return null;
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    public void setId(Integer id) {
	    // TODO Auto-generated method stub
	    
    }

}
