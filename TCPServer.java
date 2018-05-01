
package meta1;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;

public class TCPServer extends UnicastRemoteObject implements TCPServerInterface {
    private static String nomeDep;
    private static RMIInterface conexao;
    private static mesaVoto mdv;

    public TCPServer() throws RemoteException{
    }
   
    public static void main(String[] args) throws NotBoundException, RemoteException, InterruptedException, MalformedURLException {
        String cc;
        boolean aut = false;
        ConnectionThread connection;
        Scanner input=new Scanner(System.in);
        System.getProperties().put("java.security.policy","policy.all");
        System.setSecurityManager(new RMISecurityManager());
        nomeDep = args[0];
        TCPServer t = new TCPServer();
        TCPServerInterface r = (TCPServerInterface)t;
        
        try{
            conexao = (RMIInterface) Naming.lookup("RMIServer");
            mdv = new mesaVoto(conexao.departamentoExistente(args[0]));
            conexao.adicionaConsola(r);
        }
        catch(Exception e){
            System.out.println("Exception on lookup" + e);
        }

        connection = new ConnectionThread(mdv,conexao);
        
        while(true){
            System.out.println("Numero de cartão de cidadao:");
            cc = input.nextLine();
            try{
                aut = conexao.autenticacaoMesa(cc);
            }catch(RemoteException e){
                RMIServerUp();
                aut = conexao.autenticacaoMesa(cc);
            }
            if(aut){
                connection.desbloqueia();
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
                conexao =(RMIInterface) Naming.lookup("RMIServer");
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
    
    public String getNomeDep(){
        return this.nomeDep;
    }
    
    public mesaVoto getMesa(){
        return mdv;
    }
}
