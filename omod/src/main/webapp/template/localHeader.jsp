<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/roleProgram") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/patientaccesscontrol/roleProgram.list"><spring:message
				code="patientaccesscontrol.roleProgram.title" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/rolePatient") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/patientaccesscontrol/rolePatientList.htm"><spring:message
				code="patientaccesscontrol.rolePatient.title" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/userPatient") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/patientaccesscontrol/userPatientList.htm"><spring:message
				code="patientaccesscontrol.userPatient.title" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
<h2>
	<spring:message code="patientaccesscontrol.title" />
</h2>
