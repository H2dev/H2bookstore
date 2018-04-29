package org.h2dev.bookstore.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.manager.BookManager;
import org.h2dev.bookstore.manager.BookStatusManager;
import org.h2dev.bookstore.manager.GeneralBookstoreManager;
import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.model.BuyStatus;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.h2dev.bookstore.model.ShoppingCart;
import org.h2dev.bookstore.service.interrface.BookList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("bookstoreService")
@Transactional
public class BookstoreService implements BookList {

	protected static final Logger logger = Logger.getLogger("H2bookstore");

	@Autowired
	GeneralBookstoreManager generalBookstoreManager;
	@Autowired
	BookStatusManager bookStatusManager;
	@Autowired
	BookManager bookManager;
	@Autowired
	ShoppingCart shoppingCart;

	public void initDbIfNotAlready() throws H2bookstoreException {
		generalBookstoreManager.initDB();
	}

	public void fillInitialData() throws H2bookstoreException, IOException, SQLException, ParseException {
		generalBookstoreManager.fillInitialData();
	}

	public List<BookStatus> fetchAllBookStatus() throws SQLException {
		return bookStatusManager.fetchAllBookStatus();
	}

	public List<BookStatus> fetchAllBookStatusFilteredPaged(Long page, FilteringCriteria filteringCriteria)
			throws SQLException {
		return bookStatusManager.fetchAllBookStatusFilteredPaged(page, filteringCriteria);
	}

	public List<BookStatus> fetchAllBookStatusFiltered(FilteringCriteria filteringCriteria) throws SQLException {
		return bookStatusManager.fetchAllBookStatusFiltered(filteringCriteria);
	}

	public void addNewBookAndCorrespondingBookStatus(Book newBook, int quantityStock) throws H2bookstoreException {
		generalBookstoreManager.addNewBookAndCorrespondingBookStatus(newBook, quantityStock);
	}

	public void setNewPiecesInStockForBookStatus(int bookStatusId, int newNbrOfPieces) throws SQLException {
		bookStatusManager.setNewPiecesInStockForBookStatus(bookStatusId, newNbrOfPieces);
	}

	public List<Book> fetchAllBook() throws SQLException {
		return bookManager.fetchAllBook();
	}

	public Book fetchBookById(int id) throws SQLException {
		return bookManager.fetchBookById(id);
	}

	public boolean doesBookstoreHaveAllPassedBooks(Book... books) throws SQLException {
		Set<Integer> bookIds = new HashSet<Integer>();
		for (Book book : books) {
			bookIds.add(book.getId());
		}
		List<BookStatus> allBookStatus = fetchAllBookStatus();
		Set<Integer> allBookStatusIds = allBookStatus.stream().map(bookStatus -> bookStatus.getId())
				.collect(Collectors.toSet());
		for (int bookId : bookIds) {
			if (!allBookStatusIds.contains(bookId)) {
				return false;
			}
		}
		return true;
	}

	public boolean doesStockHaveAllBooksFromShoppingCart() throws SQLException {
		Map<Book, Integer> booksAndQuantityMap = shoppingCart.getBooksAndQuantityMapFromCart();
		for (Map.Entry<Book, Integer> bookEntry : booksAndQuantityMap.entrySet()) {
			Book book = bookEntry.getKey();
			int quantity = bookEntry.getValue();
			BookStatus bookStatus = fetchBookStatusByBookId(book.getId());
			if (bookStatus.getPiecesInStock() < quantity) {
				return false;
			}
		}
		return true;
	}

	public int getTotalNumberOfBookItemsInCart() {
		return shoppingCart.getTotalNumberOfBookItems();
	}

	public long getBookStatusTableEntryCount() {
		return bookStatusManager.tableEntryCount();
	}

	public Integer countBooksWithFilter(FilteringCriteria filteringCriteria) throws SQLException {
		return bookStatusManager.countBookStatusesWithFilter(filteringCriteria);
	}

	public BookStatus fetchBookStatusByBookId(int id) throws SQLException {
		return bookStatusManager.fetchBookStatusByBookId(id);
	}

	public boolean removeFromShoppingCart(int bookId) throws H2bookstoreException {
		return shoppingCart.removeBookFromCart(bookId);
	}

	public BigDecimal getOverallPriceFromShoppingCart() throws SQLException {
		return shoppingCart.getOverallPrice();
	}

	public Map<Book, Integer> getBooksAndQuantityMapFromCart() throws SQLException {
		return shoppingCart.getBooksAndQuantityMapFromCart();
	}

	@Override
	public Book[] list(String searchString) throws SQLException {
		List<Book> books = bookManager.fetchAllBook();
		return bookManager.fetchAllBook().toArray(new Book[books.size()]);
	}

	@Override
	public boolean addToCart(Book book, int quantity) throws SQLException {
		if (quantity < 1) {
			return false;
		}
		try {
			shoppingCart.addBookToCart(book.getId(), quantity);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Override
	public int[] buy(Book... books) throws SQLException, H2bookstoreException {
		List<Integer> statuses = new ArrayList<Integer>();
		if (!doesBookstoreHaveAllPassedBooks(books)) {
			statuses.add(BuyStatus.DOES_NOT_EXIST.getCode());
		}
		if (!doesStockHaveAllBooksFromShoppingCart()) {
			statuses.add(BuyStatus.NOT_IN_STOCK.getCode());
		}
		if (statuses.isEmpty()) {
			statuses.add(BuyStatus.OK.getCode());
			resolveBooksInCartAsPurchased();
		}
		return statuses.stream().mapToInt(Integer::intValue).toArray();
	}

	private void resolveBooksInCartAsPurchased() throws SQLException, H2bookstoreException {
		Map<Book, Integer> booksAndQuantityMap = shoppingCart.getBooksAndQuantityMapFromCart();
		for (Map.Entry<Book, Integer> bookEntry : booksAndQuantityMap.entrySet()) {
			Book book = bookEntry.getKey();
			int quantity = bookEntry.getValue();
			BookStatus bookStatus = fetchBookStatusByBookId(book.getId());
			if (bookStatus.getPiecesInStock() < quantity) {
				throw new H2bookstoreException(
						"Number of available books in stock is smaller than requested in an invoice.");
			} else {
				boolean successful = removeFromShoppingCart(book.getId());
				if (successful) {
					setNewPiecesInStockForBookStatus(bookStatus.getId(), bookStatus.getPiecesInStock() - quantity);
				} else {
					throw new H2bookstoreException("Failed to remove the book from shopping cart.");
				}
			}
		}
	}

}
