package io.openems.edge.controller.pbyf;

import io.openems.common.channel.Level;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.StateChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;

public interface PByFController extends Controller, OpenemsComponent {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		NO_ACTIVE_CURVE(Doc.of(Level.INFO) //
				.text("No active Curve given")), //
		SCHEDULE_PARSE_FAILED(Doc.of(Level.FAULT) //
				.text("Unable to parse Schedule")), //
		;

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}

	/**
	 * Gets the Channel for {@link ChannelId#NO_ACTIVE_CURVE}.
	 * 
	 * @return the Channel
	 */
	public default StateChannel getNoActiveCurveChannel() {
		return this.channel(ChannelId.NO_ACTIVE_CURVE);
	}

	/**
	 * Gets the Run-Failed State. See {@link ChannelId#NO_ACTIVE_CURVE}.
	 * 
	 * @return the Channel {@link Value}
	 */
	public default Value<Boolean> getNoActiveCurve() {
		return this.getNoActiveCurveChannel().value();
	}

	/**
	 * Internal method to set the 'nextValue' on {@link ChannelId#NO_ACTIVE_CURVE}
	 * Channel.
	 * 
	 * @param value the next value
	 */
	public default void _setNoActiveCurve(boolean value) {
		this.getNoActiveCurveChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#SCHEDULE_PARSE_FAILED}.
	 * 
	 * @return the Channel
	 */
	public default StateChannel getScheduleParseFailedChannel() {
		return this.channel(ChannelId.SCHEDULE_PARSE_FAILED);
	}

	/**
	 * Gets the Run-Failed State. See {@link ChannelId#SCHEDULE_PARSE_FAILED}.
	 * 
	 * @return the Channel {@link Value}
	 */
	public default Value<Boolean> getScheduleParseFailed() {
		return this.getScheduleParseFailedChannel().value();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#SCHEDULE_PARSE_FAILED} Channel.
	 * 
	 * @param value the next value
	 */
	public default void _setScheduleParseFailed(boolean value) {
		this.getScheduleParseFailedChannel().setNextValue(value);
	}

}
