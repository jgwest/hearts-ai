package com.jgw.heartsai.actions;

public abstract class Action {

	public static enum ActionType {
		PASS_3_CARDS, PLAY_CARD, SKIP_ROUND_DELETE
	}

	protected Action() {
	}

	public abstract ActionType getType();

	@Override
	public String toString() {
		return getType().name();
	}

	public String toStringUI() {
		return getType().name();
	}

	public String prettyPrint() {
		return toString();
	}

}
