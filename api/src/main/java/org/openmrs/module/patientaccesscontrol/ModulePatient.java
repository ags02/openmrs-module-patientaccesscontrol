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
package org.openmrs.module.patientaccesscontrol;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Transient;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or
 * {@link BaseOpenmrsMetadata}.
 */
public class ModulePatient extends Patient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Set<PatientProgram> patientPrograms;
	
	private Program program;
	
	public ModulePatient() {
		
	}
	
	/**
	 * Get all of this patients patientPrograms -- both voided and non-voided ones. If you want only
	 * non-voided patientPrograms
	 * 
	 * @return Set of all known patientPrograms for this patient
	 * @see org.openmrs.PatientProgram
	 * @should not return null
	 */
	public Set<PatientProgram> getPatientPrograms() {
		if (patientPrograms == null) {
			patientPrograms = new HashSet<PatientProgram>();
		}
		return this.patientPrograms;
	}
	
	/**
	 * Update all patientPrograms for patient
	 * 
	 * @param patientPrograms Set<PatientProgram> to set as update all known patientPrograms for
	 *            patient
	 * @see org.openmrs.PatientProgram
	 */
	public void setPatientPrograms(Set<PatientProgram> patientPrograms) {
		this.patientPrograms = patientPrograms;
	}
	
	@Transient
	public Program getProgram() {
		return program;
	}
	
	public void setProgram(Program program) {
		this.program = program;
	}
	
}
