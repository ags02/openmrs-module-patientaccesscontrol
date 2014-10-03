package org.openmrs.module.patientaccesscontrol.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("**/patientList.portlet")
public class PatientListPortletController extends PortletController {

	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
	}
}