package com.jgw.heartsai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.fusesource.jansi.Ansi;

public final class Card {

	public final static List<Card> ALL_CARDS;

	private final int number;
	private final Suit suit;

	private final int absoluteIndex;

	static {
		List<Card> cards = new ArrayList<>();

		Arrays.asList(Suit.values()).forEach(suit -> {
			IntStream.range(1, 14).forEach(val -> {
				cards.add(new Card(val, suit));
			});
		});

		ALL_CARDS = Collections.unmodifiableList(cards);

	}

	public Card(int number, Suit suit) {
		this.number = number;
		this.suit = suit;

		this.absoluteIndex = (this.suit.ordinal()) * 13 + number;
	}

	public int getAbsoluteIndex() {
		return absoluteIndex;
	}

	public int getNumber() {
		return number;
	}

	/**
	 * The only difference between this and getNumber() is that ACE is most strong,
	 * here.
	 */
	public int getNumberStrength() {
		int res = number;
		if (res == ACE) {
			res = 14;
		}
		return res;
	}

	public Suit getSuit() {
		return suit;
	}

	public enum Suit {
		DIAMONDS, CLUBS, HEARTS, SPADES
	};

	private static final int ACE = 1;
	private static final int JACK = 11;
	private static final int QUEEN = 12;
	private static final int KING = 13;

	public int getPointsValue() {
		if (this.getNumber() == QUEEN && this.getSuit() == Suit.SPADES) {
			return 13;
		}

		if (this.getSuit() == Suit.HEARTS) {
			return 1;
		}

		return 0;

	}

	public String toStringUI() {

		if (suit == Suit.DIAMONDS || suit == Suit.HEARTS) {

			return Ansi.ansi().fgRed().a(toString()).reset().toString();

		} else {

			return Ansi.ansi().fgBrightBlack().a(toString()).reset().toString();
		}

	}

	@Override
	public String toString() {

		String suitStr = "";

		switch (suit) {
		case CLUBS:
			suitStr = "C";
			break;
		case DIAMONDS:
			suitStr = "D";
			break;
		case HEARTS:
			suitStr = "H";
			break;
		case SPADES:
			suitStr = "S";
			break;
		default:
			suitStr = "!";
			break;
		}
//		switch (suit) {
//		case CLUBS:
//			suitStr = Emojis.CLUB;
//			break;
//		case DIAMONDS:
//			suitStr = Emojis.DIAMOND;
//			break;
//		case HEARTS:
//			suitStr = Emojis.HEART;
//			break;
//		case SPADES:
//			suitStr = Emojis.SPADE;
//			break;
//		default:
//			suitStr = "!";
//			break;
//		}

		String numberStr = "" + getNumber();

		if (number == ACE) {
			numberStr = "A";
		}

		if (number == JACK) {
			numberStr = "J";
		}

		if (number == QUEEN) {
			numberStr = "Q";
		}

		if (number == KING) {
			numberStr = "K";
		}

		return numberStr + suitStr;

	}

}
