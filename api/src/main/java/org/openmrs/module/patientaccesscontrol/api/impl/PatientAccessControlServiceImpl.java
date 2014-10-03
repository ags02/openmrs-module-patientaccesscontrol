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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.PatientProgramModel;
import org.openmrs.module.patientaccesscontrol.api.AccessControlService;
import org.openmrs.module.patientaccesscontrol.api.PatientAccessControlService;
import org.openmrs.module.patientaccesscontrol.api.RolePatientService;
import org.openmrs.module.patientaccesscontrol.api.RoleProgramService;
import org.openmrs.module.patientaccesscontrol.api.UserPatientService;
import org.openmrs.module.patientaccesscontrol.api.db.PatientAccessControlDAO;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link PatientAccessControlService}.
 */
public class PatientAccessControlServiceImpl extends BaseOpenmrsService implements PatientAccessControlService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	@SuppressWarnings("unchecked")
	private static List<Class<? extends AccessControlService>> accessControlServices = Arrays.asList(
	    RoleProgramService.class, RolePatientService.class);
	
	private PatientAccessControlDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(PatientAccessControlDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public PatientAccessControlDAO getDao() {
		return dao;
	}
	
	private boolean checkAllAccessControls() {
		return Context.getAdministrationService().getGlobalPropertyValue(Constants.PROP_CHECK_ALL_ACCESS_CONTROLS, true);
	}
	
	private List<Program> getIncludePrograms() {
		return Context.getService(RoleProgramService.class).getPrograms();
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient) {
		if (Context.getService(UserPatientService.class).hasPrivilege(patient)) {
			return true;
		}
		
		boolean checkAllAccessControls = checkAllAccessControls();
		for (Class<? extends AccessControlService> service : accessControlServices) {
			AccessControlService svc = Context.getService(service);
			if (svc.hasPrivilege(patient)) {
				if (!checkAllAccessControls) {
					return true;
				}
			} else {
				if (checkAllAccessControls) {
					return false;
				}
			}
		}
		return checkAllAccessControls;
	}
	
	private PatientAccess getPatientAccess() {
		Set<Integer> includePatients = new HashSet<Integer>();
		Set<Integer> excludePatients = new HashSet<Integer>();
		Set<Integer> explicitlyIncludedPatients = new HashSet<Integer>();
		boolean hasInclude = false;
		boolean checkAllAccessControls = checkAllAccessControls();
		for (Class<? extends AccessControlService> service : accessControlServices) {
			AccessControlService svc = Context.getService(service);
			List<Integer> ip = svc.getIncludedPatients();
			List<Integer> ep = svc.getExcludedPatients();
			if (ip != null) {
				hasInclude = true;
				includePatients.addAll(ip);
			}
			excludePatients.addAll(ep);
			if (!checkAllAccessControls()) {
				explicitlyIncludedPatients.addAll(svc.getExplicitlyIncludedPatients());
			}
		}
		List<Integer> mustInclude = Context.getService(UserPatientService.class).getIncludedPatients();
		if (!hasInclude) {
			includePatients = null;
			if (!checkAllAccessControls) {
				excludePatients.removeAll(explicitlyIncludedPatients);
			}
		} else {
			if (!checkAllAccessControls) {
				includePatients.addAll(explicitlyIncludedPatients);
				excludePatients.removeAll(includePatients);
			}
			includePatients.addAll(mustInclude);
		}
		excludePatients.removeAll(mustInclude);
		return new PatientAccess(includePatients, excludePatients);
	}
	
	private static class PatientAccess {
		
		private final Collection<Integer> includedPatients;
		
		private final Collection<Integer> excludedPatients;
		
		public PatientAccess(Collection<Integer> includedPatients, Collection<Integer> excludedPatients) {
			this.includedPatients = includedPatients;
			this.excludedPatients = excludedPatients;
		}
		
		public Collection<Integer> getIncludedPatients() {
			return includedPatients;
		}
		
		public Collection<Integer> getExcludedPatients() {
			return excludedPatients;
		}
	}
	
	/**
	 * @see PatientAccessControlService#getCountOfPatients(String)
	 */
	@Override
	public Integer getCountOfPatients(String query) {
		int count = 0;
		if (StringUtils.isBlank(query)) {
			return count;
		}
		List<PatientIdentifierType> emptyList = new Vector<PatientIdentifierType>();
		PatientAccess patientAccess = getPatientAccess();
		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatients(null, query, emptyList, false,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients()));
		} else {
			// there is no number in the string, search on name
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatients(query, null, emptyList, false,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients()));
		}
	}
	
	/**
	 * @see PatientAccessControlService#getPatients(String, Integer, Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> getPatients(String query, Integer start, Integer length) throws APIException {
		List<Patient> patients = new Vector<Patient>();
		if (StringUtils.isBlank(query)) {
			return patients;
		}
		
		PatientAccess patientAccess = getPatientAccess();
		
		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			return dao.getPatients(null, query, Collections.EMPTY_LIST, false, start, length,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients());
		} else {
			return dao.getPatients(query, null, Collections.EMPTY_LIST, false, start, length,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients());
		}
	}
	
	/**
	 * @see PatientAccessControlService#getPatients(String, Integer, Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PatientProgramModel> getPatientPrograms(String query, Integer start, Integer length) throws APIException {
		PatientAccess patientAccess = getPatientAccess();
		if (query.matches(".*\\d+.*")) {
			return dao.getPatientPrograms(null, query, Collections.EMPTY_LIST, false, start, length,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients(), getIncludePrograms());
		} else {
			// there is no number in the string, search on name
			return dao.getPatientPrograms(query, null, Collections.EMPTY_LIST, false, start, length,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients(), getIncludePrograms());
		}
	}
	
	/**
	 * @see PatientAccessControlService#getCountOfPatients(String)
	 */
	@Override
	public Integer getCountOfPatientPrograms(String query) {
		List<PatientIdentifierType> emptyList = new Vector<PatientIdentifierType>();
		PatientAccess patientAccess = getPatientAccess();
		
		if (query.matches(".*\\d+.*")) {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatientPrograms(null, query, emptyList, false,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients(), getIncludePrograms()));
		} else {
			// there is no number in the string, search on name
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatientPrograms(query, null, emptyList, false,
			    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients(), getIncludePrograms()));
		}
	}
	
	@Override
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                 boolean matchIdentifierExactly, Integer start, Integer length) throws APIException {
		PatientAccess patientAccess = getPatientAccess();
		return dao.getPatients(name, identifier, identifierTypes, matchIdentifierExactly, start, length,
		    patientAccess.getIncludedPatients(), patientAccess.getExcludedPatients());
	}
	
}
