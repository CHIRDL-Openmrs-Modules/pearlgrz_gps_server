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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.pearlgrlz.SurveySession;
import org.openmrs.module.pearlgrlz.hibernateBeans.GpsData;


/**
 *
 */
public interface PearlgrlzService {
	

	public final static String   SURVEY_STATE = "pearlgrlz_state";
	public final static String   SURVEY_TYPE_DAILY = "pearlgrlz_daily";
	public final static String   SURVEY_TYPE_WEEKLY = "pearlgrlz_weekly";
	public final static String   SURVEY_TYPE_MONTHLY = "pearlgrlz_monthly";
	public final static String   SURVEY_VOIDED_REASON_TIMESUP = "Patient did NOT finish the survey on time.";
	public final static String   SURVEY_GENERAL_PARTNER_TYPE = "general partner";
	public final static String   SURVEY_GENERAL_PARTNER_TYPE2 = "general-partner";
	public final static String   SURVEY_SEXUAL_PARTNER_TYPE = "sexual partner";
	public final static String   SURVEY_SEXUAL_PARTNER_TYPE2 = "sexual-partner";
	public final static String   SURVEY_VALUE_DELIMITOR = ":";
	
	
	public void createSurveyXML(Patient patient, Integer locationId, Integer formId, 
                                Integer numQuestions, User provider, Integer locationTagId,
                                FormInstance formInstance,Integer encounterId);
	 	 
	public Encounter getEncounter(Patient patient, User provider, Location location);
	
	public Session getSurveySession(Patient patient, String surveyType, User provider,
	                                Location location);
	
	public Session getSession(Patient patient, User provider, Location location);
	
	public String calculateSurveyType();
	
	public SurveySession getLatestSurveySession(Patient patient, String surveyType);

	public void cupSurveySession(SurveySession surveySession);

    public void addGpsData(GpsData gpsData);
}
