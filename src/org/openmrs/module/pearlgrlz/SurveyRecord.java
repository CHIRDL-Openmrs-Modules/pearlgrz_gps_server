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
public class SurveyRecord  extends BaseOpenmrsData implements OpenmrsObject {
	
	Integer recordId;
	Integer participantId;
	Integer loop;
	Integer link;
	String  records;
	String  status;
	Date    dateCreated;
	Date    dateChanged;
	
	
    public Date getDateCreated() {
    	return dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
    	this.dateCreated = dateCreated;
    }

	
    public Date getDateChanged() {
    	return dateChanged;
    }


	
    public void setDateChanged(Date dateChanged) {
    	this.dateChanged = dateChanged;
    }

    public Integer getParticipantId() {
    	return participantId;
    }

	
    public void setParticipantId(Integer participantId) {
    	this.participantId = participantId;
    }

	
    public Integer getLoop() {
    	return loop;
    }

	
    public void setLoop(Integer loop) {
    	this.loop = loop;
    }

	
    public Integer getLink() {
    	return link;
    }

	
    public void setLink(Integer link) {
    	this.link = link;
    }

	
    public String getRecords() {
    	return records;
    }

	
    public void setRecords(String records) {
    	this.records = records;
    }

	
    public String getStatus() {
    	return status;
    }

	
    public void setStatus(String status) {
    	this.status = status;
    }

	
	
    public Integer getRecordId() {
    	return recordId;
    }


    public void setRecordId(Integer recordId) {
    	this.recordId = recordId;
    }
    
    
	/**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    @Override
    public Integer getId() {
	    // TODO Auto-generated method stub
	    return getRecordId();
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(Integer id) {
	    // TODO Auto-generated method stub
	    setRecordId(id);
    }

}
