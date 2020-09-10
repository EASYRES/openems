package io.openems.edge.ess.mr.gridcon;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.ess.mr.gridcon.enums.Mode;
import io.openems.edge.ess.mr.gridcon.enums.PControlMode;
import io.openems.edge.ess.mr.gridcon.enums.ParameterSet;

/**
 * Describes functions of the gridcon pcs system.
 *
 */
public interface GridconPcs {

	public static final int MAX_POWER_PER_INVERTER = 42_000;
	public static final float DC_LINK_VOLTAGE_SETPOINT = 800f;
	public static final float Q_LIMIT = 1f;
	public static final int POWER_PRECISION_WATT = 1; // 100 TODO estimated value;
	public static final float DEFAULT_GRID_FREQUENCY = 50;
	public static final float DEFAULT_GRID_VOLTAGE = 230;

	float getMaxApparentPower();

	boolean isRunning();

	boolean isStopped();

	boolean isError();

	void setPower(int activePower, int reactivePower);

	void setStop(boolean stop);

	void setPlay(boolean play);

	void setAcknowledge(boolean acknowledge);

	void setErrorCodeFeedback(int errorCodeFeedback);

	int getErrorCode();

	float getActivePower();

	float getReactivePower();

	//	float getActivePowerInverter1();
	//	float getActivePowerInverter2();
	//	float getActivePowerInverter3();
	float getDcLinkPositiveVoltage();

	boolean isCommunicationBroken();

	void setEnableIpu1(boolean enabled);

	void setEnableIpu2(boolean enabled);

	void setEnableIpu3(boolean enabled);

	//	void setEnableIPU4(boolean enabled);
	void enableDcDc();

	void disableDcDc();

	void setParameterSet(ParameterSet set1);

		//	void setShortCircuitHAndling(boolean b);

	// ------ Methods that are important for 'grid situation'
	void setMode(Mode mode); // the mode indicates blackstart or sync approval 
	//void setSyncApproval(boolean b);
	//void setBlackStartApproval(boolean b);
	void setU0(float voltageFactor);
	void setF0(float frequencyFactor);
	// ----------------------------------------------

	void setPControlMode(PControlMode activePowerControl);

	void setQLimit(float f);

	void setPMaxChargeIpu1(float maxPower);

	void setPMaxDischargeIpu1(float maxPower);

	void setPMaxChargeIpu2(float maxPower);

	void setPMaxDischargeIpu2(float maxPower);

	void setPMaxChargeIpu3(float maxPower);

	void setPMaxDischargeIpu3(float maxPower);

	void setDcLinkVoltage(float dcLinkVoltageSetpoint);

	void setWeightStringA(Float weight);

	void setWeightStringB(Float weight);

	void setWeightStringC(Float weight);

	void setIRefStringA(Float current);

	void setIRefStringB(Float current);

	void setIRefStringC(Float current);

	void setStringControlMode(int stringControlMode);

	int getErrorCount();

	void setSyncDate(int date);

	void setSyncTime(int time);

	boolean isDcDcStarted();

	boolean isIpusStarted(boolean enableIpu1, boolean enableIpu2, boolean enableIpu3);

	void doWriteTasks() throws OpenemsNamedException;

	float getActivePowerPreset();

	double getEfficiencyLossChargeFactor();

	double getEfficiencyLossDischargeFactor();

	// Gridcon is undefined if not all relevant gridcon values are set
	boolean isUndefined();
}