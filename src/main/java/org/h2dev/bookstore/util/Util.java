package org.h2dev.bookstore.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.h2dev.bookstore.exception.H2bookstoreException;
import org.h2dev.bookstore.model.FilteringCriteria;
import org.springframework.core.io.ClassPathResource;

public class Util {

	public static Set<List<String>> parseTxt(String booksResourcePath) throws H2bookstoreException, IOException {
		Set<List<String>> allBooksAttributes = new LinkedHashSet<List<String>>();
		File file = new ClassPathResource(booksResourcePath).getFile();
		if (!file.exists() || file.isDirectory()) {
			throw new IOException("Cannot find books file.");
		}
		List<String> lines = Files.lines(file.toPath()).collect(Collectors.toList());
		for (String line : lines) {
			List<String> bookAttributes = Arrays.asList(line.split(Constants.BOOKS_FILE_ATTRIBUTE_DELIMITER));
			if (bookAttributes.size() != 4) {
				throw new H2bookstoreException("Unexpected number of attributes in one line of initial books file.");
			}
			allBooksAttributes.add(bookAttributes);
		}
		return allBooksAttributes;
	}

	public static double getFormattedStringNumberAsDouble(String numberAsString) throws ParseException {
		NumberFormat nf = NumberFormat.getInstance(new Locale("en", "GB"));
		return nf.parse(numberAsString).doubleValue();
	}

	public static int getMaxNumberOfPages(int listSize) {
		int maxNumberOfPages = listSize / (int) Constants.ITEMS_PER_PAGE;
		if (listSize % Constants.ITEMS_PER_PAGE > 0) {
			++maxNumberOfPages;
		}
		return maxNumberOfPages;
	}

	public static Boolean areFilteringCriteriaEmpty(FilteringCriteria filteringCriteria) {
		if (filteringCriteria.getTitle() != null)
			return false;
		else if (filteringCriteria.getAuthor() != null)
			return false;
		else
			return true;
	}

}
