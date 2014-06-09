import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServerInterface extends Remote {
	public void setPoints(int roomNo, int x1, int y1, int x2, int y2)
			throws RemoteException;

	public void setMessage(int roomNo, String userName, String chatMessage)
			throws RemoteException;

	public Map<Integer, String> getRooms() throws RemoteException;

	public int createRoom(ClientInterface client, String roomName,
			String userName) throws RemoteException;

	public void register(ClientInterface client, Integer roomNo, String userName)
			throws RemoteException;

	public void unRegister(ClientInterface client) throws RemoteException;
}
