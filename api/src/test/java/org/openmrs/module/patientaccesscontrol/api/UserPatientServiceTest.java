package org.openmrs.module.patientaccesscontrol.api;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.UserPatient;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class UserPatientServiceTest extends BaseModuleContextSensitiveTest {
	
	protected static final String INITIAL_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/initialTestData.xml";
	
	protected static final String DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/UserPatientServiceTest.xml";
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(UserPatientService.class));
	}
	
	private UserPatientService getUserPatientService() {
		return Context.getService(UserPatientService.class);
	}
	
	@Before
	public void beforeTest() throws Exception {
		executeDataSet(INITIAL_DATASET_XML);
		executeDataSet(DATASET_XML);
	}
	
	@Override
	@After
	public void deleteAllData() throws Exception {
		Context.addProxyPrivilege("View Users");
		super.deleteAllData();
		Context.removeProxyPrivilege("View Users");
	}
	
	/**
	 * @see UserPatientService#deleteUserPatient(User,Patient)
	 * @verifies delete user patient for user and patient successfully
	 */
	@Test
	public void deleteUserPatient_shouldDeleteUserPatientForUserAndPatientSuccessfully() throws Exception {
		Patient patient = Context.getPatientService().getPatient(6);
		User user = Context.getUserService().getUser(5505);
		getUserPatientService().deleteUserPatient(user, patient);
		Assert.assertNull(getUserPatientService().getUserPatient(user, patient));
	}
	
	/**
	 * @see UserPatientService#deleteUserPatients(Patient)
	 * @verifies delete user patients for patient successfully
	 */
	@Test
	public void deleteUserPatients_shouldDeleteUserPatientsForPatientSuccessfully() throws Exception {
		Patient patient = Context.getPatientService().getPatient(6);
		getUserPatientService().deleteUserPatients(patient);
		Assert.assertNull(getUserPatientService().getUserPatient(Context.getUserService().getUser(5505), patient));
	}
	
	/**
	 * @see UserPatientService#deleteUserPatients(User)
	 * @verifies delete user patients for user successfully
	 */
	@Test
	public void deleteUserPatients_shouldDeleteUserPatientsForUserSuccessfully() throws Exception {
		Patient patient = Context.getPatientService().getPatient(6);
		getUserPatientService().deleteUserPatients(Context.getUserService().getUser(5505));
		Assert.assertNull(getUserPatientService().getUserPatient(Context.getUserService().getUser(5505), patient));
	}
	
	/**
	 * @see UserPatientService#getIncludedPatients()
	 * @verifies return list of patients the authenticated user have access to
	 */
	@Test
	public void getIncludedPatients_shouldReturnListOfPatientsTheAuthenticatedUserHaveAccessTo() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getUserPatientService().getIncludedPatients().size(), 1);
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getUserPatientService().getIncludedPatients().size(), 1);
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getUserPatientService().getIncludedPatients().size(), 2);
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
	}
	
	/**
	 * @see UserPatientService#getUserPatient(User,Patient)
	 * @verifies return the user patient with the patient and user given
	 */
	@Test
	public void getUserPatient_shouldReturnTheUserPatientWithThePatientAndUserGiven() throws Exception {
		Assert.assertNotNull(getUserPatientService().getUserPatient(Context.getUserService().getUser(5505),
		    Context.getPatientService().getPatient(6)));
		Assert.assertNotNull(getUserPatientService().getUserPatient(Context.getUserService().getUser(5506),
		    Context.getPatientService().getPatient(2)));
	}
	
	/**
	 * @see UserPatientService#hasPrivilege(Patient)
	 * @verifies authorize if authenticated user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAuthenticatedUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertTrue(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
	}
	
	/**
	 * @see UserPatientService#hasPrivilege(Patient)
	 * @verifies not authorize if authenticated user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertFalse(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertFalse(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertFalse(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Assert.assertFalse(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertFalse(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertFalse(getUserPatientService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
	}
	
	/**
	 * @see UserPatientService#saveUserPatient(UserPatient)
	 * @verifies save given UserPatient successfully
	 */
	@Test
	public void saveUserPatient_shouldSaveGivenUserPatientSuccessfully() throws Exception {
		Patient patient = Context.getPatientService().getPatient(8);
		User user = Context.getUserService().getUser(5507);
		
		UserPatient rp = new UserPatient();
		rp.setPatient(patient);
		rp.setUser(user);
		
		getUserPatientService().saveUserPatient(rp);
		
		Assert.assertNotNull(rp.getUuid());
		Assert.assertNotNull(getUserPatientService().getUserPatient(user, patient));
	}
}
