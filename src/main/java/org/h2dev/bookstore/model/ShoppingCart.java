package org.h2dev.bookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.service.BookstoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCart implements Serializable {

	@Autowired
	BookstoreService bookstoreService;

	private static final long serialVersionUID = 5112063355390308503L;

	// map of book IDs and quantities
	private Map<Integer, Integer> mapOfBooks = new LinkedHashMap<Integer, Integer>();

	public boolean removeBookFromCart(int bookId) throws H2bookstoreException {
		try {
			Integer status = mapOfBooks.remove(bookId);
			return (status == null ? false : true);
		} catch (Exception e) {
			return false;
		}
	}

	public void addBookToCart(Integer bookId, Integer quantity) throws SQLException, H2bookstoreException {
		int currentQuantityInCart = 0;
		if (mapOfBooks.containsKey(bookId)) {
			currentQuantityInCart = mapOfBooks.get(bookId);
		}
		int currentNumberOfPiecesOfBookInStock = bookstoreService.fetchBookStatusByBookId(bookId).getPiecesInStock();
		if (currentNumberOfPiecesOfBookInStock < quantity + currentQuantityInCart) {
			throw new H2bookstoreException("Current number of selected book pieces on stock is "
					+ currentNumberOfPiecesOfBookInStock + " and quantity selected to buy is " + quantity + ".");
		} else {
			if (currentQuantityInCart != 0) {
				mapOfBooks.put(bookId, currentQuantityInCart + quantity);
			} else {
				mapOfBooks.put(bookId, quantity);
			}
		}
	}

	public Map<Integer, Integer> getMapOfBooks() {
		return mapOfBooks;
	}

	public int getTotalNumberOfBookItems() {
		int totalItems = 0;
		for (Map.Entry<Integer, Integer> book : mapOfBooks.entrySet()) {
			totalItems += book.getValue();
		}
		return totalItems;
	}

	public Map<Book, Integer> getBooksAndQuantityMapFromCart() throws SQLException {
		Map<Book, Integer> booksAndQuantityMap = new HashMap<Book, Integer>();
		for (Map.Entry<Integer, Integer> bookEntry : mapOfBooks.entrySet()) {
			int bookId = bookEntry.getKey();
			int quantity = bookEntry.getValue();
			Book book = bookstoreService.fetchBookById(bookId);
			booksAndQuantityMap.put(book, quantity);
		}
		return booksAndQuantityMap;
	}

	public BigDecimal getOverallPrice() throws SQLException {
		BigDecimal overallPrice = new BigDecimal(0);
		BigDecimal currentItemPriceIncludingQuantity;
		Map<Book, Integer> booksAndQuantityMap = getBooksAndQuantityMapFromCart();
		for (Map.Entry<Book, Integer> bookEntry : booksAndQuantityMap.entrySet()) {
			currentItemPriceIncludingQuantity = bookEntry.getKey().getPrice()
					.multiply(new BigDecimal(bookEntry.getValue()));
			overallPrice = overallPrice.add(currentItemPriceIncludingQuantity);
		}
		return overallPrice;
	}

}
