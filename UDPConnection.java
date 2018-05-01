
package meta1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPConnection{
	private DatagramSocket aSocket;
	private DatagramPacket send;
	private InetAddress aHost;
	private byte[] bsend;
	private byte[] breceive;
	private String texto;
	private DatagramPacket receive;
        private RMIInterface rmi;
	
	public UDPConnection(int port, String host, int serverPort, RMIInterface rmi) throws IOException{
		this.aSocket=new DatagramSocket(port);
		this.texto="PING";
		this.bsend=texto.getBytes();
		this.aHost=InetAddress.getByName(host);
		this.send=new DatagramPacket(bsend,bsend.length,aHost, serverPort);
		this.breceive=texto.getBytes();
		this.receive=new DatagramPacket(breceive,breceive.length);
                this.rmi = rmi;
                run();
	}
        
        /**
         * Metodo utilizado para receber um ping e enviar uma resposta de confirmacao do mesmo
         * @throws IOException
         * @throws SocketTimeoutException 
         */
	public void receive() throws IOException, SocketTimeoutException {
		aSocket.receive(receive);
		System.out.println("Recebeu: PING");
                aSocket.send(send);
	}
	
        /**
         * Metodo utilizado para enviar um ping e receber uma resposta de rececao do mesmo
         * @throws IOException
         * @throws SocketTimeoutException 
         */
	public void send() throws IOException,SocketTimeoutException {
		aSocket.send(send);
		System.out.println("Enviou: PING");
                aSocket.setSoTimeout(2000);
                aSocket.receive(receive);
	}
	
        /**
         * Metodo utilizado para fazer a contagem de pings perdidos entre servidores e proceder a troca do servidor primario pelo secundario se o numero de pings perdidos chegar a 5
         * @throws RemoteException 
         */
        public void contaPings() throws RemoteException{
            int contador = 0;
            while(contador<5){
                try{
                   if(rmi.getTipo().equals("RMIServer")){
                       this.receive();
                   }
                   else{
                     this.send();  
                   }
                   
                   contador=0;
                   Thread.sleep(2000);
                }catch(SocketTimeoutException e){
                    contador++;
                    System.out.println("Contador: " + contador);

                }
                catch(IOException e){
                    contador++;
                    System.out.println("Contador: " + contador);

                } catch (InterruptedException ex) {
                    Logger.getLogger(UDPConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.aSocket.close();
            rmi.changeRMI(rmi);
        }
        
        public void run() throws RemoteException{
            contaPings();
        }
}