package org.openmrs.module.patientaccesscontrol.api;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientaccesscontrol.Constants;
import org.openmrs.module.patientaccesscontrol.RoleProgram;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RoleProgramServiceTest extends BaseModuleContextSensitiveTest {
	
	protected static final String INITIAL_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/initialTestData.xml";
	
	protected static final String DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/RoleProgramServiceTest.xml";
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(RoleProgramService.class));
	}
	
	private RoleProgramService getRoleProgramService() {
		return Context.getService(RoleProgramService.class);
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
	 * @see RoleProgramService#deleteRolePrograms(Program)
	 * @verifies delete role programs for program successfully
	 */
	@Test
	public void deleteRolePrograms_shouldDeleteRoleProgramsForProgramSuccessfully() throws Exception {
		Program program = Context.getProgramWorkflowService().getProgram(1);
		getRoleProgramService().deleteRolePrograms(program);
		Assert.assertTrue(getRoleProgramService().getRoles(program).isEmpty());
	}
	
	/**
	 * @see RoleProgramService#deleteRolePrograms(Role)
	 * @verifies delete role programs for role successfully
	 */
	@Test
	public void deleteRolePrograms_shouldDeleteRoleProgramsForRoleSuccessfully() throws Exception {
		Role role = Context.getUserService().getRole("Some Role");
		getRoleProgramService().deleteRolePrograms(role);
		Assert.assertNull(getRoleProgramService().getRoleProgram(role, Context.getProgramWorkflowService().getProgram(1)));
		Assert.assertNull(getRoleProgramService().getRoleProgram(role, Context.getProgramWorkflowService().getProgram(2)));
	}
	
	/**
	 * @see RoleProgramService#deleteRoleProgram(Role,Program)
	 * @verifies delete role program for role and program successfully
	 */
	@Test
	public void deleteRoleProgram_shouldDeleteRoleProgramForRoleAndProgramSuccessfully() throws Exception {
		Program program = Context.getProgramWorkflowService().getProgram(2);
		Role role = Context.getUserService().getRole("Some Role");
		getRoleProgramService().deleteRoleProgram(role, program);
		Assert.assertNull(getRoleProgramService().getRoleProgram(role, program));
	}
	
	/**
	 * @see RoleProgramService#getRoleProgram(Role,Program)
	 * @verifies return the role program with the program and role given
	 */
	@Test
	public void getRoleProgram_shouldReturnTheRoleProgramWithTheProgramAndRoleGiven() throws Exception {
		Assert.assertNotNull(getRoleProgramService().getRoleProgram(Context.getUserService().getRole("Some Role"),
		    Context.getProgramWorkflowService().getProgram(1)));
	}
	
	/**
	 * @see RoleProgramService#getRoles(Program)
	 * @verifies return all roles with the given Program
	 */
	@Test
	public void getRoles_shouldReturnAllRoleProgramsWithTheGivenProgram() throws Exception {
		Assert.assertEquals(2, getRoleProgramService().getRoles(Context.getProgramWorkflowService().getProgram(1)).size());
		Assert.assertEquals(4, getRoleProgramService().getRoles(Context.getProgramWorkflowService().getProgram(2)).size());
	}
	
	/**
	 * @see RoleProgramService#getPrograms()
	 * @verifies return all programs authenticated user has view privilege to
	 */
	@Test
	public void getPrograms_shouldReturnAllProgramsAuthenticatedUserHasViewPrivilegeTo() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Assert.assertEquals(1, getRoleProgramService().getPrograms().size());
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Assert.assertEquals(2, getRoleProgramService().getPrograms().size());
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Assert.assertEquals(2, getRoleProgramService().getPrograms().size());
		
		Context.logout();
	}
	
	/**
	 * @see RoleProgramService#hasPrivilege(Patient)
	 * @verifies authorize if authenticated user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAuthenticatedUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleProgramService#hasPrivilege(Patient)
	 * @verifies authorize if anonymous user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAnonymousUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		Context.logout();
	}
	
	/**
	 * @see RoleProgramService#hasPrivilege(Patient)
	 * @verifies not authorize if authenticated user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertFalse(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertFalse(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleProgramService#hasPrivilege(Patient)
	 * @verifies not authorize if anonymous user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertFalse(getRoleProgramService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		Context.logout();
	}
	
	/**
	 * @see RoleProgramService#saveRoleProgram(RoleProgram)
	 * @verifies save given programPatientAccessControl successfully
	 */
	@Test
	public void saveRoleProgram_shouldSaveGivenRoleProgramSuccessfully() throws Exception {
		Program program = Context.getProgramWorkflowService().getProgram(3);
		Role role = Context.getUserService().getRole("Some Role");
		
		RoleProgram fac = new RoleProgram();
		fac.setProgram(program);
		fac.setRole(role);
		
		getRoleProgramService().saveRoleProgram(fac);
		
		Assert.assertNotNull(fac.getUuid());
		Assert.assertNotNull(getRoleProgramService().getRoleProgram(role, program));
	}
	
	/**
	 * @see RoleProgramService#getIncludedPatients()
	 * @verifies return list of patients the authenticated user have access to
	 */
	@Test
	public void getIncludedPatients_shouldReturnListOfPatientsTheAuthenticatedUserHaveAccessTo() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(getRoleProgramService().getIncludedPatients(), Arrays.asList(2, 7));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(getRoleProgramService().getIncludedPatients(), Arrays.asList(2, 6, 7));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleProgramService#getIncludedPatients()
	 * @verifies return null if user has access to all patients
	 */
	@Test
	public void getIncludedPatients_shouldReturnNullIfUserHasAccessToAllPatients() throws Exception {
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Assert.assertNull(getRoleProgramService().getIncludedPatients());
		Context.logout();
	}
	
	/**
	 * @see RoleProgramService#getExcludedPatients()
	 * @verifies return list of patients the authenticated user do not have access to
	 */
	@Test
	public void getExcludedPatients_shouldReturnListOfPatientsTheAuthenticatedUserDoNotHaveAccessTo() throws Exception {
		getRoleProgramService().deleteRoleProgram(Context.getUserService().getRole("Some Role"),
		    Context.getProgramWorkflowService().getProgram(1));
		getRoleProgramService().deleteRoleProgram(Context.getUserService().getRole("Parent"),
		    Context.getProgramWorkflowService().getProgram(1));
		
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(getRoleProgramService().getExcludedPatients().size(), 0);
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(getRoleProgramService().getExcludedPatients().size(), 2);
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
}
