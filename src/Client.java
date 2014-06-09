/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.BindingFactory;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;

public class Client implements ActionListener, ClientInterface, Serializable {
	private static final long serialVersionUID = 1L;

	Graphics g;
	JFrame frame;
	JTextArea area;
	Dimension dim;
	public static JPanel panel;
	MyCanvas paintCanvas;
	JScrollPane chatPane, userList;
	JTextField inputField;
	JButton sendButton;
	BindingFactory bf;
	RelativeConstraints chatPaneConstraints, paintCanvasConstraints,
			inputFieldConstraints, userListConstraints, sendButtonConstrains;

	int x1, y1, x2, y2;

	static int roomNo;
	static ServerInterface serverObj;
	public static Client object;
	JTextArea users;
	public static String userName;

	public Client() {

		area = new JTextArea();
		users = new JTextArea();
		area.setEditable(false);
		area.setBackground(new Color(255, 255, 153));
		frame = new JFrame("Discussion Room");
		panel = new JPanel(new RelativeLayout());
		chatPane = new JScrollPane(area);
		userList = new JScrollPane(users);
		paintCanvas = new MyCanvas();
		inputField = new JTextField();
		sendButton = new JButton("Send");
		bf = new BindingFactory(10, 10, 10, 10, 10, 10);
		dim = Toolkit.getDefaultToolkit().getScreenSize();
	}

	public void initializeLayout() {
		Binding leftEdge = bf.leftEdge();
		Binding topEdge = bf.topEdge();
		Binding aboveInputField = bf.above(inputField);

		// PAINT CANVAS
		// paintCanvas properties
		paintCanvas.setBorder(BorderFactory.createLineBorder(new Color(161,
				112, 23), 3));
		paintCanvas.setSize(200, 500);
		paintCanvas.setBackground(new Color(204, 204, 204));
		// paintCanvas.setCursor(Cursor.getPredefinedCursor(Cursor.));

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("images/Brush.png");
		Cursor c = toolkit.createCustomCursor(image,
				new Point(paintCanvas.getX(), paintCanvas.getY()), "img");
		paintCanvas.setCursor(c);

		paintCanvas.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				x2 = e.getX();
				y2 = e.getY();

				try {
					serverObj.setPoints(roomNo, x1, y1, x2, y2);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}

				x1 = e.getX();
				y1 = e.getY();
			}
		});

		paintCanvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				x1 = e.getX();
				y1 = e.getY();
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		Binding leftOfUserList = bf.leftOf(userList);
		paintCanvasConstraints = new RelativeConstraints(leftEdge,
				leftOfUserList, topEdge, aboveInputField);

		// User List
		userList.setBackground(new Color(102, 255, 153));
		userList.setPreferredSize(new Dimension(200, userList.getHeight()));
		users.setEditable(false);
		Binding rightEdge = bf.rightEdge();
		userListConstraints = new RelativeConstraints(topEdge, rightEdge,
				aboveInputField);

		// Input Field
		inputField.setEnabled(true);
		inputField.setVisible(true);
		inputField.setPreferredSize(new Dimension(700, 30));
		Binding bottomEdge = bf.bottomEdge();
		Binding leftOfSendButton = bf.leftOf(sendButton);
		Binding aboveChatPane = bf.above(chatPane);
		inputFieldConstraints = new RelativeConstraints(leftEdge,
				aboveChatPane, leftOfSendButton);

		// Send Button
		sendButton.setEnabled(true);
		sendButton.addActionListener(this);
		sendButton.setPreferredSize(new Dimension(150, 30));
		Binding ver = bf.verticallyCenterAlignedWith(inputField);
		sendButtonConstrains = new RelativeConstraints(rightEdge, ver);
		chatPane.setBackground(new Color(255, 255, 153));
		chatPane.setPreferredSize(new Dimension(1000, 100));
		chatPaneConstraints = new RelativeConstraints(leftEdge, bottomEdge,
				rightEdge);

	}

	public void addToGui() {

		panel.add(paintCanvas, paintCanvasConstraints);
		panel.add(userList, userListConstraints);
		panel.add(inputField, inputFieldConstraints);
		panel.add(sendButton, sendButtonConstrains);
		panel.add(chatPane, chatPaneConstraints);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String msg = inputField.getText().toString();
		inputField.setText(" ");
		// area.append(s + "\n");
		try {
			serverObj.setMessage(roomNo, userName, msg);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO code application logic here

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					object = new Client();

					// frame.setExtendedState(Frame.MAXIMIZED_BOTH);

					serverObj = (ServerInterface) Naming
							.lookup("rmi://127.0.0.1:" + constants.portNo
									+ "/DiscussionServer");

					UnicastRemoteObject.exportObject(object, 0);

					Scanner sc = new Scanner(System.in);
					System.out.println("Enter User Name : ");
					userName = sc.next();

					int flag = 0;
					Map<Integer, String> rooms = serverObj.getRooms();
					Iterator<Entry<Integer, String>> it = rooms.entrySet()
							.iterator();
					while (it.hasNext()) {
						flag = 1;
						Map.Entry<Integer, String> pairs = (Map.Entry<Integer, String>) it
								.next();
						System.out.println(pairs.getKey() + " : "
								+ pairs.getValue());
						it.remove();
					}

					if (flag == 0) {
						System.out
								.println("No rooms available...\nEnter -1 to create new room : ");
					} else {
						System.out
								.println("Enter room number...\nEnter -1 to create new room : ");
					}

					int number = sc.nextInt();

					if (number == -1) {
						System.out.println("Enter room name : ");
						String roomName = sc.next();
						if (roomName != null) {
							roomNo = serverObj.createRoom(object, roomName,
									userName);
						}
					} else {
						roomNo = number;
						serverObj.register(object, number, userName);
					}
					System.out.println("Room No : " + roomNo);

					object.initializeLayout();
					object.addToGui();
					// serverObj.register(frame);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Unregistering !!");
				try {
					serverObj.unRegister(object);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}));

	}

	@Override
	public void getPoints(int x1, int y1, int x2, int y2)
			throws RemoteException {
		paintCanvas.DrawLine(x1, y1, x2, y2);
	}

	@Override
	public void getMembers(String memberList) throws RemoteException {
		users.setText("Members : \n");
		users.append(memberList);
	}

	@Override
	public void getMessage(String chatMessage) throws RemoteException {
		area.append(chatMessage + "\n");
	}
}
