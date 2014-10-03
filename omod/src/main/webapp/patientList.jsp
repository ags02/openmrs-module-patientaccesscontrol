<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/patientaccesscontrol/patient.list" />

<openmrs:message var="pageTitle" code="findPatient.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<h2><openmrs:message code="Patient.search"/></h2>	

<br />

<openmrs:portlet id="patientList" url="patientList" parameters="postURL=${pageContext.request.contextPath}/patientDashboard.form|showIncludeVoided=false|viewType=shortEdit" moduleId="patientaccesscontrol" />

<openmrs:extensionPoint pointId="org.openmrs.findPatient" type="html" />

<%@ include file="/WEB-INF/template/footer.jsp" %>
