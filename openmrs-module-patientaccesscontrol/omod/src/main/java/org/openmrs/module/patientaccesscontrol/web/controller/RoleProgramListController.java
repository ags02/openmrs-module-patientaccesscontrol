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

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The main controller.
 */
@Controller
@RequestMapping(value = "/module/" + Constants.MODULE_ID + "/roleProgram")
public class RoleProgramListController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET)
	public String programList(ModelMap model) {
		List<Program> programList = new Vector<Program>();

		// only fill the Object if the user has authenticated properly
		if (Context.isAuthenticated()) {
			Program defaultProgram = new Program();
			defaultProgram.setName("Default");
			defaultProgram.setDescription("Default access control for patients not in any program");
			programList.add(defaultProgram);

			programList.addAll(Context.getProgramWorkflowService().getAllPrograms());
		}

		model.addAttribute("programList", programList);

		return "module/" + Constants.MODULE_ID + "/roleProgramList";
	}
}
