import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame implements ClientInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private MyCanvas canvas;
	int x1, y1, x2, y2;
	static ServerInterface serverObj;
	private final JButton btnClear = new JButton("Clear");
	static Client frame;
	static int roomNo;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Client();
					// frame.setExtendedState(Frame.MAXIMIZED_BOTH);

					serverObj = (ServerInterface) Naming
							.lookup("rmi://127.0.0.1:" + constants.portNo
									+ "/DiscussionServer");

					UnicastRemoteObject.exportObject(frame, 0);

					Map<Integer, String> rooms = serverObj.getRooms();
					Iterator<Entry<Integer, String>> it = rooms.entrySet()
							.iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, String> pairs = (Map.Entry<Integer, String>) it
								.next();
						System.out.println(pairs.getKey() + " : "
								+ pairs.getValue());
						it.remove();
					}

					System.out.println("Enter choice : ");

					Scanner sc = new Scanner(System.in);
					int number = sc.nextInt();

					if (number == -1) {
						System.out.println("Enter room name : ");
						Scanner sc2 = new Scanner(System.in);
						String roomName = sc2.next();
						if (roomName != null) {
							roomNo = serverObj.createRoom(frame, roomName);
						}
					} else {
						roomNo = number;
						serverObj.register(frame, number);
					}
					System.out.println("Room No : " + roomNo);

					frame.setVisible(true);
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
					serverObj.unRegister(frame);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	/**
	 * Create the frame.
	 */
	public Client() {

		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		canvas = new MyCanvas();
		contentPane.add(canvas, BorderLayout.CENTER);
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				canvas.clearCanvas();
			}
		});

		canvas.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub

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

		canvas.addMouseListener(new MouseListener() {

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

		contentPane.add(btnClear, BorderLayout.WEST);

	}

	@Override
	public void getPoints(int x1, int y1, int x2, int y2)
			throws RemoteException {
		// System.out.println("x1 : " + x1 + " y1 : " + y1 + " x2 : " + x2
		// + " y2 : " + y2);
		canvas.DrawLine(x1, y1, x2, y2);
	}

}
