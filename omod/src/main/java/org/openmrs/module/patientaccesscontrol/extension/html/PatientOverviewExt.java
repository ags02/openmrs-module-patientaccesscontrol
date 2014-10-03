package org.openmrs.module.patientaccesscontrol.extension.html;

import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.web.extension.BoxExt;

public class PatientOverviewExt extends BoxExt {

	@Override
	public String getPortletUrl() {
		return "/patientReferral";
	}

	@Override
	public String getTitle() {
		return Constants.MODULE_ID + ".patientReferral.title";
	}

	@Override
	public String getContent() {
		return null;
	}

	@Override
	public String getRequiredPrivilege() {
		return Constants.PRIV_REFER_PATIENT;
	}

}