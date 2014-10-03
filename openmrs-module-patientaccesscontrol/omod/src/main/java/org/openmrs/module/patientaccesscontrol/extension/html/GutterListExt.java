package org.openmrs.module.patientaccesscontrol.extension.html;

import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.web.extension.LinkExt;

public class GutterListExt extends LinkExt {

	@Override
	public String getLabel() {
		return Constants.MODULE_ID + ".patientList.title";
	}

	@Override
	public String getUrl() {
		return "module/" + Constants.MODULE_ID + "/patient.list";
	}

	/**
	 * Returns the required privilege in order to see this section. Can be a comma delimited list of privileges. If the
	 * default empty string is returned, only an authenticated user is required
	 * 
	 * @return Privilege string
	 */
	@Override
	public String getRequiredPrivilege() {
		return "View Patients";
	}

}