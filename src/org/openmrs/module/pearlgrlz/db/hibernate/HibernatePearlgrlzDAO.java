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
package org.openmrs.module.pearlgrlz.db.hibernate;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.pearlgrlz.SurveySession;
import org.openmrs.module.pearlgrlz.db.PearlgrlzDAO;
import org.openmrs.module.pearlgrlz.hibernateBeans.GpsData;


/**
 *
 */
public class HibernatePearlgrlzDAO implements PearlgrlzDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	
    public SessionFactory getSessionFactory() {
    	return sessionFactory;
    }

	
    public void setSessionFactory(SessionFactory sessionFactory) {
    	this.sessionFactory = sessionFactory;
    }
   
	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#cupSurveySession(org.openmrs.module.pearlgrlz.SurveySession)
     */
    public void cupSurveySession(SurveySession surveySession) {
	    sessionFactory.getCurrentSession().saveOrUpdate(surveySession);
    }


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getLatestSurveySession(java.lang.Integer, java.lang.String)
     */
    public SurveySession getLatestSurveySession(Patient patient, String surveyType) {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveySession.class);
    	
    	criteria.add(Restrictions.eq("patientId", patient.getPatientId()));
    	criteria.add(Restrictions.eq("surveyType", surveyType));
    	criteria.addOrder(Order.desc("startTime"));
    	criteria.setMaxResults(1); 
    	
	    return (SurveySession) criteria.uniqueResult();
    }

	public void addGpsData(GpsData gpsData)
	{
		try
		{
			this.sessionFactory.getCurrentSession().save(gpsData);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}
    
}
