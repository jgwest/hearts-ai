package com.jgw.heartsai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fusesource.jansi.AnsiConsole;

import com.jgw.heartsai.State.Phase;
import com.jgw.heartsai.State.RoundType;
import com.jgw.heartsai.actions.Action;
import com.jgw.heartsai.actions.Action.ActionType;
import com.jgw.heartsai.actions.PassThreeCards;
import com.jgw.heartsai.util.HeartsUtil;

public class Main {

	public static void main(String[] args) {

		AnsiConsole.systemInstall();

//		System.out.println(Ansi.ansi().fgRed().a("Hi").reset());

		CardPile centerPile = CardPile.EMPTY;
		{
			List<Card> cards = Card.ALL_CARDS;

//			Arrays.asList(Suit.values()).forEach(suit -> {
//				IntStream.range(1, 14).forEach(val -> {
//					cards.add(new Card(val, suit));
//				});
//			});

			centerPile = centerPile.addCards(cards.toArray(new Card[cards.size()]));
		}

		centerPile = centerPile.shuffle();

		CardPile[] playerPiles = new CardPile[4];

		for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
			List<Card> playerCards = new ArrayList<>();
			centerPile = centerPile.removeCardsFromTop(13, playerCards);

			playerPiles[playerIndex] = CardPile.EMPTY.addCards(playerCards);
		}

		SlowState slowState = new SlowState(0);

		State initialState = new State(playerPiles, centerPile, new CardPile[4], 0, Phase.INITIAL, RoundType.PASS_GT,
				slowState);

		// -----------------

		State currentState = initialState;
		while (true) {

			if (currentState.getPhase() == Phase.INITIAL) {
				currentState = currentState.toPhase(Phase.PASS);
				continue;
			}

			Action selectedAction = MainUI.getPlayerAction(currentState);

			currentState = doAction(selectedAction, currentState);

//			if (currentState.getPhase() == Phase.PASS) {
//				// are we generating all combos, as with fridai?
//			}

		}

	}

	private static State doAction(Action action, State state) {
		if (state.getPhase() == Phase.INITIAL) {
			HeartsUtil.throwErr("Invalid state");
			return null;
		}

		if (state.getPhase() == Phase.PASS) {

			State newState = state;

			if (action.getType() == ActionType.PASS_3_CARDS) {

				if (state.getRoundType() == RoundType.DONT_PASS) {
					HeartsUtil.throwErr("Invalid state");
					return null;
				}

				PassThreeCards passThreeCards = (PassThreeCards) action;

				// Remove passed cards from hand
				{
					CardPile playerHand = state.getPlayerCards()[state.getPlayerTurn()];
					CardPile newPlayerHand = playerHand.removeCards(passThreeCards.getCards());
					newState = newState.replacePlayerHand(state.getPlayerTurn(), newPlayerHand);
				}

				// Add passed cards to passed cards pile
				{
					CardPile newPlayerPassCards = CardPile.EMPTY.addCards(passThreeCards.getCards());
					newState = newState.replacePlayerPassCards(state.getPlayerTurn(), newPlayerPassCards);
				}

			} else {
				HeartsUtil.throwErr("Invalid action: " + action);
			}

			// Update to next player turn
			int nextPlayer = (state.getPlayerTurn() + 1) % 4;
			if (nextPlayer == state.getSlowState().getStartingPlayerIndex()) {

				newState = state.toPhase(Phase.PLAY);

				for (int sourcePlayerIndex = 0; sourcePlayerIndex < state
						.getPlayerPassCards().length; sourcePlayerIndex++) {

					CardPile cardsToPass = state.getPlayerPassCards()[sourcePlayerIndex];

					int targetPlayerIndex;

					if (state.getRoundType() == RoundType.PASS_GT) {
						targetPlayerIndex = (nextPlayer + 1) % 4;

					} else if (state.getRoundType() == RoundType.PASS_LT) {
						targetPlayerIndex = (4 + nextPlayer - 1) % 4;

					} else if (state.getRoundType() == RoundType.PASS_PLUS_TWO_MOD_4) {
						targetPlayerIndex = (nextPlayer + 2) % 4;

					} else {
						HeartsUtil.throwErr("Unexpected state");
						return null;
					}

					CardPile newTargetPlayerHand = state.getPlayerPassCards()[targetPlayerIndex]
							.addCards(cardsToPass.getCards());

					if (newTargetPlayerHand.getCards().size() != 13) {
						HeartsUtil.throwErr("Invalid hand size: " + newTargetPlayerHand.getCards().size());
					}

					newState = state.replacePlayerHand(targetPlayerIndex, newTargetPlayerHand);

					// Clear the player pass cards
					newState = state.replacePlayerPassCards(sourcePlayerIndex, CardPile.EMPTY);

				}

				// Add passed cards to next player's hand.
			}

			newState = state.toPlayerTurn(nextPlayer);
			return newState;
		}

		if (state.getPhase() == Phase.PLAY) {

			if (action.getType() != ActionType.PLAY_CARD && action.getType() != ActionType.SKIP_ROUND) {
				HeartsUtil.throwErr("Invalid action: " + action);
			}

			if (action.getType() == ActionType.PLAY_CARD) {

			} else if (action.getType() == ActionType.SKIP_ROUND) {

			}

			// Update to next player turn
			int nextPlayer = (state.getPlayerTurn() + 1) % 4;

			// TODO: Finish this.
		}

		HeartsUtil.throwErr("Unexpected state.");
		return null;

	}

	static List<Action> generatePossibleMoves(State state) {

		List<Action> actions = new ArrayList<>();

		if (state.getPhase() == Phase.INITIAL) {
			throw new RuntimeException();
		}

		if (state.getPhase() == Phase.PASS) {

			CardPile playerCards = state.getPlayerCards()[state.getPlayerTurn()];

			List<Card> cards = new ArrayList<>(playerCards.getCards());

			// Sort ascending by points value, then number
			Collections.sort(cards, (a, b) -> {
				return (100 * (a.getPointsValue() - b.getPointsValue()))
						+ (a.getNumberStrength() - b.getNumberStrength());
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
