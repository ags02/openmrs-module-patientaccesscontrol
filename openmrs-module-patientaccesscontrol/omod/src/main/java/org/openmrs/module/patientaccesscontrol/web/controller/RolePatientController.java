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
package org.openmrs.module.patientaccesscontrol.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.RolePatient;
import org.openmrs.module.patientaccesscontrol.api.RolePatientService;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RolePatientController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "module/" + Constants.MODULE_ID + "/rolePatientEdit")
	public void manageRolePatient() {
		if (!hasPrivilege()) {
			return;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/" + Constants.MODULE_ID + "/rolePatientEdit")
	public void obSubmit(
			@ModelAttribute("rolePatientForm") RolePatientForm rolePatientForm,
			Errors errors, HttpSession session) {
		if (!hasPrivilege()) {
			errors.reject("auth.invalid");
			return;
		}

		Patient patient = rolePatientForm.getPatient();
		RolePatientService svc = Context.getService(RolePatientService.class);

		if (rolePatientForm.isAllowAll()) {
			svc.deleteRolePatients(patient);
		} else {
			for (RoleViewModel roleView : rolePatientForm.getRoleViewModels()) {
				Role role = roleView.getRole();
				boolean hasRole = false;
				if (roleView.isCanView()) {
					hasRole = true;
					if (svc.getRolePatient(role, patient) == null) {
						RolePatient rolePatient = new RolePatient();
						rolePatient.setRole(role);
						rolePatient.setPatient(patient);
						svc.saveRolePatient(rolePatient);
					}
				} else {
					svc.deleteRolePatient(role, patient);
				}
				Role superUser = Context.getUserService().getRole(RoleConstants.SUPERUSER);
				if (!hasRole) {
					if (svc.getRolePatient(superUser, patient) == null) {
						RolePatient rolePatient = new RolePatient();
						rolePatient.setRole(superUser);
						rolePatient.setPatient(patient);
						svc.saveRolePatient(rolePatient);
					}
				} else {
					if (svc.getRolePatient(superUser, patient) != null) {
						svc.deleteRolePatient(superUser, patient);
					}
				}
			}
		}

		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, Constants.MODULE_ID + ".saved");
	}

	@ModelAttribute("superuser")
	public String getSuperUser() {
		return RoleConstants.SUPERUSER;
	}

	@ModelAttribute("patient")
	public Patient getPatient(@RequestParam("patientId") Patient patient) {
		return patient;
	}

	@ModelAttribute("rolePatientForm")
	public RolePatientForm getPatientPatientAccessControls(@RequestParam("patientId") Patient patient) {
		List<Role> roles = Context.getService(RolePatientService.class).getRoles(patient);
		List<Role> allRoles = Context.getUserService().getAllRoles();
		List<RoleViewModel> roleViews = new ArrayList<RoleViewModel>();
		for (Role role : allRoles) {
			if (role.getRole().equals(RoleConstants.SUPERUSER)) {
				continue;
			}
			roleViews.add(new RoleViewModel(role, roles.contains(role)));
		}
		return new RolePatientForm(patient, roleViews, roles.isEmpty());
	}

	private boolean hasPrivilege() {
		return Context.hasPrivilege(Constants.PRIV_MANAGE_ROLE_PATIENT);
	}
}
