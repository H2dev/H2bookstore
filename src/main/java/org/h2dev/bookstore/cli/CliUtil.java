package org.h2dev.bookstore.cli;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;
import org.h2dev.bookstore.model.ShoppingCartItem;

public class CliUtil {

	public static final String MENU_DELIMITER = " :: ";
	public static final String BOOK_ATTRIBUTES_DELIMITER = " | ";

	public static void printBookStatuses(List<BookStatus> bookStatuses, boolean filtered) {
		if (bookStatuses == null || bookStatuses.isEmpty()) {
			if (filtered) {
				System.out.println("! No books to show for entered criteria or no books are present at all !");
			} else {
				System.out.println("! No books are available at the moment !");
			}
		} else {
			for (BookStatus bookStatus : bookStatuses) {
				System.out.print("Id: " + bookStatus.getId());
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Title: " + bookStatus.getBook().getTitle());
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Author: " + bookStatus.getBook().getAuthor());
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Price: " + CliUtil.convertToCurrencyNumberFormat(bookStatus.getBook().getPrice()));
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Pieces in stock: " + bookStatus.getPiecesInStock());
				System.out.println();
			}
		}
	}

	public static void printShoppingCartItems(List<ShoppingCartItem> itemsInCart, BigDecimal overallPrice) {
		System.out.println();
		if (itemsInCart == null || itemsInCart.isEmpty()) {
			System.out.println("! No books currently in shopping cart !");
		} else {
			for (ShoppingCartItem item : itemsInCart) {
				int id = item.getBook().getId();
				String title = item.getBook().getTitle();
				String author = item.getBook().getAuthor();
				BigDecimal price = item.getBook().getPrice();
				int quantity = item.getQuantity();
				System.out.print("Id: " + id);
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Title: " + title);
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Author: " + author);
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Price: " + CliUtil.convertToCurrencyNumberFormat(price));
				System.out.print(CliUtil.BOOK_ATTRIBUTES_DELIMITER);
				System.out.print("Quantity: " + quantity);
				System.out.println();
			}
			System.out.println("\nTOTAL PRICE = " + CliUtil.convertToCurrencyNumberFormat(overallPrice));
			System.out.println();
		}
	}

	public static void printAddNewBookToDbSuccess(Book newBook, String piecesInStock) {
		System.out.println("\n");
		System.out.println("-- Successfully added new book and status to the database --");
		System.out.println("Title: " + newBook.getTitle() + ", Author: " + newBook.getAuthor() + ", Price: "
				+ newBook.getPrice() + ", Pieces in stock: " + piecesInStock);
	}

	public static String bookAsciiArt() {
		// @formatter:off
		StringBuffer sb = new StringBuffer();
		sb.append("\n\n\n");
		sb.append("          _____________"); sb.append("\n");
		sb.append("        _/ H2bookstore \\_"); sb.append("\n");
		sb.append("       // ~~ ~~ | ~~ ~  \\\\"); sb.append("\n");
		sb.append("      // ~ ~ ~~ | ~~~ ~~ \\\\"); sb.append("\n");
		sb.append("     //________.|.________\\\\"); sb.append("\n");
		sb.append("    `----------`-'----------'"); sb.append("\n");
		// @formatter:on
		return sb.toString();
	}

	public static void printChosenMenuOption(String description) {
		System.out.println("\n\n");
		String output = "//  " + description + "  \\\\";
		int length = output.length();
		char[] stars = new char[length];
		Arrays.fill(stars, '*');
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append(stars);
		sb.append("\n");
		sb.append(output);
		sb.append("\n");
		sb.append(stars);
		System.out.println(sb.toString());
	}

	public static String convertToCurrencyNumberFormat(BigDecimal price) {
		NumberFormat nf = NumberFormat.getInstance(new Locale("en", "GB"));
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		return nf.format(price.doubleValue()).concat(" kr");
	}

}
