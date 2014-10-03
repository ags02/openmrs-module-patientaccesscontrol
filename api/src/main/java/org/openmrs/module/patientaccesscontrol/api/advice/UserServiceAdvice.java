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
package org.openmrs.module.patientaccesscontrol.api.advice;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.api.RolePatientService;
import org.openmrs.module.patientaccesscontrol.api.RoleProgramService;
import org.openmrs.module.patientaccesscontrol.api.UserPatientService;
import org.springframework.aop.AfterReturningAdvice;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class UserServiceAdvice implements AfterReturningAdvice {
	
	protected static final Log log = LogFactory.getLog(UserServiceAdvice.class);
	
	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(Object, Method, Object[],
	 *      Object)
	 */
	@Override
	public void afterReturning(Object returnVal, Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("purgeRole")) {
			Role role = (Role) args[0];
			Context.getService(RoleProgramService.class).deleteRolePrograms(role);
			Context.getService(RolePatientService.class).deleteRolePatients(role);
		} else if (method.getName().equals("purgeUser") || method.getName().equals("deleteUser")) {
			User user = (User) args[0];
			Context.getService(UserPatientService.class).deleteUserPatients(user);
		}
	}
}
