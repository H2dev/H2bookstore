package org.h2dev.bookstore.cli;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.h2dev.bookstore.model.Book;
import org.h2dev.bookstore.model.BookStatus;

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

	public static void printShoppingCartItems(Map<Book, Integer> booksAndQuantityMapFromCart, BigDecimal overallPrice) {
		System.out.println();
		if (booksAndQuantityMapFromCart == null || booksAndQuantityMapFromCart.isEmpty()) {
			System.out.println("! No books currently in shopping cart !");
		} else {
			for (Map.Entry<Book, Integer> bookAndQuantityEntry : booksAndQuantityMapFromCart.entrySet()) {
				int id = bookAndQuantityEntry.getKey().getId();
				String title = bookAndQuantityEntry.getKey().getTitle();
				String author = bookAndQuantityEntry.getKey().getAuthor();
				BigDecimal price = bookAndQuantityEntry.getKey().getPrice();
				int quantity = bookAndQuantityEntry.getValue();
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
