package org.h2dev.bookstore;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.manager.BookManager;
import org.h2dev.bookstore.manager.BookStatusManager;
import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.util.Util;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public abstract class AbstractBookstoreTestCase {

	public static final String TEST_DATA_BOOKS_FILEPATH = "test_books.txt";

	ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "spring-beans.xml" });
	ConnectionSource cs = (ConnectionSource) context.getBean("connectionSource");

	protected static final Logger logger = Logger.getLogger("H2bookstore Test Logger");

	@SuppressWarnings("unchecked")
	private Dao<Book, Integer> bookDAO = (Dao<Book, Integer>) context.getBean("bookORMLite");

	@SuppressWarnings("unchecked")
	private Dao<BookStatus, Integer> bookStatusDAO = (Dao<BookStatus, Integer>) context.getBean("bookStatusORMLite");

	protected BookManager bookManager;
	protected BookStatusManager bookStatusManager;

	@Before
	public void setup() throws H2bookstoreException, IOException, SQLException, ParseException {
		initDB();
		bookDAO.updateRaw("DELETE FROM book");
		bookStatusDAO.updateRaw("DELETE FROM bookStatus");
		Set<List<String>> allBooksAttributes = Util.parseTxt("test_books.txt");
		for (List<String> bookAttributes : allBooksAttributes) {
			String title = bookAttributes.get(0);
			String author = bookAttributes.get(1);
			BigDecimal price = BigDecimal.valueOf(Util.getFormattedStringNumberAsDouble(bookAttributes.get(2)));
			int piecesInStock = Integer.parseInt(bookAttributes.get(3));
			Book book = new Book(title, author, price);
			BookStatus bookStatus = new BookStatus(book, piecesInStock);
			bookStatusDAO.create(bookStatus);
		}
		bookManager = new BookManager();
		bookStatusManager = new BookStatusManager();
		bookStatusManager.setBookManager(bookManager);
	}

	public void initDB() throws H2bookstoreException, SQLException {
		TableUtils.createTableIfNotExists(cs, BookStatus.class);
		TableUtils.createTableIfNotExists(cs, Book.class);
	}

}
