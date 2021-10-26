package com.jgw.heartsai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.fusesource.jansi.AnsiConsole;

import com.jgw.heartsai.Card.Suit;
import com.jgw.heartsai.State.Phase;
import com.jgw.heartsai.State.RoundType;
import com.jgw.heartsai.actions.Action;
import com.jgw.heartsai.actions.Action.ActionType;
import com.jgw.heartsai.actions.PassThreeCards;
import com.jgw.heartsai.actions.PlayCard;
import com.jgw.heartsai.actions.SkipRound;
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

	private static State doAction(Action action, State stateParam) {
		if (stateParam.getPhase() == Phase.INITIAL) {
			HeartsUtil.throwErr("Invalid state");
			return null;
		}

		State state = stateParam;

		if (state.getPhase() == Phase.PASS) {

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
					state = state.replacePlayerHand(state.getPlayerTurn(), newPlayerHand);
				}

				// Add passed cards to passed cards pile
				{
					CardPile newPlayerPassCards = CardPile.EMPTY.addCards(passThreeCards.getCards());
					state = state.replacePlayerPassCards(state.getPlayerTurn(), newPlayerPassCards);
				}

			} else {
				HeartsUtil.throwErr("Invalid action: " + action);
			}

			// Update to next player turn
			int nextPlayer = (state.getPlayerTurn() + 1) % 4;
			if (nextPlayer == state.getSlowState().getStartingPlayerIndex()) {

				for (int sourcePlayerIndex = 0; sourcePlayerIndex < state
						.getPlayerPassCards().length; sourcePlayerIndex++) {

					CardPile cardsToPass = state.getPlayerPassCards()[sourcePlayerIndex];

					int targetPlayerIndex;

					if (state.getRoundType() == RoundType.PASS_GT) {
						targetPlayerIndex = (sourcePlayerIndex + 1) % 4;

					} else if (state.getRoundType() == RoundType.PASS_LT) {
						targetPlayerIndex = (4 + sourcePlayerIndex - 1) % 4;

					} else if (state.getRoundType() == RoundType.PASS_PLUS_TWO_MOD_4) {
						targetPlayerIndex = (sourcePlayerIndex + 2) % 4;

					} else {
						HeartsUtil.throwErr("Unexpected state");
						return null;
					}

					System.out.println("pass index: " + sourcePlayerIndex + " -> " + targetPlayerIndex);

					CardPile newTargetPlayerHand = state.getPlayerCards()[targetPlayerIndex]
							.addCards(cardsToPass.getCards());

					state = state.replacePlayerHand(targetPlayerIndex, newTargetPlayerHand);

					// Clear the player pass cards
					state = state.replacePlayerPassCards(sourcePlayerIndex, CardPile.EMPTY);

				}

				if (Arrays.asList(state.getPlayerPassCards()).stream()
						.anyMatch(cards -> cards.getCards().size() != 0)) {
					HeartsUtil.throwErr(
							"Invalid player pass card size detected: " + Arrays.asList(state.getPlayerPassCards()));
					return null;
				}

				if (Arrays.asList(state.getPlayerCards()).stream().anyMatch(cards -> cards.getCards().size() != 13)) {
					HeartsUtil.throwErr("Invalid hand size detected: " + Arrays.asList(state.getPlayerCards()));
					return null;
				}

				// Add passed cards to next player's hand.
				state = state.toPhase(Phase.PLAY);
			}

			state = state.toPlayerTurn(nextPlayer);
			return state;
		}

		if (state.getPhase() == Phase.PLAY) {

			if (action.getType() != ActionType.PLAY_CARD && action.getType() != ActionType.SKIP_ROUND) {
				HeartsUtil.throwErr("Invalid action: " + action);
				return null;
			}

			if (action.getType() == ActionType.PLAY_CARD) {

				PlayCard playCardAction = (PlayCard) action;

				// Add the card to the center pile
				CardPile newCenterPile = state.getCenterPile().addCards(playCardAction.getCard());
				state = state.replaceCenterPile(newCenterPile);

				// Remove the card from the player's hand
				CardPile newPlayerCards = state.getPlayerCards()[state.getPlayerTurn()]
						.removeCards(playCardAction.getCard());
				state = state.replacePlayerPassCards(state.getPlayerTurn(), newPlayerCards);

			} else if (action.getType() == ActionType.SKIP_ROUND) {
				// No action needed.
			}

			// Update to next player turn
			int nextPlayer = (state.getPlayerTurn() + 1) % 4;

			state = state.toPlayerTurn(nextPlayer);
			return state;

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

		if (state.getPhase() == Phase.PLAY) {
			CardPile playerCards = state.getPlayerCards()[state.getPlayerTurn()];
			List<Card> cards = new ArrayList<>(playerCards.getCards());

			if (cards.size() != 0) {

				// If this is the first player, they are free to choose any card.
				if (state.getPlayerTurn() == state.getSlowState().getStartingPlayerIndex()) {

					// TODO: Strategy - playing a queen of spades on a particular player, based on
					// their point total.

					actions.addAll(generateValidPlayActions(playerCards.getCards()));

//					// If queen of spades in hand, add an action for it
//					if (playerCards.getCards().stream().anyMatch(card -> card == Card.QUEEN_OF_SPADES)) {
//						actions.add(PlayCard.PASS_CARD_ACTIONS[Card.QUEEN_OF_SPADES.getAbsoluteIndex()]);
//					}
//
//					ArrayList<Card> sortedCards = new ArrayList<Card>(playerCards.getCards().stream()
//							.filter(card -> card != Card.QUEEN_OF_SPADES).collect(Collectors.toList()));
//					Collections.shuffle(sortedCards);
//					Collections.sort(sortedCards, (a, b) -> {
//						return a.getNumberStrength() - b.getNumberStrength();
//					});
//
//					// Add highest and lowest non hearts as actions.
//					{
//						Card highestNonHeart = null;
//						Card lowestNonHeart = null;
//						List<Card> nonHearts = sortedCards.stream().filter(card -> card.getSuit() != Suit.HEARTS)
//								.collect(Collectors.toList());
//
//						if (nonHearts.size() > 0) {
//							lowestNonHeart = nonHearts.get(0);
//							highestNonHeart = nonHearts.get(nonHearts.size() - 1);
//							if (lowestNonHeart == highestNonHeart) {
//								highestNonHeart = null;
//							}
//						}
//
//						if (highestNonHeart != null) {
//							actions.add(PlayCard.PASS_CARD_ACTIONS[highestNonHeart.getAbsoluteIndex()]);
//						}
//						if (lowestNonHeart != null) {
//							actions.add(PlayCard.PASS_CARD_ACTIONS[lowestNonHeart.getAbsoluteIndex()]);
//						}
//
//					}
//
//					// Add highest and lowest non hearts as actions
//					{
//						Card highestHeart = null;
//						Card lowestHeart = null;
//
//						List<Card> hearts = sortedCards.stream().filter(card -> card.getSuit() == Suit.HEARTS)
//								.collect(Collectors.toList());
//
//						if (hearts.size() > 0) {
//							lowestHeart = hearts.get(0);
//							highestHeart = hearts.get(hearts.size() - 1);
//							if (lowestHeart == highestHeart) {
//								highestHeart = null;
//							}
//						}
//
//						if (highestHeart != null) {
//							actions.add(PlayCard.PASS_CARD_ACTIONS[highestHeart.getAbsoluteIndex()]);
//						}
//						if (lowestHeart != null) {
//							actions.add(PlayCard.PASS_CARD_ACTIONS[lowestHeart.getAbsoluteIndex()]);
//						}
//					}

				} else {

					Card topCard = state.getCenterPile().getTopCard();

					List<Card> validCardsToPlay;

					// If the player has the suit, they must match it
					if (playerCards.getCards().stream().anyMatch(card -> card.getSuit() == topCard.getSuit())) {
						validCardsToPlay = playerCards.getCards().stream()
								.filter(card -> card.getSuit() == topCard.getSuit()).collect(Collectors.toList());
					}

					// ... otherwise, they must match the top card if they can.

				}

//				hi
				// Do we have a matching suit?

			} else {
				actions.add(SkipRound.INSTANCE);
			}
		}

		return actions;
	}

	private static List<Action> generateValidPlayActions(List<Card> cardsParam) {

		List<Action> actions = new ArrayList<>();

		// If queen of spades in hand, add an action for it
		if (cardsParam.stream().anyMatch(card -> card == Card.QUEEN_OF_SPADES)) {
			actions.add(PlayCard.PASS_CARD_ACTIONS[Card.QUEEN_OF_SPADES.getAbsoluteIndex()]);
		}

		ArrayList<Card> sortedCards = new ArrayList<Card>(
				cardsParam.stream().filter(card -> card != Card.QUEEN_OF_SPADES).collect(Collectors.toList()));
		Collections.shuffle(sortedCards);
		Collections.sort(sortedCards, (a, b) -> {
			return a.getNumberStrength() - b.getNumberStrength();
		});

		// Add highest and lowest non hearts as actions.
		{
			Card highestNonHeart = null;
			Card lowestNonHeart = null;
			List<Card> nonHearts = sortedCards.stream().filter(card -> card.getSuit() != Suit.HEARTS)
					.collect(Collectors.toList());

			if (nonHearts.size() > 0) {
				lowestNonHeart = nonHearts.get(0);
				highestNonHeart = nonHearts.get(nonHearts.size() - 1);
				if (lowestNonHeart == highestNonHeart) {
					highestNonHeart = null;
				}
			}

			if (highestNonHeart != null) {
				actions.add(PlayCard.PASS_CARD_ACTIONS[highestNonHeart.getAbsoluteIndex()]);
			}
			if (lowestNonHeart != null) {
				actions.add(PlayCard.PASS_CARD_ACTIONS[lowestNonHeart.getAbsoluteIndex()]);
			}

		}

		// Add highest and lowest non hearts as actions
		{
			Card highestHeart = null;
			Card lowestHeart = null;

			List<Card> hearts = sortedCards.stream().filter(card -> card.getSuit() == Suit.HEARTS)
					.collect(Collectors.toList());

			if (hearts.size() > 0) {
				lowestHeart = hearts.get(0);
				highestHeart = hearts.get(hearts.size() - 1);
				if (lowestHeart == highestHeart) {
					highestHeart = null;
				}
			}

			if (highestHeart != null) {
				actions.add(PlayCard.PASS_CARD_ACTIONS[highestHeart.getAbsoluteIndex()]);
			}
			if (lowestHeart != null) {
				actions.add(PlayCard.PASS_CARD_ACTIONS[lowestHeart.getAbsoluteIndex()]);
			}
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
