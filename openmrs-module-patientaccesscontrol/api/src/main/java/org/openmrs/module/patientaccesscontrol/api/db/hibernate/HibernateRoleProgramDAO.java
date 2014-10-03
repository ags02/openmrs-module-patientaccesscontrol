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
package org.openmrs.module.patientaccesscontrol.api.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.PatientSearchCriteria;
import org.openmrs.module.patientaccesscontrol.RoleProgram;
import org.openmrs.module.patientaccesscontrol.api.db.PatientAccessControlDAO;
import org.openmrs.module.patientaccesscontrol.api.db.RoleProgramDAO;

/**
 * It is a default implementation of {@link RoleProgramDAO}.
 */
public class HibernateRoleProgramDAO implements RoleProgramDAO {
	
	protected static final Log log = LogFactory.getLog(HibernateRoleProgramDAO.class);
	
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
	 * Returns the program access control object originally passed in, which will have been
	 * persisted.
	 * 
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#createProgram(org.openmrs.module.patientaccesscontrol.RoleProgram)
	 */
	@Override
	public RoleProgram saveRoleProgram(RoleProgram roleProgram) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(roleProgram);
		return roleProgram;
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#getRoleProgram(java.lang.Integer)
	 */
	@Override
	public RoleProgram getRoleProgram(Integer roleProgramId) throws DAOException {
		return (RoleProgram) sessionFactory.getCurrentSession().get(RoleProgram.class, roleProgramId);
	}
	
	/**
	 * @see org.openmrs.api.ProgramService#getRolePrograms()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RoleProgram> getAllRolePrograms() throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(RoleProgram.class).createAlias("program", "program")
		        .createAlias("role", "role").addOrder(Order.asc("program.programId")).addOrder(Order.asc("role.role"))
		        .list();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#getRoles(Program)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getRoles(Program program) throws DAOException {
		if (program == null) {
			return sessionFactory.getCurrentSession().createCriteria(RoleProgram.class).add(Restrictions.isNull("program"))
			        .setProjection(Projections.distinct(Projections.property("role"))).list();
		} else {
			return sessionFactory.getCurrentSession().createCriteria(RoleProgram.class)
			        .add(Restrictions.eq("program", program))
			        .setProjection(Projections.distinct(Projections.property("role"))).list();
		}
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#getPrograms()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Program> getPrograms(Set<Role> roles) throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(RoleProgram.class).createAlias("role", "role")
		        .createAlias("program", "program").add(Restrictions.in("role", roles))
		        .setProjection(Projections.distinct(Projections.property("program"))).list();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#getRoleProgram(Role,Program)
	 */
	@Override
	public RoleProgram getRoleProgram(Role role, Program program) throws DAOException {
		if (program == null) {
			return (RoleProgram) sessionFactory.getCurrentSession().createCriteria(RoleProgram.class)
			        .add(Restrictions.isNull("program")).add(Restrictions.eq("role", role)).uniqueResult();
		} else {
			return (RoleProgram) sessionFactory.getCurrentSession().createCriteria(RoleProgram.class)
			        .add(Restrictions.eq("program", program)).add(Restrictions.eq("role", role)).uniqueResult();
		}
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#getDefaultRoleProgram(Role)
	 */
	@Override
	public RoleProgram getDefaultRoleProgram(Role role) throws DAOException {
		return (RoleProgram) sessionFactory.getCurrentSession().createCriteria(RoleProgram.class)
		        .add(Restrictions.isNull("program")).add(Restrictions.eq("role", role)).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.patientaccessControlService#
	 *      getDefaultRolePrograms(Set<Role>)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RoleProgram> getDefaultRolePrograms(Set<Role> roles) throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(RoleProgram.class).add(Restrictions.isNull("program"))
		        .add(Restrictions.in("role", roles)).list();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#deleteRolePrograms(Program)
	 */
	@Override
	public void deleteRolePrograms(Program program) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from RoleProgram where program = :program")
		        .setParameter("program", program).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#deleteRoleProgram(Role,Program)
	 */
	@Override
	public void deleteRoleProgram(Role role, Program program) throws DAOException {
		if (program == null) {
			sessionFactory.getCurrentSession().createQuery("delete from RoleProgram where program is null and role = :role")
			        .setParameter("role", role).executeUpdate();
		} else {
			sessionFactory.getCurrentSession()
			        .createQuery("delete from RoleProgram where program = :program and role = :role")
			        .setParameter("program", program).setParameter("role", role).executeUpdate();
		}
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RoleProgramService#deleteRolePrograms(Role)
	 */
	@Override
	public void deleteRolePrograms(Role role) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from RoleProgram where role = :role")
		        .setParameter("role", role).executeUpdate();
	}
	
	/**
	 * @see PatientAccessControlDAO#getPatients(String, String, List, boolean, Integer, Integer,
	 *      boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getIncludedPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                         boolean matchIdentifierExactly, List<Program> includePrograms)
	    throws DAOException {
		if (includePrograms.isEmpty()) {
			return new ArrayList<Integer>();
		}
		Criteria criteria = createPatientCriteria(includePrograms);
		
		criteria = new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(name, identifier, identifierTypes,
		    matchIdentifierExactly, false).setProjection(Projections.distinct(Projections.property("patientId")));
		
		return criteria.list();
	}
	
	private Criteria createPatientCriteria(List<Program> includePrograms) {
		Date now = new Date();
		Criteria criteria = sessionFactory
		        .getCurrentSession()
		        .createCriteria(Patient.class, "patient")
		        .createAlias(
		            "patientPrograms",
		            "pp",
		            Criteria.INNER_JOIN,
		            Restrictions
		                    .conjunction()
		                    .add(Restrictions.eq("pp.voided", false))
		                    .add(
		                        Restrictions.or(Restrictions.isNull("pp.dateEnrolled"),
		                            Restrictions.le("pp.dateEnrolled", now)))
		                    .add(
		                        Restrictions.or(Restrictions.isNull("pp.dateCompleted"),
		                            Restrictions.ge("pp.dateCompleted", now))));
		criteria.add(Restrictions.in("pp.program", includePrograms));
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getExcludedPatients(Collection<Program> programs) {
		DetachedCriteria subquery = DetachedCriteria.forClass(PatientProgram.class)
		        .add(Restrictions.in("program", programs))
		        .setProjection(Projections.distinct(Projections.property("patient")));
		
		return sessionFactory.getCurrentSession().createCriteria(PatientProgram.class)
		        .add(Subqueries.propertyNotIn("patient", subquery)).createAlias("patient", "p")
		        .setProjection(Projections.distinct(Projections.property("p.patientId"))).list();
	}
}
