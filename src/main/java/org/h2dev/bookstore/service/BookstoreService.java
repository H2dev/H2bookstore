package org.h2dev.bookstore.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.manager.BookManager;
import org.h2dev.bookstore.manager.BookStatusManager;
import org.h2dev.bookstore.manager.GeneralBookstoreManager;
import org.h2dev.bookstore.manager.ShoppingCartItemManager;
import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.model.BuyStatus;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.h2dev.bookstore.model.ShoppingCartItem;
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
	ShoppingCartItemManager shoppingCartItemManager;

	@PostConstruct
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
		List<ShoppingCartItem> itemsInCart = shoppingCartItemManager.fetchAllShoppingCartItems();
		for (ShoppingCartItem item : itemsInCart) {
			Book book = item.getBook();
			int quantity = item.getQuantity();
			BookStatus bookStatus = fetchBookStatusByBookId(book.getId());
			if (bookStatus.getPiecesInStock() < quantity) {
				return false;
			}
		}
		return true;
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

	public int getTotalNumberOfBookPiecesInCart() throws SQLException {
		return shoppingCartItemManager.getTotalNumberOfBookPiecesInCart();
	}

	public boolean removeFromShoppingCart(int bookId) throws H2bookstoreException {
		return shoppingCartItemManager.removeBookFromCart(bookId);
	}

	public BigDecimal getOverallPriceFromShoppingCart() throws SQLException {
		return shoppingCartItemManager.getOverallPrice();
	}

	public List<ShoppingCartItem> fetchAllShoppingCartItems() throws SQLException {
		return shoppingCartItemManager.fetchAllShoppingCartItems();
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
			shoppingCartItemManager.addBookToCart(book.getId(), quantity);
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
		List<ShoppingCartItem> listItemsInCart = fetchAllShoppingCartItems();
		for (ShoppingCartItem item : listItemsInCart) {
			Book book = item.getBook();
			int quantity = item.getQuantity();
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
