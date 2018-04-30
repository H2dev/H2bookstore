package org.h2dev.bookstore.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BuyStatus;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.h2dev.bookstore.model.ShoppingCartItem;
import org.h2dev.bookstore.service.BookstoreService;
import org.h2dev.bookstore.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	private static final String MAIN_VIEW = "main";
	private static final String CART_VIEW = "cart";
	private static final String ERROR_VIEW = "error";

	@Autowired
	BookstoreService bookstoreService;

	// MAIN VIEW
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView main(HttpServletRequest request, @Param("page") Long page, @Param("orderBy") String orderBy,
			@Param("sortAsc") String sortAsc, @Param("title") String title, @Param("author") String author,
			@Param("addedSuccessfully") String addedSuccessfully)
					throws SQLException, ParseException, H2bookstoreException {

		bookstoreService.initDbIfNotAlready();

		if (page == null) {
			page = (long) 1;
		}

		if (orderBy == null || orderBy == "" || orderBy.isEmpty() || sortAsc == null || sortAsc == ""
				|| sortAsc.isEmpty()) {
			orderBy = null;
			sortAsc = null;
		}

		FilteringCriteria filteringCriteria = new FilteringCriteria(orderBy, sortAsc, title, author);

		int maxNumberOfPages = Util.getMaxNumberOfPages(bookstoreService.countBooksWithFilter(filteringCriteria));

		if (page > maxNumberOfPages) {
			page = (long) maxNumberOfPages;
		}
		request.getSession().setAttribute("currentPage", page);

		if (addedSuccessfully == null) {
			addedSuccessfully = "not set";
		}

		ModelAndView mv = new ModelAndView();
		mv.addObject("addedSuccessfully", addedSuccessfully);
		mv.addObject("maxNumberOfPages", maxNumberOfPages);
		mv.addObject("bookStatusesOverallDbEntries", bookstoreService.getBookStatusTableEntryCount());
		mv.addObject("book_statuses", bookstoreService.fetchAllBookStatusFilteredPaged(page, filteringCriteria));
		mv.addObject("filteringCriteria", filteringCriteria);
		mv.addObject("shoppingCartItems", bookstoreService.getTotalNumberOfBookPiecesInCart());
		mv.setViewName(MAIN_VIEW);

		return mv;
	}

	// FILL IN INITIAL DATA
	@RequestMapping(value = "/fillInitialData", method = RequestMethod.POST)
	public ModelAndView fillInitialData() throws ParseException {
		try {
			bookstoreService.fillInitialData();
		} catch (H2bookstoreException | IOException | SQLException e) {
			e.printStackTrace();
		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/");
		return mv;
	}

	// ADD A BOOK TO THE CART
	@RequestMapping(value = "/addToCart/{id}/page/{currentPage}", method = RequestMethod.POST)
	public ModelAndView addBookToCart(@PathVariable("id") int id, @PathVariable("currentPage") Long page)
			throws SQLException {

		boolean addedSuccessfully = bookstoreService.addToCart(bookstoreService.fetchBookById(id), 1);

		ModelAndView mv = new ModelAndView();
		mv.addObject("addedSuccessfully", String.valueOf(addedSuccessfully));
		mv.addObject("page", page);
		mv.setViewName("redirect:/");

		return mv;
	}

	// REMOVE A BOOK FROM THE CART
	@RequestMapping(value = "/removeFromCart/{id}", method = RequestMethod.POST)
	public ModelAndView removeFromCart(@PathVariable("id") int id) throws SQLException, H2bookstoreException {

		bookstoreService.removeFromShoppingCart(id);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/shoppingCart");

		return mv;
	}

	// SHOPPING CART VIEW
	@RequestMapping(value = "/shoppingCart", method = RequestMethod.GET)
	public ModelAndView shoppingCart() throws ParseException, SQLException {

		List<ShoppingCartItem> itemsInCart = bookstoreService.fetchAllShoppingCartItems();

		ModelAndView mv = new ModelAndView();
		mv.addObject("itemsInCart", itemsInCart);
		mv.setViewName(CART_VIEW);

		return mv;
	}

	// BUY
	@RequestMapping(value = "/buy", method = RequestMethod.POST)
	public ModelAndView buy() throws SQLException, H2bookstoreException {

		// getting books collection to satisfy 'buy' method in BookList
		// interface
		List<Book> booksInCart = bookstoreService.fetchAllShoppingCartItems().stream().map(item -> item.getBook())
				.collect(Collectors.toList());
		Book[] booksToBuy = booksInCart.toArray(new Book[booksInCart.size()]);

		int[] statuses = bookstoreService.buy(booksToBuy);
		String buyStatusMessage = BuyStatus.getMessageByCode(statuses[0]);

		ModelAndView mv = new ModelAndView();

		mv.addObject("buyStatusCode", String.valueOf(statuses[0]));
		mv.addObject("buyStatusMessage", buyStatusMessage);
		mv.setViewName(CART_VIEW);

		return mv;
	}

	// GLOBAL ERROR HANDLING
	@ExceptionHandler(value = Exception.class)
	public ModelAndView redirectToErrorPage(Exception e) {
		ModelAndView mv = new ModelAndView();
		String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().toString();
		mv.addObject("errorMessage", errorMessage);
		mv.setViewName(ERROR_VIEW);
		return mv;
	}

}
