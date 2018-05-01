
package meta1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface RMIInterface extends Remote {
    public boolean registerUser (String tipo, String username, String password, String telemovel, String numeroCartao, String morada,String validadeCartao, Departamento departamento) throws RemoteException;
    public boolean criaEleicao(String tipo, Date inicio, Date fim, String titulo, String descricao) throws RemoteException;
    public boolean insereFaculdade(String nome) throws RemoteException;
    public boolean alteraFaculdade(String nome,String nomenovo) throws RemoteException;
    public boolean removeFaculdade(String nome) throws RemoteException;
    public boolean insereDepartamento(String nome,Faculdade faculdade) throws RemoteException;
    public Faculdade faculdadeExistente(String faculdade) throws RemoteException;
    public boolean loginEleitor(String username,String pass) throws RemoteException;
    public Eleicao eleicaoExistente(String eleicao) throws RemoteException;
    public void adicionaMesaVoto(Eleicao eleicao,Departamento departamento) throws RemoteException;
    public void removeMesaVoto(Eleicao eleicao,Departamento departamento) throws RemoteException;
    public Departamento departamentoExistente(String departamento) throws RemoteException;
    public boolean alteraDepartamento(String nome,String nomenovo) throws RemoteException;
    public boolean removeDepartamento(String nome) throws RemoteException;
    public boolean alteraPropriedadesTextuais(Eleicao eleicao, int opcao, String texto) throws RemoteException;
    public boolean alteraDatas(Eleicao eleicao, int opcao, Date data) throws RemoteException;
    public boolean criaListaCandidatos(String tipo,String nome) throws RemoteException;
    public User userExists(String usr) throws RemoteException;
    public String localVoto(Eleicao eleicao,User user) throws RemoteException;
    public String detalhesEleicaoPassada(Eleicao eleicao) throws RemoteException;
    public Eleicao listaEleicoes(mesaVoto mesa) throws RemoteException;
    public void adicionaConsola(TCPServerInterface consola) throws RemoteException;
    public String listasEleicao(Eleicao eleicao) throws RemoteException;
    public void votar(User user,mesaVoto mesa,String escolha) throws RemoteException;
    public boolean autenticacaoMesa(String cc) throws RemoteException;
    public String listaConsolasOn() throws RemoteException;
    public void setTipo(String tipo) throws RemoteException;
    public String getTipo() throws RemoteException;
    public void changeRMI(RMIInterface server) throws RemoteException;
    public String listasVoto2(mesaVoto mesa,User utilizador) throws RemoteException;
    public Lista listaExistente(String nome) throws RemoteException;
    public void adicionaListaCandidatos(Eleicao eleicao,Lista lista) throws RemoteException;
    public String listaUsers() throws RemoteException;
    public boolean jaVotou(mesaVoto mesa,User user) throws RemoteException;
    public String listaEleicoes() throws RemoteException;
    public String eleitoresTempoReal(Eleicao eleicao) throws RemoteException;
    public void removeListaCandidatos(Eleicao eleicao,Lista lista) throws RemoteException;
}
