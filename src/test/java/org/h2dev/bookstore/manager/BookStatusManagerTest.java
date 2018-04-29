package org.h2dev.bookstore.manager;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

import org.h2dev.bookstore.AbstractBookstoreTestCase;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookStatusManagerTest extends AbstractBookstoreTestCase {

	@Test
	public void fetchAllBookStatusTest() throws SQLException {

		List<BookStatus> bookStatuses = bookStatusManager.fetchAllBookStatus();

		assertEquals("Unexpected number of fetched book statuses.", 7, bookStatuses.size());

		assertEquals("Unexpected book title", "Mastering едц", bookStatuses.get(0).getBook().getTitle());
		assertEquals("Unexpected book author", "Average Swede", bookStatuses.get(0).getBook().getAuthor());
		assertEquals("Unexpected book price", 762d, bookStatuses.get(0).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 15, bookStatuses.get(0).getPiecesInStock());

		assertEquals("Unexpected book title", "How To Spend Money", bookStatuses.get(1).getBook().getTitle());
		assertEquals("Unexpected book author", "Rich Bloke", bookStatuses.get(1).getBook().getAuthor());
		assertEquals("Unexpected book price", 1000000d, bookStatuses.get(1).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 1, bookStatuses.get(1).getPiecesInStock());

		assertEquals("Unexpected book title", "Generic Title", bookStatuses.get(2).getBook().getTitle());
		assertEquals("Unexpected book author", "First Author", bookStatuses.get(2).getBook().getAuthor());
		assertEquals("Unexpected book price", 185.5, bookStatuses.get(2).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 5, bookStatuses.get(2).getPiecesInStock());

		assertEquals("Unexpected book title", "Generic Title", bookStatuses.get(3).getBook().getTitle());
		assertEquals("Unexpected book author", "Second Author", bookStatuses.get(3).getBook().getAuthor());
		assertEquals("Unexpected book price", 1748d, bookStatuses.get(3).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 3, bookStatuses.get(3).getPiecesInStock());

		assertEquals("Unexpected book title", "Random Sales", bookStatuses.get(4).getBook().getTitle());
		assertEquals("Unexpected book author", "Cunning Bastard", bookStatuses.get(4).getBook().getAuthor());
		assertEquals("Unexpected book price", 999d, bookStatuses.get(4).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 20, bookStatuses.get(4).getPiecesInStock());

		assertEquals("Unexpected book title", "Random Sales", bookStatuses.get(5).getBook().getTitle());
		assertEquals("Unexpected book author", "Cunning Bastard", bookStatuses.get(5).getBook().getAuthor());
		assertEquals("Unexpected book price", 499.5d, bookStatuses.get(5).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 3, bookStatuses.get(5).getPiecesInStock());

		assertEquals("Unexpected book title", "Desired", bookStatuses.get(6).getBook().getTitle());
		assertEquals("Unexpected book author", "Rich Bloke", bookStatuses.get(6).getBook().getAuthor());
		assertEquals("Unexpected book price", 564.5d, bookStatuses.get(6).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 0, bookStatuses.get(6).getPiecesInStock());
	}

	@Test
	public void aFetchBookStatusByBookIdTest() throws SQLException {
		BookStatus bookStatus = bookStatusManager.fetchBookStatusByBookId(1);
		assertEquals("Unexpected book title", "Mastering едц", bookStatus.getBook().getTitle());
		assertEquals("Unexpected book author", "Average Swede", bookStatus.getBook().getAuthor());
		assertEquals("Unexpected book price", 762d, bookStatus.getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 15, bookStatus.getPiecesInStock());
	}

	@Test
	public void countBookStatusesWithFilterTest_1() throws SQLException {
		FilteringCriteria fc = new FilteringCriteria();
		fc.setAuthor("rich");
		fc.setTitle("how");
		int count = bookStatusManager.countBookStatusesWithFilter(fc);
		assertEquals("Unexpected count number of book statuses filtered", 1, count);
	}

	@Test
	public void countBookStatusesWithFilterTest_2() throws SQLException {
		FilteringCriteria fc = new FilteringCriteria();
		fc.setAuthor("rich");
		int count = bookStatusManager.countBookStatusesWithFilter(fc);
		assertEquals("Unexpected count number of book statuses filtered", 2, count);
	}

	@Test
	public void countBookStatusesWithFilterTest_3() throws SQLException {
		FilteringCriteria fc = new FilteringCriteria();
		int count = bookStatusManager.countBookStatusesWithFilter(fc);
		assertEquals("Unexpected count number of book statuses filtered", 7, count);
	}

	@Test
	public void tableEntryCount() throws SQLException {
		long count = bookStatusManager.tableEntryCount();
		assertEquals("Unexpected overall count number of book statuses", 7, count);
	}

	@Test
	public void fetchAllBookStatusFiltered() throws SQLException {
		FilteringCriteria fc = new FilteringCriteria();
		fc.setAuthor("rich");
		List<BookStatus> bookStatuses = bookStatusManager.fetchAllBookStatusFiltered(fc);

		assertEquals("Unexpected book title", "How To Spend Money", bookStatuses.get(0).getBook().getTitle());
		assertEquals("Unexpected book author", "Rich Bloke", bookStatuses.get(0).getBook().getAuthor());
		assertEquals("Unexpected book price", 1000000d, bookStatuses.get(0).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 1, bookStatuses.get(0).getPiecesInStock());

		assertEquals("Unexpected book title", "Desired", bookStatuses.get(1).getBook().getTitle());
		assertEquals("Unexpected book author", "Rich Bloke", bookStatuses.get(1).getBook().getAuthor());
		assertEquals("Unexpected book price", 564.5d, bookStatuses.get(1).getBook().getPrice().doubleValue(), 0.001);
		assertEquals("Unexpected book pieces in stock", 0, bookStatuses.get(1).getPiecesInStock());
	}

}
