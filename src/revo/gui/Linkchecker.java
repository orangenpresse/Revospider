package revo.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
	private JTextArea resultfield;
	private Linkchecker self = this;
	private Thread spider;
	
	public Linkchecker() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		
		JPanel north = new JPanel();
		north.setLayout(new GridLayout(1,2));
		this.add(north,BorderLayout.NORTH);
		
		urlfield = new JTextField();
		north.add(urlfield);
		
		JButton button = new JButton();
		button.setText("Scan starten");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Spider s = new Spider(urlfield.getText(), self);
				spider = new Thread(s);
				spider.start();
			}
		});
		
		north.add(button);
		
		resultfield = new JTextArea();
		this.add(new JScrollPane(resultfield), BorderLayout.CENTER);
		
		this.setBounds(0, 0, 500, 500);
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void write(String message) {
		resultfield.append(message+"\n");
		resultfield.setCaretPosition(resultfield.getDocument().getLength());
	}
}
