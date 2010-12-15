package org.openmrs.module.pearlgrlz.rule;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
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
import org.openmrs.module.pearlgrlz.util.Util;

public class filterObsByParent implements Rule
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
		Result results = new Result(); 
		if (parameters != null)
		{
			Result obsToCheckResult = (Result) parameters.get("param1");
			Result parentObsResult = (Result) parameters.get("param2");
			Obs parentObs = null;
			if(!parentObsResult.isNull()){
				parentObs = (Obs) parentObsResult.toObject();
			}
			if(parentObs != null){
				Set<Obs> parentGroupMembers = parentObs.getGroupMembers();
				if(obsToCheckResult != null&&parentGroupMembers != null){
					for(Result currResult:obsToCheckResult){
						for(Obs currMemberObs:parentGroupMembers){
							if(currMemberObs.getObsId()== ((Obs) currResult.toObject()).getObsId()){
								results.add(currResult);
								continue;
							}
						}
					}
				}
			}
		}
		if(results.size()>0){
			return results;
		}
	
		return Result.emptyResult();
	}
}