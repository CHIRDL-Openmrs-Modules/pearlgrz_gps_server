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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.OpenmrsObject;


/**
 *
 */
public class SurveyPartner extends BaseOpenmrsData implements OpenmrsObject {
	
	Integer id;
	Integer patientId;
	String  partnerName;
	String  partnerType;
	Integer nbrTimeSelected;
	

	
    public Integer getPatientId() {
    	return patientId;
    }

	
    public void setPatientId(Integer patientId) {
    	this.patientId = patientId;
    }

	
    public String getPartnerName() {
    	return partnerName;
    }

	
    public void setPartnerName(String partnerName) {
    	this.partnerName = partnerName;
    }

	
    public String getPartnerType() {
    	return partnerType;
    }

	
    public void setPartnerType(String partnerType) {
    	this.partnerType = partnerType;
    }
    
    
	
    public Integer getNbrTimeSelected() {
    	return nbrTimeSelected;
    }


	
    public void setNbrTimeSelected(Integer nbrTimeSelected) {
    	this.nbrTimeSelected = nbrTimeSelected;
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
    public void setId(Integer integer) {
	    // TODO Auto-generated method stub
	    this.id = integer;
    }
	

}
