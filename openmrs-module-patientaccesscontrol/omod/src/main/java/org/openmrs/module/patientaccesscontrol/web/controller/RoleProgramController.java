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
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.RoleProgram;
import org.openmrs.module.patientaccesscontrol.api.RoleProgramService;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoleProgramController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "module/" + Constants.MODULE_ID + "/roleProgramEdit")
	public void manageRoleProgram() {
		if (!hasPrivilege()) {
			return;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/" + Constants.MODULE_ID + "/roleProgramEdit")
	public void obSubmit(
			@ModelAttribute("roleProgramForm") RoleProgramForm roleProgramForm,
			Errors errors, HttpSession session) {
		if (!hasPrivilege()) {
			errors.reject("auth.invalid");
			return;
		}

		Program program = roleProgramForm.getProgram();
		RoleProgramService svc = Context.getService(RoleProgramService.class);
		for (RoleViewModel roleView : roleProgramForm.getRoleViewModels()) {
			Role role = roleView.getRole();
			if (roleView.isCanView()) {
				if (svc.getRoleProgram(role, program) == null) {
					RoleProgram roleProgram = new RoleProgram();
					roleProgram.setRole(role);
					roleProgram.setProgram(program);
					svc.saveRoleProgram(roleProgram);
				}
			} else {
				svc.deleteRoleProgram(role, program);
			}
		}

		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, Constants.MODULE_ID + ".saved");
	}

	@ModelAttribute("superuser")
	public String getSuperUser() {
		return RoleConstants.SUPERUSER;
	}

	@ModelAttribute("program")
	public Program getProgram(@RequestParam("programId") Program program) {
		return program;
	}

	@ModelAttribute("roleProgramForm")
	public RoleProgramForm getProgramPatientAccessControls(@RequestParam("programId") Program program) {
		List<Role> roles = Context.getService(RoleProgramService.class).getRoles(program);
		List<Role> allRoles = Context.getUserService().getAllRoles();
		List<RoleViewModel> roleViews = new ArrayList<RoleViewModel>();
		for (Role role : allRoles) {
			if (role.getRole().equals(RoleConstants.SUPERUSER)) {
				continue;
			}
			roleViews.add(new RoleViewModel(role, roles.contains(role)));
		}
		return new RoleProgramForm(program, roleViews);
	}

	private boolean hasPrivilege() {
		return Context.hasPrivilege(Constants.PRIV_MANAGE_ROLE_PROGRAM);
	}
}
