package com.jgw.heartsai;

public final class Card {

	private final int number;
	private final Suit suit;

	public Card(int number, Suit suit) {
		this.number = number;
		this.suit = suit;
	}

	public int getNumber() {
		return number;
	}

	public Suit getSuit() {
		return suit;
	}

	enum Suit {
		DIAMONDS, CLUBS, HEARTS, SPADES
	};

	@Override
	public String toString() {

		String suitStr = "";

		switch (suit) {
		case CLUBS:
			suitStr = "♣️";
			break;
		case DIAMONDS:
			suitStr = "♦️";
			break;
		case HEARTS:
			suitStr = "♥️";
			break;
		case SPADES:
			suitStr = "♠️";
			break;
		default:
			suitStr = "?";
			break;
		}

		return number + suitStr;

	}

}
