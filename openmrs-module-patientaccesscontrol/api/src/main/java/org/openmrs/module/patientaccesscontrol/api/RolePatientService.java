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
import org.openmrs.Role;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.RolePatient;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(RolePatientService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface RolePatientService extends AccessControlService {
	
	/**
	 * Get the roles for patient that has view access to
	 * 
	 * @param patient the patient to get
	 * @return the list of roles with view access for the given patient
	 * @throws APIException
	 * @should return all roles with view for the given Patient
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_ROLE_PATIENT)
	public List<Role> getRoles(Patient patient);
	
	/**
	 * Create or update the given Role Patient in the database
	 * 
	 * @param rolePatient the RolePatient to save
	 * @return the RolePatient that was saved
	 * @throws APIException
	 * @should save given rolePatient successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PATIENT)
	public void saveRolePatient(RolePatient rolePatient);
	
	/**
	 * Get role patient by patient and role
	 * 
	 * @param role the role to get
	 * @param patient the patient to get
	 * @return the role patient with the patient and role given
	 * @throws APIException
	 * @should return the role patient with the patient and role given
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_ROLE_PATIENT)
	public RolePatient getRolePatient(Role role, Patient patient);
	
	/**
	 * Completely removes Role Programs with the given Role and Patient from the database. This is
	 * not reversible.
	 * 
	 * @param role
	 * @param patient
	 * @throws APIException
	 * @should delete role patient for role and patient successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PATIENT)
	public void deleteRolePatient(Role role, Patient patient);
	
	/**
	 * Completely removes Role Programs with the given Role from the database. This is not
	 * reversible.
	 * 
	 * @param role
	 * @throws APIException
	 * @should delete role patients for role successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PATIENT)
	public void deleteRolePatients(Role role);
	
	/**
	 * Completely removes Role Programs with the given Patient from the database. This is not
	 * reversible.
	 * 
	 * @param patient
	 * @throws APIException
	 * @should delete role patients for patient successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PATIENT)
	public void deleteRolePatients(Patient patient);
	
	/**
	 * Checks whether or not currently authenticated user has view privilege for the given patient
	 * 
	 * @param patient
	 * @return true if authenticated user has given privilege
	 * @throws APIException
	 * @should authorize if authenticated user has view privilege for the specified patient
	 * @should authorize if anonymous user has view privilege for the specified patient
	 * @should not authorize if authenticated user does not have view privilege for the specified
	 *         patient
	 * @should not authorize if anonymous user does not have view privilege for the specified
	 *         patient
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient);
	
	/**
	 * Return a list of patients the authenticated user do not have access to
	 * 
	 * @return list of patients the authenticated user do not have access to
	 * @throws APIException
	 * @should return list of patients the authenticated user do not have access to
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getExcludedPatients();
	
	/**
	 * Return a list of patients the authenticated user have access to
	 * 
	 * @return list of patients the authenticated user have access to. null if all patients are
	 *         included.
	 * @throws APIException
	 * @should return list of patients the authenticated user have access to
	 * @should return null if user has access to all patients
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getIncludedPatients();
	
	/**
	 * Return a list of patients the authenticated user have explicit access to
	 * 
	 * @return list of patients the authenticated user have explicit access to
	 * @throws APIException
	 * @should return list of patients the authenticated user have explicit access to
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getExplicitlyIncludedPatients();
}
