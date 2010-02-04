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
package org.openmrs.module.pearlgrlz.impl;

import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.dss.DssManager;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.pearlgrlz.db.PearlgrlzDAO;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 */
@Transactional
public class PearlgrlzServiceImpl implements PearlgrlzService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private PearlgrlzDAO dao;
	
	/**
	 * Empty constructor
	 */
	public PearlgrlzServiceImpl() {
		
	}
	
	/**
	 * @return PearlgrlzDAO
	 */
	public PearlgrlzDAO getPearlgrlzDAO() {
		return this.dao;
	}
	
	/**
	 * Sets the DAO for this service. The dao allows interaction with the database.
	 * 
	 * @param dao
	 */
	public void setPearlgrlzDAO(PearlgrlzDAO dao) {
		this.dao = dao;
	}
	
	/**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#createSurveyXML(org.openmrs.Patient, org.openmrs.PatientState, java.io.OutputStream, java.lang.Integer, java.lang.Integer, java.lang.Integer, org.openmrs.User)
     */
    @Override
    public void createSurveyXML(Patient patient, PatientState patientState, OutputStream output,
                                Integer locationId, Integer formId, Integer numQuestions, User provider) {
		
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		String formName = form.getName();
		
		ATDService atdService = Context.getService(ATDService.class);
		FormInstance formInstance = atdService.addFormInstance(formId, locationId);
		
		produce(output, patient, formName, numQuestions, formInstance, location, provider);
		
		patientState.setFormInstance(formInstance);  
	}
	
	private void produce(OutputStream output, Patient patient, String dssType, int maxDssElements, FormInstance formInstance,
	                    Location location, User provider) {
		
		DssService dssService = Context.getService(DssService.class);
		ATDService atdService = Context.getService(ATDService.class);
		
		//tell the decision support engine the types of rules to prioritize
		//and the number of questions/prompts that should be filled in
		DssManager dssManager = new DssManager(patient);
		dssManager.setMaxDssElementsByType(dssType, maxDssElements);
		
		//set values in baseParameters to have inputs to rules
		HashMap<String, Object> baseParameters = new HashMap<String, Object>();
		
		try {
			dssService.loadRule("storeObs", false);
		}
		catch (Exception e) {
			log.error("load rule failed", e);
		}
		
		Encounter encounter = createEncounter(patient,provider,location);
		Integer encounterId = encounter.getEncounterId();
		
		atdService.produce(patient, formInstance, output, dssManager, encounterId, baseParameters, null, true, null, null);
		
	}
	
	private Encounter createEncounter(Patient patient, User provider,Location location){
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setProvider(provider);
		encounter.setLocation(location);
		encounter.setEncounterDatetime(new java.util.Date());
		encounter.setCreator(Context.getAuthenticatedUser());
		encounter.setDateCreated(new java.util.Date());
		encounterService.saveEncounter(encounter);
		return encounter;
	}


}

