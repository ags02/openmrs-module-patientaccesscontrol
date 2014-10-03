package org.openmrs.module.patientaccesscontrol.api;

import static org.junit.Assert.assertNotNull;

import org.hibernate.cfg.Environment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientAccessControlServiceUsingAndTest extends BaseModuleContextSensitiveTest {
	
	protected static final String INITIAL_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/initialTestData.xml";
	
	protected static final String PROGRAM_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/RoleProgramServiceTest.xml";
	
	protected static final String ROLE_PATIENT_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/RolePatientServiceTest.xml";
	
	protected static final String USER_PATIENT_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/UserPatientServiceTest.xml";
	
	public PatientAccessControlServiceUsingAndTest() {
		super();
		runtimeProperties.put(Environment.SHOW_SQL, "false");
	}
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(PatientAccessControlService.class));
	}
	
	private PatientAccessControlService getPatientAccessControlService() {
		return Context.getService(PatientAccessControlService.class);
	}
	
	@Before
	public void beforeTest() throws Exception {
		executeDataSet(INITIAL_DATASET_XML);
		executeDataSet(PROGRAM_DATASET_XML);
		executeDataSet(ROLE_PATIENT_DATASET_XML);
		executeDataSet(USER_PATIENT_DATASET_XML);
	}
	
	@Override
	@After
	public void deleteAllData() throws Exception {
		Context.addProxyPrivilege("View Users");
		super.deleteAllData();
		Context.removeProxyPrivilege("View Users");
	}
	
	/**
	 * @see PatientAccessControlService#getCountOfPatients(String)
	 * @verifies return the right count when a patient has multiple matching person names
	 */
	@Test
	public void getCountOfPatients_shouldReturnTheRightCountWhenAPatientHasMultipleMatchingPersonNames() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(3, getPatientAccessControlService().getCountOfPatients("").intValue());
		Assert.assertEquals(3, getPatientAccessControlService().getCountOfPatients("Test").intValue());
		
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getPatientAccessControlService().getCountOfPatients("").intValue());
		Assert.assertEquals(2, getPatientAccessControlService().getCountOfPatients("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(4, getPatientAccessControlService().getCountOfPatients("").intValue());
		Assert.assertEquals(4, getPatientAccessControlService().getCountOfPatients("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#getCountOfPatients(String)
	 * @verifies return the right count of patients with a matching name
	 */
	@Test
	public void getCountOfPatients_shouldReturnTheRightCountOfPatientsWithAMatchingName() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(1, getPatientAccessControlService().getCountOfPatients("Horn").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(0, getPatientAccessControlService().getCountOfPatients("Johnny").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(1, getPatientAccessControlService().getCountOfPatients("Johnny").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#getPatients(String,Integer,Integer)
	 * @verifies find a patients with a matching name
	 */
	@Test
	public void getPatients_shouldFindAPatientsWithAMatchingName() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(3, getPatientAccessControlService().getPatients("", null, 10).size());
		Assert.assertEquals(1, getPatientAccessControlService().getPatients("Horn", null, 10).size());
		Assert.assertEquals(1, getPatientAccessControlService().getPatients("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getPatientAccessControlService().getPatients("", null, 10).size());
		Assert.assertEquals(1, getPatientAccessControlService().getPatients("Horn", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(4, getPatientAccessControlService().getPatients("", null, 10).size());
		Assert.assertEquals(1, getPatientAccessControlService().getPatients("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#hasPrivilege(Patient)
	 * @verifies authorize if authenticated user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAuthenticatedUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#hasPrivilege(Patient)
	 * @verifies authorize if anonymous user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAnonymousUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#hasPrivilege(Patient)
	 * @verifies not authorize if authenticated user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertFalse(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#hasPrivilege(Patient)
	 * @verifies not authorize if anonymous user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertFalse(getPatientAccessControlService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#getCountOfPatientPrograms(String)
	 * @verifies return the right count when a patient is enrolled in multiple programs
	 */
	@Test
	public void getCountOfPatientPrograms_shouldReturnTheRightCountWhenAPatientIsEnrolledInMultiplePrograms()
	    throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getPatientAccessControlService().getCountOfPatientPrograms("").intValue());
		Assert.assertEquals(2, getPatientAccessControlService().getCountOfPatientPrograms("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(3, getPatientAccessControlService().getCountOfPatientPrograms("").intValue());
		Assert.assertEquals(3, getPatientAccessControlService().getCountOfPatientPrograms("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(5, getPatientAccessControlService().getCountOfPatientPrograms("").intValue());
		Assert.assertEquals(5, getPatientAccessControlService().getCountOfPatientPrograms("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see PatientAccessControlService#getPatientPrograms(String,Integer,Integer)
	 * @verifies find patients with a matching name and the authenticated user has access to
	 */
	@Test
	public void getPatientPrograms_shouldFindPatientsWithAMatchingNameAndTheAuthenticatedUserHasAccessTo() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getPatientAccessControlService().getPatientPrograms("", null, 10).size());
		Assert.assertEquals(1, getPatientAccessControlService().getPatientPrograms("Horn", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(3, getPatientAccessControlService().getPatientPrograms("", null, 10).size());
		Assert.assertEquals(2, getPatientAccessControlService().getPatientPrograms("Horn", null, 10).size());
		Assert.assertEquals(0, getPatientAccessControlService().getPatientPrograms("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(5, getPatientAccessControlService().getPatientPrograms("", null, 10).size());
		Assert.assertEquals(1, getPatientAccessControlService().getPatientPrograms("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
}
