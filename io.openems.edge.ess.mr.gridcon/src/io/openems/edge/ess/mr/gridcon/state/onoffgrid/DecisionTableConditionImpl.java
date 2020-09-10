package io.openems.edge.ess.mr.gridcon.state.onoffgrid;

import java.util.Optional;

import io.openems.common.types.ChannelAddress;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.common.channel.BooleanReadChannel;
import io.openems.edge.common.channel.StateChannel;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.meter.api.SymmetricMeter;

public class DecisionTableConditionImpl implements DecisionTableCondition {

	private ComponentManager manager;
	private String meterId;
	private String inputNaProtection1;
	private String inputNaProtection2;
	private String inputSyncBridge;
	private boolean nA1Inverted;
	private boolean nA2Inverted;
	private boolean isSyncBridgeInverted;

	public DecisionTableConditionImpl(ComponentManager manager, String meterId,
			String inputNaProtection1, String inputNaProtection2, String inputSyncBridge, boolean isNa1Inverted,
			boolean isNa2Inverted, boolean isSyncBridgeInverted) {
		this.manager = manager;
		this.meterId = meterId;
		this.inputNaProtection1 = inputNaProtection1;
		this.inputNaProtection2 = inputNaProtection2;
		this.inputSyncBridge = inputSyncBridge;
		this.nA1Inverted = isNa1Inverted;
		this.nA2Inverted = isNa2Inverted;
		this.isSyncBridgeInverted = isSyncBridgeInverted;
	}

	@Override
	public NaProtection1On isNaProtection1On() {
		try {
			BooleanReadChannel c = manager.getChannel(ChannelAddress.fromString(inputNaProtection1));

			boolean value = c.value().get();

			if (nA1Inverted) {
				value = !value;
			}

			if (value) {
				return NaProtection1On.TRUE;
			} else {
				return NaProtection1On.FALSE;
			}
		} catch (Exception e) {
			return NaProtection1On.UNSET;
		}

	}

	@Override
	public NaProtection2On isNaProtection2On() {
		try {
			BooleanReadChannel c = manager.getChannel(ChannelAddress.fromString(inputNaProtection2));
			boolean value = c.value().get();

			if (nA2Inverted) {
				value = !value;
			}

			if (value) {
				return NaProtection2On.TRUE;
			} else {
				return NaProtection2On.FALSE;
			}
		} catch (Exception e) {
			return NaProtection2On.UNSET;
		}
	}

	@Override
	public MeterCommunicationFailed isMeterCommunicationFailed() {

		try {
			AbstractOpenemsModbusComponent meter = manager.getComponent(meterId);

			BridgeModbus modbusBridge = meter.getModbus();

			StateChannel slaveCommunicationFailedChannel = modbusBridge
					.channel(BridgeModbus.ChannelId.SLAVE_COMMUNICATION_FAILED);
			Optional<Boolean> communicationFailedOpt = slaveCommunicationFailedChannel.value().asOptional();
			// If the channel value is present and it is set then the communication is
			// broken
			if (communicationFailedOpt.isPresent()) {
				if (communicationFailedOpt.get()) {
					return MeterCommunicationFailed.TRUE;
				} else {
					return MeterCommunicationFailed.FALSE;
				}
			} else {
				return MeterCommunicationFailed.UNSET;
			}
		} catch (Exception e) {
			return MeterCommunicationFailed.UNSET;
		}
	}

	@Override
	public VoltageInRange isVoltageInRange() {
		try {
			SymmetricMeter meter = manager.getComponent(meterId);
			double voltage = meter.getVoltage().value().get() / 1000;
			if (voltage > DecisionTableCondition.LOWER_VOLTAGE && voltage < DecisionTableCondition.UPPER_VOLTAGE) {
				return VoltageInRange.TRUE;
			} else {
				return VoltageInRange.FALSE;
			}
		} catch (Exception e) {
			return VoltageInRange.UNSET;
		}

	}

	@Override
	public SyncBridgeOn isSyncBridgeOn() {
		try {
			BooleanReadChannel c = manager.getChannel(ChannelAddress.fromString(inputSyncBridge));

			boolean value = c.value().get();

			if (isSyncBridgeInverted) {
				value = !value;
			}

			if (value) {

				return SyncBridgeOn.TRUE;
			} else {
				return SyncBridgeOn.FALSE;
			}
		} catch (Exception e) {
			return SyncBridgeOn.UNSET;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Input NA Protection 1: ");
		sb.append(isNaProtection1On());
		sb.append("\n");

		sb.append("Input NA Protection 2: ");
		sb.append(isNaProtection2On());
		sb.append("\n");

		sb.append("MeterCommunicationFailed: ");
		sb.append(isMeterCommunicationFailed());
		sb.append("\n");

		sb.append("Voltage in Range: ");
		sb.append(isVoltageInRange());
		sb.append("\n");

		sb.append("Sync Bridge On: ");
		sb.append(isSyncBridgeOn());
		sb.append("\n");

		return sb.toString();
	}
	
}
