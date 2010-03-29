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
package org.openmrs.module.pearlgrlz.web.controller;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This controller backs the /web/module/pearlgrlzForm.jsp page. This controller is tied to that
 * jsp page in the /metadata/moduleApplicationContext.xml file
 */
public class PearlgrlzFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Returns any extra data in a key-->value pair kind of way
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {
		
		HashMap<String, Object>  modelMap = new HashMap<String, Object> ();
		
		Collection<User> surveyProviders = Context.getUserService().getAllUsers();
		Collection<Location> locationList = Context.getLocationService().getAllLocations(true);
		Collection<LocationTag> locationTagList = Context.getLocationService().getAllLocationTags();
		Collection<Form>   formList = Context.getFormService().getAllForms();
 		
		modelMap.put("theProviderList", surveyProviders);
		modelMap.put("locationList", locationList);
		modelMap.put("locationTagList", locationTagList);
		modelMap.put("formList", formList);
		
		return modelMap;
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object,
	                                BindException exceptions) throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		String patientIdString = request.getParameter("participantDropDown");
		String providerIdString = request.getParameter("providerRadioButton");	
		if(patientIdString == null) patientIdString = request.getParameter("patientId");
		if(providerIdString == null) providerIdString = request.getParameter("providerId");
		
		String locationTagIdStr = request.getParameter("locationTagDropDown");
		String locationIdStr = request.getParameter("locationDropDown");	
		if(locationTagIdStr == null) locationTagIdStr = request.getParameter("locationTagId");
		if(locationIdStr == null) locationIdStr = request.getParameter("locationId");
		
		
		try {	
			//locationId and locationTagId will eventually come from login information
			//Hard code them for now
			UserService userService = Context.getUserService();
			User provider = userService.getUser(Integer.parseInt(providerIdString));
			Integer LOCATION_TAG_ID = Integer.parseInt(locationTagIdStr);
			Integer LOCATION_ID = Integer.parseInt(locationIdStr);
			
			Integer NUM_QUESTIONS = 10; //needs to be a form attribute value
			String tmpStr = request.getParameter("formNumberQuestions");
			if (tmpStr != null) {
				try {
					NUM_QUESTIONS = Integer.parseInt(tmpStr);
				}
				catch (Exception e) {
					;
				}
			}
			
			String formName = null;
			if( (formName = request.getParameter("formName")) == null && 
					(formName = request.getParameter("formDropDown")) == null || formName.equals("")) {
				formName = "PearlGrlz";
			}
			
			ATDService atdService = Context.getService(ATDService.class);
			PatientService patientService = Context.getPatientService();
			State initialState = atdService.getStateByName("create_survey");
			// TODO:  before add session, let's get the Encounter created here, since the atd_session can save the encounterId;
			Session session = atdService.addSession();
			Integer sessionId = session.getSessionId();
			
			FormService formService = Context.getFormService();
			List<Form> allForms = formService.getForms(formName, null, null, false, null, null, null);
			Form form = allForms.get(0);
			Integer formId = form.getFormId();
			
			if (patientIdString != null) {
				//add logic here to figure out whether a new survey should be started
				//or a previous survey should be completed
				//assume new survey for now
				Patient patient = patientService.getPatient(Integer.parseInt(patientIdString));
				
				PatientState patientState = atdService.addPatientState(patient, initialState, sessionId, LOCATION_TAG_ID,
				    LOCATION_ID);
				FormInstance formInstance = atdService.addFormInstance(formId, LOCATION_ID);
				map.put("formInstance", formInstance.getLocationId() + "_" + LOCATION_TAG_ID + "_" + formId + "_"
				        + formInstance.getFormInstanceId());
				
				//write surveyXML to "merge" directory
				String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util
				        .getFormAttributeValue(formId, "defaultMergeDirectory", LOCATION_TAG_ID, LOCATION_ID));
				String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
				
				OutputStream output = new FileOutputStream(currFilename);
				PearlgrlzService pearlgrlzService = Context.getService(PearlgrlzService.class);
				pearlgrlzService
				        .createSurveyXML(patient, patientState, output, LOCATION_ID, formId, NUM_QUESTIONS, provider);
				output.close();
				patientState.setEndTime(new java.util.Date());
				atdService.updatePatientState(patientState);
				initialState = atdService.getStateByName("wait_to_submit_survey");
				
				patientState = atdService.addPatientState(patient, initialState, sessionId, LOCATION_TAG_ID, LOCATION_ID);
				
				// Pass to fillout survey, to set up the state.
				map.put("patientId", patientIdString);
				map.put("providerId", providerIdString);
				map.put("sessionId", sessionId);
				map.put("locationId", request.getParameter("locationDropDown"));
				map.put("locationTagId", request.getParameter("locationTagDropDown"));
				map.put("encounterId", pearlgrlzService.getEncounter(patient).getEncounterId());
				map.put("formName", formName);
				map.put("formNumberQuestions", NUM_QUESTIONS);
				
				return new ModelAndView(new RedirectView("fillOutSurvey.form"), map);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("createSurvey failed: ", e);
		}
		
		return new ModelAndView(new RedirectView("pearlgrlzForm.form"), map);
//		return new ModelAndView(new RedirectView(getSuccessView()));
	}
	
	/**
	 * This class returns the form backing object. This can be a string, a boolean, or a normal java
	 * pojo. The type can be set in the /config/moduleApplicationContext.xml file or it can be just
	 * defined by the return type of this method
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Collection<Patient> formBackingObject(HttpServletRequest request) throws Exception {
		
		// get all surveyParticipants that have an identifier "1234"
		// see http://resources.openmrs.org/doc/index.html?org/openmrs/api/PatientService.html for
		// a list of all PatientService methods
		Collection<Patient> surveyParticipants = Context.getPatientService().findPatients("survey", false);
//		Collection<Patient> surveyParticipants = Context.getPatientService().getPatients("survey");
		
//		Collection<User> surveyProviders = Context.getUserService().getAllUsers();
		
		// this object will be made available to the jsp page under the variable name
		// that is defined in the /metadata/moduleApplicationContext.xml file 
		// under the "commandName" tag
		return surveyParticipants;
	}
	
}
