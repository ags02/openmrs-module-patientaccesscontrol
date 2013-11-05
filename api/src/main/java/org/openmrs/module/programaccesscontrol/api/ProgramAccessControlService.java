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
package org.openmrs.module.programaccesscontrol.api;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.programaccesscontrol.Constants;
import org.openmrs.module.programaccesscontrol.PatientProgramModel;
import org.openmrs.module.programaccesscontrol.RoleProgram;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(ProgramAccessControlService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface ProgramAccessControlService extends OpenmrsService {
	
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
	@Authorized(Constants.PRIV_VIEW_PROGRAM_ACCESS_CONTROL)
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
	@Authorized(Constants.PRIV_MANAGE_PROGRAM_ACCESS_CONTROL)
	public void saveRoleProgram(RoleProgram roleProgram);
	
	/**
	 * Get role program by program and role
	 * 
	 * @param program the program to get
	 * @param role the role to get
	 * @return the role program with the program and role given
	 * @throws APIException
	 * @should return the role program with the program and role given
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_PROGRAM_ACCESS_CONTROL)
	public RoleProgram getRoleProgram(Program program, Role role);
	
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
	@Authorized(Constants.PRIV_MANAGE_PROGRAM_ACCESS_CONTROL)
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
	@Authorized(Constants.PRIV_MANAGE_PROGRAM_ACCESS_CONTROL)
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
	@Authorized(Constants.PRIV_MANAGE_PROGRAM_ACCESS_CONTROL)
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
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient);
	
	/**
	 * Get the programs the currently authenticated user has view privilege to
	 * 
	 * @return the list of programs authenticated user has view privilege to
	 * @throws APIException
	 * @should return all programs authenticated user has view privilege to
	 */
	@Transactional(readOnly = true)
	public List<Program> getPrograms();
	
	/**
	 * Similar to {@link PatientService#getCountOfPatients(String)}, but only returns count of
	 * patients the authenticated user has access to
	 * 
	 * @param query the string to search on
	 * @return the number of patients matching the given search phrase, and authenticated user has
	 *         access to
	 * @should return the right count when a patient has multiple matching person names and the
	 *         authenticated user has access to
	 * @should return the right count of patients with a matching name and the authenticated user
	 *         has access to
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Integer getCountOfPatients(String query);
	
	/**
	 * Similar to {@link PatientService#getPatients(String, Integer, Integer)}, but only returns the
	 * patients the authenticated user has access to
	 * 
	 * @param query the string to search on
	 * @param start the starting index
	 * @param length the number of patients to return
	 * @return a list of matching Patients the authenticated user has access to
	 * @throws APIException
	 * @should find patients with a matching name and the authenticated user has access to
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getPatients(String query, Integer start, Integer length) throws APIException;
	
	/**
	 * Similar to
	 * {@link PatientService#getPatients(String, String, List, boolean, Integer, Integer)}, but only
	 * returns patients the authenticated user has access to
	 * 
	 * @param name (optional) this is a slight break from the norm, patients with a partial match on
	 *            this name will be returned
	 * @param identifier (optional) only patients with a matching identifier are returned
	 * @param identifierTypes (optional) the PatientIdentifierTypes to restrict to
	 * @param matchIdentifierExactly (required) if true, then the given <code>identifier</code> must
	 *            equal the id in the database. if false, then the identifier is 'searched' for by
	 *            using a regular expression
	 * @return patients that matched the given criteria (and are not voided), and authenticated user
	 *         has access to
	 * @throws APIException
	 * @should return only patients the authenticated user has access to
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                 boolean matchIdentifierExactly, Integer start, Integer length) throws APIException;
	
	/**
	 * Similar to {@link ProgramAccessControlService#getCountOfPatients(String)}, but also returns
	 * the programs the patients are enrolled in. If patient is enrolled in multiple programs, they
	 * will also be included multiple times
	 * 
	 * @param query the string to search on
	 * @return the number of patients and programs matching the given search phrase, and
	 *         authenticated user has access to
	 * @should return the right count when a patient is enrolled in multiple programs
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Integer getCountOfPatientPrograms(String query);
	
	/**
	 * Similar to {@link ProgramAccessControlService#getPatients(String, Integer, Integer)}, but
	 * also returns the programs the patients are enrolled in. If patient is enrolled in multiple
	 * programs, they will also be included multiple times
	 * 
	 * @param query the string to search on
	 * @param start the starting index
	 * @param length the number of patients to return
	 * @return a list of matching Patient Programs the authenticated user has access to
	 * @throws APIException
	 * @should find patients with a matching name and the authenticated user has access to
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<PatientProgramModel> getPatientPrograms(String query, Integer start, Integer length) throws APIException;
}
