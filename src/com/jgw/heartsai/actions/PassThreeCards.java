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
	public String toStringUI() {
		return toStringInner(true);
	}

	@Override
	public String toString() {
		return toStringInner(false);
	}

	private String toStringInner(boolean UI) {
		String res = this.getType().name() + ": ";

		int count = 0;
		for (Card c : cards) {
			if (UI) {
				res += c.toStringUI();
			} else {
				res += c.toString();
			}

			if (count != 2) {
				res += ", ";
			}

			count++;
		}

		return res;
	}

}
