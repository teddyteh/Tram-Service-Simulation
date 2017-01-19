package views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import models.FrontEndImpl;

public class ServerControlPanelGUI extends JDialog {

	private static final long serialVersionUID = 1L;
	private int onlineServerCount = 0;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ServerControlPanelGUI dialog = new ServerControlPanelGUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ServerControlPanelGUI() {
		new FrontEndImpl();
		
		setTitle("Server Control Panel");
		setSize(265, 130);
		// Hide icon
		setResizable(false);
		getContentPane().setLayout(new BorderLayout(0, 0));

		final JLabel status_label = new JLabel("Status:");
		getContentPane().add(status_label, BorderLayout.SOUTH);

		JPanel server_panel = new JPanel();
		server_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		getContentPane().add(server_panel, BorderLayout.CENTER);

		JPanel server1_panel = new JPanel();
		server_panel.add(server1_panel);
		server1_panel.setLayout(new BorderLayout(0, 0));

		final JLabel server1_label = new JLabel("Server 1");
		server1_panel.add(server1_label, BorderLayout.PAGE_START);

		final JButton server1_button = new JButton("Start");
		server1_button.addActionListener(new ActionListener() {
			int state = 0;
			Process p;

			public void actionPerformed(ActionEvent e) {
				if (state == 0) {
					try {
						p = startServer(1);
						status_label.setText("Status: Server 1 started successfully");
						server1_label.setOpaque(true);
						server1_label.setBackground(Color.GREEN);
						server1_button.setText("Stop");
						onlineServerCount++;
						state = 1;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (state == 1) {
					if ((onlineServerCount-1) < 1) {
						status_label.setText("Status: At least 1 RM has to be running");
					} else {
						p.destroy();
						server1_label.setBackground(null);
						server1_button.setText("Start");
						onlineServerCount--;
						state = 0;
					}
				}
			}
		});
		server1_panel.add(server1_button, BorderLayout.PAGE_END);

		JPanel server2_panel = new JPanel();
		server_panel.add(server2_panel);
		server2_panel.setLayout(new BorderLayout(0, 0));

		final JLabel server2_label = new JLabel("Server 2");
		server2_panel.add(server2_label, BorderLayout.PAGE_START);

		final JButton server2_button = new JButton("Start");
		server2_button.addActionListener(new ActionListener() {
			int state = 0;
			Process p;

			public void actionPerformed(ActionEvent e) {
				if (state == 0) {
					try {
						p = startServer(2);
						status_label.setText("Status: Server 2 started successfully");
						server2_label.setOpaque(true);
						server2_label.setBackground(Color.GREEN);
						server2_button.setText("Stop");
						onlineServerCount++;
						state = 1;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (state == 1) {
					if ((onlineServerCount-1) < 1) {
						status_label.setText("Status: At least 1 RM has to be running");
					} else { 
						p.destroy();
						server2_label.setBackground(null);
						server2_button.setText("Start");
						onlineServerCount--;
						state = 0;
					}	
				}
			}
		});
		server2_panel.add(server2_button, BorderLayout.PAGE_END);

		JPanel server3_panel = new JPanel();
		server_panel.add(server3_panel);
		server3_panel.setLayout(new BorderLayout(0, 0));

		final JLabel server3_label = new JLabel("Server 3");
		server3_panel.add(server3_label, BorderLayout.PAGE_START);

		final JButton server3_button = new JButton("Start");
		server3_button.addActionListener(new ActionListener() {
			int state = 0;
			Process p;

			public void actionPerformed(ActionEvent e) {
				if (state == 0) {
					try {
						p = startServer(3);
						status_label.setText("Status: Server 3 started successfully");
						server3_label.setOpaque(true);
						server3_label.setBackground(Color.GREEN);
						server3_button.setText("Stop");
						onlineServerCount++;
						state = 1;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (state == 1) {
					if ((onlineServerCount-1) < 1) {
						status_label.setText("Status: At least 1 RM has to be running");
					} else { 
						p.destroy();
						server3_label.setBackground(null);
						server3_button.setText("Start");
						onlineServerCount--;
						state = 0;
					}
				}
			}

		});
		server3_panel.add(server3_button, BorderLayout.PAGE_END);

		server1_button.doClick();
		
		// Place frame in the center of the screen
		setLocationRelativeTo(null);
	}

	public Process startServer(int server) throws IOException {
		String classesDir = new File(".").getCanonicalPath() + "\\bin\\";
		String command = "java -classpath " + classesDir + " models.TrackingService2Impl";

		switch (server) {
		case 1:
			command = "java -classpath " + classesDir + " models.TrackingServiceImpl";
			break;
		case 2:
			command = "java -classpath " + classesDir + " models.TrackingService" + 2 + "Impl";
			break;
		case 3:
			command = "java -classpath " + classesDir + " models.TrackingService" + 3 + "Impl";
			break;
		}

		Process p = Runtime.getRuntime().exec(command);

		return p;
	}
}
