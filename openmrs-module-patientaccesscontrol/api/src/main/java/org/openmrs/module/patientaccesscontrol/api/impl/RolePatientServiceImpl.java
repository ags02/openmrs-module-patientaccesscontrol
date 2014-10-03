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
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientaccesscontrol.RolePatient;
import org.openmrs.module.patientaccesscontrol.api.RolePatientService;
import org.openmrs.module.patientaccesscontrol.api.db.RolePatientDAO;
import org.openmrs.util.RoleConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link RolePatientService}.
 */
public class RolePatientServiceImpl extends BaseOpenmrsService implements RolePatientService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private RolePatientDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(RolePatientDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public RolePatientDAO getDao() {
		return dao;
	}
	
	@Override
	public List<Role> getRoles(Patient patient) {
		return dao.getRoles(patient);
	}
	
	@Override
	public void saveRolePatient(RolePatient rolePatient) {
		dao.saveRolePatient(rolePatient);
	}
	
	@Override
	public RolePatient getRolePatient(Role role, Patient patient) {
		return dao.getRolePatient(role, patient);
	}
	
	@Override
	public void deleteRolePatient(Role role, Patient patient) {
		dao.deleteRolePatient(role, patient);
	}
	
	@Override
	public void deleteRolePatients(Role role) {
		dao.deleteRolePatients(role);
	}
	
	@Override
	public void deleteRolePatients(Patient patient) {
		dao.deleteRolePatients(patient);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient) {
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			
			List<Role> rolesWithAccess = dao.getRoles(patient);
			if (rolesWithAccess.isEmpty()) {
				return true;
			}
			
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return true;
				}
				if (rolesWithAccess.contains(role)) {
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
	public List<Integer> getExcludedPatients() {
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return new ArrayList<Integer>();
				}
			}
			
			return dao.getExcludedPatients(Context.getUserContext().getAllRoles());
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getIncludedPatients() {
		return null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getExplicitlyIncludedPatients() {
		try {
			return dao.getExplicitlyIncludedPatients(Context.getUserContext().getAllRoles());
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
}
