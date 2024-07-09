package com.jgw.heartsai;

import com.jgw.heartsai.State.Phase;
import com.jgw.heartsai.State.RoundType;
import com.jgw.heartsai.util.HeartsUtil;

public class StateBuilder {

	private RoundType roundType;

	/** The cards the player has in their hand */
	private CardPile[] playerCards;

	/** Cards that the player is passing to the next player. */
	private CardPile[] playerPassCards;

//	private CardPile centerPile;
	private Card[] turnCardsPlayed;
	private Phase phase;
	private int playerTurn;

	private SlowState slowState;

	public StateBuilder(State state) {

		roundType = state.getRoundType();
		playerCards = state.getPlayerCards();
		playerPassCards = state.getPlayerPassCards();
		turnCardsPlayed = state.getTurnCardsPlayed();
//		centerPile = state.getCenterPile();
		phase = state.getPhase();
		playerTurn = state.getPlayerTurn();
		slowState = state.getSlowState();

	}

	public StateBuilder setRoundType(RoundType roundType) {
		this.roundType = roundType;
		return this;
	}

	public StateBuilder setPlayerCards(CardPile[] playerCards) {
		this.playerCards = playerCards;
		return this;
	}

	public StateBuilder setPlayerPassCards(CardPile[] playerPassCards) {
		this.playerPassCards = playerPassCards;
		return this;
	}

//	public StateBuilder setCenterPile(CardPile centerPile) {
//
//		if (this.phase != Phase.PLAY) {
//			HeartsUtil.throwErr("Invalid phase");
//		}
//
//		this.centerPile = centerPile;
//		return this;
//	}

	public StateBuilder setPhase(Phase phase) {
		this.phase = phase;
		return this;
	}

	public StateBuilder setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
		return this;
	}

	public StateBuilder setSlowState(SlowState slowState) {
		this.slowState = slowState;
		return this;
	}

	public StateBuilder setTurnCardsPlayed(Card[] turnCardsPlayed) {
		if (this.phase != Phase.PLAY) {
			HeartsUtil.throwErr("Invalid phase");
		}
		this.turnCardsPlayed = turnCardsPlayed;
		return this;
	}

	public StateBuilder replacePlayerHand(int playerIndex, CardPile cp) {

		CardPile[] newPlayerCards = new CardPile[4];
		for (int x = 0; x < newPlayerCards.length; x++) {
			if (x != playerIndex) {
				newPlayerCards[x] = this.playerCards[x];
			} else {
				newPlayerCards[x] = cp;
			}
		}
		this.playerCards = newPlayerCards;

		return this;
	}

	public StateBuilder replacePlayerPassCards(int playerPassCardIndex, CardPile cp) {
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

		this.playerPassCards = newPlayerPassCards;
		return this;

	}

	public State build() {
		return new State(playerCards, /* centerPile, */ playerPassCards, turnCardsPlayed, playerTurn, phase, roundType,
				slowState);
	}

}
