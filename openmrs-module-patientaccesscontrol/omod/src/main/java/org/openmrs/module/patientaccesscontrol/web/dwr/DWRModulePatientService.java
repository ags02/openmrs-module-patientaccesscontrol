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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.PatientProgramModel;
import org.openmrs.module.patientaccesscontrol.UserPatient;
import org.openmrs.module.patientaccesscontrol.api.PatientAccessControlService;
import org.openmrs.module.patientaccesscontrol.api.RoleProgramService;
import org.openmrs.module.patientaccesscontrol.api.UserPatientService;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.dwr.ObsListItem;

/**
 * DWR patient methods. The methods in here are used in the webapp to get data from the database via javascript calls.
 * 
 * @see PatientService
 */
public class DWRModulePatientService implements GlobalPropertyListener {

	private static final Log log = LogFactory.getLog(DWRModulePatientService.class);

	private static Integer maximumResults;

	/**
	 * Search on the <code>searchValue</code>. If a number is in the search string, do an identifier search. Else, do a
	 * name search
	 * 
	 * @param searchValue
	 *            string to be looked for
	 * @param includeVoided
	 *            true/false whether or not to included voided patients
	 * @return Collection<Object> of PatientListItem or String
	 * @should return only patient list items with nonnumeric search
	 * @should return string warning if invalid patient identifier
	 * @should not return string warning if searching with valid identifier
	 * @should include string in results if doing extra decapitated search
	 * @should not return duplicate patient list items if doing decapitated search
	 * @should not do decapitated search if numbers are in the search string
	 * @should get results for patients that have edited themselves
	 * @should logged in user should load their own patient object
	 */
	public Collection<Object> listPatients(String searchValue, boolean includeVoided) {
		return listBatchOfPatients(searchValue, new ArrayList<Integer>(), includeVoided, null, null);
	}

	/**
	 * Search on the <code>searchValue</code>. If a number is in the search string, do an identifier search. Else, do a
	 * name search
	 * 
	 * @see RoleProgramService#getPatients(String, int, Integer)
	 * @param searchValue
	 *            string to be looked for
	 * @param includeVoided
	 *            true/false whether or not to included voided patients
	 * @param start
	 *            The starting index for the results to return
	 * @param length
	 *            The number of results of return
	 * @return Collection<Object> of PatientListItem or String
	 * @since 1.8
	 */
	public Collection<Object> listBatchOfPatients(String searchValue, List<Integer> conceptIds, boolean includeVoided,
			Integer start,
			Integer length) {
		if (maximumResults == null) {
			maximumResults = getMaximumSearchResults();
		}
		if (length != null && length > maximumResults) {
			length = maximumResults;
		}

		// the list to return
		List<Object> patientList = new Vector<Object>();

		PatientAccessControlService ps = Context.getService(PatientAccessControlService.class);
		ConceptService cs = Context.getConceptService();
		ObsService os = Context.getObsService();

		Collection<PatientProgramModel> patients;

		try {
			patients = ps.getPatientPrograms(searchValue, start, length);
		} catch (APIAuthenticationException e) {
			patientList.add(Context.getMessageSourceService().getMessage("Patient.search.error") + " - "
					+ e.getMessage());
			return patientList;
		}

		patientList = new Vector<Object>(patients.size());
		for (PatientProgramModel p : patients) {
			Map<Integer, ObsListItem> latestObs = new LinkedHashMap<Integer, ObsListItem>();
			for (Integer conceptId : conceptIds) {
				List<Obs> obs = os.getLastNObservations(1, p.getPatient(), cs.getConcept(conceptId), false);
				if (obs.isEmpty()) {
					latestObs.put(conceptId, new ObsListItem());
				} else {
					latestObs.put(conceptId, new ObsListItem(obs.get(0), Context.getLocale()));
				}
			}
			patientList.add(new ModulePatientListItem(p.getPatient(), p.getProgram(), searchValue, latestObs));
		}
		// no results found and a number was in the search --
		// should check whether the check digit is correct.
		if (patients.size() == 0 && searchValue.matches(".*\\d+.*")) {

			// Looks through all the patient identifier validators to see if this type of identifier
			// is supported for any of them. If it isn't, then no need to warn about a bad check
			// digit. If it does match, then if any of the validators validates the check digit
			// successfully, then the user is notified that the identifier has been entered correctly.
			// Otherwise, the user is notified that the identifier was entered incorrectly.

			Collection<IdentifierValidator> pivs = Context.getPatientService().getAllIdentifierValidators();
			boolean shouldWarnUser = true;
			boolean validCheckDigit = false;
			boolean identifierMatchesValidationScheme = false;

			for (IdentifierValidator piv : pivs) {
				try {
					if (piv.isValid(searchValue)) {
						shouldWarnUser = false;
						validCheckDigit = true;
					}
					identifierMatchesValidationScheme = true;
				} catch (UnallowedIdentifierException e) {
				}
			}

			if (identifierMatchesValidationScheme) {
				if (shouldWarnUser) {
					patientList
							.add("<p style=\"color:red; font-size:big;\"><b>WARNING: Identifier has been typed incorrectly!  Please double check the identifier.</b></p>");
				} else if (validCheckDigit) {
					patientList
							.add("<p style=\"color:green; font-size:big;\"><b>This identifier has been entered correctly, but still no patients have been found.</b></p>");
				}
			}
		}

		return patientList;
	}

	/**
	 * Returns a map of results with the values as count of matches and a partial list of the matching patients
	 * (depending on values of start and length parameters) while the keys are are 'count' and 'objectList'
	 * respectively, if the length parameter is not specified, then all matches will be returned from the start index if
	 * specified.
	 * 
	 * @param searchValue
	 *            patient name or identifier
	 * @param start
	 *            the beginning index
	 * @param length
	 *            the number of matching patients to return
	 * @param getMatchCount
	 *            Specifies if the count of matches should be included in the returned map
	 * @return a map of results
	 * @throws APIException
	 * @since 1.8
	 * @should signal for a new search if the new search value has matches and is a first call
	 * @should not signal for a new search if it is not the first ajax call
	 * @should not signal for a new search if the new search value has no matches
	 * @should match patient with identifiers that contain no digit
	 */
	public Map<String, Object> listCountAndPatients(String searchValue, List<Integer> conceptIds, Integer start,
			Integer length,
			boolean getMatchCount) throws APIException {

		// Map to return
		Map<String, Object> resultsMap = new HashMap<String, Object>();
		Collection<Object> objectList = new Vector<Object>();
		try {
			PatientAccessControlService ps = Context.getService(PatientAccessControlService.class);
			int patientCount = 0;
			// if this is the first call
			if (getMatchCount) {
				patientCount += ps.getCountOfPatientPrograms(searchValue);

				// if there are no results found and a number was not in the
				// search and this is the first call, then do a decapitated search:
				// trim each word down to the first three characters and search again
				if (patientCount == 0 && start == 0 && !searchValue.matches(".*\\d+.*")) {
					String[] names = searchValue.split(" ");
					String newSearch = "";
					for (String name : names) {
						if (name.length() > 3) {
							name = name.substring(0, 3);
						}
						newSearch += " " + name;
					}

					newSearch = newSearch.trim();
					if (!newSearch.equals(searchValue)) {
						newSearch = newSearch.trim();
						int newPatientCount = ps.getCountOfPatients(newSearch);
						if (newPatientCount > 0) {
							// Send a signal to the core search widget to search again against newSearch
							resultsMap.put("searchAgain", newSearch);
							resultsMap.put("notification", Context.getMessageSourceService().getMessage(
									"searchWidget.noResultsFoundFor", new Object[] { searchValue, newSearch },
									Context.getLocale()));
						}
					}
				}

				// no results found and a number was in the search --
				// should check whether the check digit is correct.
				else if (patientCount == 0 && searchValue.matches(".*\\d+.*")) {

					// Looks through all the patient identifier validators to see if this type of identifier
					// is supported for any of them. If it isn't, then no need to warn about a bad check
					// digit. If it does match, then if any of the validators validates the check digit
					// successfully, then the user is notified that the identifier has been entered correctly.
					// Otherwise, the user is notified that the identifier was entered incorrectly.

					Collection<IdentifierValidator> pivs = Context.getPatientService().getAllIdentifierValidators();
					boolean shouldWarnUser = true;
					boolean validCheckDigit = false;
					boolean identifierMatchesValidationScheme = false;

					for (IdentifierValidator piv : pivs) {
						try {
							if (piv.isValid(searchValue)) {
								shouldWarnUser = false;
								validCheckDigit = true;
							}
							identifierMatchesValidationScheme = true;
						} catch (UnallowedIdentifierException e) {
						}
					}

					if (identifierMatchesValidationScheme) {
						if (shouldWarnUser) {
							resultsMap.put("notification", "<b>"
									+ Context.getMessageSourceService().getMessage("Patient.warning.inValidIdentifier")
									+ "<b/>");
						} else if (validCheckDigit) {
							resultsMap.put("notification", "<b style=\"color:green;\">"
									+ Context.getMessageSourceService().getMessage("Patient.message.validIdentifier")
									+ "<b/>");
						}
					}
				} else {
					// ensure that count never exceeds this value because the API's service layer would never
					// return more than it since it is limited in the DAO layer
					if (maximumResults == null) {
						maximumResults = getMaximumSearchResults();
					}
					if (length != null && length > maximumResults) {
						length = maximumResults;
					}

					if (patientCount > maximumResults) {
						patientCount = maximumResults;
						if (log.isDebugEnabled()) {
							log.debug("Limitng the size of matching patients to " + maximumResults);
						}
					}
				}

			}

			// if we have any matches or this isn't the first ajax call when the caller
			// requests for the count
			if (patientCount > 0 || !getMatchCount) {
				objectList = listBatchOfPatients(searchValue, conceptIds, false, start, length);
			}

			resultsMap.put("count", patientCount);
			resultsMap.put("objectList", objectList);
		} catch (Exception e) {
			log.error("Error while searching for patients", e);
			objectList.clear();
			objectList.add(Context.getMessageSourceService().getMessage("Patient.search.error") + " - "
					+ e.getMessage());
			resultsMap.put("count", 0);
			resultsMap.put("objectList", objectList);
		}
		return resultsMap;
	}

	@Override
	public boolean supportsPropertyName(String propertyName) {
		return propertyName.equals(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS);
	}

	@Override
	public void globalPropertyChanged(GlobalProperty newValue) {
		try {
			maximumResults = Integer.valueOf(newValue.getPropertyValue());
		} catch (NumberFormatException e) {
			maximumResults = OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE;
		}
	}

	@Override
	public void globalPropertyDeleted(String propertyName) {
		maximumResults = OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE;
	}

	/**
	 * Fetch the max results value from the global properties table
	 * 
	 * @return Integer value for the person search max results global property
	 */
	private static Integer getMaximumSearchResults() {
		try {
			return Integer.valueOf(Context.getAdministrationService().getGlobalProperty(
					OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS,
					String.valueOf(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE)));
		} catch (Exception e) {
			log.warn("Unable to convert the global property "
					+ OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS
					+ "to a valid integer. Returning the default "
					+ OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE);
		}

		return OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE;
	}

	/**
	 * Refer a patient to a user.
	 * 
	 * @param patientId
	 * @param userId
	 * @return
	 */
	public String referPatientToUser(Integer patientId, Integer userId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			return "Unable to find valid patient with the supplied identification information - cannot refer patient";
		}
		User user = Context.getUserService().getUser(userId);
		if (user == null) {
			return "Unable to find valid user with the supplied identification information - cannot refer patient";
		}
		UserPatientService svc = Context.getService(UserPatientService.class);
		if (svc.getUserPatient(user, patient) == null) {
			UserPatient userPatient = new UserPatient();
			userPatient.setPatient(patient);
			userPatient.setUser(user);
			svc.saveUserPatient(userPatient);
		}

		return "Patient has been referred to user " + user.getPersonName().getFullName();
	}
}
