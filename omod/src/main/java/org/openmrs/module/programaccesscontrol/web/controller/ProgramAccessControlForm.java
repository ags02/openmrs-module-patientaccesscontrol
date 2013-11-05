package org.openmrs.module.programaccesscontrol.web.controller;

import java.util.List;

import org.openmrs.Program;

public class ProgramAccessControlForm {

	private Program program;
	private List<RoleViewModel> roleViewModels;

	public ProgramAccessControlForm(Program program, List<RoleViewModel> roleViewModels) {
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
