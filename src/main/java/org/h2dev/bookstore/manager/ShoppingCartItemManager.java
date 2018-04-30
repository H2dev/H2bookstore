package org.h2dev.bookstore.manager;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.ShoppingCartItem;
import org.h2dev.bookstore.service.BookstoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

@Repository("shoppingCartManager")
public class ShoppingCartItemManager extends AbstractManager {

	@Autowired
	BookstoreService bookstoreService;

	@SuppressWarnings("unchecked")
	private Dao<ShoppingCartItem, Integer> shoppingCartItemDAO = (Dao<ShoppingCartItem, Integer>) context
			.getBean("shoppingCartItemORMLite");

	@SuppressWarnings("unchecked")
	private Dao<Book, Integer> bookDAO = (Dao<Book, Integer>) context.getBean("bookORMLite");

	public int getTotalNumberOfBookPiecesInCart() throws SQLException {
		int count = 0;
		List<ShoppingCartItem> itemsInCart = fetchAllShoppingCartItems();
		for (ShoppingCartItem item : itemsInCart) {
			count += item.getQuantity();
		}
		return count;
	}

	public List<ShoppingCartItem> fetchAllShoppingCartItems() throws SQLException {
		logger.info("Fetching all shopping cart items.");
		return shoppingCartItemDAO.queryForAll();
	}

	public ShoppingCartItem fetchShoppingCartItemByBookId(int bookId) throws SQLException {
		logger.info("Fetching shopping cart item by bookId " + bookId);
		QueryBuilder<Book, Integer> queryBuilderBook = bookDAO.queryBuilder();
		queryBuilderBook.where().eq(Book.ID_FIELD, bookId);
		QueryBuilder<ShoppingCartItem, Integer> queryBuilderShoppingCartItem = shoppingCartItemDAO.queryBuilder();
		return queryBuilderShoppingCartItem.join(queryBuilderBook).queryForFirst();
	}

	public boolean removeBookFromCart(int bookId) throws H2bookstoreException {
		logger.info("Removing shopping cart item by bookId " + bookId + ".");
		try {
			int shoppingCartItemId = fetchShoppingCartItemByBookId(bookId).getId();
			DeleteBuilder<ShoppingCartItem, Integer> deleteBuilder = shoppingCartItemDAO.deleteBuilder();
			deleteBuilder.where().eq(ShoppingCartItem.ID_FIELD, shoppingCartItemId);
			deleteBuilder.delete();
		} catch (SQLException e) {
			logger.warn("Could not delete shopping cart item by bookId " + bookId
					+ ". Possibly the item does not exist in shopping cart. " + e.getMessage());
			return false;
		}
		logger.info("Successfully removed shopping cart item by bookId " + bookId + ".");
		return true;
	}

	public void addBookToCart(Integer bookId, Integer quantity) throws SQLException, H2bookstoreException {
		logger.info("Adding a book to shopping cart.");
		int currentQuantityInCart = 0;
		ShoppingCartItem alreadyPresentShoppingCartItem = fetchShoppingCartItemByBookId(bookId);
		if (alreadyPresentShoppingCartItem != null) {
			currentQuantityInCart = alreadyPresentShoppingCartItem.getQuantity();
			int currentNumberOfPiecesOfBookInStock = bookstoreService.fetchBookStatusByBookId(bookId)
					.getPiecesInStock();
			if (currentNumberOfPiecesOfBookInStock < quantity + currentQuantityInCart) {
				throw new H2bookstoreException("Current number of selected book pieces on stock is "
						+ currentNumberOfPiecesOfBookInStock + " and quantity selected to buy is " + quantity + ".");
			} else {
				UpdateBuilder<ShoppingCartItem, Integer> updateBuilder = shoppingCartItemDAO.updateBuilder();
				updateBuilder.where().eq(ShoppingCartItem.ID_FIELD, alreadyPresentShoppingCartItem.getId());
				updateBuilder.updateColumnValue(ShoppingCartItem.QUANTITY, currentQuantityInCart + quantity);
				updateBuilder.update();
			}
		} else {
			Book book = bookstoreService.fetchBookById(bookId);
			if (book == null) {
				throw new H2bookstoreException("Book that was to be added to shopping cart no longer exists.");
			} else {
				ShoppingCartItem shoppingCartItem = new ShoppingCartItem(book, quantity);
				shoppingCartItemDAO.create(shoppingCartItem);
			}
		}
	}

	public BigDecimal getOverallPrice() throws SQLException {
		BigDecimal overallPrice = new BigDecimal(0);
		List<ShoppingCartItem> listItems = fetchAllShoppingCartItems();
		BigDecimal currentItemPriceIncludingQuantity;
		for (ShoppingCartItem item : listItems) {
			currentItemPriceIncludingQuantity = item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity()));
			overallPrice = overallPrice.add(currentItemPriceIncludingQuantity);
		}
		return overallPrice;
	}

}
