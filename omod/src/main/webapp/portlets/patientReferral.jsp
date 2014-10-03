<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:htmlInclude file="/moduleResources/patientaccesscontrol/patientaccesscontrol.css" />
<openmrs:htmlInclude file="/dwr/interface/DWRModulePatientService.js" />

<table id="patientReferral" style="width: 100%">
	<tr class="patientReferralRow">
		<td id="patientReferral">
			<div id="patientReferralLink">
				<button id="referPatientButton" onClick="return showReferralForm();">
					<openmrs:message code="patientaccesscontrol.patientReferral.refer" />
				</button>
			</div>
			<div id="patientReferralForm"
				style="display: none; padding: 3px; border: 1px black dashed">
				<form method="post" id="referralForm">
					<table id="patientReferralTable">
						<tr class="patientReferralRow">
							<td id="patientReferralUser"><span id="patientReferralUserField"><openmrs:message
										code="patientaccesscontrol.patientReferral.user" /></span> <openmrs:fieldGen
									type="org.openmrs.User" formFieldName="referToUser" val="" />
							</td>
							<td id="patientReferralSave"><input type="button"
								onClick="javascript:referralFormValidate();"
								value="<openmrs:message code="patientaccesscontrol.patientReferral.referButton" />" />
								<input type="button" onClick="javascript:hideReferralForm();"
								value="<openmrs:message code="general.cancel" />" /></td>
						</tr>
					</table>
				</form>
			</div> <script>
			<!--
				function showReferralForm() {
					showDiv("patientReferralForm");
					hideDiv("patientReferralLink");
				}

				function hideReferralForm() {
					showDiv("patientReferralLink");
					hideDiv("patientReferralForm");
				}

				function referralFormValidate() {
					var referToUser = dwr.util.getValue("referToUser");
					
					if ( referToUser == '' ) {
						alert("<openmrs:message code="patientaccesscontrol.patientReferral.error.noUser" />");
						return;
					}
					
					var answer = confirm("<openmrs:message code="patientaccesscontrol.patientReferral.readyToSubmit" />");
					if ( answer ) {
						DWRModulePatientService.referPatientToUser( ${model.patient.patientId}, referToUser, completeReferral );
					}
				}
				
				function completeReferral(message) {
					alert(message);					
				}

				-->
			</script>
		</td>
	</tr>
</table>