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
package org.openmrs.module.pearlgrlz.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.pearlgrlz.SurveyPartner;
import org.openmrs.module.pearlgrlz.SurveyRecord;
import org.openmrs.module.pearlgrlz.SurveySession;
import org.openmrs.module.pearlgrlz.hibernateBeans.GpsData;

/**
 *
 */
public interface PearlgrlzDAO {

	public SurveyRecord getLatestSurveyRecord(Patient patient);
	
	public void cupSurveySession(SurveySession surveySession);
			
	public SurveySession getLatestSurveySession(Patient patient, String surveyType);

	public SurveyPartner getSurveyPartner(Patient patient, String partnerName, String partnerType);
	
	public List<SurveyPartner>  populatePartnerList(Patient patient, String partnerType);
	
	public void addPartner(SurveyPartner partner);

	public void voidPartner(SurveyPartner partner);
	
	public PatientATD getPatientATD(FormInstance formInstance, Rule rule);

	public void addGpsData(GpsData gpsData);
}
