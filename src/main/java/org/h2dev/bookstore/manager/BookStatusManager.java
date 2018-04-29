package org.h2dev.bookstore.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.h2dev.bookstore.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

@Repository("bookStatusManager")
public class BookStatusManager extends AbstractManager {

	@Autowired
	GeneralBookstoreManager generalBookstoreManager;
	@Autowired
	BookManager bookManager;

	@SuppressWarnings("unchecked")
	private Dao<BookStatus, Integer> bookStatusDAO = (Dao<BookStatus, Integer>) context.getBean("bookStatusORMLite");

	@SuppressWarnings("unchecked")
	private Dao<Book, Integer> bookDAO = (Dao<Book, Integer>) context.getBean("bookORMLite");

	public List<BookStatus> fetchAllBookStatus() throws SQLException {
		logger.info("Fetching all book statuses.");
		return bookStatusDAO.queryForAll();
	}

	public long tableEntryCount() {
		try {
			return bookStatusDAO.countOf();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return 0;
	}

	public Integer countBookStatusesWithFilter(FilteringCriteria filteringCriteria) throws SQLException {
		List<BookStatus> listBookStatus = new ArrayList<BookStatus>();
		try {
			QueryBuilder<Book, Integer> queryBuilderBook = bookDAO.queryBuilder();
			QueryBuilder<BookStatus, Integer> queryBuilderBookStatus = bookStatusDAO.queryBuilder();
			bookManager.buildBookQueryFromFilteringParams(queryBuilderBook, filteringCriteria);
			listBookStatus = queryBuilderBookStatus.join(queryBuilderBook).query();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return listBookStatus.size();
	}

	public BookStatus fetchBookStatusByBookId(int id) throws SQLException {
		logger.info("Fetching book status by bookId " + id);
		QueryBuilder<Book, Integer> queryBuilderBook = bookDAO.queryBuilder();
		queryBuilderBook.where().eq(Book.ID_FIELD, id);
		QueryBuilder<BookStatus, Integer> queryBuilderBookStatus = bookStatusDAO.queryBuilder();
		return queryBuilderBookStatus.join(queryBuilderBook).queryForFirst();
	}

	public void setNewPiecesInStockForBookStatus(int bookStatusId, int newNbrOfPieces) throws SQLException {
		UpdateBuilder<BookStatus, Integer> updateBuilder = bookStatusDAO.updateBuilder();
		updateBuilder.where().eq(BookStatus.ID_FIELD, bookStatusId);
		updateBuilder.updateColumnValue(BookStatus.PIECES_IN_STOCK_FIELD, newNbrOfPieces);
		updateBuilder.update();
	}

	public List<BookStatus> fetchAllBookStatusFilteredPaged(Long page, FilteringCriteria filteringCriteria) {

		logger.info("Fetching the book statuses on basis of passed filtering criteria.");

		if (page == null) {
			page = (long) 1;
		}

		List<BookStatus> bookStatuses = new ArrayList<BookStatus>();
		try {
			if (page >= (long) 0) {
				Boolean booleanAsc = true;
				String orderBy = filteringCriteria.getOrderBy() != null ? filteringCriteria.getOrderBy() : null;
				String sortAsc = filteringCriteria.getSortAsc() != null ? filteringCriteria.getSortAsc() : null;

				QueryBuilder<Book, Integer> queryBuilderBook = bookDAO.queryBuilder();
				QueryBuilder<BookStatus, Integer> queryBuilderBookStatus = bookStatusDAO.queryBuilder();
				bookManager.buildBookQueryFromFilteringParams(queryBuilderBook, filteringCriteria);

				queryBuilderBookStatus.offset((page - (long) 1) * Constants.ITEMS_PER_PAGE)
						.limit(Constants.ITEMS_PER_PAGE);

				if (orderBy != null && orderBy != "" && !orderBy.isEmpty() && sortAsc != null && sortAsc != ""
						&& !sortAsc.isEmpty()) {
					booleanAsc = Boolean.parseBoolean(sortAsc);
					queryBuilderBook.orderBy(orderBy, booleanAsc);
				}
				bookStatuses = queryBuilderBookStatus.join(queryBuilderBook).query();
			} else {
				bookStatuses = bookStatusDAO.queryForAll();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return bookStatuses;
	}

	public List<BookStatus> fetchAllBookStatusFiltered(FilteringCriteria filteringCriteria) throws SQLException {
		logger.info("Fetching the book statuses on basis of passed filtering criteria.");
		List<BookStatus> listBookStatus = new ArrayList<BookStatus>();
		try {
			QueryBuilder<Book, Integer> queryBuilderBook = bookDAO.queryBuilder();
			QueryBuilder<BookStatus, Integer> queryBuilderBookStatus = bookStatusDAO.queryBuilder();
			bookManager.buildBookQueryFromFilteringParams(queryBuilderBook, filteringCriteria);
			listBookStatus = queryBuilderBookStatus.join(queryBuilderBook).query();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return listBookStatus;
	}

	public void setBookManager(BookManager bookManager) {
		this.bookManager = bookManager;
	}

}
