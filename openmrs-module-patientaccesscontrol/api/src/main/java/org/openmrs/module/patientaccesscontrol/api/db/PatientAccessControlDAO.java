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
package org.openmrs.module.patientaccesscontrol.api.db;

import java.util.Collection;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.patientaccesscontrol.PatientProgramModel;
import org.openmrs.module.patientaccesscontrol.api.PatientAccessControlService;

/**
 * Database methods for {@link PatientAccessControlService}.
 */
public interface PatientAccessControlDAO {
	
	/**
	 * @param searchOnNamesOrIdentifiers specifies if the logic should find patients that match the
	 *            name or identifier otherwise find patients that match both the name and identifier
	 * @see PatientAccessControlService#getCountOfPatients(String)
	 */
	public Long getCountOfPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                               boolean matchIdentifierExactly, Collection<Integer> includePatients,
	                               Collection<Integer> excludePatients);
	
	/**
	 * @param searchOnNamesOrIdentifiers specifies if the logic should find patients that match the
	 *            name or identifier otherwise find patients that match both the name and identifier
	 * @should escape percentage character in name phrase
	 * @should escape underscore character in name phrase
	 * @should escape an asterix character in name phrase
	 * @should escape percentage character in identifier phrase
	 * @should escape underscore character in identifier phrase
	 * @should escape an asterix character in identifier phrase
	 * @should get patients with a matching identifier and type
	 */
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                 boolean matchIdentifierExactly, Integer start, Integer length,
	                                 Collection<Integer> includePatients, Collection<Integer> excludePatients)
	    throws DAOException;
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.PatientAccessControlService#getPatientPrograms(String,
	 *      Integer, Integer)
	 */
	public List<PatientProgramModel> getPatientPrograms(String name, String identifier,
	                                                    List<PatientIdentifierType> identifierTypes,
	                                                    boolean matchIdentifierExactly, Integer start, Integer length,
	                                                    Collection<Integer> includePatients,
	                                                    Collection<Integer> excludePatients,
	                                                    Collection<Program> includePrograms) throws DAOException;
	
	/**
	 * @see org.openmrs.module.patientaccesscontrol.api.PatientAccessControlService#getCountOfPatientPrograms(String,
	 *      Integer, Integer)
	 */
	public Long getCountOfPatientPrograms(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                      boolean matchIdentifierExactly, Collection<Integer> includePatients,
	                                      Collection<Integer> excludePatients, Collection<Program> includePrograms);
	
}
