package org.openmrs.module.patientaccesscontrol.web.controller;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.User;

public class UserPatientForm {

	private Patient patient;
	private List<User> users;

	public UserPatientForm(Patient patient, List<User> users) {
		this.patient = patient;
		this.users = users;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
