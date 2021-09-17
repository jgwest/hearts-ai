package com.jgw.heartsai;

public final class State {

	enum RoundType {
		PASS_LT, PASS_GT, PASS_PLUS_TWO_MOD_4, DONT_PASS
	}

	enum Phase {
		INITIAL, PASS, PLAY
	};

	private final RoundType roundType;
	private final CardPile[] playerCards;
	private final CardPile centerPile;
	private final Phase phase;
	private final int playerTurn;

	public State(CardPile[] playerCards, CardPile centerPile, int playerTurn, Phase phase, RoundType roundType) {
		this.playerCards = playerCards;
		this.centerPile = centerPile;
		this.playerTurn = playerTurn;
		this.phase = phase;
		this.roundType = roundType;
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

	public CardPile getCenterPile() {
		return centerPile;
	}

	public State toPhase(Phase newPhase) {
		State newState = new State(playerCards, centerPile, playerTurn, newPhase, roundType);
		return newState;
	}

	public State toPlayerTurn(int newPlayerTurn) {
		State newState = new State(playerCards, centerPile, newPlayerTurn, phase, roundType);

		return newState;
	}
}
