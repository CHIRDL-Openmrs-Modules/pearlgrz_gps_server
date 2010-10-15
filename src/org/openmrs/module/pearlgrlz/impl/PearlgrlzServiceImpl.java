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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.util.Util;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.dss.DssManager;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.pearlgrlz.SurveyParameterHandler;
import org.openmrs.module.pearlgrlz.SurveyPartner;
import org.openmrs.module.pearlgrlz.SurveyRecord;
import org.openmrs.module.pearlgrlz.SurveySession;
import org.openmrs.module.pearlgrlz.db.PearlgrlzDAO;
import org.openmrs.module.pearlgrlz.hibernateBeans.GpsData;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 */
@Transactional
public class PearlgrlzServiceImpl implements PearlgrlzService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private PearlgrlzDAO dao;

	private SurveyParameterHandler parameterHandler;
	private HashMap<Patient, Boolean> mSessionCompleted;


	/**
	 *  Default constructor
	 */
	public PearlgrlzServiceImpl() {
		
		mSessionCompleted = new HashMap<Patient, Boolean>();
	}
	
	public SurveySession getLatestSurveySession(Patient patient, String surveyType){
		return dao.getLatestSurveySession(patient, surveyType);
	}
	public void cupSurveySession(SurveySession surveySession){
		dao.cupSurveySession(surveySession);
	}

	
	
	public Session getSurveySession(Patient patient, String surveyType, User provider,
	                                Location location)  {
		final int MILLISEC_IN_DAY = 24*60*60*1000;
		ATDService atdService = Context.getService(ATDService.class);
		boolean newSession = false;
		Date now = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		Session session = null;
		Encounter encounter = null;

		if(surveyType == null) 
			surveyType = calculateSurveyType();
		
		cal.setTime(now);
		
		SurveySession surveySession = dao.getLatestSurveySession(patient, surveyType);
		
		// Very initial, or latest is voided
		if(surveySession == null || surveySession.isVoided()) {
			newSession = true;
		} else {
			// Completed, open a new session if it's the following day
			if(surveySession.getEndTime() != null) {
				GregorianCalendar endCal = new GregorianCalendar();
				endCal.setTime(surveySession.getEndTime());
				
				// TODO: let's handle this gracefully (ie, redirect user to the completion and logout page)
				if(cal.get(Calendar.DAY_OF_YEAR) - endCal.get(Calendar.DAY_OF_YEAR) > 0) {
					newSession = true;
				} else {
					log.info(Context.getAuthenticatedUser().getUsername() + " have already completed today's Survey.  A new survey will start on tomorrow");
					mSessionCompleted.put(patient, Boolean.TRUE);
					return null;
				}
			} else {
				if ((surveyType.equalsIgnoreCase(SURVEY_TYPE_DAILY) && (now.getTime() - surveySession.getStartTime()
				        .getTime()) > MILLISEC_IN_DAY)
				        || (surveyType.equalsIgnoreCase(SURVEY_TYPE_WEEKLY) && (now.getTime() - surveySession.getStartTime()
				                .getTime()) > MILLISEC_IN_DAY * 7)) {
					
					newSession = true;
					
					surveySession.setVoided(Boolean.TRUE);
					surveySession.setVoidedBy(Context.getUserService().getUser(1));
					surveySession.setVoidReason(SURVEY_VOIDED_REASON_TIMESUP);
					surveySession.setEndTime(now);
					surveySession.setDateVoided(now);
					dao.cupSurveySession(surveySession);
				} else {
					session =  atdService.getSession(surveySession.getSessionId());
					encounter = Context.getEncounterService().getEncounter(surveySession.getEncounterId());
				}
			}
		}
		
		if(newSession || session == null || encounter == null) {
			// Create Session and Encounter
			session = atdService.addSession();
			encounter = createEncounter(patient, provider, location);
			session.setEncounterId(encounter.getEncounterId());
			session = atdService.updateSession(session);
			
			// Create SurveySession
			surveySession = new SurveySession();
			surveySession.setPatientId(patient.getPatientId());
			surveySession.setSurveyType(surveyType);
			surveySession.setSessionId(session.getSessionId());
			surveySession.setEncounterId(encounter.getEncounterId());
			surveySession.setStartTime(now);
			dao.cupSurveySession(surveySession);
			mSessionCompleted.put(patient, Boolean.FALSE);
		}
		
		return session;
	}

	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#calculateSurveyType()
	 */
	public String calculateSurveyType() {
		String surveyType = null;
		Date now = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(now);
		
		if(surveyType == null) {
			if(cal.get(Calendar.DAY_OF_MONTH) == cal.getMaximum(Calendar.DAY_OF_MONTH)) {
				surveyType = SURVEY_TYPE_MONTHLY;
			} else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)  
				surveyType = SURVEY_TYPE_WEEKLY;
			else
				surveyType = SURVEY_TYPE_DAILY;
		}
		
		return surveyType;
	}
		
	public PearlgrlzDAO getPearlgrlzDAO() {
		return this.dao;
	}
	

	public void setPearlgrlzDAO(PearlgrlzDAO dao) {
		this.dao = dao;
	}
	
	
	public SurveyParameterHandler getParameterHandler() {
		return this.parameterHandler;
	}
	
	
	public void setParameterHandler(SurveyParameterHandler pHandler) {
		this.parameterHandler = pHandler;
	}
	
	
    /** 
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#createSurveyXML(org.openmrs.Patient, java.lang.Integer, java.lang.Integer, java.lang.Integer, org.openmrs.User)
     */
    public void createSurveyXML(Patient patient, Integer locationId, Integer formId, 
                                Integer numQuestions, User provider, Integer locationTagId,
                                FormInstance formInstance,Integer encounterId) {
		
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
				locationTagId,encounterId);
		
		output.close();  
		} catch(Exception e) {
			log.debug(e);
		}
	}
    
	/**
	 * @param map
	 * @param formId
	 * @param formInstanceId
	 * @param locationTagId
	 * @param locationId
	 * @param translator
	 * @param inputMergeFile
	 * @param request
	 */
	public void saveForm(Map map, Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
	                     TeleformTranslator translator, InputStream inputMergeFile, 
	                     HttpServletRequest request, User provider) {
		
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		if(translator == null ) 
    		translator = new TeleformTranslator();
		
		try {
			HashSet<String> inputFields = new HashSet<String>();
			
			Form form = Context.getFormService().getForm(formId);
	    	
	    	List<FormField> flds = form.getOrderedFormFields();
	    	
	    	for (int nbr = 0; nbr < flds.size(); ++nbr) {
	    		org.openmrs.Field currField = flds.get(nbr).getField();
	    		FieldType fieldType = currField.getFieldType();
	    		if (fieldType != null && fieldType.equals(translator.getFieldType("Export Field"))) {
	    			inputFields.add(currField.getName());
	    		}
	    	}
		    
			Records records = (Records) XMLUtil.deserializeXML(Records.class, inputMergeFile);
			inputMergeFile.close();
			Record record = records.getRecord(); 
			
			//Link the values from the submitted answers to the form fields
			for (Field currField : record.getFields()) {
				String name = currField.getId();
				
				if (inputFields.contains(name)) {
					// Check what's inside the multiselect
					String inputVal = "";
					String[] vals = request.getParameterValues(name);
					if(vals != null && vals.length >=1) {
						for(int i=0; i<vals.length; ++i) {
							inputVal += vals[i];
							if(i != vals.length -1) 
								inputVal += SURVEY_VALUE_DELIMITOR;
						}
					}
					currField.setValue(inputVal);
				}
			}
			
			
			String exportDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
			    formId, "defaultExportDirectory", locationTagId, locationId));
			
			FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
			//Write the xml for the export file
			String exportFilename = exportDirectory + formInstance.toString() + ".xml";
			
			OutputStream output = new FileOutputStream(exportFilename);
			XMLUtil.serializeXML(records, output);
			output.flush();
			output.close();
			
			
			// Reads in the saved xml file, and run the rules
			LogicService logicService = Context.getLogicService();
			InputStream input = new FileInputStream(exportFilename);
			
			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
			xmlDatasource.deleteParsedFile(formInstance);
			formInstance = xmlDatasource.parse(input, formInstance, locationTagId);
			
			// make sure storeObs gets loaded before running consume rules       
			DssService dssService = Context.getService(DssService.class); 
			ATDService atdService = Context.getService(ATDService.class);  
			dssService.loadRule("storeObs",false); 
			dssService.loadRule("storeObsMultipleAnswers",false);
			dssService.loadRule("storeGroupedObs",false);
			Patient patient = getPatientByUserId(Context.getAuthenticatedUser().getPersonId());
			                                                                  
			Encounter encounter =  getEncounter(patient,provider,location);
			Session session = getSession(patient,provider,location);
			
			atdService.consume(input, formInstance, patient, encounter.getEncounterId(),  request.getParameterMap(), null, parameterHandler,  null, locationTagId, session.getSessionId());                        
			
		} catch(Exception e) {
			log.error("Failed to save the result for this form",  e);
		}
	}

		
		
    
    
    /**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getNumberQuestions(java.lang.Integer)
     */
    public int getNumberQuestions(Integer formId,Integer locationTagId,Integer locationId) {
    	return Integer.parseInt(Util.getFormAttributeValue(formId, "numQuestions", locationTagId, locationId)); 		
    }
    
    /**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#populatePartnerList(org.openmrs.Patient)
     */
    public List<String> populatePartnerList(Patient patient, String partnerType) {
    	List<String> partnerNms = new ArrayList<String>();
    	List<SurveyPartner> list = dao.populatePartnerList(patient, partnerType);
    	for(SurveyPartner ptr : list)  {
    		partnerNms.add(ptr.getPartnerName());
    	}
    	return partnerNms;
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
	                    Location location, User provider,Integer locationTagId, Integer encounterId) {
		
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
		    getSession(patient, provider, location).getSessionId());
	}
	
	
	
	/**
	 * @param patient
	 * @param provider
	 * @param location
	 * @return
	 */
	private Encounter createEncounter(Patient patient, User provider,Location location) {
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

	
	/**
	 * @return
	 */
	private Patient createPatient(Integer personId) {
		
		Patient patient = new Patient();
		
		if( Context.getPatientService().getPatient(personId) == null) {
			patient = Context.getPatientService().savePatient(patient);		
		}
				
		return patient;
	}
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#addPartner(org.openmrs.module.pearlgrlz.SurveyPartner)
	 */
	public void addPartner(SurveyPartner partner) {
		dao.addPartner(partner);
	}
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getSurveyPartner(org.openmrs.Patient, java.lang.String, java.lang.String)
	 */
	public SurveyPartner getSurveyPartner(Patient patient, String partnerName, String partnerType) {
		return dao.getSurveyPartner(patient, partnerName, partnerType);
	}
	
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#voidPartner(org.openmrs.module.pearlgrlz.SurveyPartner, java.lang.String)
	 */
	public void voidPartner(SurveyPartner partner) {
		dao.voidPartner(partner);
	}  
    
	public Patient getPatientByUserId(Integer userId) {
		
		return Context.getPatientService().getPatient(userId);	
	}
     
    public Encounter getEncounter(Patient patient, User provider, Location location) {
    	Session session = getSurveySession(patient, null, provider,location);		// this will setEncounter
    	Integer encounterId = session.getEncounterId();
    	return Context.getEncounterService().getEncounter(encounterId);
    }
    
    
    public Session getSession(Patient patient, User provider, Location location) {
    	return getSurveySession(patient, null, provider, location);
    }
    
	/**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getPatientATD(org.openmrs.module.atd.hibernateBeans.FormInstance, org.openmrs.module.dss.hibernateBeans.Rule)
     */
    public PatientATD getPatientATD(FormInstance formInstance, Rule rule) {
	    return dao.getPatientATD(formInstance, rule);
    }

    public void updateSurveyCompleted(Patient patient, Boolean isCompleted){
		mSessionCompleted.put(patient, isCompleted);

    }
    
	/**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#isSurveyCompleted(org.openmrs.Patient)
     */
    public boolean isSurveyCompleted(Patient patient) {
	    if(mSessionCompleted.get(patient) != null) 
	    	return mSessionCompleted.get(patient).booleanValue();
	    else
	    	return false;
    }
		
    public void addGpsData(GpsData gpsData){
    	dao.addGpsData(gpsData);
    }
    
}

