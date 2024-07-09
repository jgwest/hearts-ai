package com.jgw.heartsai;

/**
 * This class is an optimization to reduce memory usage and improve performance.
 * This class includes any objects which would otherwise be part of State, but
 * that don't change often enough during a game to make them worth cloning for
 * every State change.
 */
public class SlowState {

	private final int startingPlayerIndex;

	private final boolean heartsMayBeLed;

	public SlowState(int startingPlayerIndex, boolean heartsMayBeLed) {
		this.startingPlayerIndex = startingPlayerIndex;
		this.heartsMayBeLed = heartsMayBeLed;
	}

	public int getStartingPlayerIndex() {
		return startingPlayerIndex;
	}

	public boolean isHeartsMayBeLed() {
		return heartsMayBeLed;
	}

	public SlowState mutateStartingIndex(int newStartingIndex) {
		return new SlowState(newStartingIndex, heartsMayBeLed);
	}

	public SlowState mutateHeartsMayBeLed(boolean heartsMayBeLed) {
		return new SlowState(startingPlayerIndex, heartsMayBeLed);
	}
}
