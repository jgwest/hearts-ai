package com.jgw.heartsai.actions;

public class SkipRound extends Action {

	public static final SkipRound INSTANCE = new SkipRound();

	private SkipRound() {
	}

	@Override
	public ActionType getType() {
		return ActionType.SKIP_ROUND;
	}

}
