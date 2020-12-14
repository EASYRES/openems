package io.openems.edge.ess.mr.gridcon.state.gridconstate;

import java.time.LocalDateTime;
import java.util.BitSet;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.ChannelAddress;
import io.openems.edge.battery.soltaro.SoltaroBattery;
import io.openems.edge.common.channel.BooleanWriteChannel;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.ess.mr.gridcon.GridconPcs;
import io.openems.edge.ess.mr.gridcon.WeightingHelper;

public abstract class BaseState implements GridconStateObject {

	private ComponentManager manager;
	protected String gridconPcsId;
	protected String battery1Id;
	protected String battery2Id;
	protected String battery3Id;
	protected String hardRestartRelayAdress;

	public BaseState(ComponentManager manager, String gridconPcsId, String b1Id, String b2Id, String b3Id,
			String hardRestartRelayAdress) {
		this.manager = manager;
		this.gridconPcsId = gridconPcsId;
		this.battery1Id = b1Id;
		this.battery2Id = b2Id;
		this.battery3Id = b3Id;
		this.hardRestartRelayAdress = hardRestartRelayAdress;
	}

	protected boolean isNextStateUndefined() {
		if (getGridconPcs().isCommunicationBroken() || !isGridconDefined()) {
			System.out.println("Gridcon has no communication or is undefined!");
			return true;
		}
		if (!isAtLeastOneBatteryDefined()) {
			System.out.println("All Batteries are undefined!");
			return true;
		}
		return false;
	}

	private boolean isAtLeastOneBatteryDefined() {
		boolean undefined = true;

		if (getBattery1() != null) {
			undefined = undefined && getBattery1().isUndefined();
		}
		if (getBattery2() != null) {
			undefined = undefined && getBattery2().isUndefined();
		}
		if (getBattery3() != null) {
			undefined = undefined && getBattery3().isUndefined();
		}

		return !undefined;
	}

	private boolean isGridconDefined() {
		boolean defined = false;

		if (getGridconPcs() != null) {
			defined = !getGridconPcs().isUndefined();
		}

		return defined;
	}

	protected boolean isNextStateError() {
		if (getGridconPcs() != null && (getGridconPcs().isError() || getGridconPcs().isCommunicationBroken())) {
			return true;
		}

		if (getBattery1() != null && getBattery1().isError()) {
			return true;
		}

		if (getBattery2() != null && getBattery2().isError()) {
			return true;
		}

		if (getBattery3() != null && getBattery3().isError()) {
			return true;
		}

		return false;
	}

	protected boolean isNextStateStopped() {
		return getGridconPcs() != null && getGridconPcs().isStopped();
	}

	protected boolean isNextStateRunning() {
		return (getGridconPcs() != null && getGridconPcs().isRunning());
	}

	protected void startBatteries() {
		if (getBattery1() != null) {
			if (!getBattery1().isRunning()) {
				getBattery1().start();
			}
		}
		if (getBattery2() != null) {
			if (!getBattery2().isRunning()) {
				getBattery2().start();
			}
		}
		if (getBattery3() != null) {
			if (!getBattery3().isRunning()) {
				getBattery3().start();
			}
		}
	}

	protected boolean isBatteriesStarted() {
		boolean running = true;
		if (getBattery1() != null) {
			running = running && getBattery1().isRunning();
		}
		if (getBattery2() != null) {
			running = running && getBattery2().isRunning();
		}
		if (getBattery3() != null) {
			running = running && getBattery3().isRunning();
		}
		return running;
	}

	protected void setStringControlMode() {
		int weightingMode = WeightingHelper.getStringControlMode(getBattery1(), getBattery2(), getBattery3());
		getGridconPcs().setStringControlMode(weightingMode);
	}

	protected void setStringWeighting() {
		float activePower = getGridconPcs().getActivePowerPreset();

		Float[] weightings = WeightingHelper.getWeighting(activePower, getBattery1(), getBattery2(), getBattery3());

		getGridconPcs().setWeightStringA(weightings[0]);
		getGridconPcs().setWeightStringB(weightings[1]);
		getGridconPcs().setWeightStringC(weightings[2]);
	}

	protected void setDateAndTime() {
		int date = this.convertToInteger(this.generateDate(LocalDateTime.now()));
		getGridconPcs().setSyncDate(date);
		int time = this.convertToInteger(this.generateTime(LocalDateTime.now()));
		getGridconPcs().setSyncTime(time);
	}

	private BitSet generateDate(LocalDateTime time) {
		byte dayOfWeek = (byte) time.getDayOfWeek().ordinal();
		byte day = (byte) time.getDayOfMonth();
		byte month = (byte) time.getMonth().getValue();
		byte year = (byte) (time.getYear() - 2000); // 0 == year 2000 in the protocol

		return BitSet.valueOf(new byte[] { day, dayOfWeek, year, month });
	}

	private BitSet generateTime(LocalDateTime time) {
		byte seconds = (byte) time.getSecond();
		byte minutes = (byte) time.getMinute();
		byte hours = (byte) time.getHour();
		// second byte is unused
		return BitSet.valueOf(new byte[] { seconds, 0, hours, minutes });
	}

	private int convertToInteger(BitSet bitSet) {
		long[] l = bitSet.toLongArray();
		if (l.length == 0) {
			return 0;
		}
		return (int) l[0];
	}

	GridconPcs getGridconPcs() {
		return getComponent(gridconPcsId);
	}

	protected void setHardRestartRelay(boolean val) {
		try {
			ChannelAddress address = ChannelAddress.fromString(this.hardRestartRelayAdress);
			BooleanWriteChannel outputHardResetChannel = this.manager.getChannel(address);
			outputHardResetChannel.setNextWriteValue(val);
		} catch (OpenemsNamedException e) {
			System.out.println("Failed to set the hard reset");
		}
	}

	SoltaroBattery getBattery1() {
		return getComponent(battery1Id);
	}

	SoltaroBattery getBattery2() {
		return getComponent(battery2Id);
	}

	SoltaroBattery getBattery3() {
		return getComponent(battery3Id);
	}

	<T> T getComponent(String id) {
		T component = null;
		try {
			component = manager.getComponent(id);
		} catch (OpenemsNamedException e) {
			System.out.println(e);
		}
		return component;
	}

}
