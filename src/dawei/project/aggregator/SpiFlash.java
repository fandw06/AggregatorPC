package dawei.project.aggregator;


import ioio.lib.api.SpiMaster;
import ioio.lib.api.exception.ConnectionLostException;

public class SpiFlash {
	private SpiMaster spi;
	private final byte WriteEnable = 0x06;
	private final byte WriteDisable = 0x04;
	private final byte ReadStatusReg = 0x05;
	private final byte WriteStatusReg = 0x01;
	private final byte ReadData = 0x03;
	private final byte FastReadData = 0x0B;
	private final byte PageProgram = 0x02;
	private final byte SectorErase = 0x20;            // 4KB
	private final byte BlockErase = (byte) 0xD8;      // 64KB
	private final byte ChipErase  = (byte) 0xC7;      // whole chip
	// private final byte PowerDown = (byte) 0xB9;
	// private final byte ReleasePowerDown = (byte) 0xAB;
	private final byte DeviceID = (byte) 0xAB;
	private final byte ManufactDeviceID = (byte) 0x90;
	
	SpiFlash(SpiMaster s){
		spi = s;
	}
	
	public byte[] combineArray(byte cmd[], byte address[], byte data[]){
		byte send[];
		if(data!= null)
			send = new byte[cmd.length+ address.length+ data.length];
		else
			send = new byte[cmd.length+ address.length];
		for(int i = 0; i<cmd.length; i++)
			send[i] = cmd[i];
		for(int i = 0; i<address.length; i++)
			send[i+cmd.length] = address[i];
		if(data!= null){
			for(int i = 0; i<data.length; i++)
				send[i+cmd.length+address.length] = data[i];
		}
		return send;
	}
	
	public byte[] getDeviceID() throws ConnectionLostException, InterruptedException{
		byte cmd[] = {DeviceID};
		byte address[] = {0, 0, 0};
		byte response[] = new byte[1];
		byte send[] = combineArray(cmd, address, null);
		
		spi.writeRead(0, send, send.length, 5, response, response.length);
		return response;
	}
	
	public byte[] getManufactDeviceID() throws ConnectionLostException, InterruptedException{
		byte cmd[] = {ManufactDeviceID};
		byte address[] = {0, 0, 0};
		byte response[] = new byte[2];
		byte send[] = combineArray(cmd, address, null);
		
		spi.writeRead(0, send, send.length, 6, response, response.length);
		return response;
	}
	
	public byte[] getStatus() throws ConnectionLostException, InterruptedException{
		byte cmd[] = {ReadStatusReg};
		byte response[] = new byte[4];
		
		spi.writeRead(0, cmd, cmd.length, 5, response, response.length);
		return response;
	}
	
	public void writeDisable() throws ConnectionLostException, InterruptedException{
		byte cmd[] = {WriteDisable};
		spi.writeRead(0, cmd, cmd.length, 1, null, 0);
	}
	
	public void writeEnable() throws ConnectionLostException, InterruptedException{
		byte cmd[] = {WriteEnable};
		spi.writeRead(0, cmd, cmd.length, 1, null, 0);
	}
	
	public byte[] read(byte addr[], int length) throws ConnectionLostException, InterruptedException{
	//	while(getStatus()[0]%2==1);
		byte cmd[] = {ReadData};
		byte response[] = new byte[length];
		byte send[] = combineArray(cmd, addr, null);
		
		spi.writeRead(0, send, send.length, length+4, response, response.length);
		return response;
	}
	
	public byte[] fastRead(byte addr[], int length) throws ConnectionLostException, InterruptedException{
		while(getStatus()[0]%2==1);
		byte cmd[] = {FastReadData};
		byte response[] = new byte[length];
		byte send[] = combineArray(cmd, addr, null);
		// there is dummy byte cycle
		spi.writeRead(0, send, send.length, length+5, response, response.length);
		return response;
	}

	/*
	 *  Warning£ºerase before writing.
	 */
	public void write(byte addr[], byte[] data) throws ConnectionLostException, InterruptedException{
	
		while(getStatus()[0]%2==1){System.out.println("busy");}
		writeEnable();
		byte cmd[] = {PageProgram};
		byte send[] = combineArray(cmd, addr, data);		
		spi.writeRead(0, send, send.length, send.length, null, 0);
	}
	
	public void writeStatus(byte[] data) throws ConnectionLostException, InterruptedException{
		writeEnable();
		byte cmd[] = {WriteStatusReg};
		byte send[] = combineArray(cmd, data, null);
		spi.writeRead(0, send, send.length, send.length, null, 0);
	}
		
	public void sectorErase(byte addr[]) throws ConnectionLostException, InterruptedException{
		writeEnable();
		byte cmd[] = {SectorErase};
		byte send[] = combineArray(cmd, addr, null);
		spi.writeRead(0, send, send.length, send.length, null, 0);
	}
	
	public void blockErase(byte addr[]) throws ConnectionLostException, InterruptedException{
		writeEnable();
		byte cmd[] = {BlockErase};
		byte send[] = combineArray(cmd, addr, null);
		spi.writeRead(0, send, send.length, send.length, null, 0);
	}
	
	public void chipErase() throws ConnectionLostException, InterruptedException{
		writeEnable();
		byte cmd[] = {ChipErase};
		spi.writeRead(0, cmd, cmd.length, cmd.length, null, 0);
	}

}
