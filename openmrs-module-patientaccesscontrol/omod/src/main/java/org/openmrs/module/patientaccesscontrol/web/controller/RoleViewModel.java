package org.openmrs.module.patientaccesscontrol.web.controller;

import org.openmrs.Role;

public class RoleViewModel {

	private Role role;
	private boolean canView;

	public RoleViewModel() {
	}

	public RoleViewModel(Role role, boolean canView) {
		this.role = role;
		this.canView = canView;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isCanView() {
		return canView;
	}

	public void setCanView(boolean canView) {
		this.canView = canView;
	}

}
