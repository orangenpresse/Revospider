package revo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import revo.spider.Output;
import revo.spider.Spider;

public class Linkchecker extends JFrame implements Output {
	private static final long serialVersionUID = 1L;
	private JTextField urlfield;
	private JTextField basefield;
	private JTextArea resultfield;
	private JFileChooser fc = new JFileChooser();
	private Linkchecker self = this;
	private Thread spider;
	
	
	public static void main(String[] args) throws MalformedURLException {
		new Linkchecker();
	}
	
	public Linkchecker() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		
		JPanel north = new JPanel();
		north.setLayout(new GridLayout(2,2));
		this.add(north,BorderLayout.NORTH);
		
		urlfield = new JTextField();
		urlfield.setText("http://kix.dcn.de/");
		north.add(urlfield);
		
		JButton button = new JButton();
		button.setText("Scan starten");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Spider s;
				if(fc.getSelectedFile() != null)
					s = new Spider(urlfield.getText(), self, fc.getSelectedFile(), basefield.getText());
				else
					s = new Spider(urlfield.getText(), self);
				spider = new Thread(s);
				spider.start();
			}
		});
		
		north.add(button);
		
		basefield = new JTextField();
		north.add(basefield);
		
		button = new JButton();
		button.setText("Speicherort");
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc.showOpenDialog(null);
			}
		});
		
		north.add(button);
		
		resultfield = new JTextArea();
		JScrollPane pane = new JScrollPane(resultfield);
		pane.setPreferredSize(new Dimension(900,500));
		
		this.add(pane, BorderLayout.CENTER);
		
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void write(String message) {
		resultfield.append(message+"\n");
		resultfield.setCaretPosition(resultfield.getDocument().getLength());
	}
}
