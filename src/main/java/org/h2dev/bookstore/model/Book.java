package org.h2dev.bookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Book implements Serializable {

	private static final long serialVersionUID = -7860468422786490682L;

	public static final String TITLE_FIELD = "title";
	public static final String AUTHOR_FIELD = "author";
	public static final String PRICE_FIELD = "price";
	public static final String ID_FIELD = "id";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = TITLE_FIELD, canBeNull = false)
	private String title;

	@DatabaseField(columnName = AUTHOR_FIELD, canBeNull = true)
	private String author;

	@DatabaseField(columnName = PRICE_FIELD, canBeNull = false)
	private BigDecimal price;

	public Book() {
	}

	public Book(String title, String author, BigDecimal price) {
		this.title = title != null ? title : "";
		this.author = author != null ? author : "";
		this.price = price;
	}

	public int getId() {
		return id;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
