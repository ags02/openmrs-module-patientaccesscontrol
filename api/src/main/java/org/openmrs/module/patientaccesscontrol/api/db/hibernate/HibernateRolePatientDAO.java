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

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.patientaccesscontrol.RolePatient;
import org.openmrs.module.patientaccesscontrol.api.db.RolePatientDAO;

/**
 * It is a default implementation of {@link RolePatientDAO}.
 */
public class HibernateRolePatientDAO implements RolePatientDAO {
	
	protected static final Log log = LogFactory.getLog(HibernateRolePatientDAO.class);
	
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
	 * Returns the patient access control object originally passed in, which will have been
	 * persisted.
	 * 
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#createProgram(org.openmrs.module.patientaccesscontrol.RolePatient)
	 */
	@Override
	public RolePatient saveRolePatient(RolePatient rolePatient) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(rolePatient);
		return rolePatient;
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#getRolePatient(java.lang.Integer)
	 */
	@Override
	public RolePatient getRolePatient(Integer rolePatientId) throws DAOException {
		return (RolePatient) sessionFactory.getCurrentSession().get(RolePatient.class, rolePatientId);
	}
	
	/**
	 * @see org.openmrs.api.ProgramService#getRolePatients()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RolePatient> getAllRolePatients() throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(RolePatient.class).createAlias("patient", "patient")
		        .createAlias("role", "role").addOrder(Order.asc("patient.programId")).addOrder(Order.asc("role.role"))
		        .list();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#getRoles(Patient)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getRoles(Patient patient) throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(RolePatient.class).add(Restrictions.eq("patient", patient))
		        .setProjection(Projections.distinct(Projections.property("role"))).list();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#getRolePatient(Role,
	 *      Patient)
	 */
	@Override
	public RolePatient getRolePatient(Role role, Patient patient) throws DAOException {
		return (RolePatient) sessionFactory.getCurrentSession().createCriteria(RolePatient.class)
		        .add(Restrictions.eq("patient", patient)).add(Restrictions.eq("role", role)).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#deleteRolePatients(Patient)
	 */
	@Override
	public void deleteRolePatients(Patient patient) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from RolePatient where patient = :patient")
		        .setParameter("patient", patient).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#deleteRolePatient(Role,Patient)
	 */
	@Override
	public void deleteRolePatient(Role role, Patient patient) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from RolePatient where patient = :patient and role = :role")
		        .setParameter("patient", patient).setParameter("role", role).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#deleteRolePatients(Role)
	 */
	@Override
	public void deleteRolePatients(Role role) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from RolePatient where role = :role")
		        .setParameter("role", role).executeUpdate();
	}
	
	/**
	 * @see 
	 *      org.openmrs.module.patientaccesscontrol.api.RolePatientService#getExcludedPatients(Set<Role
	 *      >)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getExcludedPatients(Set<Role> roles) {
		DetachedCriteria subquery = DetachedCriteria.forClass(RolePatient.class).add(Restrictions.in("role", roles))
		        .setProjection(Projections.distinct(Projections.property("patient")));
		
		return sessionFactory.getCurrentSession().createCriteria(RolePatient.class)
		        .add(Subqueries.propertyNotIn("patient", subquery)).createAlias("patient", "p")
		        .setProjection(Projections.distinct(Projections.property("p.patientId"))).list();
	}
	
	/**
	 * @see 
	 *      org.openmrs.module.patientaccesscontrol.api.RolePatientService#getExcludedPatients(Set<Role
	 *      >)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getExplicitlyIncludedPatients(Set<Role> roles) {
		return sessionFactory.getCurrentSession().createCriteria(RolePatient.class).add(Restrictions.in("role", roles))
		        .createAlias("patient", "p").setProjection(Projections.distinct(Projections.property("p.patientId"))).list();
	}
}
