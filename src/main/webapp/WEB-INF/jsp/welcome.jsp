<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>

<c:set var="clickToContinue" > 
	<spring:message code="clickToContinue" />
</c:set>

<body>
	<div>
		TEST
		
	<br>
	<br>
	<div style="text-align: center;">
		<form method="GET"
			action="${pageContext.servletContext.contextPath}/main">
			<input type="submit" class="button" style="width: 180px; height: 35px;"
				value="${ clickToContinue }" />
		</form>
	</div>
		
	</div>
</body>

</html>