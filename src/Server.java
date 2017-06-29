import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
	
	private JTextField usertext;
	private JTextArea window;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server(){
		super("ChatGo Version1 - Server");
		usertext = new JTextField();
		usertext.setEditable(false);
		usertext.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						usertext.setText("");
					}
				}
		);
		add(usertext, BorderLayout.NORTH);
		window = new JTextArea();
		add(new JScrollPane(window));
		setSize(500, 500);
		setVisible(true);
	}

	public void startRunning(){
		try{
			server = new ServerSocket(6789, 100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException){
					
				}finally{
					close();
				}
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		
	}

	private void waitForConnection() throws IOException{
		connection = server.accept();
		showMessage("Connected to - " + connection.getInetAddress().getHostName() + "\n");
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}
	
	
	private void whileChatting() throws IOException{
		String message = "";
		ableToType(true);
		while(true){
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
				
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("ERROR\n");
			}
			
		}
	}
	
	// Shows message to GUI
	private void showMessage(final String message){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						window.append(message);
					}
				}
		);
		
	}
	
	private void close(){
		showMessage("\n\nYour chat with " + connection.getInetAddress().getHostName() + " has been terminated\n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioexception){
			ioexception.printStackTrace();
		}
	}
	
	// TODO add JButton to end chat instead of typing <END> to end the chat and close all connections
	
	private void ableToType(final boolean choice){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					usertext.setEditable(choice);
					window.setEditable(choice);
				}
			}
		);
	}

	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ieException){
			window.append("\nCAN NOT SEND MESSAGE\n");
		}
	}
}
