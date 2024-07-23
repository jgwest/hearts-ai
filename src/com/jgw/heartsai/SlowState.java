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

	private final short[] playerRoundPoints;

	public SlowState(int startingPlayerIndex, boolean heartsMayBeLed, short[] playerRoundPoints) {
		this.startingPlayerIndex = startingPlayerIndex;
		this.heartsMayBeLed = heartsMayBeLed;
		this.playerRoundPoints = playerRoundPoints;
	}

	public int getStartingPlayerIndex() {
		return startingPlayerIndex;
	}

	public boolean isHeartsMayBeLed() {
		return heartsMayBeLed;
	}

	public short[] getPlayerRoundPoints() {
		return playerRoundPoints;
	}

	public SlowState mutateStartingIndex(int newStartingIndex) {
		return new SlowState(newStartingIndex, heartsMayBeLed, playerRoundPoints);
	}

	public SlowState mutateHeartsMayBeLed(boolean newHeartsMayBeLed) {
		return new SlowState(startingPlayerIndex, newHeartsMayBeLed, playerRoundPoints);
	}

	public SlowState mutatePlayerRoundPoints(short[] newPlayerRoundPoints) {
		return new SlowState(startingPlayerIndex, heartsMayBeLed, newPlayerRoundPoints);
	}
}
