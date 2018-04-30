package org.h2dev.bookstore.manager;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.h2dev.bookstore.AbstractBookstoreTestCase;
import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.model.Book;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookManagerTest extends AbstractBookstoreTestCase {

	@Test
	public void fetchAllBookTest() throws SQLException {
		List<Book> books = bookManager.fetchAllBook();

		assertEquals("Unexpected number of fetched books.", 7, books.size());

		assertEquals("Unexpected book title", "Mastering едц", books.get(0).getTitle());
		assertEquals("Unexpected book author", "Average Swede", books.get(0).getAuthor());
		assertEquals("Unexpected book price", 762d, books.get(0).getPrice().doubleValue(), 0.001);

		assertEquals("Unexpected book title", "How To Spend Money", books.get(1).getTitle());
		assertEquals("Unexpected book author", "Rich Bloke", books.get(1).getAuthor());
		assertEquals("Unexpected book price", 1000000d, books.get(1).getPrice().doubleValue(), 0.001);

		assertEquals("Unexpected book title", "Generic Title", books.get(2).getTitle());
		assertEquals("Unexpected book author", "First Author", books.get(2).getAuthor());
		assertEquals("Unexpected book price", 185.5, books.get(2).getPrice().doubleValue(), 0.001);

		assertEquals("Unexpected book title", "Generic Title", books.get(3).getTitle());
		assertEquals("Unexpected book author", "Second Author", books.get(3).getAuthor());
		assertEquals("Unexpected book price", 1748d, books.get(3).getPrice().doubleValue(), 0.001);

		assertEquals("Unexpected book title", "Random Sales", books.get(4).getTitle());
		assertEquals("Unexpected book author", "Cunning Bastard", books.get(4).getAuthor());
		assertEquals("Unexpected book price", 999d, books.get(4).getPrice().doubleValue(), 0.001);

		assertEquals("Unexpected book title", "Random Sales", books.get(5).getTitle());
		assertEquals("Unexpected book author", "Cunning Bastard", books.get(5).getAuthor());
		assertEquals("Unexpected book price", 499.5d, books.get(5).getPrice().doubleValue(), 0.001);

		assertEquals("Unexpected book title", "Desired", books.get(6).getTitle());
		assertEquals("Unexpected book author", "Rich Bloke", books.get(6).getAuthor());
		assertEquals("Unexpected book price", 564.5d, books.get(6).getPrice().doubleValue(), 0.001);
	}

	@Test
	public void aFetchBookByIdTest() throws SQLException, H2bookstoreException, IOException, ParseException {
		Book book = bookManager.fetchBookById(5);
		assertEquals("Unexpected book title", "Random Sales", book.getTitle());
		assertEquals("Unexpected book author", "Cunning Bastard", book.getAuthor());
		assertEquals("Unexpected book price", 999d, book.getPrice().doubleValue(), 0.001);
	}

	@Test
	public void tableEntryCount() throws SQLException {
		long count = bookManager.tableEntryCount();
		assertEquals("Unexpected overall count number of books", 7, count);
	}

}
