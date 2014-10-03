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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.UserPatient;
import org.openmrs.module.patientaccesscontrol.api.UserPatientService;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserPatientController {

	protected final Log log = LogFactory.getLog(getClass());

	/** Logger for this class and subclasses */

	@RequestMapping(method = RequestMethod.GET, value = "module/" + Constants.MODULE_ID + "/userPatientEdit")
	public void manageUserPatient() {
		if (!hasPrivilege()) {
			return;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/" + Constants.MODULE_ID + "/userPatientEdit")
	public void onSubmit(@ModelAttribute("userPatientForm") UserPatientForm userPatientForm,
			Errors errors, HttpServletRequest request, HttpSession session) {
		if (!hasPrivilege()) {
			errors.reject("auth.invalid");
			return;
		}

		UserPatientService svc = Context.getService(UserPatientService.class);

		String[] userIdValues = request.getParameter("userIds").split(" ");
		List<Integer> userIds = new ArrayList<Integer>();
		for (String userIdStr : userIdValues) {
			if (!userIdStr.trim().equals("")) {
				userIds.add(Integer.valueOf(userIdStr));
			}
		}

		Patient patient = userPatientForm.getPatient();

		for (User user : userPatientForm.getUsers()) {
			if (!userIds.contains(user.getUserId())) {
				svc.deleteUserPatient(user, patient);
			}
		}

		for (Integer userId : userIds) {
			User user = new User(userId);
			if (svc.getUserPatient(user, patient) == null) {
				UserPatient userPatient = new UserPatient();
				userPatient.setUser(user);
				userPatient.setPatient(patient);
				svc.saveUserPatient(userPatient);
			}
		}

		userPatientForm.setUsers(Context.getService(UserPatientService.class).getUsers(patient));
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

	@ModelAttribute("userPatientForm")
	public UserPatientForm getUserPatientForm(@RequestParam("patientId") Patient patient) {
		return new UserPatientForm(patient, Context.getService(UserPatientService.class).getUsers(patient));
	}

	private boolean hasPrivilege() {
		return Context.hasPrivilege(Constants.PRIV_MANAGE_USER_PATIENT);
	}

}
