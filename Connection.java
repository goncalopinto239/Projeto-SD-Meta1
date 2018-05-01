
package meta1;

import java.net.*;
import java.io.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Thread {
    BufferedReader in;
    PrintWriter out;
    private static RMIInterface rmi;
    boolean blocked;
    Socket clientSocket;
    int thread_number;
    mesaVoto mesa;
    String primario;
    
    public Connection (Socket aClientSocket, int numero,mesaVoto mesa,RMIInterface rmiServer) throws NotBoundException, MalformedURLException, RemoteException {
        this.blocked=true;
        this.mesa = mesa;
        thread_number = numero;
        rmi = rmiServer;
        try{
            clientSocket = aClientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    //=============================
    
    //colocar cointains em falta
    /**
     * Metodo utilizado para descodificar os dados de autenticacao de um utilizador e proceder depois a autenticacao do mesmo
     * @param entrada
     * @return
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException 
     */
    public User leitura(String entrada,int opc) throws RemoteException, NotBoundException, MalformedURLException{
        boolean conf = false;
        User user = null;
        HashMap<String,String> dados = new HashMap<>();
        String parts[] = entrada.split("(;)|(\\|)");
        for(int i=0;i<parts.length;i+=2){
            parts[i] = parts[i].replace(" ", "");
            parts[i+1] = parts[i+1].replace(" ","");
            dados.put(parts[i],parts[i+1]);
        }
        if(dados.containsKey("type")){
            if(dados.get("type").equals("login")){
                if(dados.containsKey("username") && dados.containsKey("password")){
                    try {
                        conf = rmi.loginEleitor(dados.get("username"), dados.get("password"));
                    } catch (RemoteException e) {
                        RMIServerUp();
                        conf = rmi.loginEleitor(dados.get("username"), dados.get("password"));
                    }
                }
               if(conf){
                   if(opc==1){
                        out.println("type | status ; logged | on ; msg | Bem vindo a iVotas");
                   }
                   user = rmi.userExists(dados.get("username"));
                   return user;
               }
               else{
                   if(opc==1){
                        out.println("type | status ; logged | off ; msg | Acesso negado, por favor tente de novo");
                   }
                   user = rmi.userExists(dados.get("username"));
                   return user;
               }
            }
        }
        return user;
    }
    
    /**
     * Metodo utilizado para descodificar a opcao de voto de um dado utilizador eefectuar depois o voto
     * @param entrada
     * @param user
     * @throws RemoteException 
     */
    public void votar(String entrada,User user) throws RemoteException{
        HashMap<String,String> dados = new HashMap<>();
        String parts[] = entrada.split("(;)|(\\|)");
        for(int i=0;i<parts.length;i+=2){
            parts[i] = parts[i].replace(" ", "");
            parts[i+1] = parts[i+1].replace(" ","");
            dados.put(parts[i],parts[i+1]);
        }
        if(dados.containsKey("type")){
            if(dados.get("type").equals("vote")){
                if(dados.containsKey("list")){
                    rmi.votar(user, mesa, dados.get("list"));
                }
            }
        }
        
    }
    /**
     * Metodo utilizado para quando o servidor RMI nao conseguir responder tentar conectar-se a ele durante 30 segundos para que se estiver a ocorrer a troca do servidor backup para servidor primario haja tempo de realizar esta troca
     * @throws NotBoundException
     * @throws MalformedURLException 
     */
    public static void RMIServerUp() throws NotBoundException, MalformedURLException{
        boolean b = false;
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis()<start+30000){
            try{
                rmi =(RMIInterface) Naming.lookup("RMIServer");
                b = true;
                break;
            }
            catch(RemoteException e){
                //System.out.println("Exception on lookup" + e);
            }
        }
        if(b){
            System.out.println("Servidores em baixo");
        }
    }
    
    public void run(){
        String user="";
        String pass="";
        String aut="";
        User utilizador = null;
        while(true){
            if(this.blocked==false){
                out.println("type | status ; blocked | off ; msg | Terminal desbloqueado");
                try {
                    clientSocket.setSoTimeout(120000);
                    do{
                        aut = in.readLine();
                        utilizador = this.leitura(aut,0);
                    }while(this.leitura(aut,1)==null);
                    out.println(rmi.listasVoto2(mesa, utilizador));
                    String escolha = in.readLine();
                    if(!rmi.jaVotou(mesa, utilizador)){
                        votar(escolha,utilizador);
                        out.println("type | status ; logged | off ; msg | Voto recebido");
                    }
                    else{
                        out.println("type | status ; logged | off ; msg | Voto ja efectuado anteriormente");
                    }
                    out.println("type | status ; blocked | on ; msg | Terminal bloqueado");
                    this.setBlocked(true);
                } catch (SocketTimeoutException ex) {
                    out.println("type | status ; blocked | on ; msg | Terminal bloqueado");
                    this.setBlocked(true);
                }catch (RemoteException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }catch(EOFException e){System.out.println("EOF:" + e);
                }catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}