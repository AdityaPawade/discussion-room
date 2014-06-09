import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = 1L;
	// private List<ClientInterface> clients;

	int roomCount;
	private Map<Integer, String> rooms;
	private Map<ClientInterface, member> members;

	public class member {
		public int roomNo;
		public String userName;

		public member(int roomNo, String userName) {
			this.roomNo = roomNo;
			this.userName = userName;
		}
	};

	protected ServerImpl() throws RemoteException {
		super();
		roomCount = 0;
		rooms = new HashMap<Integer, String>();
		members = new HashMap<ClientInterface, member>();
	}

	@Override
	public void setPoints(int roomNo, int x1, int y1, int x2, int y2)
			throws RemoteException {
		// System.out.println("x1 : " + x1 + " y1 : " + y1 + " x2 : " + x2
		// + " y2 : " + y2);
		Iterator<Entry<ClientInterface, member>> it = members.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<ClientInterface, member> pairs = (Map.Entry<ClientInterface, member>) it
					.next();
			if (pairs.getValue().roomNo == roomNo) {
				pairs.getKey().getPoints(x1, y1, x2, y2);
			}
		}
	}

	@Override
	public void register(ClientInterface client, Integer roomNo, String userName)
			throws RemoteException {
		members.put(client, new member(roomNo, userName));
		System.out.println("Client registered.. : " + userName);

		String memberList = "";
		List<ClientInterface> memberObjs = new ArrayList<ClientInterface>();
		Iterator<Entry<ClientInterface, member>> it = members.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<ClientInterface, member> pairs = (Map.Entry<ClientInterface, member>) it
					.next();
			if (pairs.getValue().roomNo == roomNo) {
				memberObjs.add(pairs.getKey());
				memberList = memberList + pairs.getValue().userName + "\n";
			}
		}

		for (ClientInterface memberObj : memberObjs) {
			memberObj.getMembers(memberList);
		}

	}

	@Override
	public void unRegister(ClientInterface client) throws RemoteException {
		// clients.remove(client);
		member retMember = members.get(client);
		int memberRoom = retMember.roomNo;
		int flag = 0;
		System.out.println("Removing client : " + retMember.userName);

		members.remove(client);

		String memberList = "";
		List<ClientInterface> memberObjs = new ArrayList<ClientInterface>();

		Iterator<Entry<ClientInterface, member>> it = members.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<ClientInterface, member> pairs = (Map.Entry<ClientInterface, member>) it
					.next();
			if (pairs.getValue().roomNo == memberRoom) {
				flag = 1;
				memberObjs.add(pairs.getKey());
				memberList = memberList + pairs.getValue().userName + "\n";
			}
		}

		if (flag == 0) {
			System.out.println("Removing room : " + memberRoom);
			rooms.remove(memberRoom);
		} else {
			for (ClientInterface memberObj : memberObjs) {
				memberObj.getMembers(memberList);
			}
		}

	}

	@Override
	public Map<Integer, String> getRooms() throws RemoteException {
		return rooms;
	}

	@Override
	public int createRoom(ClientInterface client, String roomName,
			String userName) throws RemoteException {
		rooms.put(roomCount, roomName);
		members.put(client, new member(roomCount, userName));
		System.out.println("Room Created..");
		System.out.println("Client registered.. : " + userName);
		roomCount++;
		client.getMembers(userName);
		return roomCount - 1;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setMessage(int roomNo, String userName, String chatMessage)
			throws RemoteException {
		Date date = new Date();
		chatMessage = userName + " ( " + date.getHours() + " : "
				+ date.getMinutes() + " : " + date.getSeconds() + " ) >> "
				+ chatMessage;
		Iterator<Entry<ClientInterface, member>> it = members.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<ClientInterface, member> pairs = (Map.Entry<ClientInterface, member>) it
					.next();
			if (pairs.getValue().roomNo == roomNo) {
				pairs.getKey().getMessage(chatMessage);
			}
		}
	}

}
