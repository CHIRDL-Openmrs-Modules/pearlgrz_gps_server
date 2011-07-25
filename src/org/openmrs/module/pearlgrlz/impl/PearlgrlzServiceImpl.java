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

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.dss.DssManager;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.pearlgrlz.db.PearlgrlzDAO;
import org.openmrs.module.pearlgrlz.hibernateBeans.GpsData;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;


/**
 *
 */
//@Transactional
public class PearlgrlzServiceImpl implements PearlgrlzService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private PearlgrlzDAO dao;


	/**
	 *  Default constructor
	 */
	public PearlgrlzServiceImpl() {
		
	}
		
	public PearlgrlzDAO getPearlgrlzDAO() {
		return this.dao;
	}
	

	public void setPearlgrlzDAO(PearlgrlzDAO dao) {
		this.dao = dao;
	}

    /** 
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#createSurveyXML(org.openmrs.Patient, java.lang.Integer, java.lang.Integer, java.lang.Integer, org.openmrs.User)
     */
    public void createSurveyXML(Patient patient, Integer locationId, Integer formId, 
                                Integer numQuestions, User provider, Integer locationTagId,
                                FormInstance formInstance,Integer encounterId,Integer sessionId) {
		
    	LogicService logicService = Context.getLogicService();

		Integer patientId = patient.getPatientId();
		
		ObsService obsService = Context.getObsService();
		Set<Obs> allObs = obsService.getObservations(patient, false);
		ArrayList<Obs> list = new ArrayList<Obs>(allObs);
		
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		
		//write surveyXML to "merge" directory
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util
		        .getFormAttributeValue(formId, "defaultMergeDirectory", locationTagId, locationId));
		
		String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
		
		OutputStream output;

		try {
			output = new FileOutputStream(currFilename);
		
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formId);
			String formName = form.getName();
			
			produce(output, patient, formName, numQuestions, formInstance, location, provider,
				locationTagId,encounterId,sessionId);
		
		output.close();  
		} catch(Exception e) {
			log.debug(e);
		}
	}
    
	/**
	 * @param output
	 * @param patient
	 * @param dssType
	 * @param maxDssElements
	 * @param formInstance
	 * @param location
	 * @param provider
	 */
	private void produce(OutputStream output, Patient patient, 
	                     String dssType, int maxDssElements, FormInstance formInstance,
	                    Location location, User provider,Integer locationTagId, Integer encounterId,
	                    Integer sessionId) {
		
		DssService dssService = Context.getService(DssService.class);
		ATDService atdService = Context.getService(ATDService.class);
		
		//tell the decision support engine the types of rules to prioritize
		//and the number of questions/prompts that should be filled in
		DssManager dssManager = new DssManager(patient);
		dssManager.setMaxDssElementsByType(dssType, maxDssElements);
		
		//set values in baseParameters to have inputs to rules
		HashMap<String, Object> baseParameters = new HashMap<String, Object>();
		
		try {
			dssService.loadRule("storeObs",false); 
			dssService.loadRule("storeObsMultipleAnswers",false);
			dssService.loadRule("storeGroupedObs",false);
		}
		catch (Exception e) {
			log.error("load rule failed", e);
		}
		
		atdService.produce(patient, formInstance, output, dssManager, encounterId, baseParameters, null, locationTagId,
		    sessionId);
	}
			
    public void addGpsData(GpsData gpsData){
    	dao.addGpsData(gpsData);
    }
    
}

