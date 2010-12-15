package org.openmrs.module.pearlgrlz.rule;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.pearlgrlz.util.Util;

public class storeObsMultipleAnswers implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		String conceptName = null;
		Integer encounterId = null;
		String values = null;

		if (parameters != null)
		{
			conceptName = (String) parameters.get("param1");

			if (conceptName == null)
			{
				return Result.emptyResult();
			}
			encounterId = (Integer) parameters.get("encounterId");
			values = (String) parameters.get("param2");
		}
		ConceptService conceptService = Context.getConceptService();

		Concept currConcept = conceptService.getConceptByName(conceptName);
		
		StringTokenizer tokenizer = new StringTokenizer(values,",");

		while(tokenizer.hasMoreElements()){
			String value = tokenizer.nextToken();
			Util.saveObs(patient, currConcept, encounterId, value.trim());
		}
		
		return Result.emptyResult();
	}
}