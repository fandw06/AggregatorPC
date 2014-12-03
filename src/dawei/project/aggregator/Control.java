package dawei.project.aggregator;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;








import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.pc.SerialPortIOIOConnectionBootstrap;
import ioio.lib.spi.Log;


public class Control extends JPanel implements ActionListener{
	
	private boolean isConnected;

	private boolean saveToFile;
	private JComboBox serialPorts;
	private JButton connect;
	private JButton startStreaming;
	private JButton stopStreaming;
	private boolean isDisplayingHistory;
	private File dataFile;
	private FileWriter writeData;	
	
	private File dataHistory;
	private BufferedReader historyReader;
		
	private GraphForm waveform;	
	private Profile profile;
	
	private readThread readData;
	
	public readHistory readHistory;

	Control(Profile profile, GraphForm gf, boolean save){
		super(null);
		
		this.profile = profile;
		this.waveform = gf;
		setDisplayingHistory(false);
		isConnected = false;	
		setSaveToFile(save);
		
		this.setLocation(0,0);
		
		this.setOpaque(true);
		this.setVisible(true);
		this.setFocusable(true);
		this.setPreferredSize(new Dimension(270, 80));
	/*
		List<String> result = new LinkedList<String>();
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> identifiers = CommPortIdentifier.getPortIdentifiers();
		while (identifiers.hasMoreElements()) {
			final CommPortIdentifier identifier = identifiers.nextElement();
			if (identifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (checkIdentifier(identifier)) {
					System.out.println("haha   "+identifier.getName());
					result.add(identifier.getName());
				}
			}
		}		
		String ts[] = new String[result.size()];
		ts = (String[]) result.toArray(new String[0]);
		Util.printArray(ts);	*/
		String ts[] = {"12", "23"};
		serialPorts = new JComboBox(ts);
		

		connect = new JButton("Connect");
		startStreaming = new JButton("Start");
		stopStreaming = new JButton("Stop");
		startStreaming.addActionListener(this);
		startStreaming.addActionListener(this);
		stopStreaming.addActionListener(this);
		
		serialPorts.setPreferredSize(new Dimension(130, 20));		
		connect.setPreferredSize(new Dimension(110, 20));	
		startStreaming.setPreferredSize(new Dimension(120, 20));	
		stopStreaming.setPreferredSize(new Dimension(120, 20));	
		
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(30);
		fl.setVgap(11);
		fl.setHgap(5);
		this.setLayout(fl);
		this.add(serialPorts);
		this.add(connect);
		this.add(startStreaming);
		this.add(stopStreaming);	
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readData = new readThread();
        readData.start();
	}
	
	static boolean checkIdentifier(CommPortIdentifier id) {
		/*
		if (id.isCurrentlyOwned()) {
			return false;
		}
		// The only way to find out is apparently to try to open the port...
		try {
			CommPort port = id.open(
					SerialPortIOIOConnectionBootstrap.class.getName(), 1000);
			port.close();
		} catch (PortInUseException e) {
			return false;
		}
		*/
		return true;
	}
		
    class readThread extends Thread {
 
        readThread() {
 //       	System.out.println("Starting Thread...");
        }

        public void run() {
  //      	System.out.println("In thread...");
        	byte addr[] = {01, 00, 00};
        	int i = 0; 
        	int j = 0;
        	byte val[] = new byte[1];
        	int unsigned = 0;
        	while(true){

        		try {
        			while(!profile.isStreaming()){
        				Thread.sleep(20);
        		//		System.out.println("我出不来了！");
        			};
					val = profile.getFlash().read(addr, 1);
					if(val[0]<0)
						unsigned = val[0] + 256;
					else
						unsigned = val[0];

				} catch (ConnectionLostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}       		
        		
        		
        		// Enqueue the data to display
        		if (waveform.values.size() > waveform.MAX_COUNT) 
        			waveform.values.remove(0);
        		waveform.values.add(unsigned);
        		waveform.repaint();
        		
        		// If save to file, write data to a file
        		// Now just write the data read from spi. In reality, checking the header first
        		if(saveToFile){
        			try {
						writeData.write(Integer.toString(unsigned));
						writeData.write('\n');
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
        		}
        		++i;
        		addr[2] = (byte) (addr[2]+1);
        		if(i == 255){
        			addr[2] = (byte) (0);
        			addr[1] = (byte)(addr[1]+1);
        			i = 0;
        			j++;
        		}
        		if(j == 4){
        			addr[1] = 0;
        			addr[2] = 0;
        			j = 0;
        		}
        		      		
        		try {
        			Thread.sleep(10);
        		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }
    }
	
    class readHistory extends Thread {

        readHistory(){
        }

        public void run() {
        	
        	try {
				historyReader = new BufferedReader(new FileReader(dataHistory));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	int d = 0;
        	try {
				while(historyReader.ready()){
					d = Integer.parseInt(historyReader.readLine());
					if (waveform.values.size() > waveform.MAX_COUNT) 
						waveform.values.remove(0);
					waveform.values.add(d);
					waveform.repaint();
					Thread.sleep(10);     		
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }

    }
    
    
    
	public FileWriter getFileWriter(){
		return writeData;
	}
	
	public void setFileWriter(FileWriter fw){
		writeData = fw;
	}
	
	public void paintComponent (Graphics g){
		super.paintComponent(g);
	}

	public boolean getSaveToFile() {
		return saveToFile;
	}

	public void setSaveToFile(boolean saveToFile) {
		this.saveToFile = saveToFile;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == connect){
			System.out.println("Connected");
		}
		if(e.getSource() == startStreaming){
			if(isDisplayingHistory){
				isDisplayingHistory = false;
				waveform.values.clear();
				waveform.repaint();
			}
			profile.setStreaming(true);
		}
		if(e.getSource() == stopStreaming){
			profile.setStreaming(false);
		}
	}

	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	public File getDataHistory() {
		return dataHistory;
	}

	public void setDataHistory(File dataHistory) {
		this.dataHistory = dataHistory;
	}

	public BufferedReader getHistoryReader() {
		return historyReader;
	}

	public void setHistoryReader(BufferedReader historyReader) {
		this.historyReader = historyReader;
	}

	public boolean isDisplayingHistory() {
		return isDisplayingHistory;
	}

	public void setDisplayingHistory(boolean isDisplayingHistory) {
		this.isDisplayingHistory = isDisplayingHistory;
	}
}
