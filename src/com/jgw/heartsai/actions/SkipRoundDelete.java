package com.jgw.heartsai.actions;

public class SkipRoundDelete extends Action {

	public static final SkipRoundDelete INSTANCE = new SkipRoundDelete();

	private SkipRoundDelete() {
	}

	@Override
	public ActionType getType() {
		return ActionType.SKIP_ROUND_DELETE;
	}

}
