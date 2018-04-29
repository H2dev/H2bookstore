package org.h2dev.bookstore.service.interrface;

import java.sql.SQLException;

import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.model.Book;

public interface BookList {

	public Book[] list(String searchString) throws SQLException;

	public boolean addToCart(Book book, int quantity) throws SQLException;

	public int[] buy(Book... books) throws SQLException, H2bookstoreException;
}
