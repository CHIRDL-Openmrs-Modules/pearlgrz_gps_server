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
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;


/**
 *
 */
public class SurveyParameterHandler implements ParameterHandler {
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.atd.ParameterHandler#addParameters(java.util.Map, org.openmrs.module.dss.hibernateBeans.Rule)
	 */
	@Override
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
		
		if(fieldMap.get(input1Lookup) != null && (value = fieldMap.get(input1Lookup).getValue()) != null) {
			parameters.put("Input1", "true");
			
			if(value.equals("Y"))  
				value = "Yes";
			else if(value.equals("N")) {
				parameters.put("Input1", "false");
				value = "No";
			}
			else
				parameters.put("Input1Value", value );
		}
		if(fieldMap.get(input2Lookup) != null && (value = fieldMap.get(input2Lookup).getValue()) != null) {
			parameters.put("Input2", "true");
			parameters.put("Input2Value", value);
		}
		
	}  // close method
	
}
	
