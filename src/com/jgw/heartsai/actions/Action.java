package com.jgw.heartsai.actions;

public abstract class Action {

	public static enum ActionType {
		PASS_3_CARDS,
	}

	protected Action() {
	}

	public abstract ActionType getType();

	@Override
	public String toString() {
		return getType().name();
	}

	public String prettyPrint() {
		return toString();
	}

}
