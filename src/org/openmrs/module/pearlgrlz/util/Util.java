/**
 * 
 */
package org.openmrs.module.pearlgrlz.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.XMLUtil;

/**
 * @author Tammy Dugan
 * 
 */
public class Util
{
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";
	
	private static Log log = LogFactory.getLog( Util.class );
	public static final Random GENERATOR = new Random();
	
	
	public synchronized static void saveObs(Patient patient, Concept currConcept,
			int encounterId, String value)
	{
		if (value == null || value.length() == 0)
		{
			return;
		}
		
		ObsService obsService = Context.getObsService();
		Obs obs = new Obs();
		String datatypeName = currConcept.getDatatype().getName();

		if (datatypeName.equalsIgnoreCase("Numeric"))
		{
			try
			{
				obs.setValueNumeric(Double.parseDouble(value));
			} catch (NumberFormatException e)
			{
				log.error("Could not save value: " + value
						+ " to the database for concept "+currConcept.getName().getName());
			}
		} else if (datatypeName.equalsIgnoreCase("Coded"))
		{
			ConceptService conceptService = Context.getConceptService();
			Concept answer = conceptService.getConceptByName(value);
			if(answer == null){
				log.error(value+" is not a valid concept name. "+value+" will be stored as text.");
				obs.setValueText(value);
			}else{
				obs.setValueCoded(answer);
			}
		} else
		{
			obs.setValueText(value);
		}

		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		Location location = encounter.getLocation();
		
		obs.setPerson(patient);
		obs.setConcept(currConcept);
		obs.setLocation(location);
		obs.setEncounter(encounter);
		
		obs.setObsDatetime(new Date());
		obsService.saveObs(obs, null);

	}
}
