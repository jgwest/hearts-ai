package com.jgw.heartsai.util;

import java.util.Collections;
import java.util.List;

import com.jgw.heartsai.Card;

public class HeartsUtil {

	public static final String CRLF = System.getProperty("line.separator");

	public static void throwErr(String str) throws RuntimeException {
		throw new RuntimeException(str);
	}

	public static void sortForUI(List<Card> cards) {

		Collections.sort(cards, (a, b) -> {

			return 100 * (a.getSuit().ordinal() - b.getSuit().ordinal())
					+ (a.getNumberStrength() - b.getNumberStrength());
		});

	}

}
