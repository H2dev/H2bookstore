package org.h2dev.bookstore.manager;

import java.sql.SQLException;
import java.util.List;

import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.springframework.stereotype.Repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

@Repository("bookManager")
public class BookManager extends AbstractManager {

	@SuppressWarnings("unchecked")
	private Dao<Book, Integer> bookDAO = (Dao<Book, Integer>) context.getBean("bookORMLite");

	public List<Book> fetchAllBook() throws SQLException {
		logger.info("Fetching all books from DB.");
		return bookDAO.queryForAll();
	}

	public Book fetchBookById(int id) throws SQLException {
		logger.info("Fetching book with id " + id);
		return bookDAO.queryForId(id);
	}

	public void buildBookQueryFromFilteringParams(QueryBuilder<Book, Integer> queryBuilderBook,
			FilteringCriteria filteringCriteria) throws SQLException {

		String title = filteringCriteria.getTitle() != null ? filteringCriteria.getTitle() : null;
		String author = filteringCriteria.getAuthor() != null ? filteringCriteria.getAuthor() : null;

		Where<Book, Integer> where = null;
		if (title != null && title != "" && !title.isEmpty()) {
			where = queryBuilderBook.where();
			where.like(Book.TITLE_FIELD, "%" + title + "%");
		}
		if (author != null && author != "" && !author.isEmpty()) {
			if (where != null) {
				where.and();
			} else {
				where = queryBuilderBook.where();
			}
			where.like(Book.AUTHOR_FIELD, "%" + author + "%");
		}
	}

	public long tableEntryCount() {
		try {
			return bookDAO.countOf();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return 0;
	}

}
