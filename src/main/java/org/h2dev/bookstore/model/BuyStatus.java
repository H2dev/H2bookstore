package org.h2dev.bookstore.model;

public enum BuyStatus {

	// @formatter:off
	OK(0, "Books purchased."), 
	NOT_IN_STOCK(1, "Some books cannot be purchased because they are not currently in stock" 
					+ " or the quantity is over the stock's availability for those books."), 
	DOES_NOT_EXIST(2, "Some books cannot be purchased because they do not exist.");
	// @formatter:on

	private int code;
	private String message;

	BuyStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public static String getMessageByCode(int code) {
		for (BuyStatus e : BuyStatus.values()) {
			if (code == e.code)
				return e.message;
		}
		return null;
	}
}
