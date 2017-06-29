import javax.swing.*;

public class ClientTest extends JFrame{
	public static void main(String args[]){
		
		String IP = JOptionPane.showInputDialog(null, "Enter in the IP address of the Server");

		Client test = new Client("127.0.0.1");
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.startRunning();
	}
}
