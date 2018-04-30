package org.h2dev.bookstore.cli;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.manager.BookManager;
import org.h2dev.bookstore.manager.BookStatusManager;
import org.h2dev.bookstore.manager.GeneralBookstoreManager;
import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.model.BuyStatus;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.h2dev.bookstore.model.ShoppingCartItem;
import org.h2dev.bookstore.service.BookstoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "org.h2dev.bookstore")
public class Cli {

	@Autowired
	BookstoreService bookstoreService;
	@Autowired
	GeneralBookstoreManager generalBookstoreManager;
	@Autowired
	BookStatusManager bookStatusManager;
	@Autowired
	BookManager bookManager;

	Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws IOException, H2bookstoreException, SQLException, ParseException {

		disableORMLiteLoggerToConsoleOutput();

		@SuppressWarnings("resource")
		ApplicationContext context = new AnnotationConfigApplicationContext(Cli.class);

		Cli cli = context.getBean(Cli.class);
		cli.startCli();
	}

	private void startCli() throws IOException, H2bookstoreException, SQLException, ParseException {
		try {
			String currentOption = "";

			bookstoreService.initDbIfNotAlready();
			bookstoreService.fillInitialData();
			outputMenu();

			while (!(currentOption = scanner.nextLine().toUpperCase().trim()).equals("H")) {
				switch (currentOption) {
				case "A":
					handleFetchAllBookStatus();
					break;
				case "B":
					handleListAllBooksStatusByFilterCriteria();
					break;
				case "C":
					handleAddNewBookToDb();
					break;
				case "D":
					handleAddBookToCart();
					break;
				case "E":
					handleViewCart();
					break;
				case "F":
					handleRemoveBookFromCart();
					break;
				case "G":
					handleBuy();
					break;
				default:
					break;
				}
				outputMenu();
			}
			scanner.close();
			System.out.println("\nYou are welcome back anytime!");

		} catch (Exception e) {
			System.out.println(
					"\n\n\n! Something went wrong. " + (e.getMessage() != null ? e.getMessage() : e.toString()) + " !");
		}
	}

	private void handleFetchAllBookStatus() throws SQLException {
		System.out.println();
		List<BookStatus> bookStatuses = bookstoreService.fetchAllBookStatus();
		CliUtil.printChosenMenuOption(MenuOption.LIST_ALL_BOOKS.getDescription());
		CliUtil.printBookStatuses(bookStatuses, false);
	}

	private void handleListAllBooksStatusByFilterCriteria() throws SQLException {
		System.out.println("\n-- Filtering criteria -- ");
		System.out.print("Title: ");
		String titleFilter = scanner.nextLine();
		System.out.print("Author: ");
		String authorFilter = scanner.nextLine();
		System.out.println();
		FilteringCriteria filteringCriteria = new FilteringCriteria();
		filteringCriteria.setTitle(titleFilter);
		filteringCriteria.setAuthor(authorFilter);
		List<BookStatus> bookStatuses = bookstoreService.fetchAllBookStatusFiltered(filteringCriteria);
		CliUtil.printChosenMenuOption(MenuOption.LIST_ALL_BOOKS_WITH_CRITERIA.getDescription());
		CliUtil.printBookStatuses(bookStatuses, true);
	}

	private void handleAddNewBookToDb() throws NumberFormatException, H2bookstoreException {
		CliUtil.printChosenMenuOption(MenuOption.ADD_NEW_BOOK_TO_DB.getDescription());
		System.out.println("\n-- Enter book parameters -- ");
		System.out.print("Title: ");
		String title = scanner.nextLine();
		System.out.print("Author: ");
		String author = scanner.nextLine();
		System.out.print("Price: ");
		String price = scanner.nextLine();
		System.out.print("Pieces in stock: ");
		String piecesInStock = scanner.nextLine();
		System.out.println();
		if (price == null || price.isEmpty()) {
			price = "0";
		}
		if (piecesInStock == null || piecesInStock.isEmpty()) {
			piecesInStock = "0";
		}
		try {
			Book newBook = new Book(title, author, BigDecimal.valueOf(Double.parseDouble(price)));
			bookstoreService.addNewBookAndCorrespondingBookStatus(newBook, Integer.parseInt(piecesInStock));
			CliUtil.printAddNewBookToDbSuccess(newBook, piecesInStock);
		} catch (Exception e) {
			System.out
					.println("\nAttempt for a new book entry was not successful. Please check your input parameters.");
			handleAddNewBookToDb();
		}
	}

	private void handleAddBookToCart() {
		CliUtil.printChosenMenuOption(MenuOption.ADD_BOOK_TO_CART.getDescription());
		System.out.println("\n-- Enter ID of the book to be added to shopping cart and book quantity -- ");
		System.out.print("Id: ");
		String idInput = scanner.nextLine();
		System.out.print("Quantity: ");
		String quantityInput = scanner.nextLine();
		System.out.println();
		try {
			int id = Integer.parseInt(idInput);
			int quantity = Integer.parseInt(quantityInput);
			Book book = bookstoreService.fetchBookById(id);
			if (book == null) {
				System.out.println("\nThere is no book in the database with ID: " + id);
			} else {
				boolean successful = bookstoreService.addToCart(book, quantity);
				if (successful) {
					System.out.println(
							"\n\nSuccessfully added " + quantity + " book(s) with ID " + id + " to shopping cart.");
				} else {
					System.out.println(
							"\nAdding the book to shopping cart was unsuccessful. Possibly invalid book ID or there is not enough pieces of that book in stock.");
				}
			}
		} catch (Exception e) {
			System.out.println("\nInvalid book ID or quantity number.");
		}
	}

	private void handleViewCart() throws SQLException {
		List<ShoppingCartItem> itemsInCart = bookstoreService.fetchAllShoppingCartItems();
		BigDecimal overallPrice = bookstoreService.getOverallPriceFromShoppingCart();
		CliUtil.printChosenMenuOption(MenuOption.VIEW_CART.getDescription());
		CliUtil.printShoppingCartItems(itemsInCart, overallPrice);
	}

	private void handleRemoveBookFromCart() {
		CliUtil.printChosenMenuOption(MenuOption.REMOVE_BOOK_FROM_CART.getDescription());
		System.out.println("\n-- Enter ID of the book to be removed from shopping cart -- ");
		System.out.print("Id: ");
		String idInput = scanner.nextLine();
		System.out.println();
		try {
			int id = Integer.parseInt(idInput);
			Book book = bookstoreService.fetchBookById(id);
			if (book == null) {
				System.out.println("\nThere is no book in the database with ID: " + id);
			} else {
				boolean successful = bookstoreService.removeFromShoppingCart(book.getId());
				if (successful) {
					System.out.println("\n\nSuccessfully removed book with ID " + id + " from shopping cart.");
				} else {
					System.out.println(
							"\nRemoving the book from shopping cart was unsuccessful. Possibly invalid book ID.");
				}
			}
		} catch (Exception e) {
			System.out.println("\nInvalid book ID.");
		}
	}

	private void handleBuy() throws SQLException, H2bookstoreException {
		CliUtil.printChosenMenuOption(MenuOption.BUY_BOOKS.getDescription());
		System.out.println();
		List<ShoppingCartItem> itemsInCart = bookstoreService.fetchAllShoppingCartItems();
		if (itemsInCart == null || itemsInCart.isEmpty()) {
			System.out.println("\n! The shopping cart is empty ! ");
		} else {
			String totalPrice = CliUtil
					.convertToCurrencyNumberFormat(bookstoreService.getOverallPriceFromShoppingCart());
			System.out.println("\n-- Are you sure you want to buy books that are in shopping cart? -- ");
			System.out.println("Total price would be =  " + totalPrice);
			System.out.println("\nConfirm this purchase with 'y', otherwise type any key.");
			String input = scanner.nextLine();
			System.out.println();
			if (input != null && input.toLowerCase().equals("y")) {
				List<Book> booksInCart = bookstoreService.fetchAllShoppingCartItems().stream()
						.map(item -> item.getBook()).collect(Collectors.toList());
				Book[] booksToBuy = booksInCart.toArray(new Book[booksInCart.size()]);
				int[] statuses = bookstoreService.buy(booksToBuy);
				int status = statuses[0];
				if (status == BuyStatus.DOES_NOT_EXIST.getCode()) {
					System.out.println("\nThe purchase was not successful. " + BuyStatus.DOES_NOT_EXIST.getMessage());
				} else if (status == BuyStatus.NOT_IN_STOCK.getCode()) {
					System.out.println("\nThe purchase was not successful. " + BuyStatus.NOT_IN_STOCK.getMessage());
				} else if (status == BuyStatus.OK.getCode()) {
					System.out.println("\n" + BuyStatus.OK.getMessage());
				}
			}
		}
	}

	private void outputMenu() throws IOException {
		// @formatter:off
		System.out.println(CliUtil.bookAsciiArt());
		System.out.println(MenuOption.LIST_ALL_BOOKS.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.LIST_ALL_BOOKS.getDescription());
		System.out.println(MenuOption.LIST_ALL_BOOKS_WITH_CRITERIA.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.LIST_ALL_BOOKS_WITH_CRITERIA.getDescription());
		System.out.println(MenuOption.ADD_NEW_BOOK_TO_DB.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.ADD_NEW_BOOK_TO_DB.getDescription());
		System.out.println(MenuOption.ADD_BOOK_TO_CART.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.ADD_BOOK_TO_CART.getDescription());
		System.out.println(MenuOption.VIEW_CART.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.VIEW_CART.getDescription());
		System.out.println(MenuOption.REMOVE_BOOK_FROM_CART.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.REMOVE_BOOK_FROM_CART.getDescription());
		System.out.println(MenuOption.BUY_BOOKS.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.BUY_BOOKS.getDescription());
		System.out.println(MenuOption.EXIT.getCode() + CliUtil.MENU_DELIMITER +  MenuOption.EXIT.getDescription());
		System.out.println("_____________________________________________________");
		System.out.print("> ");
		// @formatter:on
	}

	private static void disableORMLiteLoggerToConsoleOutput() {
		System.setProperty("com.j256.ormlite.logger.type", "LOCAL");
		System.setProperty("com.j256.ormlite.logger.level", "FATAL");
	}

}
