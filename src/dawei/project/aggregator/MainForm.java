package dawei.project.aggregator;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;
import ioio.lib.pc.SerialPortIOIOConnectionBootstrap;
import dawei.project.aggregator.Util;

/*
 *  @Data: 05/2014 
 *  @Author Dawei Fan
 *  This is the main function of Aggregator.
 */
public class MainForm extends IOIOSwingApp implements ActionListener{

	private byte ecg[];
	private byte ozone[];
	private Profile profile; 
	private Status status;
	private Program program;
	private Control control;
	private GraphForm waveform;
	
	/**
	 * 	Declare of menu variables.
	 *  	b- for button, t- for text field, l- for label;
	 *  	m- for menu, mi- for menu item;
	 *		
	 */
	private JMenu mFile; 
	private JMenu mTools; 
	private JMenu mHelp; 
	private JMenuItem miQuit; 
	private JMenuItem miDisplayHistory;
	private JMenuItem miAbout; 
	private JCheckBoxMenuItem miSaveToCSV;
	
	/**
	 * 	Members of components
	 * 	1, status panel
	 * 	2, program panel
	 * 	3, wave form panel
	 */
	protected final int MAX_VALUE = 100;
	protected final int MAX_COUNT = 200;	

	private SpiMaster spi;
	private SpiFlash flash;
	private boolean TEST_WRITEFINISH = true; 
	
	MainForm(){
		profile = new Profile();
		ecg = new byte[1024];
		ozone = new byte[256];
	}
	
	/**
	 * Adding SPI function.
	 * SpiMaster spi = ioio.openSpiMaster(misoPin, mosiPin, clkPin, ssPins, SpiMaster.Rate.RATE_1M);
	 * miso:1
	 * mosi:2
	 * clk :3
	 * ss  :4
	 */
	// Boilerplate main(). Copy-paste this code into any IOIOapplication.
	public static void main(String[] args) throws Exception {
		new MainForm().go(args);
	}

	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {

			@Override
			protected void setup() throws ConnectionLostException, InterruptedException {
				DigitalOutput led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
				spi = ioio_.openSpiMaster(1, 2, 3, 4, SpiMaster.Rate.RATE_125K);
				flash = new SpiFlash(spi);				
				profile.setSpi(spi);
				profile.setFlash(flash);
				program.setLed_(led_);

				/**
				 *  ECG data is from 010000 to 0103FF
				 *  Write Command is from 020000, several bytes
				 */
				byte[] addr = {01, 00, 00};
				if(TEST_WRITEFINISH == false){
					try {
						writeECGData(flash, spi, addr);
					} catch (IOException e) {
						e.printStackTrace();
					}
					TEST_WRITEFINISH = true;
				}

				// read test
				byte[] add = {01, 00, 00};	
				
//				byte da[] = new byte[30];
//				da = flash.read(add, 30);				
//				Util.printArray(da);
				for(int i = 0; i<4; i++){
					for(int j= 0; j<8; j++){
						add[2] += 32;
					}
					add[2] = 0;
					add[1] += 1;
				}
				
			}

			@Override
			public void loop() throws ConnectionLostException, InterruptedException {
				Thread.sleep(10);
//				System.out.println(control.readHistory.isAlive());
			}
		};
	}
	
	/*
	 * Write ECG data to SPI flash for SPI test
	 * Data size: 1KB, write 32 times as program once will write 32B
	 * Start addr1
	 * 
	 */
	public void writeECGData(SpiFlash flash, SpiMaster spi, byte[] addr) throws 
										ConnectionLostException, InterruptedException, IOException{
		
		System.out.println("Writing flash...");
		DataInputStream binFile = null;
		binFile = new DataInputStream(new FileInputStream("ecgbin.dat"));
		byte[][] ecg = new byte[32][32];

		// read data into arrays
		for(int i = 0; i<32; i++){
			for(int j = 0; j<32; j++)
				ecg[i][j] = binFile.readByte();
		}		
		binFile.close();

		/*
		 *  Erase before data write. Sector erasing will erase 4KB
		 *  Write 32 times
		 */				
		flash.sectorErase(addr);
		
		for(int i = 0; i<4; i++){
			for(int j= 0; j<8; j++){
				flash.write(addr, ecg[i*8+j]);
				addr[2] += 32;
			}
			addr[2] = 0;
			addr[1] += 1;
		}
		System.out.println("Writing finished...");
		
	}
		
	@Override
	protected Window createMainWindow(String[] args) {
		// TODO Auto-generated method stub
		JFrame f = new JFrame("GEN1 Aggregator beta"); 
		Container contentPane = f.getContentPane(); 
				
		/**
		 *  set menu
		 */
		JMenuBar mBar = new JMenuBar(); 
		mBar.setOpaque(true);

		mFile = new JMenu("File"); 
		mFile.setMnemonic(KeyEvent.VK_F); 
		miQuit = new JMenuItem("Quit"); 
		miQuit.setMnemonic(KeyEvent.VK_Q); 
		miQuit.addActionListener(this);
		mFile.add(miQuit); 

		mTools = new JMenu("Tools"); 
		mTools.setMnemonic(KeyEvent.VK_T); 
		miSaveToCSV = new JCheckBoxMenuItem("Save data to file");
		miSaveToCSV.setMnemonic(KeyEvent.VK_S);
		miSaveToCSV.addActionListener(this);
		mTools.add(miSaveToCSV);
		miDisplayHistory = new JMenuItem("Display history data");
		miDisplayHistory.setMnemonic(KeyEvent.VK_D);
		miDisplayHistory.addActionListener(this);
		mTools.add(miDisplayHistory);
		
		

		mHelp = new JMenu("Help"); 
		mHelp.setMnemonic(KeyEvent.VK_H); 
		miAbout = new JMenuItem("About");	
		miAbout.setMnemonic(KeyEvent.VK_A);
		miAbout.addActionListener(this);
		mHelp.add(miAbout);

		mBar.add(mFile); 
		mBar.add(mTools); 
		mBar.add(mHelp); 
		f.setJMenuBar(mBar); 
		
		
		waveform = new GraphForm();	
		status = new Status(profile);
		program = new Program(profile);
		control = new Control(profile, waveform, miSaveToCSV.getState());
		JPanel temp = new JPanel();
		temp.setLayout(new GridLayout(2, 1));
		temp.add(status);
		temp.add(program);
		temp.setPreferredSize(new Dimension(700, 40));
		
	
		f.setLayout(new FlowLayout());

		f.setLayout(new BorderLayout());	
		contentPane.add(control, BorderLayout.WEST);
		contentPane.add(temp, BorderLayout.CENTER);
		contentPane.add(waveform, BorderLayout.SOUTH); 

		f.setSize(720, 540);
		f.setLocation(400, 0);
		f.setResizable(true); 
		f.setVisible(true); 

		return f;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == miSaveToCSV){
			// Stop streaming before enabling data store;
			if(profile.isStreaming()&&(!control.getSaveToFile())){
				JOptionPane.showMessageDialog(null, 
						"Please stop streaming before enabling data save function.",
						"Warning",
						JOptionPane.WARNING_MESSAGE);
				miSaveToCSV.setSelected(false);
				return;
			}		
					
			control.setSaveToFile(!control.getSaveToFile());
			if(control.getSaveToFile()){
				// Create a new file to write data
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("F:\\Project_IOIO\\commands"));
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
	
					if(!fc.getSelectedFile().isFile()){
						String dir = fc.getSelectedFile().getAbsolutePath();
						try {
							Files.createFile(Paths.get(dir));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						File file = new File(dir);
						control.setDataFile(file);
					}
					else
						control.setDataFile(fc.getSelectedFile());
					// create a filewriter
					FileWriter fw = null;
					try {
						fw = new FileWriter(control.getDataFile());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					char[] test = {'D','a','t','a',':', '\n'};
					try {
						fw.write(test);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					control.setFileWriter(fw);
				}
				else{
					JOptionPane.showConfirmDialog(null, 
							"No file is selected", 
							"Warning", JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE);
					miSaveToCSV.setSelected(false);
					control.setSaveToFile(false);
				}
				
				
			}
			// control.getSaveToFile() == false
			else{
				// if is writing, close the file
				try {
					control.setSaveToFile(false);
					control.getFileWriter().close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		}
		
		if(e.getSource() == miDisplayHistory){
			if(profile.isStreaming()){
				JOptionPane.showConfirmDialog(null, 
						"Please stop streaming before displaying history data.", 
						"Warning", JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE);
				return;
			}
			// Disable write to file function and close files
			if(control.getSaveToFile()){
				control.setSaveToFile(false);
				miSaveToCSV.setSelected(false);
				try {
					control.getFileWriter().close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			control.setDisplayingHistory(true);
			// clear the current data
			waveform.values.clear();
			waveform.repaint();
			System.out.println("Clearing data...");
			// select a data file from filechooser
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("F:\\Project_IOIO\\commands"));
			if(fc.showOpenDialog(control) == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				control.setDataHistory(file);		
				control.readHistory = control.new readHistory();
				control.readHistory.start();

			}
			// not select any files
			else{
				JOptionPane.showConfirmDialog(null,
						"No file is selected!", "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			
		}
		
		if(e.getSource() == miQuit){
			System.exit(0);
		}
		if(e.getSource() == miAbout){
			JOptionPane.showMessageDialog(null, 
					"ASSIST Gen1 Aggregator v2.0\n"+ "Date: 05/17\n"+ "Author: Dawei Fan",
					"About",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
