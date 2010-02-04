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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FieldType;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class FillOutFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String idString = request.getParameter("formInstance");
		
		Integer chosenFormId = null;
		Integer chosenFormInstanceId = null;
		Integer chosenLocationId = null;
		Integer chosenLocationTagId = null;
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		if (idString != null) {
			StringTokenizer tokenizer = new StringTokenizer(idString, "_");
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenLocationId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}
			
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenLocationTagId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenFormId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}
			
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenFormInstanceId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}
			
		} else { // TODO: Need to get the parameter from the page here.
			chosenLocationId = 1; chosenFormId = 68; chosenFormInstanceId =1;
		}
		
		String submitAnswers = request.getParameter("submitAnswers");
		TeleformTranslator translator = new TeleformTranslator();
		InputStream input = null;
		
		//if a form is chosen or scanned, find the merge file that goes with
		//that form
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
		    chosenFormId, "defaultMergeDirectory", chosenLocationTagId, chosenLocationId));
		
		// Parse the merge file
		FormInstance formInstance = new FormInstance(chosenLocationId, chosenFormId, chosenFormInstanceId);
		
		String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
		
		File file = new File(currFilename);
		if (file.exists()) {
			input = new FileInputStream(currFilename);
		}
		
		map.put("formInstance", idString);
		
		//Run this if the form is scanned
		if (submitAnswers != null && submitAnswers.length() > 0) {
			scanForm(map, chosenFormId, chosenFormInstanceId, chosenLocationTagId, chosenLocationId, translator, input,
			    request);
		} else {
			//Run this to show the form
			showForm(map, chosenFormId, chosenFormInstanceId, chosenLocationId, translator, input);
			
		}
		
		return map;
	}
	
	private static void showForm(Map map, Integer formId, Integer formInstanceId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile) throws Exception {
		FormService formService = Context.getFormService();
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource.getParsedFile(new FormInstance(
		        locationId, formId, formInstanceId));
		
		//Parse the merge file to get the field values to display
		FormInstance formInstance = xmlDatasource.parse(inputMergeFile, null, null);
		inputMergeFile.close();
		fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		List<org.openmrs.Field> fields = formService.getAllFields();
		
		//store the values of fields in the jsp map
		for (org.openmrs.Field currField : fields) {
			FieldType fieldType = currField.getFieldType();
			if (fieldType == null || !fieldType.equals(translator.getFieldType("Export Field"))) {
				Field lookupField = fieldMap.get(currField.getName());
				if (lookupField != null) {
					map.put(currField.getName(), lookupField.getValue());
				}
			}
		}
	}
	
	private static void scanForm(Map map, Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile, HttpServletRequest request) {
		try {
			//pull all the input fields from the database for the form
			FormService formService = Context.getFormService();
			HashSet<String> inputFields = new HashSet<String>();
			List<org.openmrs.Field> fields = formService.getAllFields();
			
			for (org.openmrs.Field currField : fields) {
				FieldType fieldType = currField.getFieldType();
				if (fieldType != null && fieldType.equals(translator.getFieldType("Export Field"))) {
					inputFields.add(currField.getName());
				}
			}
			
			Records records = (Records) XMLUtil.deserializeXML(Records.class, inputMergeFile);
			inputMergeFile.close();
			Record record = records.getRecord();
			
			//Link the values from the submitted answers to 
			//the form fields
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
			
			map.put("scanned", "scanned");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
