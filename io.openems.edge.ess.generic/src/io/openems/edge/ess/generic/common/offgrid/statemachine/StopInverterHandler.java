package io.openems.edge.ess.generic.common.offgrid.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.statemachine.StateHandler;
import io.openems.edge.ess.generic.common.offgrid.statemachine.OffGridStateMachine.State;

public class StopInverterHandler extends StateHandler<State, OffGridContext> {

	private Instant lastAttempt = Instant.MIN;

	private final Logger log = LoggerFactory.getLogger(StopInverterHandler.class);

	@Override
	protected void onEntry(OffGridContext context) throws OpenemsNamedException {

		this.lastAttempt = Instant.now();
		System.out.println(this.lastAttempt);
	}

	@Override
	protected State runAndGetNextState(OffGridContext context) throws OpenemsNamedException {

		Instant now = Instant.now();

		log.info("last attempt - now is : " + Duration.between(this.lastAttempt, now).getSeconds());
		long waitingSeconds = 0;

		// isOngrid ?
		if (!context.getGridDetector()) {
			log.info("Going from off-grid to on-grid");
			waitingSeconds = 65;
		}

		// isOffgrid ?
		if (context.getGridDetector()) {

			log.info("Going from on-grid to off-grid");
			waitingSeconds = 3;
		}

		boolean isWaitingTimePassed = Duration.between(this.lastAttempt, now).getSeconds() > waitingSeconds;

		if (isWaitingTimePassed) {
			log.info("Inside  StopInverter handler");
			// Stop the inverter no matter what
			context.setInverterOff();
			context.setInverterOff();
			
			log.info("Is inverter on ? :" + context.stateOnOff());

			// go to grounding set step after stopping
			return State.GROUNDSET;
		} else {
			log.info("Waiting seconds " + Duration.between(this.lastAttempt, now).getSeconds() + " seconds");
			return State.STOP;
		}

	}

}
