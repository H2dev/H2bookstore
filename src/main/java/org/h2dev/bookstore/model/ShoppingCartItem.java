package org.h2dev.bookstore.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class ShoppingCartItem implements Serializable {

	private static final long serialVersionUID = 5112063355390308503L;

	public static final String ID_FIELD = "id";
	public static final String BOOK_FIELD = "book";
	public static final String QUANTITY = "quantity";

	@DatabaseField(columnName = ID_FIELD, generatedId = true)
	private int id;

	@DatabaseField(columnName = BOOK_FIELD, canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Book book;

	@DatabaseField(columnName = QUANTITY, canBeNull = false)
	private int quantity;

	public ShoppingCartItem() {
	}

	public ShoppingCartItem(Book book, int quantity) {
		this.book = book;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
