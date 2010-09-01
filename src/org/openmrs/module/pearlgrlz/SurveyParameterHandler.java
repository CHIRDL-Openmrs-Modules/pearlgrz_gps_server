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
package org.openmrs.module.pearlgrlz;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.pearlgrlz.impl.PearlgrlzServiceImpl;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;


/**
 *
 */
public class SurveyParameterHandler implements ParameterHandler {
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.atd.ParameterHandler#addParameters(java.util.Map, org.openmrs.module.dss.hibernateBeans.Rule)
	 */
	public void addParameters(Map<String, Object> parameters, Rule rule) {
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		LogicService logicService = Context.getLogicService();
		PearlgrlzService pearlgrlzService =  (PearlgrlzService) Context.getService(PearlgrlzService.class);
		PatientATD patientATD  = null;
		Integer fieldId = null;
		String fieldName = null;
		String idx = null;
		String value = null;
		String input1Lookup = null;
		String input2Lookup = null;
		String input1Unit = null;
		String input2Unit = null;
		String tmpStr = null;
		String ruleType = rule.getRuleType();
		
		if (ruleType == null||formInstance==null)  
			return;

		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String,Field> fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		patientATD =  pearlgrlzService.getPatientATD(formInstance, rule);
		
		if(patientATD == null) {
			log.error("Failed to get PatientATD for formInstance (" + formInstance + ") ruleId (" + rule.getRuleId() + ")");
			return;
		}
		
		 fieldId = patientATD.getFieldId(); 
		 fieldName = Context.getFormService().getField(fieldId).getName();
		 
		 if(!fieldName.contains("Question")) {
				log.info("It's not a Question to save for fieldId (" + fieldId + ") fieldName (" + fieldName 
																+ ") formInstance (" + formInstance + ") ruleId (" + rule.getRuleId() + ")");
				return;
		 }
		 
		 idx = fieldName.substring("Question".length());
		 
		input1Lookup = "QuestionEntry_" + idx + "_1"; 
		input2Lookup = "QuestionEntry_" + idx + "_2"; 
		input1Unit =input1Lookup + "_1"; 
		input2Unit = input2Lookup + "_1"; 
		
		if(fieldMap.get(input1Lookup) != null && (value = fieldMap.get(input1Lookup).getValue()) != null 
				&& (value = value.trim()) != null && !value.isEmpty()) {
			parameters.put("Input1", "true");
			
			if(value.equalsIgnoreCase("Y"))  
				value = "Yes";
			else if(value.equalsIgnoreCase("N")) {
				parameters.put("Input1", "false");
				value = "No";
			} else  {
				if(fieldMap.get(input1Unit) != null && (tmpStr = fieldMap.get(input1Unit).getValue()) != null && !tmpStr.isEmpty())
					value += " " + tmpStr;
				parameters.put("Input1Value", value );
				
				String prompt = patientATD.getText();
				if(prompt.contains(PearlgrlzService.SURVEY_SEXUAL_PARTNER_TYPE) || 
						prompt.contains(PearlgrlzService.SURVEY_GENERAL_PARTNER_TYPE) ||
						prompt.contains(PearlgrlzService.SURVEY_SEXUAL_PARTNER_TYPE2) ||
						prompt.contains(PearlgrlzService.SURVEY_GENERAL_PARTNER_TYPE2))  {
					Date now = new Date();
					String type = PearlgrlzService.SURVEY_SEXUAL_PARTNER_TYPE;
					if(prompt.contains(PearlgrlzService.SURVEY_GENERAL_PARTNER_TYPE) || 
							prompt.contains(PearlgrlzService.SURVEY_GENERAL_PARTNER_TYPE2))
						type = PearlgrlzService.SURVEY_GENERAL_PARTNER_TYPE;
					if(prompt.contains("add") || prompt.contains("Add")) {
						SurveyPartner partner = new SurveyPartner();
						partner.setPatientId(patientATD.getPatientId());
						partner.setPartnerName(value);
						partner.setDateCreated(now);
						partner.setDateChanged(now);
						partner.setNbrTimeSelected(1);
						partner.setPartnerType(type);
						Context.getService(PearlgrlzService.class).addPartner(partner);
					} else {
						String [] vals = value.split(PearlgrlzService.SURVEY_VALUE_DELIMITOR);
						if(prompt.contains("remove") || prompt.contains("Remove")) {
							for (String val : vals) {
								SurveyPartner ptnr = new SurveyPartner();
								ptnr.setPatientId(patientATD.getPatientId());
								ptnr.setPartnerName(val);
								ptnr.setPartnerType(type);
								ptnr.setDateCreated(now);
								ptnr.setDateChanged(now);
								ptnr.setVoidedBy(Context.getUserService().getUser(patientATD.getPatientId()));
								ptnr.setVoidReason("Deleted by user");
								pearlgrlzService.voidPartner(ptnr);
							}
						} else if(prompt.contains("select") || prompt.contains("Select")) {
							for(int i = 0; i < vals.length; ++i) {
								// Update the counter
								SurveyPartner partner = pearlgrlzService.getSurveyPartner(
															Context.getPatientService().getPatient(patientATD.getPatientId()), vals[i], type);
								if(partner != null && i < 3) {
									int pstn = i+1;
									parameters.put("Input" + pstn + "Value", partner.getPartnerName());
									parameters.put("Input" + pstn, "true");
								}
							}
						}
					}
				}
			}
		}
		if(fieldMap.get(input2Lookup) != null && (value = fieldMap.get(input2Lookup).getValue()) != null 
				&& (value = value.trim()) != null && !value.isEmpty()) {
			if(fieldMap.get(input2Unit) != null && (tmpStr = fieldMap.get(input2Unit).getValue()) != null && !tmpStr.isEmpty())
				value += " " + tmpStr;
			parameters.put("Input2", "true");
			parameters.put("Input2Value", value);
		}
	}
	
}
	
