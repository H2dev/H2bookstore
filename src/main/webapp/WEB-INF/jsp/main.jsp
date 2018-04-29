<%@page import="org.h2dev.bookstore.model.Book"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<%@include file="header.jsp"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="mainTitle" /></title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/style.css" />
<spring:url value="/resources/pictures" var="pics" />
</head>

<c:set var="fillWithInitialData">
	<spring:message code="fillWithInitialData" />
</c:set>
<c:set var="filterBooks">
	<spring:message code="filterBooks" />
</c:set>
<c:set var="clearFilters">
	<spring:message code="clearFilters" />
</c:set>
<c:set var="add">
	<spring:message code="add" />
</c:set>
<c:set var="add">
	<spring:message code="add" />
</c:set>
<c:set var="currency">
	<spring:message code="currency" />
</c:set>

<c:set var="book_statuses_size">
	${fn:length(book_statuses)}
</c:set>

<c:set var="author_field">
	<%=Book.AUTHOR_FIELD%>
</c:set>
<c:set var="title_field">
	<%=Book.TITLE_FIELD%>
</c:set>
<c:set var="price_field">
	<%=Book.PRICE_FIELD%>
</c:set>

<body>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<div class="cart">
		<a class="shoppingCartLink"
			href="${pageContext.servletContext.contextPath}/shoppingCart"> <img
			src="${pics}/cart.png" /> <spring:message code="shoppingCart" />${ shoppingCartItems }
		</a>
	</div>
	<div style="text-align: center;">
		<c:choose>
			<c:when test="${bookStatusesOverallDbEntries < 1}">
				<br>
				<p>
					<spring:message code="noBooks" />
				</p>
				<p>
					<spring:message code="initialBooks" />
				</p>
				<br>
				<form method="POST"
					action="${pageContext.servletContext.contextPath}/fillInitialData">
					<input type="submit" class="button"
						style="width: 180px; height: 35px;"
						value="${ fillWithInitialData }" />
				</form>
				<br>
				<br>
				<br>
				<br>
			</c:when>
			<c:otherwise>
				<table style="margin-left: auto; margin-right: auto;">
					<form:form method="get" name="filtersForm" id="filtersForm"
						modelAttribute="filteringCriteria"
						action="${pageContext.servletContext.contextPath}/">
						<input type="hidden" id="page" name="page" value="1">
						<tr style="vertical-align: top; height: 50px;">
							<td></td>
							<td class="filterLabel"><spring:message code="filter.title" /></td>
							<td class="filter"><form:input class="filterInputText"
									type="text" id="titleFilterInput" path="title"
									value="${ param.title }" /></td>
							<td class="filterLabel"><spring:message code="filter.author" /></td>
							<td class="filter"><form:input class="filterInputText"
									type="text" id="authorFilterInput" path="author"
									value="${ param.author }" /></td>
							<td></td>
						</tr>

						<form:input type="hidden" id="orderByInput" name="orderByInput"
							path="orderBy" value="${ param.orderBy }" />
						<form:input type="hidden" id="sortAscInput" name="sortAscInput"
							path="sortAsc" value="${ param.sortAsc }" />
					</form:form>
					<tr>
						<td style="height: 0px;"></td>
					</tr>
				</table>

				<div style="text-align: center;">
					<button id="filterButton" class="buttonFilter"
						onclick="submitFilter()" style="width: 200px; height: 35px;">${ filterBooks }</button>
					<button class="buttonFilter" onclick="clearFilterInputs()"
						style="width: 200px; height: 35px;">${ clearFilters }</button>
				</div>
				<br>

				<c:choose>
					<c:when test="${book_statuses_size == 0}">
						<br>
						<p>
							<spring:message code="noMatchingResults" />
						</p>
						<br>
						<br>
						<br>
					</c:when>
					<c:otherwise>
						<table style="margin-left: auto; margin-right: auto;">
							<tr style="vertical-align: middle; height: 45px;">
								<td colspan="16" class="italicMessage"><spring:message
										code="listBooks" /></td>
							</tr>
							<tr>
								<td></td>
							</tr>
							<tr
								style="vertical-align: bottom; height: 35px; font-weight: bold;">
								<td class="titles"><spring:message code="titleBook" /></td>
								<td class="titles"><spring:message code="authorBook" /></td>
								<td class="titles"><spring:message code="priceBook" /></td>
								<td class="titles"><spring:message code="stockBook" /></td>
							</tr>
							<tr style="vertical-align: top; height: 25px;">
								<td class="arrows"><c:choose>
										<c:when
											test="${ param.orderBy != title_field || ( param.sortAsc != 'true' && param.sortAsc != 'false' ) }">
											<input type="image" src="${pics}/arrow-black-up.png"
												onclick="setOrderByParam('${title_field}', 'true')" />
											<input type="image" src="${pics}/arrow-black-down.png"
												onclick="setOrderByParam('${title_field}', 'false')" />
										</c:when>
										<c:otherwise>
											<c:if test="${ param.sortAsc == 'false' }">
												<input type="image" src="${pics}/arrow-black-up.png"
													onclick="setOrderByParam('${title_field}', 'true')" />
												<input type="image" src="${pics}/arrow-orange-down.png" />
											</c:if>
											<c:if test="${ param.sortAsc == 'true' }">
												<input type="image" src="${pics}/arrow-orange-up.png" />
												<input type="image" src="${pics}/arrow-black-down.png"
													onclick="setOrderByParam('${title_field}', 'false')" />
											</c:if>
										</c:otherwise>
									</c:choose></td>
								<td class="arrows"><c:choose>
										<c:when
											test="${ param.orderBy != author_field || ( param.sortAsc != 'true' && param.sortAsc != 'false' ) }">
											<input type="image" src="${pics}/arrow-black-up.png"
												onclick="setOrderByParam('${author_field}', 'true')" />
											<input type="image" src="${pics}/arrow-black-down.png"
												onclick="setOrderByParam('${author_field}', 'false')" />
										</c:when>
										<c:otherwise>
											<c:if test="${ param.sortAsc == 'false' }">
												<input type="image" src="${pics}/arrow-black-up.png"
													onclick="setOrderByParam('${author_field}', 'true')" />
												<input type="image" src="${pics}/arrow-orange-down.png" />
											</c:if>
											<c:if test="${ param.sortAsc == 'true' }">
												<input type="image" src="${pics}/arrow-orange-up.png" />
												<input type="image" src="${pics}/arrow-black-down.png"
													onclick="setOrderByParam('${author_field}', 'false')" />
											</c:if>
										</c:otherwise>
									</c:choose></td>
							</tr>
							<c:forEach var="book_status" items="${book_statuses}">
								<tr>
									<td class="Padding8px"><c:out
											value="${book_status.book.title}" /></td>
									<td class="Padding8px"><c:out
											value="${book_status.book.author}" /></td>
									<td class="Padding8px"><fmt:formatNumber type="currency"
											pattern="#,###.##" minFractionDigits="2"
											maxFractionDigits="2" value="${book_status.book.price}" /> <c:out
											value="${currency}" /></td>
									<td class="Padding8px"><c:out
											value="${book_status.piecesInStock}" /></td>
									<td>&nbsp;&nbsp;</td>
									<td class="noPadding">
										<form method="POST"
											action="${pageContext.servletContext.contextPath}/addToCart/${book_status.book.id}/page/${currentPage}">
											<input type="submit" class="button"
												style="height: 20px; font-size: 14px;" value="${ add }" />
										</form>
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
		<br>
		<c:if test="${not empty book_statuses}">
			<br>
			<div style="text-align: center; font-weight: bold">
				<c:if test="${not empty book_statuses}">
					<span><spring:message code="page" /> ${ currentPage } / ${ maxNumberOfPages }
					</span>
					<br>
				</c:if>
				<br>
				<c:if test="${ currentPage > 1 }">
					<button class="link" onclick="pageFuction(${currentPage - 1})">
						<spring:message code="previousPage" />
					</button>
				</c:if>
				<c:if test="${ currentPage < maxNumberOfPages }">
					<button class="link" onclick="pageFuction(${currentPage + 1})">
						<spring:message code="nextPage" />
					</button>
				</c:if>
				<br>
			</div>
			<br>
			<br>
		</c:if>
		<br> <br> <br> <br> <br> <br>
	</div>

</body>

<%@include file="footer.jsp"%>

<script type="text/javascript">
	var addedSuccessfully = "${addedSuccessfully}";
	if (addedSuccessfully === "false") {
		alert("Book was not added to the cart. Possibly not enough quantity on stock.")
	}

	var titleFilterInput = document.getElementById("titleFilterInput");
	titleFilterInput.addEventListener("keyup", function(event) {
	    event.preventDefault();
	    if (event.keyCode === 13) {
	        document.getElementById("filterButton").click();
	    }
	});

	var authorFilterInput = document.getElementById("authorFilterInput");
	authorFilterInput.addEventListener("keyup", function(event) {
	    event.preventDefault();
	    if (event.keyCode === 13) {
	        document.getElementById("filterButton").click();
	    }
	});
	
	function confirmDialogDelete() {
		var retVal = confirm("Are you sure you want to delete this book?");
		if (retVal == true) {
			return true;
		} else {
			return false;
		}
	}

	function confirmDialogDeleteAll() {
		var retVal = confirm("Are you sure you want to delete all books?");
		if (retVal == true) {
			return true;
		} else {
			return false;
		}
	}
	
	function pageFuction(page) {
		document.forms["filtersForm"].elements["page"].value =	page;
		document.forms["filtersForm"].submit(); 
	}
	
	function submitFilter() {
		document.forms["filtersForm"].submit(); 
	}
	
	function clearFilterInputs(link) {
		var form = document.forms["filtersForm"];
		var textInputs = document.querySelectorAll('input[type=text]');
		for (var i = 0; i < textInputs.length; i++) {
			textInputs[i].value = "";
		}
		form.elements["page"].value = 1;
		form.elements["orderByInput"].value = "";
		form.elements["sortAscInput"].value = "";
		form.submit(); 
		
		return true;
	}
	
	function setOrderByParam(orderBy, sortAsc) {
		document.forms["filtersForm"].elements["orderByInput"].value = orderBy;
		document.forms["filtersForm"].elements["sortAscInput"].value = sortAsc;
		document.forms["filtersForm"].submit(); 
	}
	
</script>

</html>