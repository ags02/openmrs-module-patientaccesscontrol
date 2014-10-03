package org.openmrs.module.patientaccesscontrol.web.controller;

import java.util.List;

import org.openmrs.Program;

public class RoleProgramForm {

	private Program program;
	private List<RoleViewModel> roleViewModels;

	public RoleProgramForm(Program program, List<RoleViewModel> roleViewModels) {
		this.program = program;
		this.roleViewModels = roleViewModels;
	}

	public List<RoleViewModel> getRoleViewModels() {
		return roleViewModels;
	}

	public void setRoleViewModels(List<RoleViewModel> roleViewModels) {
		this.roleViewModels = roleViewModels;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

}
