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
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
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
public interface AccessControlService extends OpenmrsService {
	
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
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient);
	
	/**
	 * Return a list of patients the authenticated user do not have access to
	 * 
	 * @return list of patients the authenticated user do not have access to
	 * @throws APIException
	 * @should return list of patients the authenticated user do not have access to
	 */
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
	@Transactional(readOnly = true)
	public List<Integer> getIncludedPatients();
	
	/**
	 * Return a list of patients the authenticated user have explicit access to
	 * 
	 * @return list of patients the authenticated user have explicit access to
	 * @throws APIException
	 * @should return list of patients the authenticated user have explicit access to
	 */
	@Transactional(readOnly = true)
	public List<Integer> getExplicitlyIncludedPatients();
}
