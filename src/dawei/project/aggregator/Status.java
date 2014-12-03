package dawei.project.aggregator;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class Status extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	/**
	 * 	status panel 
	 */	
	private Profile profile;
	private JLabel lOzone;
	private JLabel lECG;
	private JLabel lPower;
	private JLabel lReliability;	
	private JTextField tOzone;
	private JTextField tECG;
	private JTextField tPower;
	private JTextField tReliability;

	
	Status(Profile p){
		super(null);
		this.profile = p;
			
		this.setPreferredSize(new Dimension(700, 40));
		lOzone = new JLabel("Ozone sensor");
		lOzone.setSize(20, 20);
		lECG = new JLabel("ECG");
		lPower = new JLabel("Capacity");
		lReliability = new JLabel("Data reliability");
		
		tOzone = new JTextField(" 1", 2);
		tOzone.setPreferredSize(new Dimension(30, 20));
		tOzone.setHorizontalAlignment(JTextField.RIGHT);
		tOzone.setEnabled(false);
		tECG = new JTextField(" 1", 2);
		tECG.setPreferredSize(new Dimension(30, 20));
		tECG.setHorizontalAlignment(JTextField.RIGHT);
		tECG.setEnabled(false);
		tPower = new JTextField(" 30%", 4);
		tPower.setPreferredSize(new Dimension(30, 20));
		tPower.setHorizontalAlignment(JTextField.RIGHT);
		tPower.setEnabled(false);
		tReliability = new JTextField(" 50%", 4);
		setPreferredSize(new Dimension(60, 20));
		tReliability.setHorizontalAlignment(JTextField.RIGHT);
		tReliability.setEnabled(false);


		
		this.setLayout(new FlowLayout());
		this.add(lOzone);
		this.add(tOzone);
		this.add(lECG);
		this.add(tECG);
		this.add(lPower);
		this.add(tPower);
		this.add(lReliability);
		this.add(tReliability);

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
