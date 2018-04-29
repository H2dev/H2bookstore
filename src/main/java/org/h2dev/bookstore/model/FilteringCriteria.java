package org.h2dev.bookstore.model;

public class FilteringCriteria {

	private String orderBy;
	private String sortAsc;
	private String title;
	private String author;

	public FilteringCriteria() {
	}

	public FilteringCriteria(String orderBy, String sortAsc, String title, String author) {
		this.orderBy = orderBy;
		this.sortAsc = sortAsc;
		this.title = title;
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getSortAsc() {
		return sortAsc;
	}

	public void setSortAsc(String sortAsc) {
		this.sortAsc = sortAsc;
	}

}
