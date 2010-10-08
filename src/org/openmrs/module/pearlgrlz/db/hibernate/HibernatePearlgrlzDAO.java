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
import org.openmrs.module.pearlgrlz.SurveyPartner;
import org.openmrs.module.pearlgrlz.SurveyRecord;
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
	 * Auto generated method comment
	 * 
	 * @param patientId
	 * @return
	 */
	public SurveyRecord getLatestSurveyRecord(Patient patient) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveyRecord.class);
		
		criteria.addOrder(Order.desc("loop"));
		criteria.setMaxResults(1);
		
		return (SurveyRecord) criteria.uniqueResult();
	}
	
    /**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getSurveyPartner(org.openmrs.Patient, java.lang.String, java.lang.String)
     */
    public SurveyPartner getSurveyPartner(Patient patient, String partnerName, String partnerType) {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveyPartner.class);
    	
    	criteria.add(Restrictions.eq("patientId", patient.getPatientId()));
    	criteria.add(Restrictions.eq("partnerName", partnerName));
    	criteria.add(Restrictions.eq("partnerType", partnerType));
    	criteria.add(Restrictions.eq("voided", Boolean.FALSE));
    	criteria.addOrder(Order.desc("dateChanged"));
    	criteria.addOrder(Order.desc("nbrTimeSelected"));
    	criteria.setMaxResults(1);

    	return (SurveyPartner) criteria.uniqueResult();
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


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#populatePartnerList(org.openmrs.Patient)
     */
    public List<SurveyPartner> populatePartnerList(Patient patient, String partnerType) {
	    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveyPartner.class);
	    
	    criteria.add(Restrictions.eq("patientId", patient.getPatientId()));
	    criteria.add(Restrictions.eq("partnerType", partnerType));
	    criteria.add(Restrictions.eq("voided", Boolean.FALSE));
	    criteria.addOrder(Order.desc("dateChanged")); 
	    criteria.addOrder(Order.desc("nbrTimeSelected")); 
	    
	    return (List<SurveyPartner>) criteria.list();
    }


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#addPartner(org.openmrs.Patient, org.openmrs.module.pearlgrlz.SurveyPartner)
     */
    public void addPartner(SurveyPartner partner) {
    	if(partner == null || partner.getVoided())
    		return;
	    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveyPartner.class);
	    SurveyPartner match = null;
	    Date now = new Date();
	    
	    
	    criteria.add(Restrictions.eq("patientId", partner.getPatientId()));
	    criteria.add(Restrictions.eq("partnerType", partner.getPartnerType()));
	    criteria.add(Restrictions.eq("voided", partner.getVoided()));
	    criteria.add(Restrictions.eq("partnerName", partner.getPartnerName()));
	    criteria.addOrder(Order.desc("dateChanged")); 
	    criteria.addOrder(Order.desc("nbrTimeSelected")); 
	    criteria.setMaxResults(1);
	    
	    if( (match = (SurveyPartner) criteria.uniqueResult()) != null) {
	    	match.setDateChanged(now);
	    	match.setDateCreated(now);
	    	match.setNbrTimeSelected(match.getNbrTimeSelected() + 1);
	    	sessionFactory.getCurrentSession().saveOrUpdate(match);
	    } else
	    sessionFactory.getCurrentSession().saveOrUpdate(partner);
    }
	
	
    
    public void voidPartner(SurveyPartner partner) {
    	 if(partner == null)
 	    	return;
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveyPartner.class);
    	SurveyPartner match = null;
    	Date now = new Date();
	    
	    criteria.add(Restrictions.eq("patientId", partner.getPatientId()));
	    criteria.add(Restrictions.eq("partnerType", partner.getPartnerType()));
	    criteria.add(Restrictions.eq("voided", partner.getVoided()));
	    criteria.addOrder(Order.desc("dateChanged")); 
	    criteria.setMaxResults(1);
	    
	    if( (match = (SurveyPartner) criteria.uniqueResult()) != null) {
	    	match.setDateCreated(now);
	    	match.setDateChanged(now);
	    	match.setVoided(Boolean.TRUE);
	    	match.setVoidedBy(partner.getVoidedBy());
	    	match.setVoidReason(partner.getVoidReason());
	    	sessionFactory.getCurrentSession().saveOrUpdate(match);
	    }
    }


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getPatientATD(org.openmrs.module.atd.hibernateBeans.FormInstance, org.openmrs.module.dss.hibernateBeans.Rule)
     */
    public PatientATD getPatientATD(FormInstance formInstance, Rule rule) {
    	PatientATD patientATD = null;
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PatientATD.class);
    	
    	criteria.add(Restrictions.eq("formInstanceId", formInstance.getFormInstanceId())); 
    	criteria.add(Restrictions.eq("rule", rule));
    	
    	List<PatientATD> list = criteria.list();
    	if(list != null && list.size() > 0) 
    		patientATD = list.get(0);
    	
	    return patientATD;
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
