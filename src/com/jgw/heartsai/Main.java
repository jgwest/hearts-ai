package com.jgw.heartsai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import com.jgw.heartsai.Card.Suit;
import com.jgw.heartsai.State.Phase;
import com.jgw.heartsai.State.RoundType;
import com.jgw.heartsai.actions.Action;
import com.jgw.heartsai.actions.PassThreeCards;

public class Main {

	public static void main(String[] args) {

		AnsiConsole.systemInstall();

		System.out.println(Ansi.ansi().fgRed().a("Hi").reset());

		CardPile centerPile = CardPile.EMPTY;
		{
			List<Card> cards = new ArrayList<>();

			Arrays.asList(Suit.values()).forEach(suit -> {
				IntStream.range(0, 13).forEach(val -> {
					cards.add(new Card(val, suit));
				});
			});

			centerPile = centerPile.addCards(cards.toArray(new Card[cards.size()]));
		}

		centerPile = centerPile.shuffle();

		CardPile[] playerPiles = new CardPile[4];

		for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
			List<Card> playerCards = new ArrayList<>();
			centerPile = centerPile.removeCardsFromTop(13, playerCards);

			playerPiles[playerIndex] = CardPile.EMPTY.addCards(playerCards);
		}

		State initialState = new State(playerPiles, centerPile, 0, Phase.INITIAL, RoundType.PASS_GT);

		State currentState = initialState;
		while (true) {

			if (currentState.getPhase() == Phase.INITIAL) {
				currentState = currentState.toPhase(Phase.PASS);
				continue;
			}

			if (currentState.getPhase() == Phase.PASS) {
				// are we generating all combos, as with fridai?

			}

		}

	}

	static List<Action> generatePossibleMoves(State state) {

		List<Action> actions = new ArrayList<>();

		if (state.getPhase() == Phase.INITIAL) {
			throw new RuntimeException();
		}

		// TODO: We need actions that affect the game state

		if (state.getPhase() == Phase.PASS) {

			CardPile playerCards = state.getPlayerCards()[state.getPlayerTurn()];

			List<Card> cards = new ArrayList<>(playerCards.getCards());

			// Sort ascending by number
			Collections.sort(cards, (a, b) -> {
				return a.getNumber() - b.getNumber();
			});

			// pass the lowest value cards
			PassThreeCards passLowestCards = new PassThreeCards(cards.subList(0, 3).toArray(new Card[3]));
			actions.add(passLowestCards);

			// pass the highest value cards
			PassThreeCards passHighestCards = new PassThreeCards(cards.subList(10, 13).toArray(new Card[3]));
			actions.add(passHighestCards);
		}

		return actions;
	}

	private static void doLogic(State state) {

		switch (state.getPhase()) {
		case INITIAL:
			// Create pass actions,

			break;
		case PASS:
			doLogic_passPhase(state);
			break;
		case PLAY:
			break;
		}

	}

	private static void doLogic_passPhase(State state) {
		if (state.getPlayerTurn() < 4) {

			// strategies:
			// select lowest cards, hearts and queen
			// select highest cards, hearts and queen
			// random

			switch (state.getRoundType()) {
			case DONT_PASS:
				break;
			case PASS_GT:
				break;
			case PASS_LT:
				break;
			case PASS_PLUS_TWO_MOD_4:
				break;
			default:
				break;
			}

		}

	}
}
