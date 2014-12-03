package dawei.project.aggregator;

import ioio.lib.api.SpiMaster;

public class Profile {
	
	private double firmwareVersion;
	private byte firmwareIdentifier;
	private byte SamplingRateECG;
	private int numChannels;
	
	private byte statusECG;
	private byte statusOZONE;
	private boolean led;
	
	private boolean isStreaming;
	
	private SpiMaster spi;
	private SpiFlash flash;
	
	Profile(){
		firmwareVersion = 0.0d;
		firmwareIdentifier = 0;
		SamplingRateECG = 0;
		numChannels = 0;
		statusECG = 0;
		statusOZONE = 0;
		led = false;		
		isStreaming = false;
	}
	
	public double getFirmwareVersion() {
		return firmwareVersion;
	}
	public void setFirmwareVersion(double firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	public byte getFirmwareIdentifier() {
		return firmwareIdentifier;
	}
	public void setFirmwareIdentifier(byte firmwareIdentifier) {
		this.firmwareIdentifier = firmwareIdentifier;
	}
	public byte getSamplingRateECG() {
		return SamplingRateECG;
	}
	public void setSamplingRateECG(byte samplingRateECG) {
		SamplingRateECG = samplingRateECG;
	}
	public int getNumChannels() {
		return numChannels;
	}
	public void setNumChannels(int numChannels) {
		this.numChannels = numChannels;
	}
	public byte getStatusECG() {
		return statusECG;
	}
	public void setStatusECG(byte statusECG) {
		this.statusECG = statusECG;
	}
	public byte getStatusOZONE() {
		return statusOZONE;
	}
	public void setStatusOZONE(byte statusOZONE) {
		this.statusOZONE = statusOZONE;
	}
	public boolean getLED() {
		return led;
	}
	public void setLED(boolean led) {
		this.led = led;
	}

	public SpiMaster getSpi() {
		return spi;
	}

	public void setSpi(SpiMaster spi) {
		this.spi = spi;
	}

	public SpiFlash getFlash() {
		return flash;
	}

	public void setFlash(SpiFlash flash) {
		this.flash = flash;
	}

	public boolean isStreaming() {
		return isStreaming;
	}

	public void setStreaming(boolean isStreaming) {
		this.isStreaming = isStreaming;
	}
	
}

