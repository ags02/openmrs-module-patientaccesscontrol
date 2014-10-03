<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/patientaccesscontrol/rolePatient.list" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>

<openmrs:portlet id="findPatient" url="findPatient" parameters="size=full|postURL=userPatientEdit.form|showIncludeVoided=false|viewType=shortEdit|hideAddNewPatient=true" />

<%@ include file="/WEB-INF/template/footer.jsp" %>