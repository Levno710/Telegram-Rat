package de.oelschner.twolf;

import javax.swing.JOptionPane;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

public class Main {

	public static final String DEFAULT_TOKEN = "YOUR_TOKEN_HERE";
  public static final String PASSWORD = "YOUR_PASSWORD_HERE";
	
	public static void main(String[] args) {
		try {
			ApiContextInitializer.init();
			TelegramBotsApi api = new TelegramBotsApi();
			api.registerBot(new Bot(DEFAULT_TOKEN));
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getClass().getName() + " : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
		}
	}
}
