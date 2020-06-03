package de.oelschner.twolf;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Window.Type;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ChatFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;

	public ChatFrame(long chatId, Bot bot) {
		setTitle("Chat with " + chatId);
		setType(Type.UTILITY);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 670, 502);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!textField.getText().isBlank()) {
					try {
						bot.sendMessage(new SendMessage().setChatId(chatId).setText(textField.getText()));
						addText("You : " + textField.getText() + "\n");
					} catch (TelegramApiException e1) {
						e1.printStackTrace();
					}
				}
				textField.setText("");
			}
		});
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.getText().isBlank()) {
					try {
						bot.sendMessage(new SendMessage().setChatId(chatId).setText(textField.getText()));
						addText("You : " + textField.getText() + "\n");
					} catch (TelegramApiException e1) {
						e1.printStackTrace();
					}
				}
				textField.setText("");
			}
		});
		panel.add(btnNewButton, BorderLayout.EAST);
		setVisible(true);
	}
	
	public void addText(String text) {
		setText(getText() + text);
	}
	
	public void setText(String text) {
		textArea.setText(text);
	}
	
	public String getText() {
		return textArea.getText();
	}

}
