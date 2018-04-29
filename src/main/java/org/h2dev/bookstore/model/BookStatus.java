package org.h2dev.bookstore.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class BookStatus implements Serializable {

	private static final long serialVersionUID = 5150304226300308391L;

	public static final String PIECES_IN_STOCK_FIELD = "piecesInStock";
	public static final String BOOK_FIELD = "book";
	public static final String ID_FIELD = "id";

	@DatabaseField(columnName = ID_FIELD, generatedId = true)
	private int id;

	@DatabaseField(columnName = PIECES_IN_STOCK_FIELD, canBeNull = false)
	private int piecesInStock;

	@DatabaseField(columnName = BOOK_FIELD, canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Book book;

	public BookStatus() {
	}

	public BookStatus(Book book, int pieces) {
		this.book = book;
		this.piecesInStock = pieces;
	}

	public int getId() {
		return id;
	}

	public int getPiecesInStock() {
		return piecesInStock;
	}

	public void setPiecesInStock(int piecesInStock) {
		this.piecesInStock = piecesInStock;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

}
