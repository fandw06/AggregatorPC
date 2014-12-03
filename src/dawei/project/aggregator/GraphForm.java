package dawei.project.aggregator;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.JPanel;

public class GraphForm extends JPanel{

	private static final long serialVersionUID = 1L;
	public final int MAX_VALUE = 160;  //Max ECG value
	public final int MIN_VALUE = 90;   //Min ECG value
	public final int MAX_COUNT = 800;
	private final int VGAP = 5;         // vertical gap between waveform and frame
	private final int HGAP = 5;         // Horizontal gap between waveform and frame
	private final int NET_GAP = 50;     // gap between square nets
    public List<Integer> values;
	private static int ecgData[];
	
	public GraphForm() {
		super(null);
		this.setPreferredSize(new Dimension(800, 400));
		this.setMaximumSize(new Dimension(800, 400));
		this.setMaximumSize(new Dimension(800, 400));
		ecgData = new int[1024];
		values = Collections.synchronizedList(new ArrayList<Integer>());
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("ecg8.dat"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		for(int i = 0; i<1024; i++){
			try {
				ecgData[i] = Integer.parseInt(br.readLine());				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.setName("Waveform");
	}
	 	
	@Override
	/*
	 *  Paint the list values into graph
	 */
	public void paintComponent(Graphics g) {
		 super.paintComponent(g);	 
		 Graphics2D g2d = (Graphics2D) g;
		 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		 
		 int w = getWidth();
		 int h = getHeight();
		 double dx = (double)(w -2*HGAP) / MAX_COUNT;
		 double dy = (double)(h -2*VGAP) / (MAX_VALUE-MIN_VALUE); 
		 
		 int length = values.size();

//		 Util.printArray("value", values.toArray());
	 
		 g2d.drawLine(HGAP, VGAP, HGAP, h-VGAP);
		 g2d.drawLine(HGAP, h-VGAP, w-HGAP, h-VGAP);
		 
		 // draw nets:
		 Stroke st = g2d.getStroke();
		 Stroke bs;
		 bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 7}, 0);	 
		 g2d.setStroke(bs);     
		 // draw vertical lines
		 for(int i = 1; i<=w/NET_GAP; i++)
			 g2d.drawLine(NET_GAP*i, VGAP, NET_GAP*i, h-VGAP);
		 // draw horizontal lines
		 for(int i = 1; i<=h/NET_GAP; i++)
			 g2d.drawLine(HGAP, h-NET_GAP*i, w-HGAP, h-NET_GAP*i);
		 
		 //restore stroke
         g2d.setStroke(st);
		 for (int i = 0; i < length - 1; ++i)
			 g2d.drawLine((int)(dx*i) + HGAP, (int)(dy*(MAX_VALUE-values.get(i)) + VGAP),
					 (int)(dx*(i + 1)) + HGAP, (int)(dy*(MAX_VALUE-values.get(i+1)) + VGAP));

	 }	 
}
