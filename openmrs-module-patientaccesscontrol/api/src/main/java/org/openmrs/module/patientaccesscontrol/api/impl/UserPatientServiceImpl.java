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
package org.openmrs.module.patientaccesscontrol.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientaccesscontrol.UserPatient;
import org.openmrs.module.patientaccesscontrol.api.UserPatientService;
import org.openmrs.module.patientaccesscontrol.api.db.UserPatientDAO;
import org.openmrs.util.RoleConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link UserPatientService}.
 */
public class UserPatientServiceImpl extends BaseOpenmrsService implements UserPatientService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private UserPatientDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(UserPatientDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public UserPatientDAO getDao() {
		return dao;
	}
	
	@Override
	public void saveUserPatient(UserPatient userPatient) {
		dao.saveUserPatient(userPatient);
	}
	
	@Override
	public UserPatient getUserPatient(User user, Patient patient) {
		return dao.getUserPatient(user, patient);
	}
	
	@Override
	public void deleteUserPatient(User user, Patient patient) {
		dao.deleteUserPatient(user, patient);
	}
	
	@Override
	public void deleteUserPatients(User user) {
		dao.deleteUserPatients(user);
	}
	
	@Override
	public void deleteUserPatients(Patient patient) {
		dao.deleteUserPatients(patient);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient) {
		try {
			if (!Context.getUserContext().isAuthenticated()) {
				return false;
			}
			if (dao.getUserPatient(Context.getUserContext().getAuthenticatedUser(), patient) != null) {
				return true;
			}
			
			Set<Role> roles = Context.getUserContext().getAllRoles();
			
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return true;
				}
			}
			
			return false;
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getIncludedPatients() {
		try {
			if (!Context.getUserContext().isAuthenticated()) {
				return new ArrayList<Integer>();
			}
			return dao.getIncludedPatients(Context.getUserContext().getAuthenticatedUser());
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<User> getUsers(Patient patient) {
		return dao.getUsers(patient);
	}
}
