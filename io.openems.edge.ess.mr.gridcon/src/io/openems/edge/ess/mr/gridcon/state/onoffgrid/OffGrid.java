package io.openems.edge.ess.mr.gridcon.state.onoffgrid;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.ess.mr.gridcon.GridconPcs;
import io.openems.edge.ess.mr.gridcon.IState;
import io.openems.edge.ess.mr.gridcon.WeightingHelper;
import io.openems.edge.ess.mr.gridcon.enums.Mode;

public class OffGridRunning extends BaseState {

	private float targetFrequencyOffgrid;

	public OffGridRunning(ComponentManager manager, DecisionTableCondition condition, String gridconPcsId, String b1Id,
			String b2Id, String b3Id, String inputNa1, String inputNa2, String inputSyncBridge, String outputSyncBridge,
			String meterId, float targetFrequencyOffgrid, boolean na1Inverted, boolean na2Inverted,
			boolean inputSyncInverted) {
		super(manager, condition, gridconPcsId, b1Id, b2Id, b3Id, inputNa1, inputNa2, inputSyncBridge, outputSyncBridge,
				meterId, na1Inverted, na2Inverted);
		this.targetFrequencyOffgrid = targetFrequencyOffgrid;
	}

	@Override
	public IState getState() {
		return OnOffGridState.OFF_GRID_RUNNING_MODE;
	}

	@Override
	public IState getNextState() {

		if (DecisionTableHelper.isUndefined(condition)) {
			return OnOffGridState.UNDEFINED;
		}

		if (DecisionTableHelper.isOffGridGridBack(condition)) {
			return OnOffGridState.OFF_GRID_MODE_GRID_BACK;
		}

		if (DecisionTableHelper.isAdjustParameters(condition)) {
			return OnOffGridState.OFF_GRID_MODE_ADJUST_PARMETER;
		}

		if (DecisionTableHelper.isOffGridRunningMode(condition)) {
			return OnOffGridState.OFF_GRID_RUNNING_MODE;
		}

		return OnOffGridState.OFF_GRID_RUNNING_MODE;
	}

	@Override
	public void act() throws OpenemsNamedException {
		float factor = targetFrequencyOffgrid / GridconPcs.DEFAULT_GRID_FREQUENCY;
		getGridconPcs().setF0(factor);

		getGridconPcs().setBlackStartApproval(true);
		getGridconPcs().setSyncApproval(false);
		getGridconPcs().setModeSelection(Mode.VOLTAGE_CONTROL);

		// Set weighting to strings, use a fix value for active power because in
		// off grid mode it is always discharging
		float activePower = 1000;
		Float[] weightings = WeightingHelper.getWeighting(activePower, getBattery1(), getBattery2(), getBattery3());

		getGridconPcs().setWeightStringA(weightings[0]);
		getGridconPcs().setWeightStringB(weightings[1]);
		getGridconPcs().setWeightStringC(weightings[2]);

		try {
			getGridconPcs().doWriteTasks();
		} catch (OpenemsNamedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
