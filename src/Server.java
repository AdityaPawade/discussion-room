import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static void main(String[] argv) {
		try {
			ServerInterface server = new ServerImpl();
			Registry reg = LocateRegistry.createRegistry(constants.portNo);
			reg.rebind("DiscussionServer", server);
			System.out.println("Server Ready !");
		} catch (Exception e) {
			System.out.println("Server not connected : " + e);
		}
	}
}
