package org.openmrs.module.patientaccesscontrol.api;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.RolePatient;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RolePatientServiceTest extends BaseModuleContextSensitiveTest {
	
	protected static final String INITIAL_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/initialTestData.xml";
	
	protected static final String DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/RolePatientServiceTest.xml";
	
	public RolePatientServiceTest() {
		super();
		//runtimeProperties.put(Environment.SHOW_SQL, "true");
	}
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(RolePatientService.class));
	}
	
	private RolePatientService getRolePatientService() {
		return Context.getService(RolePatientService.class);
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
	 * @see RolePatientService#deleteRolePatient(Role,Patient)
	 * @verifies delete role patient for role and patient successfully
	 */
	@Test
	public void deleteRolePatient_shouldDeleteRolePatientForRoleAndPatientSuccessfully() throws Exception {
		Patient patient = Context.getPatientService().getPatient(6);
		Role role = Context.getUserService().getRole("Some Role");
		getRolePatientService().deleteRolePatient(role, patient);
		Assert.assertNull(getRolePatientService().getRolePatient(role, patient));
	}
	
	/**
	 * @see RolePatientService#deleteRolePatients(Patient)
	 * @verifies delete role patients for patient successfully
	 */
	@Test
	public void deleteRolePatients_shouldDeleteRolePatientsForPatientSuccessfully() throws Exception {
		Patient patient = Context.getPatientService().getPatient(6);
		getRolePatientService().deleteRolePatients(patient);
		Assert.assertTrue(getRolePatientService().getRoles(patient).isEmpty());
	}
	
	/**
	 * @see RolePatientService#deleteRolePatients(Role)
	 * @verifies delete role patients for role successfully
	 */
	@Test
	public void deleteRolePatients_shouldDeleteRolePatientsForRoleSuccessfully() throws Exception {
		Role role = Context.getUserService().getRole("Some Role");
		getRolePatientService().deleteRolePatients(role);
		Assert.assertNull(getRolePatientService().getRolePatient(role, Context.getPatientService().getPatient(6)));
		Assert.assertNull(getRolePatientService().getRolePatient(role, Context.getPatientService().getPatient(2)));
	}
	
	/**
	 * @see RolePatientService#getRolePatient(Role, Patient)
	 * @verifies return the role patient with the patient and role given
	 */
	@Test
	public void getRolePatient_shouldReturnTheRolePatientWithThePatientAndRoleGiven() throws Exception {
		Assert.assertNotNull(getRolePatientService().getRolePatient(Context.getUserService().getRole("Some Role"),
		    Context.getPatientService().getPatient(6)));
		Assert.assertNotNull(getRolePatientService().getRolePatient(Context.getUserService().getRole("Parent"),
		    Context.getPatientService().getPatient(2)));
	}
	
	/**
	 * @see RolePatientService#getRoles(Patient)
	 * @verifies return all roles with view for the given Patient
	 */
	@Test
	public void getRoles_shouldReturnAllRolesWithViewForTheGivenPatient() throws Exception {
		Assert.assertEquals(getRolePatientService().getRoles(Context.getPatientService().getPatient(6)).size(), 1);
		Assert.assertEquals(getRolePatientService().getRoles(Context.getPatientService().getPatient(2)).size(), 4);
		Assert.assertEquals(getRolePatientService().getRoles(Context.getPatientService().getPatient(7)).size(), 0);
		Assert.assertEquals(getRolePatientService().getRoles(Context.getPatientService().getPatient(8)).size(), 1);
	}
	
	/**
	 * @see RolePatientService#hasPrivilege(Patient)
	 * @verifies authorize if authenticated user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAuthenticatedUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
	}
	
	/**
	 * @see RolePatientService#hasPrivilege(Patient)
	 * @verifies authorize if anonymous user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAnonymousUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.logout();
	}
	
	/**
	 * @see RolePatientService#hasPrivilege(Patient)
	 * @verifies not authorize if authenticated user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertFalse(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertFalse(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertFalse(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
	}
	
	/**
	 * @see RolePatientService#hasPrivilege(Patient)
	 * @verifies not authorize if anonymous user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Assert.assertFalse(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertFalse(getRolePatientService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		Context.logout();
	}
	
	/**
	 * @see RolePatientService#saveRolePatient(RolePatient)
	 * @verifies save given rolePatient successfully
	 */
	@Test
	public void saveRolePatient_shouldSaveGivenRolePatientSuccessfully() throws Exception {
		Patient patient = Context.getPatientService().getPatient(8);
		Role role = Context.getUserService().getRole("Some Role");
		
		RolePatient rp = new RolePatient();
		rp.setPatient(patient);
		rp.setRole(role);
		
		getRolePatientService().saveRolePatient(rp);
		
		Assert.assertNotNull(rp.getUuid());
		Assert.assertNotNull(getRolePatientService().getRolePatient(role, patient));
	}
	
	/**
	 * @see RolePatientService#getExcludedPatients()
	 * @verifies return list of patient ids the authenticated user do not have access to
	 */
	@Test
	public void getExcludedPatients_shouldReturnListOfPatientIdsTheAuthenticatedUserDoNotHaveAccessTo() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getRolePatientService().getExcludedPatients().size(), 2);
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getRolePatientService().getExcludedPatients().size(), 1);
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getRolePatientService().getExcludedPatients().size(), 0);
		Context.removeProxyPrivilege("View Patients");
		Context.logout();
	}
	
	/**
	 * @see RolePatientService#getExcludedPatients()
	 * @verifies return list of patients the authenticated user do not have access to
	 */
	@Test
	public void getExcludedPatients_shouldReturnListOfPatientsTheAuthenticatedUserDoNotHaveAccessTo() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getRolePatientService().getExcludedPatients(), Arrays.asList(6, 8));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertEquals(getRolePatientService().getExcludedPatients(), Arrays.asList(6));
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertTrue(getRolePatientService().getExcludedPatients().isEmpty());
		Context.removeProxyPrivilege("View Patients");
		
		Context.logout();
	}
	
	/**
	 * @see RolePatientService#getIncludedPatients()
	 * @verifies return null if user has access to all patients
	 */
	@Test
	public void getIncludedPatients_shouldReturnNullIfUserHasAccessToAllPatients() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Assert.assertNull(getRolePatientService().getIncludedPatients());
		Context.removeProxyPrivilege("View Patients");
	}
}
