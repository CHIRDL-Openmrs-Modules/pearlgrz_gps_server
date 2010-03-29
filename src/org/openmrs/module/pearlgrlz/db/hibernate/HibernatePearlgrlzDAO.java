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
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.pearlgrlz.ConceptPromptPage;
import org.openmrs.module.pearlgrlz.SurveyPartner;
import org.openmrs.module.pearlgrlz.SurveyRecord;
import org.openmrs.module.pearlgrlz.SurveySession;
import org.openmrs.module.pearlgrlz.db.PearlgrlzDAO;


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
	 * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getReprintRescanStatesByEncounter(java.lang.Integer, java.util.Date, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction,
	                                                            Integer locationTagId, Integer locationId) {
		// TODO Auto-generated method stub
		return null;
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
	 * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#createSurveyRecord(org.openmrs.module.pearlgrlz.SurveyRecord)
	 */
	public void cupSurveyRecord(SurveyRecord record) {
		sessionFactory.getCurrentSession().saveOrUpdate(record);
	}
	
	
	public String getConceptPormpt(Concept concept) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptPromptPage.class);
		
		criteria.add(Restrictions.eq("conceptId", concept.getConceptId()));
		criteria.setMaxResults(1);
		return ((ConceptPromptPage) criteria.uniqueResult()).getPrompt();
	}
	
	
	
	public String getConceptPormptSP(Concept concept) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptPromptPage.class);
		
		criteria.add(Restrictions.eq("conceptId", concept.getConceptId()));
		criteria.setMaxResults(1);
		return ((ConceptPromptPage) criteria.uniqueResult()).getPrompt_sp();
	}
	
	
	
	public Integer getPageToAdd(Concept concept) {
		Integer formId = null;
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptPromptPage.class);
		
		criteria.add(Restrictions.eq("conceptId", concept.getConceptId()));
		criteria.add(Restrictions.isNotNull("formId"));
		criteria.setMaxResults(1);
		
		if(criteria.uniqueResult() != null) {
			formId = ( (ConceptPromptPage) criteria.uniqueResult()).getFormId();
		}
		
		return formId;
	}


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getOpenSession(org.openmrs.Patient)
     */
    @Override
    public SurveySession getOpenSurveySession(Patient patient, String surveyType) {

    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveySession.class);
    	
    	criteria.add(Restrictions.eq("patientId", patient.getPatientId()));
    	criteria.add(Restrictions.eq("surveyType", surveyType));
    	criteria.add(Restrictions.eq("voided", Boolean.FALSE));
    	criteria.add(Restrictions.isNull("endTime"));
    	criteria.setMaxResults(1);
    	
	    return (SurveySession) criteria.uniqueResult();
    }
    
    
    /**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getSurveyPartners(org.openmrs.Patient)
     */
    @Override
    public List<SurveyPartner> getSurveyPartners(Patient patient, String partnerType) {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveyPartner.class);
    	
    	criteria.add(Restrictions.eq("patientId", patient.getPatientId()));
    	criteria.add(Restrictions.eq("partnerType", partnerType));
    	criteria.add(Restrictions.eq("voided", Boolean.FALSE));
    	criteria.addOrder(Order.desc("dateChanged"));
    	criteria.addOrder(Order.desc("nbrTimeSelected"));

    	return criteria.list();
    }
    
    
    
    /**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getSurveyPartner(org.openmrs.Patient, java.lang.String, java.lang.String)
     */
    @Override
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
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#cupSurveyPartner(org.openmrs.module.pearlgrlz.SurveyPartner)
     */
    @Override
    public void cupSurveyPartner(SurveyPartner surveyPartner) {
	    sessionFactory.getCurrentSession().saveOrUpdate(surveyPartner);
    }


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#cupSurveySession(org.openmrs.module.pearlgrlz.SurveySession)
     */
    @Override
    public void cupSurveySession(SurveySession surveySession) {
	    sessionFactory.getCurrentSession().saveOrUpdate(surveySession);
    }


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#getLatestSurveySession(java.lang.Integer, java.lang.String)
     */
    @Override
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
    @Override
    public List<SurveyPartner> populatePartnerList(Patient patient, String partnerType) {
	    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SurveyPartner.class);
	    
	    criteria.add(Restrictions.eq("patientId", patient.getPatientId()));
	    criteria.add(Restrictions.eq("partnerType", partnerType));
	    criteria.add(Restrictions.eq("voided", Boolean.FALSE));
	    criteria.addOrder(Order.desc("dateChanged")); 
	    criteria.addOrder(Order.desc("nbrTimeSelected")); 
	    
	    System.out.println("patientId<" + patient.getPatientId() + "> partnerType<" + partnerType + ">");
	    
	    return (List<SurveyPartner>) criteria.list();
    }


	/**
     * @see org.openmrs.module.pearlgrlz.db.PearlgrlzDAO#addPartner(org.openmrs.Patient, org.openmrs.module.pearlgrlz.SurveyPartner)
     */
    @Override
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
	    	System.out.println("inside HibernateDAO, got a match, so to set VOID");
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
    @Override
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
    
    
}
