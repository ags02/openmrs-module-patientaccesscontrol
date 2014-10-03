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
package org.openmrs.module.patientaccesscontrol.web.dwr;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.web.dwr.ObsListItem;
import org.openmrs.web.dwr.PatientListItem;

public class ModulePatientListItem extends PatientListItem {

	protected final Log log = LogFactory.getLog(getClass());

	private String programName;

	private Map<Integer, ObsListItem> latestObs;

	public ModulePatientListItem() {
	}

	public ModulePatientListItem(Patient patient, Program program) {
		this(patient, program, null, null);
	}

	public ModulePatientListItem(Patient patient, Program program, String searchName,
			Map<Integer, ObsListItem> latestObs) {
		super(patient, searchName);

		if (program == null) {
			programName = null;
		} else {
			programName = program.getName();
		}

		if (latestObs != null) {
			this.latestObs = latestObs;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModulePatientListItem other = (ModulePatientListItem) obj;
		if (getPatientId() == null) {
			if (other.getPatientId() != null) {
				return false;
			}
		} else if (!getPatientId().equals(other.getPatientId())) {
			return false;
		}
		if (programName == null) {
			if (other.programName != null) {
				return false;
			}
		} else if (!programName.equals(other.programName)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((programName == null) ? 0 : programName.hashCode());
		return result;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public Map<Integer, ObsListItem> getLatestObs() {
		return latestObs;
	}

	public void setLatestObs(Map<Integer, ObsListItem> latestObs) {
		this.latestObs = latestObs;
	}

}
