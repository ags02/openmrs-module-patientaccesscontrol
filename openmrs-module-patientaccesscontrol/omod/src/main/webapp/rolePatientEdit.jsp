<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Role Patient" otherwise="/login.htm"
	redirect="/module/patientaccesscontrol/rolePatientEdit.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<br />

<script type="text/javascript">
	function toggleAllowAll(input) {
		var checked = input.checked;

		$j(":checkbox").each(function() {
			var id = $j(this).attr('id');
			if (id != 'superuser' && id.indexOf('allowAll') != 0) {
				$j(this).attr('disabled', checked);				
			}
		});
	}
	$j(document).ready(function() {
		toggleAllowAll($j("input[id^=allowAll]")[0]);
	});
</script>

<h3>
	<spring:message code="patientaccesscontrol.rolePatient.title" />
</h3>

<form:form method="post" modelAttribute="rolePatientForm">
	<table style="border-spacing: 10px;">
		<tr>
			<th align="right"><spring:message
					code="patientaccesscontrol.patient" /></th>
			<td>${patient.givenName} ${patient.familyName}</td>
		</tr>
		<tr>
			<th align="right" style="vertical-align: top;"><spring:message
					code="patientaccesscontrol.rolePatient" /></th>
			<td>

				<table cellpadding="2" cellspacing="0" width="100%">
					<tr>
						<th><spring:message code="patientaccesscontrol.role" /></th>
						<th align="center"><spring:message
								code="patientaccesscontrol.view" /></th>
					</tr>
					<tr class="oddRow">
						<td>${superuser}</td>
						<td align="center"><input id="superuser" type="checkbox"
							checked="checked" disabled="disabled" /></td>
					</tr>
					<c:forEach items="${rolePatientForm.roleViewModels}"
						var="roleViewModel" varStatus="rowStatus">
						<tr
							class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td>${roleViewModel.role.role}</td>
							<td align="center"><form:checkbox
									path="roleViewModels[${rowStatus.index}].canView" /></td>
						</tr>
					</c:forEach>
					<c:if test="${empty rolePatientForm.roleViewModels}">
						<tr>
							<td colspan="4" style="padding: 10px; text-align: center"><spring:message
									code="patientaccesscontrol.noresults" /></td>
						</tr>
					</c:if>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td><spring:message code="patientaccesscontrol.allowAll" /></td>
						<td align="center"><form:checkbox path="allowAll"
								onclick="toggleAllowAll(this)" /></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<input type="submit"
		value="<openmrs:message code="patientaccesscontrol.save"/>">
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
