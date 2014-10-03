<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Role Program" otherwise="/login.htm" redirect="/module/patientaccesscontrol/roleProgramEdit.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>

<br/>

<h3><spring:message code="patientaccesscontrol.roleProgram.title"/></h3>

<form:form method="post" modelAttribute="roleProgramForm">
<table style="border-spacing: 10px;">
	<tr>
		<th align="right"><spring:message code="patientaccesscontrol.program"/></th>
		<td>
			${program.name}
		</td>
	</tr>
	<tr>
		<th align="right" style="vertical-align: top;"><spring:message code="patientaccesscontrol.roleProgram"/></th>
		<td>
		
			<table cellpadding="2" cellspacing="0" width="100%">
				<tr>
					<th><spring:message code="patientaccesscontrol.role"/></th>
					<th align="center"><spring:message code="patientaccesscontrol.view"/></th>
				</tr>
				<tr class="oddRow">
					<td>${superuser}</td>
					<td align="center"><input type="checkbox" checked="checked" disabled="disabled" /></td>
				</tr>
				<c:forEach items="${roleProgramForm.roleViewModels}" var="roleViewModel" varStatus="rowStatus">
					<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
						<td>${roleViewModel.role.role}</td>
						<td align="center"><form:checkbox path="roleViewModels[${rowStatus.index}].canView" /></td>
					</tr>	
				</c:forEach>
				<c:if test="${empty roleProgramForm.roleViewModels}">
					<tr>
						<td colspan="4" style="padding: 10px; text-align: center"><spring:message code="patientaccesscontrol.noresults"/></td>
					</tr>
				</c:if>
			</table>
		</td>
	</tr>
</table>

<input type="submit" value="<openmrs:message code="patientaccesscontrol.save"/>">
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
