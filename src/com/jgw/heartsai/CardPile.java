package com.jgw.heartsai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CardPile {

	final public static CardPile EMPTY = new CardPile(Collections.emptyList());

	final List<Card> cards;

	private CardPile(List<Card> cards) {
		this.cards = cards;
	}

	public CardPile addCards(Card... newCards) {
		List<Card> newList = new ArrayList<>(cards);
		newList.addAll(Arrays.asList(newCards));

		return new CardPile(newList);
	}

	public CardPile addCards(List<Card> newCards) {
		List<Card> newList = new ArrayList<>(cards);
		newList.addAll(newCards);
		return new CardPile(newList);
	}

	public CardPile shuffle() {
		List<Card> newList = new ArrayList<>(cards);
		Collections.shuffle(newList);
		return new CardPile(newList);
	}

	public CardPile removeCardsFromTop(int numCardsToRemove, List<Card> removed) {
		for (int x = 0; x < numCardsToRemove; x++) {
			removed.add(cards.get(x));
		}

		return new CardPile(cards.subList(numCardsToRemove, cards.size()));

	}

	public List<Card> getCards() {
		return Collections.unmodifiableList(cards);
	}
}
