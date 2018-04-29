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
<c:set var="buy">
	<spring:message code="buy" />
</c:set>
<c:set var="remove">
	<spring:message code="remove" />
</c:set>
<c:set var="currency">
	<spring:message code="currency" />
</c:set>

<body>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>

	<c:set var="totalPrice" value="${0}" />

	<c:choose>
		<c:when test="${not empty booksAndQuantityMap}">
			<table class="cart">
				<tr class="cartHeadings">
					<td class="titles"><spring:message code="titleBook" /></td>
					<td class="titles"><spring:message code="authorBook" /></td>
					<td class="titles"><spring:message code="priceBook" /></td>
					<td class="titles"><spring:message code="piecesBook" /></td>
				</tr>
				<tr>
					<td style="height: 2px;"></td>
				</tr>
				<c:forEach var="entry" items="${booksAndQuantityMap}">
					<tr class="cartNormal">
						<td><c:out value="${entry.key.title}" /></td>
						<td><c:out value="${entry.key.author}" /></td>
						<td><fmt:formatNumber type="currency" pattern="#,###.##"
								minFractionDigits="2" maxFractionDigits="2"
								value="${entry.key.price}" /> <c:out value="${currency}" /></td>
						<td><c:out value="${entry.value}" /></td>
						<td class="noPadding">
							<form method="POST"
								action="${pageContext.servletContext.contextPath}/removeFromCart/${entry.key.id}">
								<input type="submit" class="button"
									style="height: 20px; font-size: 14px;" value="${ remove }" />
							</form>
						</td>
					</tr>
					<c:set var="totalPrice"
						value="${totalPrice + (entry.key.price * entry.value)}" />
				</c:forEach>
				<tr>
					<td style="height: 30px;"></td>
				</tr>
				<tr>
					<td colspan="6" class="totalPrice"><spring:message
							code="totalPrice" /> <fmt:formatNumber type="currency"
							pattern="#,###.##" minFractionDigits="2" maxFractionDigits="2"
							value="${totalPrice}" /> <c:out value="${currency}" /></td>
				</tr>
			</table>
		</c:when>
		<c:otherwise>
			<div style="text-align: center; font-size: 20px;">
				<br> <br> <br>
				<spring:message code="cartEmpty" />
			</div>
		</c:otherwise>
	</c:choose>

	<div style="text-align: center;">
		<br> <br>
		<c:if test="${not empty booksAndQuantityMap}">
			<form style="display: inline" method="POST"
				action="${pageContext.servletContext.contextPath}/buy"
				onsubmit="return confirmDialogBuy()">
				<input type="submit" class="button"
					style="width: 100px; height: 28px;" value="${ buy }" />
			</form>
		</c:if>
		<form style="display: inline" method="GET" id="goBackForm"
			action="${pageContext.servletContext.contextPath}/">
			<input type="submit" class="button"
				style="width: 100px; height: 28px;" value="${ goBack }" />
		</form>
	</div>

	<%@include file="footer.jsp"%>

	<script type="text/javascript">
		var buyStatusMessage = "${buyStatusMessage}";
		var buyStatusCode = "${buyStatusCode}";
		if (buyStatusCode === "0") {
			alert(buyStatusMessage);
			document.forms["goBackForm"].submit();
		}
		if (buyStatusCode === "1" || buyStatusCode === "2") {
			alert(buyStatusMessage);
		}

		function confirmDialogBuy() {
			var retVal = confirm("Are you sure you want to buy books that are currently in the cart?");
			if (retVal == true) {
				return true;
			} else {
				return false;
			}
		}
	</script>
</html>