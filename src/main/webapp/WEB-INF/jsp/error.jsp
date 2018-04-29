<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<%@include file="header.jsp"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="mainTitle" /></title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/style.css" />
</head>

<c:set var="goBack">
	<spring:message code="goBack" />
</c:set>
<c:set var="errorOccurred">
	<spring:message code="errorOccurred" />
</c:set>


<body>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>

	<c:set var="totalPrice" value="${0}" />

	<div style="text-align: center; font-size: 20px;">
		<br> <br> <br>
		<c:out value="${ errorOccurred }"></c:out>
		<br><br>
		<c:out value="${ errorMessage }"></c:out>
	</div>

	<div style="text-align: center;">
		<br> <br>
		<form style="display: inline" method="GET"
			action="${pageContext.servletContext.contextPath}/">
			<input type="submit" class="button"
				style="width: 100px; height: 28px;" value="${ goBack }" />
		</form>
	</div>

	<%@include file="footer.jsp"%>

</html>