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
package org.openmrs.module.patientaccesscontrol.api;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.UserPatient;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml.
 * <p>
 * Patients granted access through this service will always have access to patient regardless of
 * other access control checks.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(UserPatientService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface UserPatientService {
	
	/**
	 * Create or update the given User Patient in the database
	 * 
	 * @param UserPatient the UserPatient to save
	 * @return the UserPatient that was saved
	 * @throws APIException
	 * @should save given UserPatient successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_USER_PATIENT)
	public void saveUserPatient(UserPatient UserPatient);
	
	/**
	 * Get user patient by patient and user
	 * 
	 * @param user the user to get
	 * @param patient the patient to get
	 * @return the user patient with the patient and user given
	 * @throws APIException
	 * @should return the user patient with the patient and user given
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_USER_PATIENT)
	public UserPatient getUserPatient(User user, Patient patient);
	
	/**
	 * Get the list of users with access to a given a patient
	 * 
	 * @param patient the patient to get
	 * @return the list of users with access to a given patient
	 * @throws APIException
	 * @should return the list of users with access to a given patient
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_USER_PATIENT)
	public List<User> getUsers(Patient patient);
	
	/**
	 * Completely removes User Programs with the given User and Patient from the database. This is
	 * not reversible.
	 * 
	 * @param user
	 * @param patient
	 * @throws APIException
	 * @should delete user patient for user and patient successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_USER_PATIENT)
	public void deleteUserPatient(User user, Patient patient);
	
	/**
	 * Completely removes User Programs with the given User from the database. This is not
	 * reversible.
	 * 
	 * @param user
	 * @throws APIException
	 * @should delete user patients for user successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_USER_PATIENT)
	public void deleteUserPatients(User user);
	
	/**
	 * Completely removes User Patient with the given Patient from the database. This is not
	 * reversible.
	 * 
	 * @param patient
	 * @throws APIException
	 * @should delete user patients for patient successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_USER_PATIENT)
	public void deleteUserPatients(Patient patient);
	
	/**
	 * Checks whether or not currently authenticated user has view privilege for the given patient
	 * 
	 * @param patient
	 * @return true if authenticated user has given privilege
	 * @throws APIException
	 * @should authorize if authenticated user has view privilege for the specified patient
	 * @should not authorize if authenticated user does not have view privilege for the specified
	 *         patient
	 */
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient);
	
	/**
	 * Return a list of patients the authenticated user have access to
	 * 
	 * @return list of patients the authenticated user have access to. null if all patients are
	 *         included.
	 * @throws APIException
	 * @should return list of patients the authenticated user have access to
	 */
	@Transactional(readOnly = true)
	public List<Integer> getIncludedPatients();
}
