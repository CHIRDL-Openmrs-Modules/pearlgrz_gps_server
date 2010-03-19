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
import java.util.SortedMap;
import java.util.TreeMap;

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
import org.openmrs.module.pearlgrlz.SurveyRecord;
import org.openmrs.module.pearlgrlz.SurveySession;
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

	private SurveyParameterHandler parameterHandler;
	
	private User provider;	
	private Location location;
	private LocationTag locationTag;
	
	private HashMap<Integer, Patient> mPatient;
	private HashMap<Patient, Integer> mFormId;
	private HashMap<Integer, Integer> mFormQuestions;
	private HashMap<Patient, Integer> mActivePage;
	private HashMap<Patient, String> mSurveyType;
	private HashMap<Patient, Encounter> mEncounter;
	private HashMap<Patient, FormInstance> mFormInstance;
	private HashMap<Patient, Session> mSession;
	private List<String> lFrequencyOptions;	// One time, Two times, ..., Not Applicable
	private List<String> lMoodOptions;		// None, Some, A lot
	private List<String> lDurationOptions;		// 1 to 3 hours, stuff
	private List<String> lScaleOptions;	// 1....5
	private HashMap<Patient, List<String>> mPatientPartnersOptions;	// The List may be changed every survey.
	
	
	private final static String   SURVEY_LINKS_DELIMITOR = ",";
	private final static String   SURVEY_DONE = "finished";
	private final static String   SURVEY_INIT = "init";
	private final static String   SURVEY_WIP = "wip";

	
	public final static String   SURVEY_STATE = "pearlgrlz_state";
	public final static String   SURVEY_TYPE_DAILY = "pearlgrlz_daily";
	public final static String   SURVEY_TYPE_WEEKLY = "pearlgrlz_weekly";
	public final static String   SURVEY_TYPE_MONTHLY = "pearlgrlz_monthly";
	public final static String   SURVEY_VOIDED_REASON_TIMESUP = "Patient did NOT finish the survey on time.";
	
	

	public PearlgrlzServiceImpl() {
		
		mActivePage = new HashMap<Patient, Integer>();
		mPatient = new HashMap<Integer, Patient>();
		mSurveyType = new HashMap<Patient, String>();
		mEncounter = new HashMap<Patient, Encounter>();
		mFormInstance = new HashMap<Patient, FormInstance>();
		mSession = new HashMap<Patient, Session>();
		mFormId = new HashMap<Patient, Integer>();
		mFormQuestions = new HashMap<Integer, Integer>();
		
		lFrequencyOptions = new ArrayList<String>();
		lFrequencyOptions.add("One time");
		lFrequencyOptions.add("Two times");
		lFrequencyOptions.add("Three times");
		lFrequencyOptions.add("Four or more times");
		lFrequencyOptions.add("Not Applicable");
		
		lMoodOptions = new ArrayList<String>();
		lMoodOptions.add("None");
		lMoodOptions.add("Some");
		lMoodOptions.add("A lot");
		
		lDurationOptions = new ArrayList<String>();
		lDurationOptions.add("Less than an hour");
		lDurationOptions.add("1 to 3 hours");
		lDurationOptions.add("4 to 6 hours");
		lDurationOptions.add("More than 6 hours");
		
		mPatientPartnersOptions = new HashMap<Patient, List<String>>(); // every patient can choose upto three partner (can pick up from the previous List);
		
		lScaleOptions = new ArrayList<String>();
		lScaleOptions.add("1");
		lScaleOptions.add("2");
		lScaleOptions.add("3");
		lScaleOptions.add("4");
		lScaleOptions.add("5");
		
	}
	
	
	public void getDefaultInfor() {
		
		// Get the default locationId and locationTagId
		if(location == null)
			location = Context.getLocationService().getDefaultLocation();
		if(locationTag == null) 
			locationTag = Context.getLocationService().getLocationTagByName("Default Location Tag");
		
		if(provider == null) 
			provider = Context.getUserService().getUsersByRole(new Role("Survey Provider")).get(0);   
	}
	
	
	
	public Session getSurveySession(Patient patient, String surveyType)  {
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
		
		SurveySession surveySession = (SurveySession) dao.getLatestSurveySession(patient, surveyType);
		
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
					log.info(Context.getAuthenticatedUser().getUserId() + "have already completed today's Survey.  A new survey will start on tomorrow");
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
		}
		
		setSession(patient, session);
		setEncounter(patient, encounter);
		
		return session;
	}
	
	
	
	/**
	 * @param patient
	 * @param surveyType
	 * @param voided
	 */
	public void endSurveySession(Patient patient, String surveyType, Boolean voided) {
		Date now = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(now);
		
		if(surveyType == null) 
			surveyType = calculateSurveyType();
		
		SurveySession surveySession = (SurveySession) dao.getLatestSurveySession(patient, surveyType);
		
		if(surveySession != null && surveySession.getEndTime() == null) {
			if(voided) 
				surveySession.setVoided(Boolean.TRUE);
			surveySession.setEndTime(new Date());
			dao.cupSurveySession(surveySession);
		}
		
		setEncounter(patient, null);
		setSession(patient, null);
	}
	
	
	
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
	
	
	public void updateState() {
		
	}
	
	
	
	public void getState() {
		
	}
	
	

	/**
	 * @param personId
	 * @param surveyType
	 */
	public String initSurvey(Integer personId,  String surveyType) {
		// Get the default locationId and locationTagId
		if(location == null)
			location = Context.getLocationService().getDefaultLocation();
		if(locationTag == null) 
			locationTag = Context.getLocationService().getLocationTagByName("Default Location Tag");
		
//		personId = Context.getUserContext().getAuthenticatedUser().getPersonId();		// Move to the initial-page controller ......
		if(provider == null) 
			provider = Context.getUserService().getUsersByRole(new Role("Survey Provider")).get(0);   
		
		
		Patient patient = null;
		
		if( (patient = Context.getPatientService().getPatient(personId)) == null) {
			patient = createPatient(personId);
		}
		
		// Cache the surveyType
		mSurveyType.put(patient, surveyType);
		
		// Get link
		Integer link = getSurveyLatestLink(patient, surveyType);
		
		if(link != null) 
			return Context.getFormService().getForm(link).getName();
		else
			return null;
	}
	
	
	public void finishSurvey() {
		
	}
	
	public void getNextState(Patient patient) {
		
	}
	

	public String nextPage() {
		String next = null;
		
		return next;
	}
	

	public String prevPage() {
		String prev = null;
		
		return prev;
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
    @Override
    public void createSurveyXML(Patient patient, Integer locationId, Integer formId, Integer numQuestions, User provider) {
		if(locationId == null) locationId = this.location.getLocationId();
		if(provider == null) provider = this.provider;
		
		FormInstance formInstance = getFormInstance(patient);
		
		//write surveyXML to "merge" directory
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util
		        .getFormAttributeValue(formId, "defaultMergeDirectory", locationTag.getLocationTagId(), locationId));
		
		String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
		
		OutputStream output;

		try {
			output = new FileOutputStream(currFilename);
		
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formId);
			String formName = form.getName();
			
			produce(output, patient, formName, numQuestions, formInstance, location, provider);
		
		output.close();  
		} catch(Exception e) {
			log.debug(e);
		}
	}
    
    
    
   
    /**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#createSurveyXML(org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.io.OutputStream, java.lang.Integer, java.lang.Integer, java.lang.Integer, org.openmrs.User)
     */
    public void createSurveyXML(Patient patient, PatientState patientState, OutputStream output, Integer locationId,Integer formId, Integer numQuestions,User provider) {
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

    
    
    
    /**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#preRender(java.util.Map, java.lang.Integer, java.lang.Integer, java.lang.Integer, org.openmrs.module.atd.TeleformTranslator, java.io.InputStream)
     */
    public void preRender(Map map, Integer formId, Integer formInstanceId, Integer locationId,
                           TeleformTranslator translator, InputStream inputMergeFile)  throws Exception {
    	FormInstance formInstance = null;
    	PatientATD patientATD  = null;
    	Rule rule = null;
    	String category = "singleChoice";
    	List<String> tokens = null;
    	int nbrQuestions = 0;
    	
    	
    	ATDService atdService = Context.getService(ATDService.class);
    	if(translator == null ) 
    		translator = new TeleformTranslator();
    	
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource.getParsedFile(new FormInstance(
		        																												locationId, formId, formInstanceId));
		
		//Parse the merge file to get the field values to display
		if(fieldMap == null) {
			formInstance = xmlDatasource.parse(inputMergeFile, null, null);
			inputMergeFile.close();
			fieldMap = xmlDatasource.getParsedFile(formInstance);
		}
		
    	
    	List<FormField> fields = Context.getFormService().getForm(formId).getOrderedFormFields();
    	
		//store the values of fields in the jsp map
		for (FormField currField : fields) {
			FieldType fieldType = currField.getField().getFieldType();
			String fieldName = currField.getField().getName();
			if (fieldType == null || !fieldType.equals(translator.getFieldType("Export Field"))) {
				Field lookupField = fieldMap.get(fieldName);
				
				if (lookupField != null) {
					if(fieldName.contains("Question")) {
						patientATD =  atdService.getPatientATD(formInstance, currField.getField().getFieldId());
						
						if(patientATD != null) {
							if( (rule = patientATD.getRule()) != null) 
								tokens = parseParameters(rule.getPurpose(), null);
							
							if(fieldName.equals("Question1") && tokens.size() >= 1) {
								category = tokens.get(0);
								map.put("category", category.trim()); 
							} else  if(tokens.size() < 1 && !category.equalsIgnoreCase("singleChoice") || tokens.size() >=1 &&  !tokens.get(0).equals(category) )  {
									break;
							}
							addParameters(map, fieldName, tokens);
							nbrQuestions++;
						} 
					} 
					map.put(fieldName, lookupField.getValue());
				} 
			} 
		} 
		
		if(nbrQuestions <= 0) {
			map.put("redirectto", "redirectto");
			map.put("nextPage", "surveyComplete");
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
	                     TeleformTranslator translator, InputStream inputMergeFile, HttpServletRequest request) {
		
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
					String inputVal = request.getParameter(name);
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
			xmlDatasource.deleteParsedFile(formInstance);	// yinweiyiqian dedongxizhaineichun.
			formInstance = xmlDatasource.parse(input, formInstance, locationTagId);
			
			// make sure storeObs gets loaded before running consume rules       
			DssService dssService = Context.getService(DssService.class); 
			ATDService atdService = Context.getService(ATDService.class);  
			dssService.loadRule("storeObs",false);  
			Patient patient = getPatientByUserId(Context.getAuthenticatedUser().getPersonId());
			                                                                  
			Encounter encounter =  getEncounter(patient);
			Session session = getSession(patient);
			
			atdService.consume(input, formInstance, patient, encounter.getEncounterId(),  request.getParameterMap(), null, parameterHandler,  null, locationTagId, session.getSessionId());                        
			
		} catch(Exception e) {
			log.error("Failed to save the result for this form",  e);
		}
	}

		
		
    
    
    /**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getNumberQuestions(java.lang.Integer)
     */
    public int getNumberQuestions(Integer formId) {
    	int nbr = 0;
    	if(mFormQuestions.get(formId) == null) {
    		nbr = Integer.parseInt(Util.getFormAttributeValue(formId, "numQuestions", locationTag.getLocationTagId(), location.getLocationId()));
    		mFormQuestions.put(formId, nbr);
    	} else {
    		nbr = mFormQuestions.get(formId).intValue();
    	}
    	
    	return nbr;
    }
    
	
    
    private List<String> parseParameters(String string, String delimitor) {
    	List<String> parameters = new ArrayList<String>();
    	
    	if(string == null)
    		return null;
    	else if(delimitor == null)
    		// Arden Syntax uses ;; for the statement, we use this ,, (double comma) for delimited-string 
    		// (note Chica Rule parser added a space in between.
    		delimitor = ", ,";				
    	
    	String [] tokens = string.split(delimitor);
    	
    	for(int i=1; i < tokens.length; ++i) {
    		if( !(tokens[i] = tokens[i].trim()).equals("") ) {
    			parameters.add(tokens[i]);
    		}
    	}
    	
    	return parameters;
    }
    
    
    
    private void addParameters(Map<String, List<String>> parametersMap, String questionFldNm, List<String> parameters) {
    	String idx;
    	
    	if(!questionFldNm.contains("Question") || (idx = questionFldNm.substring("Question".length())) == null || idx.isEmpty() 
    			|| parameters.size() <= 0 )
    		return;
    	
    	if(parameters.size() > 1) {
    		if(parameters.get(1).equalsIgnoreCase("frequencyOptions") )   {
    			System.out.println("it's frequencyOptions select");
    			parametersMap.put(questionFldNm + "List", lFrequencyOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("moodOptions") )  {
    			System.out.println("it's moodOptions select");
    			parametersMap.put(questionFldNm + "List", lMoodOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("durationOptions") )  {
    			System.out.println("it's durationOptions select");
    			parametersMap.put(questionFldNm + "List", lDurationOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("scaleOptions") )   {
    			System.out.println("it's scaleOptions select");
    			parametersMap.put(questionFldNm + "List", lScaleOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("partnerOptions") ) { 
    			System.out.println("it's partnerOptions select");
    			Patient patient = Context.getPatientService().getPatient(Context.getAuthenticatedUser().getUserId());
    			if(mPatientPartnersOptions.get(patient) == null)
    				populatePartnerList(patient);
    			parametersMap.put(questionFldNm + "List",  mPatientPartnersOptions.get(patient));
    		}
    	}
    }
    
    
    
    public List<String> populatePartnerList(Patient patient) {
    	List<String> list = dao.populatePartnerList(patient);
    	return list;
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
		
		SurveyRecord record = dao.getLatestSurveyRecord(patient);
		
		Integer encounterId = atdService.getSession(record.getLoop()).getEncounterId();
		
		atdService.produce(patient, formInstance, output, dssManager, encounterId, baseParameters, null, false, 
																		getLocationTag().getLocationTagId(), getSession(patient).getSessionId());
		
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
//		mEncounter.put(patient, encounter);
		setEncounter(patient, encounter);
		return encounter;
	}

	
	/**
	 * @return
	 */
	private Patient createPatient(Integer personId) {
		
		 String  DEFAULT_SURVEY_USER_MRS = "2222-8";
		 Integer  DEFAULT_MRS_IDENT_TYPE = 3;

		Patient patient = new Patient();
		
		if( Context.getPatientService().getPatient(personId) == null) {
			PatientIdentifier ident = new PatientIdentifier(); 
			PatientIdentifierType identType = new PatientIdentifierType();
			ident.setId(personId);
			ident.setIdentifier(DEFAULT_SURVEY_USER_MRS);
			
			identType.setPatientIdentifierTypeId(DEFAULT_MRS_IDENT_TYPE);
			ident.setIdentifierType(identType);
			
			Set<PatientIdentifier> identTypes = new HashSet<PatientIdentifier> ();
			identTypes.add(ident);
			
			patient.setId(personId);
			patient.setIdentifiers(identTypes);
			
			Context.getPatientService().savePatient(patient);
		}
		
		mPatient.put(personId, patient);
		
		return patient;
	}
	
	
	

	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getSurveyLatestLink(org.openmrs.Patient, java.lang.String)
	 */
	public Integer getSurveyLatestLink(Patient patient, String surveyType) {
		
		final String SURVEY_COMPLETE_PAGE = "surveyComplete";
		
		Integer link = null;
		boolean startNew = false;
		Encounter encounter = null;
		
		SurveyRecord record = dao.getLatestSurveyRecord(patient);
		
		// Only starts new when the loop is done, and now's a new day
		if(record != null && record.getStatus() != null && record.getStatus().equalsIgnoreCase(SURVEY_DONE) ) {
			long tm = record.getDateChanged().getTime();
			
			GregorianCalendar calc = new GregorianCalendar();
			calc.setTimeInMillis(tm);
			
			GregorianCalendar now = new GregorianCalendar();
			
			if(now.get(Calendar.DAY_OF_YEAR) > calc.get(Calendar.DAY_OF_YEAR)) {
				startNew = true;
			}
		}
		
		if(record == null || startNew || (record.getStatus() == null) ) {
			encounter = createEncounter(patient, provider, location);
			
			ATDService atdService = Context.getService(ATDService.class);
			Session session = atdService.addSession();
			session.setEncounterId(encounter.getEncounterId());
//			session = ((ATDServiceImpl)atdService).getATDDAO().addSession(session);	// Populate the sessionId in the obj
			session = atdService.updateSession(session);
			
			setSession(patient, session);
			
			record = new SurveyRecord();
			record.setParticipantId(patient.getPatientId());
			record.setLoop(session.getSessionId());
			record.setStatus(SURVEY_INIT);
			record.setDateCreated(new Date());
			record.setDateChanged(new Date());
			
			// Populate the mand links
			String records = "";
			List<Form> forms = Context.getFormService().getForms(surveyType, false);
			
			for(int i=0; i<forms.size(); ++i) {
	            records += forms.get(i).getFormId();
	            if(i != forms.size()-1) 
	            	records += ",";
            }
			
			record.setRecords(records);
			
			dao.cupSurveyRecord(record);
		
		} else {
			String []  links = record.getRecords().split(SURVEY_LINKS_DELIMITOR);
			int idx = -1;
			
			try {
				// Init state come 
				if(record.getLink() == null) {
					setActivePage(patient, Integer.parseInt(links[0]));
				} else {
					for (idx = 0; idx < links.length; ++idx) {
						if (Integer.parseInt(links[idx]) == record.getLink())
							break;
					}
					
					// Complete, mark it
					if (idx == links.length) {
						record.setStatus(SURVEY_DONE);
						record.setDateChanged(new Date());
						dao.cupSurveyRecord(record);
						link = Context.getFormService().getForm(SURVEY_COMPLETE_PAGE).getFormId();
					} else {
						link = Integer.parseInt(links[idx + 1]);
					}
				}
			} catch (Exception e) {
				;
			}
		}
		
		if(link != null) 
			setActivePage(patient, link);
		
		// If finished survey
		if(!startNew && record.getStatus().equalsIgnoreCase(SURVEY_DONE) )
			link = Context.getFormService().getForm(SURVEY_COMPLETE_PAGE).getFormId();
		
		return link;
	}
	
	
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#setSurveyLatestLink(org.openmrs.Patient, java.lang.Integer)
	 */
	public void setSurveyLatestLink(Patient patient, ArrayList<Concept> listToAdd, ArrayList<Concept> listToRemove) {
		Integer currActivePage = getActivePage(patient);	// To update the SurveyRecord.link field
		
		String linksStr = dao.getLatestSurveyRecord(patient).getRecords();
		String [] currLinks =linksStr.split(SURVEY_LINKS_DELIMITOR);
		
		ArrayList<Integer> links  = new ArrayList<Integer>();
		
		for(int i=0; i<currLinks.length; ++i) {
			links.add(Integer.parseInt(currLinks[i]));
		}
		
		
		if(listToAdd != null) {
			for (int i = 0; i < listToAdd.size(); ++i) {
				if(getPageToAdd(listToAdd.get(i)) != null && !linksStr.contains(getPageToAdd(listToAdd.get(i)) + SURVEY_LINKS_DELIMITOR)) {
					links.add(getPageToAdd(listToAdd.get(i)));
				}
			}
		}
		
		if(listToRemove != null) {
			for(int i=0; i<listToRemove.size(); ++i) {
				if(getPageToAdd(listToRemove.get(i)) != null && linksStr.contains(getPageToAdd(listToRemove.get(i)) +SURVEY_LINKS_DELIMITOR)) {
					links.remove(getPageToAdd(listToRemove.get(i)));
				}
			}
		}
		
		linksStr = "";
		for(int i=0; i<links.size(); ++i) {
			linksStr += links.get(i);
			
			if (i != links.size() - 1)
				linksStr += SURVEY_LINKS_DELIMITOR;
		}
		
		
		SurveyRecord record = dao.getLatestSurveyRecord(patient);
		record.setDateChanged(new Date());
		record.setRecords(linksStr);
		record.setLink(currActivePage);
		
		// After all the check, if current-active-page is the last one to work on
		if(links.get(links.size() -1).equals(currActivePage)) {
			record.setStatus(SURVEY_DONE);
		} else
			record.setStatus(SURVEY_WIP);
		
		dao.cupSurveyRecord(record);
		
	}
	
	
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getPageToAdd(org.openmrs.Concept)
	 */
	public Integer getPageToAdd(Concept concept) {
		return dao.getPageToAdd(concept);
	}
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getConceptPormpt(org.openmrs.Concept)
	 */
	public String getConceptPormpt(Concept concept) {
		return dao.getConceptPormpt(concept);
	}
	
	
	public String getConceptPormptSP(Concept concept) {
		return dao.getConceptPormptSP(concept);
	}
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#setActivePage(org.openmrs.Patient, java.lang.Integer)
	 */
	public void setActivePage(Patient patient, Integer  formId) {
		mActivePage.put(patient, formId);
	}
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getActivePage(org.openmrs.Patient)
	 */
	public Integer getActivePage(Patient patient) {
		return mActivePage.get(patient);
	}

	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getPreviousPages(org.openmrs.Patient, java.lang.Integer)
	 */
	public ArrayList<Integer> getPreviousPages(Patient patient, Integer activePage) {
		ArrayList<Integer> pages = new ArrayList<Integer>();
		
		SurveyRecord record = dao.getLatestSurveyRecord(patient);
		
		Integer lastPage = record.getLink();
		String list = record.getRecords();
		
		String lists[] = list.split(SURVEY_LINKS_DELIMITOR);
		
		for (String string : lists) {
			pages.add(Integer.parseInt(string));

			if(string.equals(lastPage)) 
	        	break;
        }
		
		return pages;
	}
	
	
	
	/**
	 * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getPreviousPage(org.openmrs.Patient, java.lang.Integer)
	 */
	public  Integer getPreviousPage(Patient patient, Integer activePage) {
		ArrayList<Integer> prevPages = getPreviousPages(patient, activePage);
		
		return prevPages.get(prevPages.size() -1);
	}
	
	
    public User getProvider() {
    	return provider;
    }


	
    public void setProvider(User provider) {
    	this.provider = provider;
    }



	
    public Location getLocation() {
    	return location;
    }



	
    public void setLocation(Location location) {
    	this.location = location;
    }



	
    public LocationTag getLocationTag() {
    	return locationTag;
    }



	
    public void setLocationTag(LocationTag locationTag) {
    	this.locationTag = locationTag;
    }
    
    
    
    public Patient getPatientByUserId(Integer userId) {
    	Patient patient = mPatient.get(userId);
    	if(patient == null) {
    		patient = Context.getPatientService().getPatient(userId);
    		
    		if(patient != null) {
    			mPatient.put(userId, patient);
    		} else {
    			patient = createPatient(userId);
    			mPatient.put(userId, patient);
    		}
    	}
    		
    	return mPatient.get(userId);
    }
    
    
    
    public String getSurveyType(Patient patient) {
    	return mSurveyType.get(patient);
    }
    
    
    public void setSurveyType(Patient patient, String surveyType) {
    	mSurveyType.put(patient, surveyType);
    }
    
   
    public Integer getEncounterId() {
    	// Note, this method does not 
    	return (mEncounter.get(mPatient.get(Context.getAuthenticatedUser().getPersonId()))).getEncounterId();
    }
    
    
    public Encounter getEncounter(Patient patient) {
    	if(mEncounter.get(patient)  ==  null)  {
    		getSurveySession(patient, null);		// this will setEncounter
    	}
    	return mEncounter.get(patient);
    }
    
    
    public void setEncounter(Patient patient, Encounter encounter) {
    	mEncounter.put(patient, encounter);
    }
    

    public FormInstance getFormInstance(Patient patient) {
    	return mFormInstance.get(patient);
    }
    
    
    public void setFormInstance(Patient patient, FormInstance formInstance) {
    	mFormInstance.put(patient, formInstance);
    }
    
    
    public Session getSession(Patient patient) {
    	if(mSession.get(patient) == null) {
    		getSurveySession(patient, null);
    	}
    	return mSession.get(patient);
    }
    
    public void setSession(Patient patient, Session session) {
    	mSession.put(patient, session);
    }


	/**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#getPatientATD(org.openmrs.module.atd.hibernateBeans.FormInstance, org.openmrs.module.dss.hibernateBeans.Rule)
     */
    @Override
    public PatientATD getPatientATD(FormInstance formInstance, Rule rule) {
	    return dao.getPatientATD(formInstance, rule);
    }
		
    
    
}

