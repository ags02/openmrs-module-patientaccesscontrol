package org.openmrs.module.patientaccesscontrol.web.controller;

import java.util.List;

import org.openmrs.Patient;

public class RolePatientForm {

	private Patient patient;
	private List<RoleViewModel> roleViewModels;
	private boolean allowAll;

	public RolePatientForm(Patient patient, List<RoleViewModel> roleViewModels, boolean allowAll) {
		this.patient = patient;
		this.roleViewModels = roleViewModels;
		this.allowAll = allowAll;
	}

	public List<RoleViewModel> getRoleViewModels() {
		return roleViewModels;
	}

	public void setRoleViewModels(List<RoleViewModel> roleViewModels) {
		this.roleViewModels = roleViewModels;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public boolean isAllowAll() {
		return allowAll;
	}

	public void setAllowAll(boolean allowAll) {
		this.allowAll = allowAll;
	}

}
