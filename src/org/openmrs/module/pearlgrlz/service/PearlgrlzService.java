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
package org.openmrs.module.pearlgrlz.service;

import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.pearlgrlz.hibernateBeans.GpsData;


/**
 *
 */
public interface PearlgrlzService {

	
	public void createSurveyXML(Patient patient, Integer locationId, Integer formId, 
                                Integer numQuestions, User provider, Integer locationTagId,
                                FormInstance formInstance,Integer encounterId,Integer sessionId);
	 	 
    public void addGpsData(GpsData gpsData);
}
