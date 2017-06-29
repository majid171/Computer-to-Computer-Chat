import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Client extends JFrame{
	
	private JTextField usertext;
	private JTextArea window;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = new String();
	private String serverIP = new String();
	private Socket connection;
	
	
	public Client(String host){
		super("ChatGo Version 1 - Client");
		serverIP = host;
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
		add(new JScrollPane(window), BorderLayout.CENTER);
		setSize(500, 500);
		setVisible(true);
	}

	public void startRunning(){
		try{
			connectServer();
			setupStreams();
			whileChatting();
			
		}catch(EOFException eofException){
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			close();
		}
	}

	private void connectServer() throws IOException{
		connection  = new Socket(InetAddress.getByName(serverIP), 6789);	
		showMessage("Connected to - " + connection.getInetAddress().getHostName() + "\n");
	}

	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}
	
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\nERROR");
			}
		}while(!message.equals("END"));
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
		showMessage("\n\nYour chat with " + connection.getInetAddress().getHostName() + " has been terminated");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioexception){
			ioexception.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException ioException){
			window.append("\nERROR!");
		}
	}

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

	




}










