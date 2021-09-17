package com.jgw.heartsai.actions;

import com.jgw.heartsai.Card;

public class PassThreeCards extends Action {

	private final Card[] cards;

	public PassThreeCards(Card[] cards) {
		this.cards = cards;
	}

	public Card[] getCards() {
		return cards;
	}

	@Override
	public ActionType getType() {
		return ActionType.PASS_3_CARDS;
	}

	@Override
	public String toString() {
		String res = this.getType().name() + ": ";

		int count = 0;
		for (Card c : cards) {
			res += c.toString();
			if (count != 2) {
				res += ", ";
			}

			count++;
		}

		return res;
	}

}
