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
package org.openmrs.module.programaccesscontrol.api.db.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.PatientSearchCriteria;
import org.openmrs.module.programaccesscontrol.ModulePatient;
import org.openmrs.module.programaccesscontrol.PatientProgramModel;
import org.openmrs.module.programaccesscontrol.api.db.ExtraProjections;
import org.openmrs.module.programaccesscontrol.api.db.OrderNullsLast;
import org.openmrs.module.programaccesscontrol.api.db.RoleAccessControlDAO;
import org.openmrs.util.OpenmrsConstants;

/**
 * It is a default implementation of {@link RoleAccessControlDAO}.
 */
public class HibernateRoleAccessControlDAO implements RoleAccessControlDAO {
	
	protected static final Log log = LogFactory.getLog(HibernateRoleAccessControlDAO.class);
	
	private SessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * @see RoleAccessControlDAO#getCountOfPatients(String, String, List, boolean, boolean)
	 */
	@Override
	public Long getCountOfPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                               boolean matchIdentifierExactly, boolean searchOnNamesOrIdentifiers,
	                               List<Program> includePrograms, boolean excludePatientNotInPrograms,
	                               List<Integer> excludePatients) {
		if (excludePatientNotInPrograms && includePrograms.isEmpty()) {
			return 0L;
		}
		
		Criteria criteria = createPatientCriteria(includePrograms, false, false, excludePatientNotInPrograms,
		    excludePatients);
		
		criteria = new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(name, identifier, identifierTypes,
		    matchIdentifierExactly, false, searchOnNamesOrIdentifiers).setProjection(Projections.countDistinct("patientId"));
		
		return (Long) criteria.uniqueResult();
	}
	
	/**
	 * @see RoleAccessControlDAO#getPatients(String, String, List, boolean, Integer, Integer,
	 *      boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                 boolean matchIdentifierExactly, Integer start, Integer length,
	                                 boolean searchOnNamesOrIdentifiers, List<Program> includePrograms,
	                                 boolean excludePatientNotInPrograms, List<Integer> excludePatients) throws DAOException {
		if (excludePatientNotInPrograms && includePrograms.isEmpty()) {
			return new ArrayList<Patient>();
		}
		Criteria criteria = createPatientCriteria(includePrograms, false, false, excludePatientNotInPrograms,
		    excludePatients);
		
		criteria = new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(name, identifier, identifierTypes,
		    matchIdentifierExactly, true, searchOnNamesOrIdentifiers).setFetchMode("name", FetchMode.SELECT);
		
		if (start != null) {
			criteria.setFirstResult(start);
		}
		int limit = getMaximumSearchResults();
		if (length == null || length > limit) {
			if (log.isDebugEnabled()) {
				log.debug("Limitng the size of the number of matching patients to " + limit);
			}
			length = limit;
		}
		if (length != null) {
			criteria.setMaxResults(length);
		}
		
		return criteria.list();
	}
	
	/**
	 * @see RoleAccessControlDAO#getCountOfPatientPrograms(String, String, List, boolean, boolean)
	 */
	@Override
	public Long getCountOfPatientPrograms(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                      boolean matchIdentifierExactly, boolean searchOnNamesOrIdentifiers,
	                                      List<Program> includePrograms, boolean excludePatientNotInPrograms,
	                                      List<Integer> excludePatients) {
		if (excludePatientNotInPrograms && includePrograms.isEmpty()) {
			return 0L;
		}
		
		Criteria criteria = createPatientCriteria(includePrograms, true, false, excludePatientNotInPrograms, excludePatients);
		
		criteria = new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(name, identifier, identifierTypes,
		    matchIdentifierExactly, false, searchOnNamesOrIdentifiers).setProjection(
		    ExtraProjections.countMultipleDistinct("program.programId", true).addProperty("patientId"));
		
		return (Long) criteria.uniqueResult();
	}
	
	/**
	 * @see RoleAccessControlDAO#getPatientPrograms(String, String, List, boolean, Integer, Integer,
	 *      boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PatientProgramModel> getPatientPrograms(String name, String identifier,
	                                                    List<PatientIdentifierType> identifierTypes,
	                                                    boolean matchIdentifierExactly, Integer start, Integer length,
	                                                    boolean searchOnNamesOrIdentifiers, List<Program> includePrograms,
	                                                    boolean excludePatientNotInPrograms, List<Integer> excludePatients)
	    throws DAOException {
		
		if (excludePatientNotInPrograms && includePrograms.isEmpty()) {
			return new ArrayList<PatientProgramModel>();
		}
		Criteria criteria = createPatientCriteria(includePrograms, true, true, excludePatientNotInPrograms, excludePatients);
		
		criteria = new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(name, identifier, identifierTypes,
		    matchIdentifierExactly, true, searchOnNamesOrIdentifiers).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		if (start != null) {
			criteria.setFirstResult(start);
		}
		int limit = getMaximumSearchResults();
		if (length == null || length > limit) {
			if (log.isDebugEnabled()) {
				log.debug("Limitng the size of the number of matching patients to " + limit);
			}
			length = limit;
		}
		if (length != null) {
			criteria.setMaxResults(length);
		}
		
		List<Map<String, Object>> list = criteria.list();
		Set<PatientProgramModel> patientModels = new LinkedHashSet<PatientProgramModel>();
		for (Map<String, Object> map : list) {
			ModulePatient patient = (ModulePatient) map.get("patient");
			Program program = (Program) map.get("program");
			PatientProgramModel patientModel = new PatientProgramModel(patient, program);
			patientModels.add(patientModel);
		}
		return new ArrayList<PatientProgramModel>(patientModels);
	}
	
	private Criteria createPatientCriteria(List<Program> includePrograms, boolean includeProgramAlias,
	                                       boolean orderByProgram, boolean excludePatientsNotInPrograms,
	                                       List<Integer> excludePatients) {
		Date now = new Date();
		Criteria criteria = sessionFactory
		        .getCurrentSession()
		        .createCriteria(ModulePatient.class, "patient")
		        .createAlias(
		            "patientPrograms",
		            "pp",
		            excludePatientsNotInPrograms ? Criteria.INNER_JOIN : Criteria.LEFT_JOIN,
		            Restrictions
		                    .conjunction()
		                    .add(Restrictions.eq("pp.voided", false))
		                    .add(
		                        Restrictions.or(Restrictions.isNull("pp.dateEnrolled"),
		                            Restrictions.le("pp.dateEnrolled", now)))
		                    .add(
		                        Restrictions.or(Restrictions.isNull("pp.dateCompleted"),
		                            Restrictions.ge("pp.dateCompleted", now))));
		if (includeProgramAlias) {
			criteria.createAlias("pp.program", "program", Criteria.LEFT_JOIN);
			if (orderByProgram) {
				criteria.addOrder(OrderNullsLast.asc("program.name"));
			}
		}
		if (excludePatientsNotInPrograms) {
			if (!includePrograms.isEmpty()) {
				criteria.add(Restrictions.in("pp.program", includePrograms));
			}
		} else {
			if (!includePrograms.isEmpty()) {
				criteria.add(Restrictions.or(Restrictions.isNull("pp.program"),
				    Restrictions.in("pp.program", includePrograms)));
			} else {
				criteria.add(Restrictions.isNull("pp.program"));
			}
		}
		
		if (excludePatients != null && !excludePatients.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in("patientId", excludePatients)));
		}
		
		return criteria;
	}
	
	/**
	 * Fetch the max results value from the global properties table
	 * 
	 * @return Integer value for the person search max results global property
	 */
	protected static Integer getMaximumSearchResults() {
		try {
			return Integer.valueOf(Context.getAdministrationService().getGlobalProperty(
			    OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS,
			    String.valueOf(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE)));
		}
		catch (Exception e) {
			log.warn("Unable to convert the global property " + OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS
			        + "to a valid integer. Returning the default "
			        + OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE);
		}
		
		return OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE;
	}
}
