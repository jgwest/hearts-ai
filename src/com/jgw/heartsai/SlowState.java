package com.jgw.heartsai;

/**
 * This class is an optimization to reduce memory usage and improve performance.
 * This class includes any objects which would otherwise be part of GameState,
 * but that don't change often enough during a game to make them worth cloning
 * for every GameState change.
 */
public class SlowState {

	private final int startingPlayerIndex;

	public SlowState(int startingPlayerIndex) {
		this.startingPlayerIndex = startingPlayerIndex;
	}

	public int getStartingPlayerIndex() {
		return startingPlayerIndex;
	}
}
