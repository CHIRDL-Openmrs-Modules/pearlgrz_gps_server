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


/**
 *
 */
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class StartSurveyController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj,
	                                org.springframework.validation.BindException errors) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String patientIdString = request.getParameter("patientId");
		Integer patientId = null;
		
		try {
			if (patientIdString != null) {

				patientId = Integer.parseInt(patientIdString);
			}
		}
		catch (Exception e) {
			
		}
		
		try {
		ATDService atdService = Context.getService(ATDService.class);
		PatientService patientService = Context.getPatientService();
		State initialState = atdService.getStateByName("create_survey");
		Session session = atdService.addSession();
		Integer sessionId = session.getSessionId();
		
		//locationId and locationTagId will eventually come from login information
		//Hard code them for now
		UserService userService = Context.getUserService();
		String providerId = request.getParameter("providerId");
		User provider = userService.getUser(Integer.parseInt(providerId));
		Integer LOCATION_TAG_ID = 1;
		Integer LOCATION_ID = 1;
		Integer NUM_QUESTIONS = 10; //needs to be a form attribute value
		String formName = "PearlGrlz";
		FormService formService = Context.getFormService();
		List<Form> allForms = formService.getForms(formName, null, null, false, null, null, null);
		Form form = allForms.get(0);
		Integer formId = form.getFormId();
		
		if (patientId != null) {
			//add logic here to figure out whether a new survey should be started
			//or a previous survey should be completed
			//assume new survey for now
			Patient patient = patientService.getPatient(patientId);
			
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
			pearlgrlzService.createSurveyXML(patient, patientState, output, LOCATION_ID, formId, NUM_QUESTIONS, provider);
			output.close();  
			patientState.setEndTime(new java.util.Date());
			atdService.updatePatientState(patientState);
			initialState = atdService.getStateByName("wait_to_submit_survey");
			
			patientState = atdService.addPatientState(patient, initialState, sessionId, LOCATION_TAG_ID, LOCATION_ID);
			return new ModelAndView(new RedirectView("fillOutSurvey.form"), map);
		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return new ModelAndView(new RedirectView("startSurvey.form"), map);
	}
	
}
