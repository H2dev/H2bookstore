package org.h2dev.bookstore.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.model.ShoppingCart;
import org.h2dev.bookstore.util.Constants;
import org.h2dev.bookstore.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

@Repository("generalBookstoreManager")
public class GeneralBookstoreManager extends AbstractManager {

	@Autowired
	ShoppingCart shoppingCart;

	@SuppressWarnings("unchecked")
	private Dao<Book, Integer> bookDAO = (Dao<Book, Integer>) context.getBean("bookORMLite");

	@SuppressWarnings("unchecked")
	private Dao<BookStatus, Integer> bookStatusDAO = (Dao<BookStatus, Integer>) context.getBean("bookStatusORMLite");

	public void initDB() throws H2bookstoreException {
		try {
			TableUtils.createTableIfNotExists(cs, BookStatus.class);
			TableUtils.createTableIfNotExists(cs, Book.class);
			shoppingCart = new ShoppingCart();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Application failed at creating database table(s).", e);
			throw new H2bookstoreException("Application failed at creating database table(s). " + e.getMessage());
		}
	}

	public void fillInitialData() throws H2bookstoreException, IOException, SQLException, ParseException {
		logger.info("Filling DB with initial data.");
		initDB();
		bookDAO.updateRaw("DELETE FROM book");
		bookStatusDAO.updateRaw("DELETE FROM bookStatus");
		Set<List<String>> allBooksAttributes = Util.parseTxt(Constants.INITIAL_BOOKS_FILEPATH);
		for (List<String> bookAttributes : allBooksAttributes) {
			String title = bookAttributes.get(0);
			String author = bookAttributes.get(1);
			BigDecimal price = BigDecimal.valueOf(Util.getFormattedStringNumberAsDouble(bookAttributes.get(2)));
			int piecesInStock = Integer.parseInt(bookAttributes.get(3));
			Book book = new Book(title, author, price);
			BookStatus bookStatus = new BookStatus(book, piecesInStock);
			try {
				bookStatusDAO.create(bookStatus);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed at creating a new entry in database. " + e.getMessage());
				throw new H2bookstoreException("Failed at creating a new entry in database. " + e.getMessage());
			}
		}
	}

	public void addNewBookAndCorrespondingBookStatus(Book newBook, int quantityStock) throws H2bookstoreException {
		logger.info("Adding a new book and book status. Book title: "
				+ (newBook.getTitle() != null ? newBook.getTitle() : ""));
		BookStatus bookStatus = new BookStatus(newBook, quantityStock);
		try {
			bookStatusDAO.create(bookStatus);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed at creating a new entry in database. " + e.getMessage());
			throw new H2bookstoreException("Failed at creating a new entry in database. " + e.getMessage());
		}
	}

}
