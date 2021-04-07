package io.openems.edge.batteryinverter.sinexcel.statemachine;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.batteryinverter.sinexcel.Sinexcel;
import io.openems.edge.batteryinverter.sinexcel.statemachine.StateMachine.State;
import io.openems.edge.common.statemachine.StateHandler;

public class GoRunningHandler extends StateHandler<State, Context> {

	@Override
	public State runAndGetNextState(Context context) throws OpenemsNamedException {
		Sinexcel inverter = context.getParent();
		context.setclearFailureCommand();


		context.softStart(true);
		inverter.setInverterOn();

		if (inverter.getInverterState().orElse(false)) {
			// Inverter is ON
			return State.RUNNING;
		} else {
			// Still waiting
			return State.GO_RUNNING;
		}
	}

}
