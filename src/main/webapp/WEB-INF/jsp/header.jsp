<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/style.css" />
<spring:url value="/resources/pictures" var="pics" />
</head>

<body style="width: 100%; margin-left: 0px; margin-top: 0px;">

	<div class="header">
		<div class="headline">
			<a href="${pageContext.servletContext.contextPath}/"> <img
				src="${pics}/books_pic.png" />
			</a>
		</div>
		<div class="headline">
			<a href="${pageContext.servletContext.contextPath}/"
				style="text-decoration: none; color: inherit;"><span
				class="headline_text"><spring:message code="mainTitle" /></span></a>
		</div>
	</div>

</body>

