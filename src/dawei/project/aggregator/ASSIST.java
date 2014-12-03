package dawei.project.aggregator;

public class ASSIST {
	public static String projectName = "ASSIST";
	public static String versionName = "GEN1";
	public static double versionNumber = 1.0;
	
	public String ECGSamplingRate[] = {
		"0Hz  (off)",
		" 50Hz",
		"100Hz",
		"200Hz",
		"400Hz"
	};
	
	public String OzoneSamplingRate[] = {
		"0Hz  (off)",
		" 50Hz",
		"100Hz",
		"200Hz",
		"400Hz"
	};
	
	public byte sensorBitMap = 0x00;
	
	public enum PacketType{
		DATA_ECG((byte)0x00),
		DATA_OZONE((byte)0x01),
		INQUIRY_COMMAND((byte)0x01),
		SET_FREQUENCY((byte)0x02),
		GET_FREQUENCY((byte)0x03),
		GET_FREQUENCY_RES((byte)0x04),
		GET_STATUS((byte)0x03),
		TOGGLE_LED((byte)0x05);
		
		private final byte code;
		PacketType(byte n){
			this.code = n;
		}
	}
	
	public enum ChannelType{
		ECG((byte)0x00),
		OZONE((byte)0x01);
		
		private final byte code;
		ChannelType(byte n){
			this.code = n;
		}
	}
	
	
	
}
