import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	public void getPoints(int x1, int y1, int x2, int y2)
			throws RemoteException;

	public void getMembers(String memberList) throws RemoteException;

	public void getMessage(String chatMessage) throws RemoteException;
}
