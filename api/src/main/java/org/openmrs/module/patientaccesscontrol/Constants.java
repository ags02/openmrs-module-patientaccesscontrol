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

/**
 * Constants used by the Patient Access Control module
 */
public class Constants {
	
	// Module properties
	public static final String MODULE_ID = "patientaccesscontrol";
	
	// Privileges
	public static final String PRIV_VIEW_ROLE_PROGRAM = "View Role Program";	
	public static final String PRIV_MANAGE_ROLE_PROGRAM = "Manage Role Program";	
	public static final String PRIV_VIEW_ROLE_PATIENT = "View Role Patient";	
	public static final String PRIV_MANAGE_ROLE_PATIENT = "Manage Role Patient";
	public static final String PRIV_VIEW_USER_PATIENT = "View User Patient";	
	public static final String PRIV_MANAGE_USER_PATIENT = "Manage User Patient";
	public static final String PRIV_REFER_PATIENT = "Refer Patient";
	
	// Global property names
	public static final String PROP_CHECK_ALL_ACCESS_CONTROLS = MODULE_ID + ".checkAllAccessControls";
	
}
