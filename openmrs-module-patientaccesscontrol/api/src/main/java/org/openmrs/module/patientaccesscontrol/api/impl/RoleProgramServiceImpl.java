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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientaccesscontrol.RoleProgram;
import org.openmrs.module.patientaccesscontrol.api.RoleProgramService;
import org.openmrs.module.patientaccesscontrol.api.db.RoleProgramDAO;
import org.openmrs.util.RoleConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link RoleProgramService}.
 */
public class RoleProgramServiceImpl extends BaseOpenmrsService implements RoleProgramService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private RoleProgramDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(RoleProgramDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public RoleProgramDAO getDao() {
		return dao;
	}
	
	@Override
	public List<Role> getRoles(Program program) {
		return dao.getRoles(program);
	}
	
	@Override
	public void saveRoleProgram(RoleProgram programPatientAccessControl) {
		dao.saveRoleProgram(programPatientAccessControl);
	}
	
	@Override
	public RoleProgram getRoleProgram(Role role, Program program) {
		return dao.getRoleProgram(role, program);
	}
	
	@Override
	public void deleteRoleProgram(Role role, Program program) {
		dao.deleteRoleProgram(role, program);
	}
	
	@Override
	public void deleteRolePrograms(Role role) {
		dao.deleteRolePrograms(role);
	}
	
	@Override
	public void deleteRolePrograms(Program program) {
		dao.deleteRolePrograms(program);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient) {
		try {
			if (patient == null) {
				return true;
			}
			
			Set<Role> roles = Context.getUserContext().getAllRoles();
			
			for (Role role : roles) {
				if (hasPrivilege(role, patient)) {
					return true;
				}
			}
			
			return false;
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	private boolean hasPrivilege(Role role, Patient patient) {
		if (role.getRole().equals(RoleConstants.SUPERUSER)) {
			return true;
		}
		boolean enrolled = false;
		for (PatientProgram patientProgram : Context.getProgramWorkflowService().getPatientPrograms(patient, null, null,
		    null, null, null, false)) {
			if (!patientProgram.getActive()) {
				continue;
			}
			enrolled = true;
			if (hasPrivilege(role, patientProgram.getProgram())) {
				return true;
			}
		}
		// not in any programs
		if (!enrolled) {
			return dao.getDefaultRoleProgram(role) != null;
		} else {
			return false;
		}
	}
	
	private boolean hasPrivilege(Role role, Program program) {
		if (role.getRole().equals(RoleConstants.SUPERUSER)) {
			return true;
		}
		RoleProgram fac = getRoleProgram(role, program);
		return fac != null;
	}
	
	@Override
	public List<Program> getPrograms() {
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return Context.getProgramWorkflowService().getAllPrograms();
				}
			}
			
			return dao.getPrograms(roles);
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	private boolean canViewPatientsNotInPrograms() {
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return true;
				}
			}
			
			return !dao.getDefaultRolePrograms(roles).isEmpty();
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getExcludedPatients() {
		if (!canViewPatientsNotInPrograms()) {
			return new ArrayList<Integer>();
		}
		
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return new ArrayList<Integer>();
				}
			}
			
			return dao.getExcludedPatients(getPrograms());
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getIncludedPatients() {
		if (canViewPatientsNotInPrograms()) {
			return null;
		}
		return dao.getIncludedPatients(null, null, Collections.EMPTY_LIST, false, getPrograms());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getExplicitlyIncludedPatients() {
		return dao.getIncludedPatients(null, null, Collections.EMPTY_LIST, false, getPrograms());
	}
	
}
