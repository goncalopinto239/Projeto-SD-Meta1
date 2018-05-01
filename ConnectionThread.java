
package meta1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;


public class ConnectionThread extends Thread {
    private mesaVoto mesa;
    private ArrayList<Connection> terminais;
    private RMIInterface rmi;
    
    public ConnectionThread(mesaVoto mesa,RMIInterface rmi){
        this.mesa = mesa;
        this.terminais = new ArrayList<>();
        this.rmi = rmi;
        this.start();
    }
    
    public void run(){
        int numero = 0;
        try{
            int serverPort = 6000;
            System.out.println("A Escuta no Porto 6000");
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("LISTEN SOCKET="+listenSocket);
            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
                numero ++;
                try {
                    Connection c = new Connection(clientSocket, numero,this.mesa,rmi);
                    //mesa.setTerminais(c);
                    this.terminais.add(c);
                } catch (NotBoundException ex) {
                    Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RemoteException ex) {
                    Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Consegui fazer a ligacao\n");
            }
        }catch(IOException e)
            {System.out.println("Listen:" + e.getMessage());}
    }
    
    public void desbloqueia(){
        for (int i = 0; i < this.terminais.size(); i++) {
            if(this.terminais.get(i).isBlocked()==true){
                this.terminais.get(i).setBlocked(false);
                break;
            }
        }
    }
}

