package com.jgw.heartsai;

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
	}

	private static Action getPlayerAction(State state) {

		printUICurrentState(state);
		System.out.println();

		List<Action> actions = Main.generatePossibleMoves(state);

		return null;
	}

	// -----------------------------------------------------

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
