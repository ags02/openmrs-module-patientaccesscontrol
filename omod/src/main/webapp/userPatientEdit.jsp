<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage User Patient" otherwise="/login.htm"
	redirect="/module/patientaccesscontrol/userPatientEdit.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>


<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<script type="text/javascript">
	dojo.require("dojo.widget.openmrs.UserSearch");
	dojo.require("dojo.widget.openmrs.OpenmrsPopup");
	
	dojo.addOnLoad( function() {
		
		searchWidget = dojo.widget.manager.getWidgetById("uSearch");			
		
		dojo.event.topic.subscribe("uSearch/select", 
			function(msg) {
				for (var i=0; i< msg.objs.length; i++) {
					var obj = msg.objs[i];
					var options = $("userNames").options;
					
					var isAddable = true;
					for (x=0; x<options.length; x++)
						if (options[x].value == obj.userId)
							isAddable = false;
					
					if (isAddable) {
						var opt = new Option(obj.personName, obj.userId);
						opt.selected = true;
						options[options.length] = opt;
						copyIds("userNames", "userIds", " ");
					}
				}
			}
		);
	});
	
</script>

<script type="text/javascript">
		
	function removeItem(nameList, idList, delim)
	{
		var sel   = document.getElementById(nameList);
		var input = document.getElementById(idList);
		var optList   = sel.options;
		var lastIndex = -1;
		var i = 0;
		while (i<optList.length) {
			// loop over and erase all selected items
			if (optList[i].selected) {
				optList[i] = null;
				lastIndex = i;
			}
			else {
				i++;
			}
		}
		copyIds(nameList, idList, delim);
		while (lastIndex >= optList.length)
			lastIndex = lastIndex - 1;
		if (lastIndex >= 0) {
			optList[lastIndex].selected = true;
			return optList[lastIndex];
		}
		return null;
	}
	
	function copyIds(from, to, delimiter)
	{
		var sel = document.getElementById(from);
		var input = document.getElementById(to);
		var optList = sel.options;
		var remaining = new Array();
		var i=0;
		while (i < optList.length)
		{
			remaining.push(optList[i].value);
			i++;
		}
		input.value = remaining.join(delimiter);
	}
	
	function removeHiddenRows() {
		var rows = document.getElementsByTagName("TR");
		var i = 0;
		while (i < rows.length) {
			if (rows[i].style.display == "none") {
				rows[i].parentNode.removeChild(rows[i]);
			}
			else {
				i = i + 1;
			}
		}
	}
	
	function listKeyPress(from, to, delim, event) {
		var keyCode = event.keyCode;
		if (keyCode == 8 || keyCode == 46) {
			removeItem(from, to, delim);
			window.Event.keyCode = 0;	//attempt to prevent backspace key (#8) from going back in browser
		}
	}
</script>

<style>
	th { text-align: left; }
	.description { display: none; }
</style>

<h2><openmrs:message code="patientaccesscontrol.userPatient.title"/></h2>	

<spring:hasBindErrors name="userPatient">
	<openmrs:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<openmrs:message code="${error.code}" text="${error.code}"/><br/><!-- ${error} -->
		</c:forEach>
	</div>
</spring:hasBindErrors>

<form method="post">
<table>
	<tr>
		<th valign="top"><openmrs:message code="patientaccesscontrol.patient"/></th>
		<td>
			${userPatientForm.patient.givenName} ${userPatientForm.patient.familyName}
		</td>
	</tr>
	<tr>
		<th valign="top"><spring:message code="patientaccesscontrol.userPatient.users"/></th>
		<td valign="top">
			<input type="hidden" name="userIds" id="userIds" size="40" value='<c:forEach items="${userPatientForm.users}" var="user">${user.userId} </c:forEach>' />
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
						<select class="mediumWidth" size="6" id="userNames" multiple onkeyup="listKeyPress('userNames', 'userIds', ' ', event);">
							<c:forEach items="${userPatientForm.users}" var="user">
								<option value="${user.userId}">${user.personName}</option>
							</c:forEach>
						</select>
					</td>
					<td valign="top" class="buttons">
						&nbsp;<span dojoType="UserSearch" widgetId="uSearch"></span><span dojoType="OpenmrsPopup" searchWidget="uSearch" searchTitle='<openmrs:message code="User.find"/>' changeButtonValue='<openmrs:message code="general.add"/>'></span> <br/>
						&nbsp; <input type="button" value="<openmrs:message code="general.remove"/>" class="smallButton" onClick="removeItem('userNames', 'userIds', ' ');" /> <br/>
						&nbsp; <input type="button" value="<openmrs:message code="User.goTo"/>" class="smallButton" onClick="gotoUser('userNames');" /><br/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
</table>

<input type="submit" value="<openmrs:message code="patientaccesscontrol.save"/>">
</form>

<script type="text/javascript">
 document.forms[0].elements[0].focus();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>