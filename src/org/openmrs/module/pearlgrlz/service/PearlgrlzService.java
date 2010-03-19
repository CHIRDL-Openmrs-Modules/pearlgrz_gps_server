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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.dss.hibernateBeans.Rule;


/**
 *
 */
public interface PearlgrlzService {
	
	/**
	 * 
	 * 
	 * @param patient
	 * @param state
	 * @param output
	 * @param locationId
	 * @param formId
	 * @param numQuestions
	 * @param provider
	 * 
	 * @deprecated use {@link #createSurveyXML(Patient patient, Integer locationId,Integer formId, Integer numQuestions,User provider)}
	 */
	public void createSurveyXML(Patient patient, PatientState state, OutputStream output, Integer locationId,Integer formId, Integer numQuestions,User provider);	// for backward compat.
	
	
	public void createSurveyXML(Patient patient, Integer locationId,Integer formId, Integer numQuestions,User provider);
	
	public void preRender(Map map, Integer formId, Integer formInstanceId, Integer locationId,
                           TeleformTranslator translator, InputStream inputMergeFile)  throws Exception;
	
	public void saveForm(Map map, Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
	                     TeleformTranslator translator, InputStream inputMergeFile, HttpServletRequest request);
	
	public int getNumberQuestions(Integer formId);
	
	public String getConceptPormpt(Concept concept);
	
	public void getDefaultInfor();
	
	 public Patient getPatientByUserId(Integer userId);
	 
	 public String getSurveyType(Patient patient);
	 
	 public void setSurveyType(Patient patient, String surveyType);
	 
	public Encounter getEncounter(Patient patient);
	
	public void setEncounter(Patient patient, Encounter value) ;
	
	public Location getLocation();
	
	 public LocationTag getLocationTag();
	
	public FormInstance getFormInstance(Patient patient) ;
	
	public void setFormInstance(Patient patient, FormInstance formInstance);
	
	public Session getSurveySession(Patient patient, String SurveyType);
	
	public Session getSession(Patient patient);
	
	public void setSession(Patient patient, Session session);
	
	public String calculateSurveyType();
	
	public void endSurveySession(Patient patient, String surveyType, Boolean voided);
	
	public List<String>  populatePartnerList(Patient patient);
	
	public PatientATD getPatientATD(FormInstance formInstance, Rule rule);
	
}
