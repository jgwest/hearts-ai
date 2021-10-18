package com.jgw.heartsai;

import com.jgw.heartsai.util.HeartsUtil;

public final class State {

	public static enum RoundType {
		PASS_LT, PASS_GT, PASS_PLUS_TWO_MOD_4, DONT_PASS
	}

	public static enum Phase {
		INITIAL, PASS, PLAY
	};

	private final RoundType roundType;

	/** The cards the player has in their hand */
	private final CardPile[] playerCards;

	/** Cards that the player is passing to the next player. */
	private final CardPile[] playerPassCards;

	private final CardPile centerPile;
	private final Phase phase;
	private final int playerTurn;

	private final SlowState slowState;

	public State(CardPile[] playerCards, CardPile centerPile, CardPile[] playerPassCards, int playerTurn, Phase phase,
			RoundType roundType, SlowState slowState) {

		this.playerCards = playerCards;
		this.playerPassCards = playerPassCards;
		this.centerPile = centerPile;
		this.playerTurn = playerTurn;
		this.phase = phase;
		this.roundType = roundType;
		this.slowState = slowState;
	}

	public Phase getPhase() {
		return phase;
	}

	public int getPlayerTurn() {
		return playerTurn;
	}

	public RoundType getRoundType() {
		return roundType;
	}

	public CardPile[] getPlayerCards() {
		return playerCards;
	}

	public CardPile[] getPlayerPassCards() {
		return playerPassCards;
	}

	public CardPile getCenterPile() {
		return centerPile;
	}

	public SlowState getSlowState() {
		return slowState;
	}

	public State toPhase(Phase newPhase) {
		State newState = new State(playerCards, centerPile, playerPassCards, playerTurn, newPhase, roundType,
				slowState);
		return newState;
	}

	public State toPlayerTurn(int newPlayerTurn) {
		State newState = new State(playerCards, centerPile, playerPassCards, newPlayerTurn, phase, roundType,
				slowState);

		return newState;
	}

	public State replacePlayerHand(int playerIndex, CardPile cp) {

		CardPile[] newPlayerCards = new CardPile[4];
		for (int x = 0; x < newPlayerCards.length; x++) {
			if (x != playerIndex) {
				newPlayerCards[x] = this.playerCards[x];
			} else {
				newPlayerCards[x] = cp;
			}
		}
		State newState = new State(newPlayerCards, centerPile, playerPassCards, playerTurn, phase, roundType,
				slowState);

		return newState;

	}

	public State replacePlayerPassCards(int playerPassCardIndex, CardPile cp) {
		if (this.phase != Phase.PASS) {
			HeartsUtil.throwErr("Invalid phase");
		}

		CardPile[] newPlayerPassCards = new CardPile[4];
		for (int x = 0; x < newPlayerPassCards.length; x++) {
			if (x != playerPassCardIndex) {
				newPlayerPassCards[x] = this.playerPassCards[x];
			} else {
				newPlayerPassCards[x] = cp;
			}
		}

		State newState = new State(playerCards, centerPile, newPlayerPassCards, playerTurn, phase, roundType,
				slowState);

		return newState;
	}
}
