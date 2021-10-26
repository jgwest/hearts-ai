package com.jgw.heartsai.actions;

import com.jgw.heartsai.Card;

public class PlayCard extends Action {

	public static final PlayCard[] PASS_CARD_ACTIONS;

	static {
		PASS_CARD_ACTIONS = new PlayCard[52];
		Card.ALL_CARDS.forEach(card -> {
			PASS_CARD_ACTIONS[card.getAbsoluteIndex()] = new PlayCard(card);
		});
	}

	private final Card card;

	private PlayCard(Card card) {
		this.card = card;
	}

	@Override
	public ActionType getType() {
		return ActionType.PLAY_CARD;
	}

	public Card getCard() {
		return card;
	}

	@Override
	public String toString() {
		return super.toString() + ": " + card;
	}

	@Override
	public String toStringUI() {
		return super.toString() + ": " + card.toStringUI();
	}
}
