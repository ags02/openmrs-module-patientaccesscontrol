<%@ include file="/WEB-INF/template/include.jsp" %>

<c:if test="${model.authenticatedUser != null}">

	<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/programaccesscontrol/patient.list" />
	<style>
		#openmrsSearchTable_wrapper{
		/* Removes the empty space between the widget and the Create New Patient section if the table is short */
		/* Over ride the value set by datatables */
			min-height: 0px; height: auto !important;
		}
	</style>
	<openmrs:htmlInclude file="/dwr/interface/DWRPatientListService.js"/>
	<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css"/>
	<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js"/>
	<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

	<openmrs:globalProperty key="patient.listingAttributeTypes" var="attributesToList"/>
	
	<script type="text/javascript">
		var lastSearch;
		$j(document).ready(function() {
			new OpenmrsSearch("findPatients", false, doPatientSearch, doSelectionHandler,
				[	{fieldName:"programName", header:"Program"},
					{fieldName:"identifier", header:omsgs.identifier},
					{fieldName:"givenName", header:omsgs.givenName},
					{fieldName:"middleName", header:omsgs.middleName},
					{fieldName:"familyName", header:omsgs.familyName},
					{fieldName:"age", header:omsgs.age},
					{fieldName:"gender", header:omsgs.gender},
					{fieldName:"birthdateString", header:omsgs.birthdate}
				],
				{
						  doSearchWhenEmpty: true,
						  displayLength: 25,
                          searchLabel: '<openmrs:message code="Patient.searchBox" javaScriptEscape="true"/>',
                          searchPlaceholder:'<openmrs:message code="Patient.searchBox.placeholder" javaScriptEscape="true"/>',
                          attributes: [
                                   	<c:forEach var="attribute" items="${fn:split(attributesToList, ',')}" varStatus="varStatus">
                                         <c:if test="${fn:trim(attribute) != ''}">
                                         <c:set var="attributeName" value="${fn:trim(attribute)}" />
                              			     <c:choose>
                              					<c:when test="${varStatus.index == 0}">
                              						{name:"${attributeName}", header:"<openmrs:message code="PersonAttributeType.${fn:replace(attributeName, ' ', '')}" text="${attributeName}"/>"}
                              					</c:when>
                              					<c:otherwise>
                              						,{name:"${attributeName}", header:"<openmrs:message code="PersonAttributeType.${fn:replace(attributeName, ' ', '')}" text="${attributeName}"/>"}
                              					</c:otherwise>
                              				 </c:choose>
                                         </c:if>
                                 		</c:forEach>
                                   ]
                          <c:if test="${not empty param.phrase}">
                              , searchPhrase: '<openmrs:message text="${ param.phrase }" javaScriptEscape="true"/>'
                          </c:if>                      
                      });

			//set the focus to the first input box on the page(in this case the text box for the search widget)
			var inputs = document.getElementsByTagName("input");
		    if(inputs[0])
		    	inputs[0].focus();


		});

		function doSelectionHandler(index, data) {
			document.location = "${model.postURL}?patientId=" + data.patientId + "&phrase=" + lastSearch;
		}

		//searchHandler for the Search widget
		function doPatientSearch(text, resultHandler, getMatchCount, opts) {
			lastSearch = text;
			DWRPatientListService.listCountAndPatients(text, opts.start, opts.length, getMatchCount, resultHandler);
		}

	</script>

	<div>
		<b class="boxHeader"><openmrs:message code="Patient.find"/></b>
		<div class="box">
			<div class="searchWidgetContainer" id="findPatients"></div>
		</div>
	</div>

</c:if>
