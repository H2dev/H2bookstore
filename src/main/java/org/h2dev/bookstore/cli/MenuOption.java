package org.h2dev.bookstore.cli;

public enum MenuOption {

	// @formatter:off
	LIST_ALL_BOOKS("A", "List of all books"), 
	LIST_ALL_BOOKS_WITH_CRITERIA("B", "List of all books by filtering criteria"), 
	ADD_NEW_BOOK_TO_DB("C", "Add a book to database"),
	ADD_BOOK_TO_CART("D", "Add a book to shopping cart"),
	VIEW_CART("E", "Shopping cart view"),
	REMOVE_BOOK_FROM_CART("F", "Remove a book from shopping cart"),
	BUY_BOOKS("G", "Buy books that are in shopping cart"),
	EXIT("H", "Exit");
	// @formatter:on

	private String code;
	private String description;

	MenuOption(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public static String getDescriptionByCode(String code) {
		for (MenuOption e : MenuOption.values()) {
			if (code.toUpperCase().trim().equals(e.code))
				return e.description;
		}
		return null;
	}
}
