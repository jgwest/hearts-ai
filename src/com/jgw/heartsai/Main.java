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
import com.jgw.heartsai.util.HeartsUtil;

public class Main {

	private static final List<Integer> playerIndicesList = Arrays.asList(0, 1, 2, 3);

	public static void main(String[] args) {

		AnsiConsole.systemInstall();

//		CardPile centerPile = CardPile.EMPTY;
//		{
//			List<Card> cards = Card.ALL_CARDS;
//
//			centerPile = centerPile.addCards(cards.toArray(new Card[cards.size()]));
//		}
//
//		centerPile = centerPile.shuffle();
//
//		CardPile[] playerPiles = new CardPile[4];
//
//		for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
//			List<Card> playerCards = new ArrayList<>();
//			centerPile = centerPile.removeCardsFromTop(13, playerCards);
//
//			playerPiles[playerIndex] = CardPile.EMPTY.addCards(playerCards);
//		}
//
//		// TODO: I have player round points, but I need player game points
//
//		SlowState slowState = new SlowState(0, false, new short[4]);
//
//		State initialState = new State(playerPiles, /* centerPile, */new CardPile[4], new Card[4], 0, Phase.INITIAL,
//				RoundType.PASS_GT, slowState);

		State initialState = startNewHand(null);

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

	private static State doPassPhaseActions(Action action, State stateParam) {

		int playerTurn = stateParam.getPlayerTurn();

		StateBuilder state = stateParam.mutate();

		if (action.getType() != ActionType.PASS_3_CARDS) {
			HeartsUtil.throwErr("Invalid action: " + action);
			return null;
		}

		if (stateParam.getRoundType() == RoundType.DONT_PASS) {
			HeartsUtil.throwErr("Invalid state");
			return null;
		}

		PassThreeCards passThreeCards = (PassThreeCards) action;

		// Remove passed cards from hand
		{
			CardPile playerHand = stateParam.getPlayerCards()[playerTurn];
			CardPile newPlayerHand = playerHand.removeCards(passThreeCards.getCards());
			state = state.replacePlayerHand(playerTurn, newPlayerHand);
		}

		// Add passed cards to passed cards pile
		{
			CardPile newPlayerPassCards = CardPile.EMPTY.addCards(passThreeCards.getCards());
			state = state.replacePlayerPassCards(playerTurn, newPlayerPassCards);
		}

		// Update to next player turn
		int nextPlayer = (playerTurn + 1) % 4;
		state = state.setPlayerTurn(nextPlayer);

		if (nextPlayer == stateParam.getSlowState().getStartingPlayerIndex()) {

//			// Pass all four players cards to the appropriate receiver
//			for (int sourcePlayerIndex = 0; sourcePlayerIndex < 4; sourcePlayerIndex++) {
//
//				CardPile cardsToPass = stateParam.getPlayerPassCards()[sourcePlayerIndex];
//
//				if (cardsToPass.getCards().size() == 0 && stateParam.getRoundType() != RoundType.DONT_PASS) {
//					HeartsUtil.throwErr("Player provided no cards to pass: " + cardsToPass);
//					return null;
//				}
//
//				int targetPlayerIndex;
//
//				if (stateParam.getRoundType() == RoundType.PASS_GT) {
//					targetPlayerIndex = (sourcePlayerIndex + 1) % 4;
//
//				} else if (stateParam.getRoundType() == RoundType.PASS_LT) {
//					targetPlayerIndex = (4 + sourcePlayerIndex - 1) % 4;
//
//				} else if (stateParam.getRoundType() == RoundType.PASS_PLUS_TWO_MOD_4) {
//					targetPlayerIndex = (sourcePlayerIndex + 2) % 4;
//
//				} else {
//					HeartsUtil.throwErr("Unexpected state");
//					return null;
//				}
//
//				System.out.println("pass index: " + sourcePlayerIndex + " -> " + targetPlayerIndex);
//
//				CardPile newTargetPlayerHand = stateParam.getPlayerCards()[targetPlayerIndex]
//						.addCards(cardsToPass.getCards());
//
//				state = state.replacePlayerHand(targetPlayerIndex, newTargetPlayerHand);
//
//				// Clear the player pass cards
//				state = state.replacePlayerPassCards(sourcePlayerIndex, CardPile.EMPTY);
//
//			}
//
//			// Add passed cards to next player's hand.
//			state = state.setPhase(Phase.PLAY);
//			phaseChange = true;

			return finalizePassRound(state.build());

		}

		State finalState = state.build();
		return finalState;

	}

	private static State finalizePassRound(State stateParam) {

		StateBuilder state = stateParam.mutate();

		boolean twoOfClubsFound = false;

		// Pass all four players cards to the appropriate receiver
		for (int sourcePlayerIndex = 0; sourcePlayerIndex < 4; sourcePlayerIndex++) {

			CardPile cardsToPass = stateParam.getPlayerPassCards()[sourcePlayerIndex];

			if (cardsToPass.getCards().size() == 0 && stateParam.getRoundType() != RoundType.DONT_PASS) {
				HeartsUtil.throwErr("Player provided no cards to pass: " + cardsToPass);
				return null;
			}

			int targetPlayerIndex;

			if (stateParam.getRoundType() == RoundType.PASS_GT) {
				targetPlayerIndex = (sourcePlayerIndex + 1) % 4;

			} else if (stateParam.getRoundType() == RoundType.PASS_LT) {
				targetPlayerIndex = (4 + sourcePlayerIndex - 1) % 4;

			} else if (stateParam.getRoundType() == RoundType.PASS_PLUS_TWO_MOD_4) {
				targetPlayerIndex = (sourcePlayerIndex + 2) % 4;

			} else {
				HeartsUtil.throwErr("Unexpected state");
				return null;
			}

			System.out.println("pass index: " + sourcePlayerIndex + " -> " + targetPlayerIndex);

			CardPile newTargetPlayerHand = stateParam.getPlayerCards()[targetPlayerIndex]
					.addCards(cardsToPass.getCards());

			if (newTargetPlayerHand.getCards().contains(Card.TWO_OF_CLUBS)) {
				state = state.setSlowState(stateParam.getSlowState().mutateStartingIndex(targetPlayerIndex));
				state = state.setPlayerTurn(targetPlayerIndex);
				if (twoOfClubsFound) {
					HeartsUtil.throwErr("Two of clubs found twice");
					return null;
				}
				twoOfClubsFound = true;
			}

			state = state.replacePlayerHand(targetPlayerIndex, newTargetPlayerHand);

			// Clear the player pass cards
			state = state.replacePlayerPassCards(sourcePlayerIndex, CardPile.EMPTY);

		}

		// Add passed cards to next player's hand.
		state = state.setPhase(Phase.PLAY);

		State finalState = state.build();

		if (!twoOfClubsFound) {
			HeartsUtil.throwErr("Unable to locate two of clubs at end of pass phase.");
			return null;
		}

		// If we switch to 'play' phase, make sure our invariants are still true.
		if (Arrays.asList(finalState.getPlayerPassCards()).stream().anyMatch(cards -> cards.getCards().size() != 0)) {
			HeartsUtil.throwErr(
					"Invalid player pass card size detected: " + Arrays.asList(finalState.getPlayerPassCards()));
			return null;
		}

		if (Arrays.asList(finalState.getPlayerCards()).stream().anyMatch(cards -> cards.getCards().size() != 13)) {
			HeartsUtil.throwErr("Invalid hand size detected: " + Arrays.asList(finalState.getPlayerCards()));
			return null;
		}

		return finalState;
	}

	private static State doAction(Action action, State stateParam) {
		if (stateParam.getPhase() == Phase.INITIAL) {
			HeartsUtil.throwErr("Invalid state");
			return null;
		}

		if (stateParam.getPhase() == Phase.PASS) {
			return doPassPhaseActions(action, stateParam);
		}

		if (stateParam.getPhase() == Phase.PLAY) {
			int playerTurn = stateParam.getPlayerTurn();

			StateBuilder state = stateParam.mutate();

			if (action.getType() != ActionType.PLAY_CARD/* && action.getType() != ActionType.SKIP_ROUND */) {
				HeartsUtil.throwErr("Invalid action: " + action);
				return null;
			}

			Card cardPlayed = null;

			if (action.getType() == ActionType.PLAY_CARD) {

				PlayCard playCardAction = (PlayCard) action;

				// Add the card to the player's played cards this turn
				Card[] newTurnCardsPlayed = new Card[4];
				System.arraycopy(stateParam.getTurnCardsPlayed(), 0, newTurnCardsPlayed, 0, 4);
				if (newTurnCardsPlayed[playerTurn] != null) {
					HeartsUtil.throwErr("Invalid action: " + action);
					return null;
				}
				newTurnCardsPlayed[playerTurn] = playCardAction.getCard();
				state = state.setTurnCardsPlayed(newTurnCardsPlayed);

				// Remove the card from the player's hand
				CardPile newPlayerCards = stateParam.getPlayerCards()[playerTurn].removeCards(playCardAction.getCard());
				state = state.replacePlayerHand(playerTurn, newPlayerCards);

				cardPlayed = playCardAction.getCard();

//			} else if (action.getType() == ActionType.SKIP_ROUND) {
//				// No action needed.
			} else {
				HeartsUtil.throwErr("Unexpected action: " + action);
				return null;
			}

			int nextPlayerTurn = (playerTurn + 1) % 4;
			if (nextPlayerTurn == stateParam.getSlowState().getStartingPlayerIndex()) {
				// The turn has ended

				if (cardPlayed != null && (cardPlayed.getSuit() == Suit.HEARTS || cardPlayed == Card.QUEEN_OF_SPADES)) {
					state = state.setSlowState(stateParam.getSlowState().mutateHeartsMayBeLed(true));
				}

				State finalState = state.build();

				return completeRound(finalState);

			} else {
				// Update to next player turn
				state = state.setPlayerTurn(nextPlayerTurn);
				if (cardPlayed != null && (cardPlayed.getSuit() == Suit.HEARTS || cardPlayed == Card.QUEEN_OF_SPADES)) {
					state = state.setSlowState(stateParam.getSlowState().mutateHeartsMayBeLed(true));
				}
			}

			State finalState = state.build();

			// Verify our state invariant has not changed.
			if (cardPlayed != null) {
				if (stateParam.getPlayerCards()[playerTurn].getCards().size()
						- 1 != finalState.getPlayerCards()[playerTurn].getCards().size()) {
					HeartsUtil.throwErr("Player played a card, but their hand size did not go down.");
					return null;
				}

				if (finalState.getTurnCardsPlayed()[playerTurn] != cardPlayed) {
					HeartsUtil.throwErr("A card was played, but turnsCardsPlayed was not updated.");
					return null;
				}

			}

			return finalState;

		}

		HeartsUtil.throwErr("Unexpected state.");
		return null;

	}

	private static State startNewHand(State inputState) {

		System.out.println("-------- Starting new hand --------");

		// Shuffle cards to players
		CardPile centerPile = CardPile.EMPTY;
		{
			List<Card> cards = Card.ALL_CARDS;

			centerPile = centerPile.addCards(cards.toArray(new Card[cards.size()]));
		}

		centerPile = centerPile.shuffle();

		CardPile[] playerPiles = new CardPile[4];

		for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
			List<Card> playerCards = new ArrayList<>();
			centerPile = centerPile.removeCardsFromTop(13, playerCards);

			playerPiles[playerIndex] = CardPile.EMPTY.addCards(playerCards);
		}

		// TODO: I have player round points, but I need player game points

		SlowState slowState = null;
		if (inputState != null) {
			slowState = inputState.getSlowState();
		} else {
			slowState = new SlowState(0, false, new short[4]);
		}

		slowState = slowState.mutateStartingIndex(0);
		slowState = slowState.mutateHeartsMayBeLed(false);
		slowState = slowState.mutatePlayerRoundPoints(new short[4]);

		// Increment the round type, or start with PASS_LT
		RoundType roundType = RoundType.PASS_LT;
		if (inputState != null) {
			roundType = RoundType.values()[(inputState.getRoundType().ordinal() + 1) % RoundType.values().length];
		}

		State initialState = new State(playerPiles, new CardPile[4], new Card[4], 0, Phase.INITIAL, roundType,
				slowState);

		return initialState;
	}

	private static State completeRound(State inputState) {

		// - Look at the cards that each player has played
		// - Find the player that played the highest card matching the suit of the first
		// card played
		// - Give them the points
		// - They start the turn next
		SlowState newSlowState;
		{
			Card firstCardPlayed = inputState.getTurnCardsPlayed()[inputState.getSlowState().getStartingPlayerIndex()];

			int trickWonByPlayerIndex = playerIndicesList.stream().filter(index -> {
				// Only players that played a matching suit
				Card lambdaCardPlayed = inputState.getTurnCardsPlayed()[index];
				return lambdaCardPlayed != null && lambdaCardPlayed.getSuit() == firstCardPlayed.getSuit();
				// Find player that played the highest matching card
			}).sorted((a, b) -> inputState.getTurnCardsPlayed()[b].getNumberStrength()
					- inputState.getTurnCardsPlayed()[a].getNumberStrength()).findFirst().get();

			int pointsValue = Arrays.asList(inputState.getTurnCardsPlayed()).stream().map(card -> card.getPointsValue())
					.reduce((a, b) -> a + b).get();

			short[] newPlayerRoundPoints = new short[4];
			System.arraycopy(inputState.getSlowState().getPlayerRoundPoints(), 0, newPlayerRoundPoints, 0,
					newPlayerRoundPoints.length);
			newPlayerRoundPoints[trickWonByPlayerIndex] += pointsValue;

			newSlowState = inputState.getSlowState().mutatePlayerRoundPoints(newPlayerRoundPoints)
					.mutateStartingIndex(trickWonByPlayerIndex);
		}

		State completedRoundState = inputState.mutate().setSlowState(newSlowState).setTurnCardsPlayed(new Card[4])
				.setPlayerTurn(newSlowState.getStartingPlayerIndex()).build();

		System.out.println("-------- New Round -------------------");

		if (Arrays.asList(inputState.getPlayerCards()).stream().map(cards -> cards.getCards().size())
				.reduce((a, b) -> a + b).get() == 0) {
			// Hand ends

			return startNewHand(completedRoundState);

		} else {
			// Hand continues
			return completedRoundState;

		}

	}

	static List<Action> generatePossibleMoves(State state) {

		List<Action> actions = new ArrayList<>();

		if (state.getPhase() == Phase.INITIAL) {
			throw new RuntimeException();
		}

		else if (state.getPhase() == Phase.PASS) {

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

		else if (state.getPhase() == Phase.PLAY) {
			CardPile playerCards = state.getPlayerCards()[state.getPlayerTurn()];
			List<Card> cards = new ArrayList<>(playerCards.getCards());

			// TODO: Implement this rule: However, if a player has no clubs when the first
			// trick is led, a heart or the queen of spades cannot be discarded.

			if (cards.size() != 0) {

//				boolean firstRound = Arrays.asList(state.getTurnCardsPlayed()).stream().allMatch(card -> card == null);

				// If this is the first player, and this is the start of a new round, then they
				// are free to choose any card (but may not be able to lead hearts)
				if (/*
					 * state.getCenterPile().getCards().size() == 0 &&
					 */ state.getPlayerTurn() == state.getSlowState().getStartingPlayerIndex()) {

					// TODO: Strategy - playing a queen of spades on a particular player, based on
					// their point total.

					actions.addAll(
							generateValidPlayActions(playerCards.getCards(), state.getSlowState().isHeartsMayBeLed()));

				} else {
					// Second, third, or fourth player, by turn order

					Card topCard = state.getTurnCardsPlayed()[state.getSlowState().getStartingPlayerIndex()];
					if (topCard == null) {
						HeartsUtil.throwErr("No top card found for state: " + state);
						return null;
					}

					List<Card> validCardsToPlay;

					// If the player has the suit, they must match it
					if (playerCards.getCards().stream().anyMatch(card -> card.getSuit() == topCard.getSuit())) {
						validCardsToPlay = playerCards.getCards().stream()
								.filter(card -> card.getSuit() == topCard.getSuit()).collect(Collectors.toList());
					} else {
						// otherwise, they can play anything
						validCardsToPlay = playerCards.getCards();
					}

					// 2nd, 3rd, and 4th player have the option of playing hearts (if they are not
					// suit restricted)
					actions.addAll(generateValidPlayActions(validCardsToPlay, true));

				}

			} else {
				HeartsUtil.throwErr("Unexpected state");
			}
//			else {
//				actions.add(SkipRound.INSTANCE);
//			}

		} else {
			HeartsUtil.throwErr("Unexpected state.");
		}

		return actions;
	}

	private static List<Action> generateValidPlayActions(List<Card> cardsParam, boolean mayPlayHearts) {

		List<Action> actions = new ArrayList<>();

		// Must play two of clubs, if it is present.
		if (cardsParam.stream().anyMatch(card -> card == Card.TWO_OF_CLUBS)) {
			actions.add(PlayCard.PASS_CARD_ACTIONS[Card.TWO_OF_CLUBS.getAbsoluteIndex()]);
			return actions;
		}

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

		// Add highest and lowest hearts as actions
		if (mayPlayHearts) {
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

//	private static void doLogic(State state) {
//
//		switch (state.getPhase()) {
//		case INITIAL:
//			// Create pass actions,
//
//			break;
//		case PASS:
//			doLogic_passPhase(state);
//			break;
//		case PLAY:
//			break;
//		}
//
//	}

//	private static void doLogic_passPhase(State state) {
//		if (state.getPlayerTurn() < 4) {
//
//			// strategies:
//			// select lowest cards, hearts and queen
//			// select highest cards, hearts and queen
//			// random
//
//			switch (state.getRoundType()) {
//			case DONT_PASS:
//				break;
//			case PASS_GT:
//				break;
//			case PASS_LT:
//				break;
//			case PASS_PLUS_TWO_MOD_4:
//				break;
//			default:
//				break;
//			}
//
//		}
//
//	}
}
