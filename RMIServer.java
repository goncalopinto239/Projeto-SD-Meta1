
package meta1;

import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class RMIServer extends UnicastRemoteObject implements RMIInterface {
    private ArrayList<User> Users;
    private ArrayList<Eleicao> eleicoes;
    private ArrayList<Faculdade> faculdades;
    private ArrayList<Departamento> departamentos;
    private ArrayList<Lista> listas;
    private ArrayList<TCPServerInterface> consolas;
    private Files file;
    private Storage s;
    private static String tipo;
    
    public RMIServer() throws RemoteException{
        super();
        file = new Files();
        s = this.leitura();
        Users = s.getLista_users();
        faculdades = s.getLista_faculdades();
        departamentos = s.getLista_departamentos();
        eleicoes = s.getLista_eleicoes();
        listas = s.getLista_candidatos();
        this.consolas = new ArrayList<>();
    }
    
    public static void main(String[] args) throws RemoteException{
        boolean b = true;
        RMIServer server = null;
        RMIServer serverb = null;
         try{
            java.rmi.registry.LocateRegistry.createRegistry(1099); //verificar se o registo esta livre e se estiver ira ligar como servidor primario
            System.out.println("RMI registry ready.");
        }
        catch (Exception e){
            b = false;
            tipo = "BackupRMIServer"; //se o registo estiver em uso ira ficar como servidor secundario
            System.out.println(tipo);
            serverb = new RMIServer();
        }
        System.out.println(b);
        if(b){
            tipo = "RMIServer";
            try {
                server = new RMIServer();
                Naming.rebind("RMIServer",server);
                System.out.println("RMI Server on");
            } catch (MalformedURLException ex) {
                Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(tipo);
        }
        //cria as conexoes udp em ambos os sentidos para a troca de pings posterior entre os mesmos
        if(tipo.equals("RMIServer")){
            UDPConnection udp = null;
            try {
                udp=new UDPConnection(6790,"localhost",6789,server);
            } catch (IOException ex) {
                Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            UDPConnection udp = null;
            try {
                udp=new UDPConnection(6789,"localhost",6790,serverb);
            } catch (IOException ex) {
                Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /***
     * Metodo utilizado para trocar o servidor rmi primario com o secundario
     * @param server
     * @throws RemoteException 
     */
    
    public void changeRMI(RMIInterface server) throws RemoteException{
        try{
            java.rmi.registry.LocateRegistry.createRegistry(1099); //volta a utilizar o mesmo registo pois como o servidor primario ja foi a baixo o servidor secundario pode ligar no mesmo registo
            System.out.println("RMI registry ready.");
        }
        catch (Exception e){
            System.out.println("");
        }
        try {
            Naming.rebind("RMIServer", server);
            System.out.println("RMI Server on");
        } catch (MalformedURLException ex) {
            Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        server.setTipo("RMIServer"); //altera o seu tipo para passar a ter o tipo de servidor primario
        //cria uma nova ligacao udp com as portas corretas a utilizacao do servidor primario
        UDPConnection udp = null;
        try {
            udp = new UDPConnection(6790, "localhost", 6789, server);
        } catch (IOException ex) {
            Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getTipo(){ 
        return this.tipo;
    }
    
    public void setTipo(String tipo){ 
        this.tipo = tipo;
    }
    
    /***
     * Metodo utilizado para leitura da base de dados
     * @return 
     */
    public Storage leitura() {
        Storage base=new Storage();
	try{
            if (file.abreLeitura("storage.bin")) {
                base = (Storage) file.leObjecto();
                file.fechaLeitura();
            }
        }
        catch (Exception c) {
            System.out.println("Ocorreu um erro " + c);
        }
	return base;
    }
    
    /***
     * Metodo utilizado para escrever para a base de dados
     * @param base 
     */
    public void escrita(Storage base) {
	try{
            file.abreEscrita("storage.bin");
            file.escreveObjecto(base);
            file.fechaEscrita();
        }
        catch (Exception c) {
            System.out.println("Ocorreu um erro " + c);
        }
    }
    
    /***
     * Metodo para verificar a existencia de um utilizador
     * @param usr
     * @return 
     */
    public User userExists(String usr){
        synchronized(Users){
            for(User us : Users){
                if(us.getUsername().equals(usr)){
                    return us;
                }
            }
        }
        return null;
    }
    
    /***
     * Metodo para verificar a existencia de uma eleicao
     * @param eleicao
     * @return 
     */
    public Eleicao eleicaoExistente(String eleicao){
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                if(elei.getTitulo().equals(eleicao)){
                    return elei;
                }
            }
        }
        return null;
    }
    
    /***
     * Metodo para verificar a existencia de uma faculdade
     * @param faculdade
     * @return 
     */
    public Faculdade faculdadeExistente(String faculdade){
        synchronized(faculdades){
            for(Faculdade fac : faculdades){
                if(fac.getNome().equals(faculdade)){
                    return fac;
                }
            }
        }
        return null;
    }
    
    /***
     * Metodo para verificar a existencia de um departamento
     * @param departamento
     * @return 
     */
    
    public Departamento departamentoExistente(String departamento){
        synchronized(departamentos){
            for(Departamento dep : departamentos){
                if(dep.getNome().equals(departamento)){
                    return dep;
                }
            }
        }
        return null;
    }
    
    /***
     * Metodo para verificar a existencia de uma lista
     * @param nome
     * @return 
     */
    public Lista listaExistente(String nome){
        synchronized(listas){
            for(Lista lista : listas){
                if(lista.getNome().equals(nome)){
                    return lista;
                }
            }
        }
        return null;
    }
    
    /**
     * Metodo utilizado para criar um utilizador 
     * @param tipo
     * @param username
     * @param password
     * @param telemovel
     * @param numeroCartao
     * @param morada
     * @param validadeCartao
     * @param departamento
     * @return
     * @throws RemoteException 
     */
    public boolean registerUser (String tipo, String username, String password, String telemovel, String numeroCartao, String morada,
            String validadeCartao, Departamento departamento) throws RemoteException{
        synchronized(Users){
            for(User user : Users){
                synchronized(user){
                    if(user.getUsername().equals(username)){
                        System.out.println("Username já existente");
                        return false;
                    }
                }
            }
            User user = new User(tipo,username,password,telemovel,numeroCartao,morada,validadeCartao,departamento);
            Users.add(user);
        }
        synchronized(s){
            try{
                s.lista_users = Users;
                escrita(s);
            }catch(Exception e){
                System.out.println("Exception " + e);
            }
        }
        System.out.println("Utilizador criado");
        return true;
    }
    
    /**
     * Metodo para listar os utilizadores existentes
     * @return 
     */
    public String listaUsers(){
        String ret = "Utilizadores existentes\n\n";
        for(User user: Users){
            ret = ret.concat("Nome: " + user.getUsername() + " Tipo: " + user.getType() + " Numero cartao: " + user.getNumeroCartao() +
                    " Morada: " + user.getMorada() + " Telemovel: " + user.getTelemovel() + " Departamento: " + user.getDepartamento().getNome()) + "\n";
        }
        return ret;
    }
    
    /**
     * Metodo para criar uma eleicao
     * @param tipo
     * @param inicio
     * @param fim
     * @param titulo
     * @param descricao
     * @return 
     */
    public boolean criaEleicao(String tipo, Date inicio, Date fim, String titulo, String descricao){
        synchronized(eleicoes){
            for(Eleicao eleicao : eleicoes){
                synchronized(eleicao){
                    if(eleicao.getTitulo().equals(titulo)){
                        //System.out.println("Eleição já existente");
                        return false;
                    }
                }
            }
            Eleicao eleicao = new Eleicao(tipo,inicio,fim,titulo,descricao);
            eleicoes.add(eleicao);
        }
        synchronized(s){
            try{
                s.lista_eleicoes = eleicoes;
                escrita(s);
            }catch(Exception e){
                System.out.println("Exception " + e);
            }
        }
        //System.out.println("Eleicao criada");
        return true; 
    }
    
    /**
     * Metodo para listar as eleicoes existentes
     * @return 
     */
    public String listaEleicoes(){
        String ret = "Eleicoes existentes\n\n";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        synchronized(eleicoes){
            for(Eleicao eleicao : eleicoes){
                synchronized(eleicao){
                    ret = ret.concat("Titulo: " + eleicao.getTitulo() + " Tipo: " + eleicao.getTipo() + " Inicio: "
                    + formatter.format(eleicao.getInicio()) + " Fim: " + formatter.format(eleicao.getFim()) + " Descricao: " + eleicao.getDescricao() + "\n");
                }
            }
        }
        return ret;
    }
    
    /*novo 21/10/17*/
    public Eleicao listaEleicoes(mesaVoto mesa){
        synchronized(eleicoes){
            for(Eleicao eleicao : eleicoes){
                synchronized(eleicao.getmesasVoto()){
                    for(mesaVoto m : eleicao.getmesasVoto()){
                        synchronized(m){
                            if(m.getDepartamento().getNome().equals(mesa.getDepartamento().getNome())){
                                return eleicao;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Metodo para listar as listas candidatas a uma dada eleicao
     * @param eleicao
     * @return 
     */
    public String listasEleicao(Eleicao eleicao){
        String ret = ""; 
        ret = ret.concat("Listas existentes na eleicao" + eleicao.getTitulo() + "\n\n");
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                if(elei.getTitulo().equals(eleicao.getTitulo())){
                    synchronized(elei.getListas()){
                        for(Lista lista : elei.getListas()){
                           ret = ret.concat("Nome: " + lista.getNome() + " Tipo: " + lista.getTipo() + "\n");
                        }
                        break;
                    }
                }
            }
        }
        return ret;
    }
    
    /**
     * Metodo utilizado para passar ao cliente atraves do protocolo as listas em que este pode votar na eleicao em causa
     * @param mesa
     * @param utilizador
     * @return 
     */
    public String listasVoto2(mesaVoto mesa,User utilizador){
        String ret = "";
        ArrayList<Lista> listas = new ArrayList<>();
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei.getmesasVoto()){
                    for(mesaVoto m : elei.getmesasVoto()){
                        if(m.getDepartamento().getNome().equals(mesa.getDepartamento().getNome())){
                            synchronized (elei.getListas()) {
                                for (Lista lista : elei.getListas()) {
                                    if (lista.getTipo().equals(utilizador.getType())) {
                                        listas.add(lista);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ret = ret.concat("type | item_list ; item_count | " + listas.size()) + " ";
        for(int i=0;i<listas.size();i++){
            if(i==listas.size()-1){
                ret = ret.concat("item_" + i + "_name | "+listas.get(i).getNome() + " ");
            }
            else{
                ret = ret.concat("item_" + i + "_name | " + listas.get(i).getNome()) + " ; ";
            }
        }
        return ret;
    }
    
    /**
     * Metodo para criar faculdade
     * @param nome
     * @return 
     */
    public boolean insereFaculdade(String nome) {
        boolean conf = false;
        synchronized(faculdades){
            for(Faculdade fac : faculdades){
                synchronized(fac){
                    if(fac.getNome().equals(nome)){
                        conf = true;
                        break;
                    }
                }
            }
        }
        if(conf){
            //System.out.println("Faculdade já existente");
            return false;
        }
        else{
            Faculdade faculdade = new Faculdade(nome);
            faculdades.add(faculdade);
            synchronized(s){
                try{
                    s.lista_faculdades = faculdades;
                    escrita(s);
                }catch(Exception e){
                    System.out.println("Exception " + e);
                }
            }
            //System.out.println("Faculdade inserida");
        }
        return true;
    }
    
    /**
     * Metodo para alterar dados de uma faculdade
     * @param nome
     * @param nomenovo
     * @return
     * @throws RemoteException 
     */
    public boolean alteraFaculdade(String nome,String nomenovo) throws RemoteException{
        boolean conf = false;
        synchronized(faculdades){
            for(Faculdade fac : faculdades){
                synchronized(fac){
                    if(fac.getNome().equals(nome)){
                        conf = true;
                        fac.setNome(nomenovo);
                        synchronized(s){
                            try{
                                s.lista_faculdades = faculdades;
                                escrita(s);
                            }catch(Exception e){
                                System.out.println("Exception " + e);
                            }
                        }
                        System.out.println("Nome da faculdade alterado");
                        break;
                    }
                }
            }
        }
        if(!conf) {
            System.out.println("Faculdade inexistente");
            return false;
        }
        return true;
    }
    
    /**
     * Metodo para remover faculdade
     * @param nome
     * @return
     * @throws RemoteException 
     */
    public boolean removeFaculdade(String nome) throws RemoteException{
        boolean conf = false;
        synchronized(faculdades){
            for(Faculdade fac : faculdades){
                synchronized(fac){
                    if(fac.getNome().equals(nome)){
                        conf = true;
                        faculdades.remove(fac);
                        synchronized(s){
                            try{
                                s.lista_faculdades = faculdades;
                                escrita(s);
                            }catch(Exception e){
                                System.out.println("Exception " + e);
                            }
                        }
                        System.out.println("Faculdade removida");
                        break;
                    }
                }
            }
        }
        if(!conf) {
            System.out.println("Faculdade inexistente");
            return false;
        }
        return true;
    }
    
    /**
     * Metodo para criar departamento
     * @param nome
     * @param faculdade
     * @return
     * @throws RemoteException 
     */
    public boolean insereDepartamento(String nome,Faculdade faculdade) throws RemoteException{
        boolean conf = false;
        synchronized(departamentos){
            for(Departamento dep : departamentos){
                synchronized(dep){
                    if(dep.getNome().equals(nome)){
                        conf = true;
                        break;
                    }
                }
            }
        }
        if(conf){
            System.out.println("Departamento ja existente");
            return false;
        }
        else{
            Departamento dep = new Departamento(nome, faculdade);
            departamentos.add(dep);
            synchronized (s) {
                try {
                    s.lista_departamentos = departamentos;
                    escrita(s);
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            }
            System.out.println("Departamento inserido");
            return true;
        }
    }
    
    /**
     * Metodo utilizado para alterar dados de um departamento
     * @param nome
     * @param nomenovo
     * @return
     * @throws RemoteException 
     */
    public boolean alteraDepartamento(String nome,String nomenovo) throws RemoteException{
        boolean conf = false;
        synchronized(departamentos){
            for(Departamento dep : departamentos){
                synchronized(dep){
                    if(dep.getNome().equals(nome)){
                        conf = true;
                        dep.setNome(nomenovo);
                        synchronized(s){
                            try{
                                s.lista_departamentos = departamentos;
                                escrita(s);
                            }catch(Exception e){
                                System.out.println("Exception " + e);
                            }
                        }
                        System.out.println("Nome do departamentos alterado");
                    }
                }
            }
        }
        if(!conf) {
            System.out.println("Departamento inexistente");
            return false;
        }
        return true;
    }
    
    /**
     * Metodo utilizado para remover um departamento
     * @param nome
     * @return
     * @throws RemoteException 
     */
    public boolean removeDepartamento(String nome) throws RemoteException{
        boolean conf = false;
        synchronized(departamentos){
            for(Departamento dep : departamentos){
                synchronized(dep){
                    if(dep.getNome().equals(nome)){
                        conf = true;
                        departamentos.remove(dep);
                        synchronized(s){
                            try{
                                s.lista_departamentos = departamentos;
                                escrita(s);
                            }catch(Exception e){
                                System.out.println("Exception " + e);
                            }
                        }
                        System.out.println("Departamento removido");
                        break;
                    }
                }
            }
        }
        if(!conf) {
            System.out.println("Departamento inexistente");
            return false;
        }
        return true;
    }
    
    /**
     * Metodo utilizado para criar uma lista de candidatos
     * @param tipo
     * @param nome
     * @return 
     */
    public boolean criaListaCandidatos(String tipo,String nome){
        boolean conf = false;
        synchronized(listas){
            for(Lista l : listas){
                synchronized(l){
                    if(l.getNome().equals(nome)){
                        conf = true;
                        break;
                    }
                }
            }
        }
        if(conf){
            return false;
        }
        else{
            Lista lista = new Lista(tipo,nome);
            listas.add(lista);
            synchronized(s){
                try{
                    s.lista_candidatos = listas;
                    escrita(s);
                }catch(Exception e){
                    System.out.println("Exception " + e);
                }
            }
            return true;
        }
        
    }
    
    /**
     * Metodo utilizado para adicionar uma dada lista de candidatos a uma dada eleicao
     * @param eleicao
     * @param lista 
     */
    public void adicionaListaCandidatos(Eleicao eleicao,Lista lista){
        boolean conf = false;
        synchronized(eleicoes){
            for(Eleicao e : eleicoes){
                synchronized(e){
                    if(e.getTitulo().equals(eleicao.getTitulo())){
                        synchronized (e.getListas()) {
                            for (Lista l : e.getListas()) {
                                if (l.getNome().equals(lista.getNome())) {
                                    conf = true;
                                    break;
                                }
                            }
                        }
                        if (conf) {
                            System.out.println("Lista já existente nesta eleição");
                        } else {
                            e.getListas().add(lista);
                            synchronized (s) {
                                try {
                                    s.lista_eleicoes = eleicoes;
                                    escrita(s);
                                } catch (Exception er) {
                                    System.out.println("Exception " + e);
                                }
                            }
                            System.out.println("Lista adicionada à eleição");
                        }
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Metodo para remover uma dada lista de candidatos de uma dada eleicao
     * @param eleicao
     * @param lista 
     */
    public void removeListaCandidatos(Eleicao eleicao,Lista lista){
        boolean conf = false;
        synchronized(eleicoes){
            for(Eleicao e : eleicoes){
                synchronized(e){
                    if(e.getTitulo().equals(eleicao.getTitulo())){
                        synchronized (e.getListas()) {
                            for (Lista l : e.getListas()) {
                                if (l.getNome().equals(lista.getNome())) {
                                    conf = true;
                                    e.getListas().remove(l);
                                    synchronized (s) {
                                        try {
                                            s.lista_eleicoes = eleicoes;
                                            escrita(s);
                                        } catch (Exception c) {
                                            System.out.println("Exception " + c);
                                        }
                                    }
                                    System.out.println("Lista removida desta eleição");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(!conf){
            System.out.println("Lista inexistente nesta eleição");
        }
    }
    
    /**
     * Metodo utilizado para alterar as propiedades textuais de uma dada eleicao
     * @param eleicao
     * @param opcao
     * @param texto
     * @return 
     */
    public boolean alteraPropriedadesTextuais(Eleicao eleicao, int opcao, String texto){
        Calendar cal = Calendar.getInstance();
        if(eleicao.getInicio().compareTo(cal.getTime())>0){
            boolean conf = false;
            synchronized(eleicoes){
                for(Eleicao e : eleicoes){
                    synchronized(e){
                        if(e.getTitulo().equals(eleicao.getTitulo())){
                            conf = true;
                            if(opcao==1){
                                e.setTitulo(texto);
                            }
                            else if(opcao==2){
                                e.setDescricao(texto);
                            }
                            synchronized(s){
                                try{
                                    s.lista_eleicoes = eleicoes;
                                    escrita(s);
                                }catch(Exception c){
                                    System.out.println("Exception " + c);
                                }
                            }
                            System.out.println("Alteração efectuada com sucesso");
                            break;
                        }
                    }
                }
            }
            if(!conf){
                System.out.println("Eleição inexistente"); //mudar
                return false;
            }
        }
        else{
            System.out.println("Eleição em curso");
            return false;
        }
        return true;
    }
    
    /**
     * Metodo utilizado para alterar as datas de inicio e fim de uma dada eleicao
     * @param eleicao
     * @param opcao
     * @param data
     * @return 
     */
    public boolean alteraDatas(Eleicao eleicao, int opcao, Date data){
        Calendar cal = Calendar.getInstance();
        if(eleicao.getInicio().compareTo(cal.getTime())>0){
            boolean conf = false;
            synchronized(eleicoes){
                for(Eleicao e : eleicoes){
                    synchronized(e){
                        if(e.getTitulo().equals(eleicao.getTitulo())){
                            conf = true;
                            if(opcao==1){
                                e.setInicio(data);
                            }
                            else if(opcao==2){
                                e.setFim(data);
                            }
                            synchronized(s){
                                try{
                                    s.lista_eleicoes = eleicoes;
                                    escrita(s);
                                }catch(Exception c){
                                    System.out.println("Exception " + c);
                                }
                            }
                            System.out.println("Alteração efectuada com sucesso");
                            break;
                        }
                    }
                }
            }
            if(!conf){
                System.out.println("Eleição inexistente"); //mudar
                return false;
            }
        }
        else{
            System.out.println("Eleição em curso");
            return false;
        }
        return true;
    }
    
    /**
     * Metodo utilizado para adicionar uma mesa de voto a uma dada eleicao
     * @param eleicao
     * @param departamento
     * @throws RemoteException 
     */
    public void adicionaMesaVoto(Eleicao eleicao,Departamento departamento) throws RemoteException{
        boolean conf = true;
        mesaVoto mesa;
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei){
                    if(elei.getTitulo().equals(eleicao.getTitulo())){
                        synchronized(elei.getmesasVoto()){
                            for(mesaVoto m : elei.getmesasVoto()){
                                synchronized(m){
                                    if(m.getDepartamento().getNome().equals(departamento.getNome())){
                                        conf = false;
                                        System.out.println("Este departamento já possui uma mesa de voto");
                                        break;
                                    }
                                }
                            }
                        }
                        if(conf){
                            synchronized(consolas){
                                for(int i=0;i<consolas.size();i++){
                                    if(consolas.get(i).getNomeDep().equals(departamento.getNome())){ //verifica se existe algum tcp server ligado com o nome do departamento pretendido
                                       elei.getmesasVoto().add(consolas.get(i).getMesa()); //se sim adiciona esse tcp server as mesas de voto da eleicao em causa
                                    }
                                }
                            }
                            synchronized(s){
                                try{
                                    s.lista_eleicoes = eleicoes;
                                    escrita(s);
                                }catch(Exception e){
                                    System.out.println("Exception " + e);
                                }
                            }
                            System.out.println("Mesa de voto adicionada");
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Metodo utilizado para remover uma mesa de voto de uma eleicao 
     * @param eleicao
     * @param departamento 
     */
    public void removeMesaVoto(Eleicao eleicao,Departamento departamento){
        boolean conf = false;
        mesaVoto mesa = null;
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei){
                    if(elei.getTitulo().equals(eleicao.getTitulo())){
                        synchronized(elei.getmesasVoto()){
                            for(mesaVoto m : elei.getmesasVoto()){
                                synchronized(m){
                                    if(m.getDepartamento().getNome().equals(departamento.getNome())){
                                        conf = true;
                                        mesa = m;
                                        break;
                                    }
                                }
                            }
                        }
                        if(conf){
                            elei.getmesasVoto().remove(mesa);
                            synchronized(s){
                                try{
                                    s.lista_eleicoes = eleicoes;
                                    escrita(s);
                                }catch(Exception e){
                                    System.out.println("Exception " + e);
                                }
                            }
                            System.out.println("Mesa de voto removida");
                        }
                        else{
                            System.out.println("Este departamento nao possui mesa de voto para a eleicao em causa");
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Metodo utilizado para proceder a autenticacao pelo numero de cartao de cidadao 
     * @param cc
     * @return 
     */
    public boolean autenticacaoMesa(String cc){
        synchronized(Users){
            for(User user : Users){
                synchronized(user){
                    if(user.getNumeroCartao().equals(cc)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Metodo utilizado para autenticar um utilizador atraves do nome de utilizador e palavra passe
     * @param username
     * @param pass
     * @return 
     */
    public boolean loginEleitor(String username,String pass){
        synchronized(Users){
            for(User user : Users){
                synchronized(user){
                    if(user.getUsername().equals(username) && user.getPassword().equals(pass)){
                        //System.out.println("Autenticado com sucesso");
                        return true;
                    }
                }
            }
        }
        //System.out.println("Acesso negado");
        return false;
    }
    
    /**
     * Metodo utilizado para determinar o local e a data em que um determinado utilizador votou
     * @param eleicao
     * @param user
     * @return 
     */
    public String localVoto(Eleicao eleicao,User user){
        ArrayList<Voto> votos;
        HashMap<User,Date> eleitores;
        String retorno = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei){
                    if(elei.getTitulo().equals(eleicao.getTitulo())){
                        votos = elei.getVotos();
                        synchronized(votos){
                            for(Voto voto : votos){
                                eleitores = voto.getEleitores();
                                for (HashMap.Entry<User, Date> temp : eleitores.entrySet()) {//percorre a HashMap presente na classe voto que guarda no instante do voto o utilizador que votou e a data a que o mesmo votou
                                    //if(eleitores.containsKey(user)){
                                    if (user.getUsername().equals(temp.getKey().getUsername())) {
                                        retorno = ("Utilizador " + temp.getKey().getUsername() + " votou no departamento"
                                                + voto.getMesa().getDepartamento().getNome()) + " em " + formatter.format(temp.getValue());//+"em"+formatter.format(eleitores.get(user)));
                                        return retorno;
                                    }
                                }
                            }
                        }
                        retorno = "O utilizador "+user.getUsername()+" não votou na eleição "+elei.getTitulo();
                        break;
                    }
                }
            }
        }
        return retorno;
    }
    
    /**
     * Metodo utilizado para confirmar se um dado utilizador ja votou numa dada eleicao
     * @param mesa
     * @param user
     * @return 
     */
    public boolean jaVotou(mesaVoto mesa,User user){
        HashMap<User,Date> eleitores;
        boolean conf = false;
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei.getmesasVoto()){
                    for(mesaVoto m : elei.getmesasVoto()){
                        if(m.getDepartamento().getNome().equals(mesa.getDepartamento().getNome())){
                            synchronized (elei.getVotos()) {
                                for (Voto voto : elei.getVotos()) {
                                    synchronized (voto) {
                                        eleitores = voto.getEleitores();
                                        synchronized(eleitores){
                                            for (HashMap.Entry<User, Date> temp : eleitores.entrySet()) {
                                                //verifica na HashMap que guarda os utilizadores que ja votaram se se encontra o utilizador pretendido
                                                if (user.getUsername().equals(temp.getKey().getUsername())) {
                                                    conf = true;
                                                    return conf;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return conf;
    }
   
    /*ver se a verificação das datas esta bem*/
    /**
     * Metodo utilizado para efectuar o voto de um determinado utilizador numa dada eleicao
     * @param user
     * @param mesa
     * @param escolha 
     */
    public void votar(User user,mesaVoto mesa,String escolha){
        Calendar cal = Calendar.getInstance();
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei.getmesasVoto()){
                    for(mesaVoto m : elei.getmesasVoto()){
                        if(m.getDepartamento().getNome().equals(mesa.getDepartamento().getNome())){
                            if((elei.getInicio().compareTo(cal.getTime())<=0) && (elei.getFim().compareTo(cal.getTime())>=0)){
                                if (elei.getTipo().equals("Nucleo de estudantes")) {
                                    if (user.getType().equals("Estudante") && user.getDepartamento().getNome().equals(elei.getmesasVoto().get(0).getDepartamento().getNome())) {
                                        if (elei.getVotos().isEmpty()) {
                                            HashMap<Lista, Integer> listasVotos = new HashMap<>();
                                            //coloca na HashMap as listas de voto existentes para a eleicao em causa com o numero de votos a 0
                                            for (int i = 0; i < elei.getListas().size(); i++) {
                                                listasVotos.put(elei.getListas().get(i), 0);
                                            }
                                            Voto novo = new Voto(elei, mesa, listasVotos);
                                            novo.confirmaVoto(escolha, elei.getListas(),user);
                                            elei.getVotos().add(novo);
                                        } else {
                                            //como e uma eleicao de estudantes so tem um departamento e assim um objeto do tipo Voto no ArrayList
                                            elei.getVotos().get(0).confirmaVoto(escolha, elei.getListas(),user);
                                        }
                                    } else {
                                        System.out.println("Utilizador não autorizado a votar nesta eleição");
                                    }
                                } else {
                                    ArrayList<Lista> listas = new ArrayList<>();
                                    //colocar em listas as listas do tipo correspondente ao utilizador em causa, visto que este so vota nas listas do seu tipo
                                    if (user.getType().equals("Estudante")) {
                                        for (int i = 0; i < elei.getListas().size(); i++) {
                                            if (elei.getListas().get(i).getTipo().equals("Estudante")) {
                                                listas.add(elei.getListas().get(i));
                                            }
                                        }
                                    } else if (user.getType().equals("Docente")) {
                                        for (int i = 0; i < elei.getListas().size(); i++) {
                                            if (elei.getListas().get(i).getTipo().equals("Docente")) {
                                                listas.add(elei.getListas().get(i));
                                            }
                                        }
                                    } else {
                                        for (int i = 0; i < elei.getListas().size(); i++) {
                                            if (elei.getListas().get(i).getTipo().equals("Funcionario")) {
                                                listas.add(elei.getListas().get(i));
                                            }
                                        }
                                    }
                                    boolean conf = false;
                                    for (Voto voto : elei.getVotos()) {
                                        //verifica se ja existem votos naquele departamento
                                        if (voto.getMesa().getDepartamento().getNome().equals(mesa.getDepartamento().getNome())) {
                                            conf = true;
                                            voto.confirmaVoto(escolha, listas,user);
                                            break;
                                        }
                                    }
                                    if (!conf) {
                                        //se nao existir inicia as listas com os votos a 0 e procede depois ao voto
                                        HashMap<Lista, Integer> listasVotos = new HashMap<>();
                                        for (int i = 0; i < elei.getListas().size(); i++) {
                                            listasVotos.put(elei.getListas().get(i), 0);
                                        }
                                        Voto novo = new Voto(elei, mesa, listasVotos);
                                        novo.confirmaVoto(escolha, elei.getListas(),user);
                                        elei.getVotos().add(novo);
                                    }
                                }
                                synchronized (s) {
                                    try {
                                        s.lista_eleicoes = eleicoes;
                                        escrita(s);
                                    } catch (Exception e) {
                                        System.out.println("Exception " + e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Metodo utilizado para que consolas de administrador possam ver em tempo real o numero de eleitores que votaram numa dada eleicao em cada departamento
     * @param eleicao
     * @return 
     */
    public String eleitoresTempoReal(Eleicao eleicao){
        String ret = "Número de votos nos diferenes departamentos\n\n";
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei){
                    if(elei.getTitulo().equals(eleicao.getTitulo())){
                        synchronized(elei.getVotos()){
                            for(Voto voto : elei.getVotos()){
                                synchronized(voto){
                                   ret = ret.concat("Departamento: " + voto.getMesa().getDepartamento().getNome() + " Número de votos: " + voto.contaVotos());
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    /**
     * Metodo utilizado para listar o resultado dos votos numa dada eleicao
     * @param eleicao
     * @return 
     */
    public String detalhesEleicaoPassada(Eleicao eleicao){
        String retorno="";
        String totais="";
        String percentagem="";
        int total=0;
        boolean first = true;
        HashMap<String,Integer> votosTotaisLista = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        synchronized(eleicoes){
            for(Eleicao elei : eleicoes){
                synchronized(elei){
                    if(elei.getTitulo().equals(eleicao.getTitulo())){
                        if(eleicao.getFim().compareTo(cal.getTime())<0){
                            synchronized(elei.getVotos()){
                                for(Voto voto : elei.getVotos()){
                                    synchronized(voto){
                                        if(first){
                                            //retira da primeira mesa de voto o numero de votos em cada lista e coloca-o juntamente com a lista na HasmMap votosTotaisLista
                                            for (HashMap.Entry<Lista, Integer> entry : voto.getVotosListas().entrySet()) {
                                                votosTotaisLista.put(entry.getKey().getNome(), entry.getValue());
                                            }
                                            votosTotaisLista.put("Brancos", voto.getBrancos());
                                            votosTotaisLista.put("Nulos", voto.getNulos());
                                            first = false;
                                        }
                                        else{
                                            //incrementa o numero de votos dos outros departamentos em cada lista correspondente na HashMap votosTotaisLista
                                            for (HashMap.Entry<Lista, Integer> entry : voto.getVotosListas().entrySet()) {
                                                votosTotaisLista.put(entry.getKey().getNome(),votosTotaisLista.get(entry.getKey().getNome())+entry.getValue());
                                            }
                                            votosTotaisLista.put("Brancos", votosTotaisLista.get("Brancos")+voto.getBrancos());
                                            votosTotaisLista.put("Nulos", votosTotaisLista.get("Nulos")+voto.getNulos());
                                        }
                                        total+=voto.contaVotos();
                                    }
                                }
                            }
                            for (HashMap.Entry<String, Integer> entry : votosTotaisLista.entrySet()) {
                                totais = totais.concat(entry.getKey()+": "+entry.getValue()+"\n");
                                double total2 = (double)total;
                                percentagem = percentagem.concat(entry.getKey()+": "+((entry.getValue()/total2)*100)+"%\n");
                            }
                            retorno = retorno.concat("Número absoluto de votos\n"+totais+"\n"+"Percentagem de votos\n"+percentagem);
                            return retorno;
                        }
                        else{
                            retorno = "Eleição ainda não concluída";
                            return retorno;
                        }
                    }
                }
            }
        }
        return retorno;
    }
    
    public void eleicaoFinalizada(){
        Calendar cal = Calendar.getInstance();
        synchronized(eleicoes){
            for(Eleicao eleicao : eleicoes){
                synchronized(eleicao){
                    if(eleicao.getFim().compareTo(cal.getTime())>0){
                        eleicao.setFinalizada(true);
                    }
                }
            }
        }
    }
    
    /**
     * Metodo utilizado para adicionar um tcp server ao array de tcp servers presente nesta classe
     * @param consola 
     */
    public void adicionaConsola(TCPServerInterface consola){
        this.consolas.add(consola);
    }
     
    /**
     * Metodo utilizado para ver quais os tcp servers activos atraves do uso de callback
     * @param consola
     * @return 
     */
    public boolean consolaOn(TCPServerInterface consola){
        try{
            consola.getNomeDep();
            return true;
        }
        catch(RemoteException e){
            return false;
        }
    }
    
    /**
     * Metodo para listar tcp servers que se encontram ligados de momento
     * @return
     * @throws RemoteException 
     */
    public String listaConsolasOn() throws RemoteException{
        String ret = "Consolas on\n\n";
        for(int i=0;i<consolas.size();i++){
            if(this.consolaOn(consolas.get(i))){
                ret = ret.concat(consolas.get(i).getNomeDep()+"\n");
            }
        }
        return ret;
    }
}