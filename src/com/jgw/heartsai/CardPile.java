package com.jgw.heartsai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.jgw.heartsai.util.HeartsUtil;

public final class CardPile {

	public final static CardPile EMPTY = new CardPile(Collections.emptyList());

	private final List<Card> cards;

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

	public CardPile removeCards(Card[] cardsToRemove) {
		List<Card> newPileWithoutCards = cards.stream().filter(card -> {
			for (Card cardToRemove : cardsToRemove) {
				if (cardToRemove == card) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList());

		if (newPileWithoutCards.size() != cards.size() - cardsToRemove.length) {
			HeartsUtil.throwErr("Cards were not removed as expected.");
		}

		return new CardPile(newPileWithoutCards);
	}

	public List<Card> getCards() {
		return Collections.unmodifiableList(cards);
	}
}
