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

import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.patientaccesscontrol.UserPatient;
import org.openmrs.module.patientaccesscontrol.api.UserPatientService;

/**
 * Database methods for {@link UserPatientService}.
 */
public interface UserPatientDAO {
	
	/**
	 * @see UserPatientService#saveUserPatient(UserPatient)
	 */
	public UserPatient saveUserPatient(UserPatient userPatient) throws DAOException;
	
	/**
	 * Get user patient by internal patient patient identifier
	 * 
	 * @param userPatientId <code>Integer</code> internal identifier for requested User Patient *
	 * @return requested <code>UserPatient</code>
	 * @throws DAOException
	 */
	public UserPatient getUserPatient(Integer userPatientId) throws DAOException;
	
	/**
	 * Get all user patients
	 * 
	 * @return the list of all user patients
	 * @throws DAOException
	 */
	public List<UserPatient> getAllUserPatients() throws DAOException;
	
	/**
	 * Get the users for the given patient
	 * 
	 * @param patient the patient to get
	 * @return the list of user with the given patient
	 * @throws DAOException
	 */
	public List<User> getUsers(Patient patient) throws DAOException;
	
	/**
	 * Get user patient by patient and user
	 * 
	 * @param user the user to get
	 * @param patient the patient to get
	 * @return the user patient with the patient and user given
	 * @throws DAOException
	 */
	public UserPatient getUserPatient(User user, Patient patient) throws DAOException;
	
	/**
	 * Delete user patients from database. This is included for troubleshooting and low-level system
	 * administration. This should only be called when the Patient is deleted, which ideally should
	 * never happen.
	 * 
	 * @param userPatient UserPatient to delete
	 * @throws DAOException
	 */
	public void deleteUserPatients(Patient patient) throws DAOException;
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#deleteUserPatients(User)
	 */
	public void deleteUserPatients(User user) throws DAOException;
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#deleteUserPatients(User,Patient)
	 */
	public void deleteUserPatient(User user, Patient patient);
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.UserPatientService#getIncludedPatients()
	 */
	public List<Integer> getIncludedPatients(User user);
	
}
