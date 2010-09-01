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
public class SurveySession extends BaseOpenmrsData implements OpenmrsObject {
	
	Integer id;
	Integer patientId;
	String  surveyType;
	Integer sessionId;
	Integer encounterId;
	Date    startTime;
	Date    endTime;
	
	
	
    public Integer getPatientId() {
    	return patientId;
    }

	
    public void setPatientId(Integer patientId) {
    	this.patientId = patientId;
    }

	
    public String getSurveyType() {
    	return surveyType;
    }

	
    public void setSurveyType(String surveyType) {
    	this.surveyType = surveyType;
    }

	
    public Integer getSessionId() {
    	return sessionId;
    }

	
    public void setSessionId(Integer sessionId) {
    	this.sessionId = sessionId;
    }

	
    public Integer getEncounterId() {
    	return encounterId;
    }

	
    public void setEncounterId(Integer encounterId) {
    	this.encounterId = encounterId;
    }

	
    public Date getStartTime() {
    	return startTime;
    }

	
    public void setStartTime(Date startTime) {
    	this.startTime = startTime;
    }

	
    public Date getEndTime() {
    	return endTime;
    }

	
    public void setEndTime(Date endTime) {
    	this.endTime = endTime;
    }

	

	/**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    public Integer getId() {
	    // TODO Auto-generated method stub
	    return id;
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    public void setId(Integer id) {
	    // TODO Auto-generated method stub
	    this.id = id;
    }

}
