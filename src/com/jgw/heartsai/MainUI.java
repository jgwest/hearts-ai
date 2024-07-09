package com.jgw.heartsai;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.jgw.heartsai.State.Phase;
import com.jgw.heartsai.actions.Action;
import com.jgw.heartsai.util.HeartsUtil;

public class MainUI {

	private static void printUICurrentState(State state) {

		if (state.getPhase() == Phase.INITIAL) {
			throw new RuntimeException();
		}

		System.out.println("Phase: " + state.getPhase().name());

		if (state.getPhase() == Phase.PASS) {
			System.out.println("Player #: " + state.getPlayerTurn());
		}

		if (state.getPhase() == Phase.PLAY) {

			if (state.getPlayerTurn() == state.getSlowState().getStartingPlayerIndex()) {
				System.out.println("Top card: N/A");
			} else {
				Card topCard = state.getTurnCardsPlayed()[state.getSlowState().getStartingPlayerIndex()];
				System.out.println("Top card: " + topCard);
			}

		}

		CardPile playerCards = state.getPlayerCards()[state.getPlayerTurn()];
		System.out.println(cardList(playerCards.getCards()));

	}

	public static Action getPlayerAction(State state) {

		printUICurrentState(state);
		System.out.println();

		List<Action> actions = Main.generatePossibleMoves(state);

		if (actions.size() == 0) {
			HeartsUtil.throwErr("End of game?");
		}

		if (actions.size() == 1) {
			Action action = actions.get(0);
			System.out.println("* Automatically choosing action: " + action.toStringUI());
			return action;
		}

		for (int x = 0; x < actions.size(); x++) {
			Action action = actions.get(x);

			printf("%d) %s", x + 1, action.toStringUI());

		}

		int result = readValidNumericInput(1, actions.size());

		return actions.get(result - 1);

	}

	private static String cardList(List<Card> cardsParam) {
		ArrayList<Card> sortedList = new ArrayList<>(cardsParam);
		HeartsUtil.sortForUI(sortedList);

		String res = "";
		for (int x = 0; x < sortedList.size(); x++) {

			res += sortedList.get(x).toStringUI() + " ";

		}
		return res;

	}

	// -----------------------------------------------------

	private static void printf(String format, Object... args) {
		System.out.printf(format + "\n", args);
	}

	private static int readValidNumericInput(int minVal, int maxVal) {

		Integer result = null;
		while (result == null) {
			result = readNumericInput(minVal, maxVal).orElse(null);
		}

		return result;

	}

	private static Optional<Integer> readNumericInput(int minVal, int maxVal) {
		String str = readInput().trim();
		try {
			int val = Integer.parseInt(str);

			if (!(val >= minVal && val <= maxVal)) {
				System.err.println("Invalid option: [" + str + "]");
				return Optional.empty();
			}

			return Optional.of(val);

		} catch (NumberFormatException nfe) {
			return Optional.empty();
		}
	}

	public static String readInput() {
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		String line = s.nextLine();
		return line;
	}

	public static void throwErr(String str) {
		HeartsUtil.throwErr(str);
	}

}
