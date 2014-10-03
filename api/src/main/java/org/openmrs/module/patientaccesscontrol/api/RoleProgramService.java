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
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.RoleProgram;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(RoleProgramService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface RoleProgramService extends AccessControlService {
	
	/**
	 * Get the roles for program that has view access to
	 * 
	 * @param program the program to get
	 * @return the list of roles with view access for the given program
	 * @throws APIException
	 * @should return all roles with view for the given Program
	 * @should return default roles when program is null
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_ROLE_PROGRAM)
	public List<Role> getRoles(Program program);
	
	/**
	 * Create or update the given Role Program in the database
	 * 
	 * @param roleProgram the RoleProgram to save
	 * @return the RoleProgram that was saved
	 * @throws APIException
	 * @should save given roleProgram successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PROGRAM)
	public void saveRoleProgram(RoleProgram roleProgram);
	
	/**
	 * Get role program by program and role
	 * 
	 * @param role the role to get
	 * @param program the program to get
	 * @return the role program with the program and role given
	 * @throws APIException
	 * @should return the role program with the program and role given
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_ROLE_PROGRAM)
	public RoleProgram getRoleProgram(Role role, Program program);
	
	/**
	 * Completely removes Role Programs with the given Role and Program from the database. This is
	 * not reversible.
	 * 
	 * @param role
	 * @param program
	 * @throws APIException
	 * @should delete role program for role and program successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PROGRAM)
	public void deleteRoleProgram(Role role, Program program);
	
	/**
	 * Completely removes Role Programs with the given Role from the database. This is not
	 * reversible.
	 * 
	 * @param role
	 * @throws APIException
	 * @should delete role programs for role successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PROGRAM)
	public void deleteRolePrograms(Role role);
	
	/**
	 * Completely removes Role Programs with the given Program from the database. This is not
	 * reversible.
	 * 
	 * @param program
	 * @throws APIException
	 * @should delete role programs for program successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_ROLE_PROGRAM)
	public void deleteRolePrograms(Program program);
	
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
	 * Return a list of patients the authenticated user have explicit access to
	 * 
	 * @return list of patients the authenticated user have explicit access to
	 * @throws APIException
	 * @should return list of patients the authenticated user have explicit access to
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getExplicitlyIncludedPatients();
	
	/**
	 * Get the programs the currently authenticated user has view privilege to
	 * 
	 * @return the list of programs authenticated user has view privilege to
	 * @throws APIException
	 * @should return all programs authenticated user has view privilege to
	 */
	@Transactional(readOnly = true)
	public List<Program> getPrograms();
}
