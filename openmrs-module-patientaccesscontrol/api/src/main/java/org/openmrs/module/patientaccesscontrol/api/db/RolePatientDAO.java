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
package org.openmrs.module.patientaccesscontrol.api.db;

import java.util.List;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.patientaccesscontrol.RolePatient;
import org.openmrs.module.patientaccesscontrol.api.RolePatientService;

/**
 * Database methods for {@link RolePatientService}.
 */
public interface RolePatientDAO {
	
	/**
	 * @see RolePatientService#saveRolePatient(RolePatient)
	 */
	public RolePatient saveRolePatient(RolePatient rolePatient) throws DAOException;
	
	/**
	 * Get role patient by internal patient patient identifier
	 * 
	 * @param rolePatientId <code>Integer</code> internal identifier for requested Role Patient *
	 * @return requested <code>RolePatient</code>
	 * @throws DAOException
	 */
	public RolePatient getRolePatient(Integer rolePatientId) throws DAOException;
	
	/**
	 * Get all role patients
	 * 
	 * @return the list of all role patients
	 * @throws DAOException
	 */
	public List<RolePatient> getAllRolePatients() throws DAOException;
	
	/**
	 * Get the roles for the given patient
	 * 
	 * @param patient the patient to get
	 * @return the list of role with the given patient
	 * @throws DAOException
	 */
	public List<Role> getRoles(Patient patient) throws DAOException;
	
	/**
	 * Get role patient by patient and role
	 * 
	 * @param role the role to get
	 * @param patient the patient to get
	 * @return the role patient with the patient and role given
	 * @throws DAOException
	 */
	public RolePatient getRolePatient(Role role, Patient patient) throws DAOException;
	
	/**
	 * Delete role patients from database. This is included for troubleshooting and low-level system
	 * administration. This should only be called when the Patient is deleted, which ideally should
	 * never happen.
	 * 
	 * @param rolePatient RolePatient to delete
	 * @throws DAOException
	 */
	public void deleteRolePatients(Patient patient) throws DAOException;
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#deleteRolePatients(Role)
	 */
	public void deleteRolePatients(Role role) throws DAOException;
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#deleteRolePatients(Role,Patient)
	 */
	public void deleteRolePatient(Role role, Patient patient);
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#getExcludedPatients()
	 */
	public List<Integer> getExcludedPatients(Set<Role> roles);
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.RolePatientService#getExplicitlyIncludedPatients()
	 */
	public List<Integer> getExplicitlyIncludedPatients(Set<Role> roles);
	
}
