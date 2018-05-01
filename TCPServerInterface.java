
package meta1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TCPServerInterface extends Remote {
    public String getNomeDep() throws RemoteException;
    public mesaVoto getMesa() throws RemoteException;
}
