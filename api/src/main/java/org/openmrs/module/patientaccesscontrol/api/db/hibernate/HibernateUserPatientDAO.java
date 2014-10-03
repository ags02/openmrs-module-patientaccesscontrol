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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.patientaccesscontrol.UserPatient;
import org.openmrs.module.patientaccesscontrol.api.db.UserPatientDAO;

/**
 * It is a default implementation of {@link UserPatientDAO}.
 */
public class HibernateUserPatientDAO implements UserPatientDAO {
	
	protected static final Log log = LogFactory.getLog(HibernateUserPatientDAO.class);
	
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
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#createProgram(org.openmrs.module.patientaccesscontrol.UserPatient)
	 */
	@Override
	public UserPatient saveUserPatient(UserPatient userPatient) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(userPatient);
		return userPatient;
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#getUserPatient(java.lang.Integer)
	 */
	@Override
	public UserPatient getUserPatient(Integer userPatientId) throws DAOException {
		return (UserPatient) sessionFactory.getCurrentSession().get(UserPatient.class, userPatientId);
	}
	
	/**
	 * @see org.openmrs.api.ProgramService#getUserPatients()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<UserPatient> getAllUserPatients() throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(UserPatient.class).createAlias("patient", "patient")
		        .createAlias("user", "user").addOrder(Order.asc("patient.programId")).addOrder(Order.asc("user.user"))
		        .list();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#getUsers(Patient)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUsers(Patient patient) throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(UserPatient.class).add(Restrictions.eq("patient", patient))
		        .setProjection(Projections.distinct(Projections.property("user"))).list();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#getUserPatient(User,
	 *      Patient)
	 */
	@Override
	public UserPatient getUserPatient(User user, Patient patient) throws DAOException {
		return (UserPatient) sessionFactory.getCurrentSession().createCriteria(UserPatient.class)
		        .add(Restrictions.eq("patient", patient)).add(Restrictions.eq("user", user)).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#deleteUserPatients(Patient)
	 */
	@Override
	public void deleteUserPatients(Patient patient) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from UserPatient where patient = :patient")
		        .setParameter("patient", patient).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#deleteUserPatient(User,Patient)
	 */
	@Override
	public void deleteUserPatient(User user, Patient patient) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from UserPatient where patient = :patient and user = :user")
		        .setParameter("patient", patient).setParameter("user", user).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#deleteUserPatients(User)
	 */
	@Override
	public void deleteUserPatients(User user) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from UserPatient where user = :user")
		        .setParameter("user", user).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#getExcludedPatients(user)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getIncludedPatients(User user) {
		return sessionFactory.getCurrentSession().createCriteria(UserPatient.class).add(Restrictions.eq("user", user))
		        .createAlias("patient", "p").setProjection(Projections.distinct(Projections.property("p.patientId"))).list();
	}
}
