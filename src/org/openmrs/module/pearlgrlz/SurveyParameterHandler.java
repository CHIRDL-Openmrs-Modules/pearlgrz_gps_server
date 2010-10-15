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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.dss.hibernateBeans.Rule;


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
		
		if (formInstance == null)
			return;
		
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, Field> fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		setBoxParameters(parameters, fieldMap);
	}
	
	private void setBoxParameters(Map<String, Object> parameters,HashMap<String,Field> fieldMap){
		String child0Val = (String) parameters.get("child0");
		String child1Val = (String) parameters.get("child1");
		
		processInput(child0Val,parameters,fieldMap,"input1value");
		processInput(child1Val,parameters,fieldMap,"input2value");
	}
	
	private void processInput(String inputField, Map<String, Object> parameters, HashMap<String, Field> fieldMap,
	                          String parameterName) {
		if (inputField != null) {
			String answer = fieldMap.get(inputField).getValue();
			if (answer != null) {
				answer = answer.trim();
				
				if (answer.equalsIgnoreCase("Y")) {
					parameters.put("Box1", "true");
					parameters.put("box1", "true");
				} else if (answer.equalsIgnoreCase("N")) {
					parameters.put("Box2", "true");
					parameters.put("box2", "true");
				} else {
					parameters.put(parameterName, answer);
				}
			}
		}
	}
}
	
