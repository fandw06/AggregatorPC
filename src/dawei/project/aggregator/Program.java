package dawei.project.aggregator;


import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import dawei.project.aggregator.Util;

public class Program extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JToggleButton bLED; 
	private DigitalOutput led_;
	private JLabel lProgram;
	private JTextField tProgram; 
	private JButton bSelect;
	private JButton bSend;
	private final String programDescriptor = "Binary file for ASSIST";
	private final String programExtension = "bin";	
	private JFileChooser fc;
	private byte program[];
	
	
		
	private Profile profile;
	
	Program(Profile p){
		this.profile = p;
		this.led_ = null;
		this.setPreferredSize(new Dimension(600, 20));
		
		bLED = new JToggleButton("LED");
		bLED.setPreferredSize(new Dimension(70, 20));
		bLED.addActionListener(this);
		bLED.setActionCommand("led");
		bLED.addActionListener(this);
		
		
		
		lProgram = new JLabel("Program");
		lProgram.setPreferredSize(new Dimension(50, 20));
		lProgram.setAlignmentX(0);
		
		fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(programDescriptor, programExtension);
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File("F:\\Project_IOIO\\commands"));
		tProgram = new JTextField(null, 9);
		tProgram.setPreferredSize(new Dimension(120, 20));
		
		bSelect = new JButton("Select");
		bSelect.setPreferredSize(new Dimension(80, 20));
		bSelect.addActionListener(this);
		
		
		bSend = new JButton("Send");
		bSend.setPreferredSize(new Dimension(80, 20));
		bSend.addActionListener(this);
		
		this.setLayout(new FlowLayout());
		this.add(bLED);
		this.add(lProgram);
		this.add(tProgram);
		this.add(bSelect);
		this.add(bSend);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
			
		/*
		String s=e.getActionCommand(); 
		if (s.equals("led")) 
			profile.setLED(((JToggleButton) e.getSource()).isSelected());
			*/
		if(e.getSource() == bLED){
			profile.setLED(((JToggleButton) e.getSource()).isSelected());
			try {
				led_.write(profile.getLED());
			} catch (ConnectionLostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(e.getSource() == bSelect){
			if(fc.showOpenDialog(Program.this) == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				FileInputStream in = null;
				tProgram.setText(file.getName());
				//write the file into program array
				try {
					in = new FileInputStream(file);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				try {
					program = null;
					program = new byte[(int) file.length()];
					System.out.println(file.length());
					in.read(program);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Util.printArray("Program", program);
			}
			// not select any files
			else{
				JOptionPane.showConfirmDialog(null,
						"No file is selected!", "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			
		}
		if(e.getSource() == bSend){
			if(profile.isStreaming()){
				JOptionPane.showConfirmDialog(null, "Please stop streaming before sending program!","Warning",JOptionPane.CLOSED_OPTION,
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if(program.length==0){
				JOptionPane.showMessageDialog(null, "The selected file is empty!","Cannot Program",
						JOptionPane.WARNING_MESSAGE);
			}
			if(tProgram.getText() == null){
				JOptionPane.showMessageDialog(null, "No file is selected!","Cannot Program",
						JOptionPane.WARNING_MESSAGE);
			}
			else{
				// write command into flash
				byte addr[] = {02, 00, 00};
				try {
					profile.getFlash().sectorErase(addr);
				} catch (ConnectionLostException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					profile.getFlash().write(addr, program);
				} catch (ConnectionLostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// read back data to see if it is correct
				byte temp[] = new byte[program.length];
				try {
					profile.getFlash().read(addr, program.length);
				} catch (ConnectionLostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Util.printArray(program);
			}
		}			
	}

	public void setLed_(DigitalOutput led_) {
		this.led_ = led_;
	}
}
