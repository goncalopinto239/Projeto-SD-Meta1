
package meta1;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;


class ConsolaAdministrador {
    private Scanner input=new Scanner(System.in);
    private static RMIInterface serverInterface;
    
    public ConsolaAdministrador() {	
    }
    
    public static void main(String[] args) {
    	System.getProperties().put("java.security.policy","policy.all");
        System.setSecurityManager(new RMISecurityManager());
        ConsolaAdministrador admin=new ConsolaAdministrador();
        
    	try{
            admin.serverInterface = (RMIInterface) Naming.lookup("RMIServer");
            admin.run();
        }
        catch(NotBoundException | MalformedURLException | RemoteException e){
            System.out.println("Exception on lookup" + e);
        }     
    }
    
    /**
     * Metodo utilizado para pedir os dados necessarios ao administrador para proceder a criacao de um utilizador
     * @throws NotBoundException
     * @throws RemoteException 
     */
    private void registaUser() throws NotBoundException, RemoteException {
        boolean result = false;
        Departamento dep;
        String tipo,nome,password,telemovel,cc,morada,validade,nome_dep;
        do{
            System.out.println("\nTipo de utilizador: ");
            tipo=input.nextLine();
        }while(!tipoUtilizador(tipo));
        do{
            System.out.println("\nNome de utilizador: ");
            nome=input.nextLine();
        }while(!dadosAutenticacao(nome));
        do{
        System.out.println("\nPassword: ");
        password=input.nextLine();
        }while(!dadosAutenticacao(password));
        do{
            System.out.println("\nTelemovel: ");
            telemovel=input.nextLine();
        }while(!telemovelUtilizador(telemovel));
        do{
            System.out.println("\nNumero de cartao de cidadao: ");
            cc=input.nextLine();
        }while(!ccUtilizador(cc));
        do{
            System.out.println("\nMorada: ");
            morada=input.nextLine();
        }while(!nomes(morada));
        do{
            System.out.println("\nData de validade do cartao de cidadao: ");
            validade=input.nextLine();
        }while(!validadeUtilizador(validade));
        do{
            System.out.println("\nDepartamento de utilizador de utilizador: ");
            nome_dep=input.nextLine();
            dep = serverInterface.departamentoExistente(nome_dep);
        }while(serverInterface.departamentoExistente(nome_dep)==null);
        result = serverInterface.registerUser(tipo,nome,password,telemovel,cc,morada,validade,dep);
        if (result) {
            System.out.println("Utilizador criado");
        } else {
            System.out.println("Não foi possível criar o utilizador");
        }
    }
    
    /**
     * Metodo utilizado para pedir ao administrador os dados necessarios a criacao de uma lista de candidatos e proceder a criacao da mesma
     * @throws RemoteException 
     */
    private void criaListaCandidatos() throws RemoteException{
        String tipo,nome;
        boolean result = false;
        do{
            System.out.println("Tipo de lista de candidatos:");
            tipo = input.nextLine();
        }while(!tipoUtilizador(tipo));
        do{
            System.out.println("Nome da lista de candidatos:");
            nome = input.nextLine();
        }while(!nomes(nome));
        result = serverInterface.criaListaCandidatos(tipo, nome);
        if(result){
            System.out.println("Lista criada com sucesso");
        }
        else{
            System.out.println("Lista já existente");
        }
    }
    
    /**
     * Metodo utilizado para adicionar uma lista de candidatos a uma eleicao por parte do administrador
     * @throws RemoteException 
     */
    private void adicionaListaCandidatos() throws RemoteException{
        Eleicao eleicao;
        Lista lista;
        String nomeLista;
        String tituloEleicao;
        do{
            System.out.println("Titulo da eleicao a adicionar lista de candidatos:");
            tituloEleicao = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(tituloEleicao);
        }while(serverInterface.eleicaoExistente(tituloEleicao)==null);
        do{
            System.out.println("Nome da lista de candidatos a adicionar:");
            nomeLista = input.nextLine();
            lista = serverInterface.listaExistente(nomeLista);
        }while(serverInterface.listaExistente(nomeLista)==null);
        serverInterface.adicionaListaCandidatos(eleicao, lista);
        
    }
    /**
     * Metodo utilizado para remover uma lista de candidatos de uma dada eleicao
     * @throws RemoteException 
     */
    private void removeListaCandidatos() throws RemoteException{
        Eleicao eleicao;
        Lista lista;
        String nomeLista;
        String tituloEleicao;
        do{
            System.out.println("Titulo da eleicao a eliminar lista de candidatos:");
            tituloEleicao = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(tituloEleicao);
        }while(serverInterface.eleicaoExistente(tituloEleicao)==null);
        do{
            System.out.println("Nome da lista de candidatos a eliminar:");
            nomeLista = input.nextLine();
            lista = serverInterface.listaExistente(nomeLista);
        }while(serverInterface.listaExistente(nomeLista)==null);
        serverInterface.removeListaCandidatos(eleicao, lista);
        
    }
    
    /**
     * Metodo utilizado para pedir todos os dados necessarios a criacao de uma eleicao e proceder a criacao da mesma
     * @throws RemoteException 
     */
    private void criaEleicao() throws RemoteException{
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String tipo,inicio,fim,titulo,descricao;
        boolean conf;
        Date dateI = new Date();
        Date dateF = new Date();
        do{
            System.out.println("Tipo da eleicao:");
            tipo = input.nextLine();
        }while(!tipoEleicao(tipo));
        do{
            System.out.println("Data de inicio da eleicao:");
            inicio = input.nextLine();
        }while(!dataEleicao(inicio));
        do{
            System.out.println("Data de fim da eleicao:");
            fim = input.nextLine();
        }while(!dataEleicao(fim));
        do{
            System.out.println("Titulo da eleicao:");
            titulo = input.nextLine();
        }while(!nomes(titulo));
        do{
            System.out.println("Descricao da eleicao:");
            descricao = input.nextLine();
        }while(!nomes(descricao));
        try {
            dateI = formatter.parse(inicio);
            dateF = formatter.parse(fim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conf = serverInterface.criaEleicao(tipo,dateI,dateF,titulo,descricao);
        if(conf){
            System.out.println("Eleicao criada");
        }
        else{
            System.out.println("Eleicao ja existente");
        }
    }
    
    /**
     * Metodo utilizado para adicionar uma mesa de voto a uma dada eleicao
     * @throws RemoteException 
     */
    private void adicionaMesaVoto() throws RemoteException{
        String titulo,departamento;
        Eleicao eleicao;
        Departamento dep;
        do{
            System.out.println("Titulo da eleicao a adicionar mesa de voto:");
            titulo = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(titulo);
        }while(serverInterface.eleicaoExistente(titulo)==null);
        do{
            System.out.println("Nome do departamento a adicionar mesa de voto::");
            departamento = input.nextLine();
            dep = serverInterface.departamentoExistente(departamento);
        }while(serverInterface.departamentoExistente(departamento)==null);;
        serverInterface.adicionaMesaVoto(eleicao,dep);
    }
    
    /**
     * Metodo utilizado para remover uma mesa de voto de uma dada eleicao
     * @throws RemoteException 
     */
    private void removeMesaVoto() throws RemoteException{
        String titulo,departamento;
        Eleicao eleicao;
        Departamento dep;
        do{
            System.out.println("Titulo da eleicao a remover mesa de voto:");
            titulo = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(titulo);
        }while(serverInterface.eleicaoExistente(titulo)==null);
        do{
            System.out.println("Nome do departamento a remover mesa de voto::");
            departamento = input.nextLine();
            dep = serverInterface.departamentoExistente(departamento);
        }while(serverInterface.departamentoExistente(departamento)==null);
        serverInterface.removeMesaVoto(eleicao, dep);
    }
    
    /**
     * Metodo utilizado para pedir os dados necessarios a criacao de um departamento e proceder a criacao do mesmo
     * @throws NotBoundException
     * @throws RemoteException 
     */
    public void insereDepartamento() throws NotBoundException, RemoteException  {
    	boolean result = false;
    	String nome_dep,nome_fac;
        Faculdade fac;
    	System.out.println("\nNome do departamento: ");
    	nome_dep=input.nextLine();
        do{
            System.out.println("\nNome da faculdade a que o departamento pertence: ");
            nome_fac=input.nextLine();
            fac = serverInterface.faculdadeExistente(nome_fac);
        }while(serverInterface.faculdadeExistente(nome_fac)==null);
        result = serverInterface.insereDepartamento(nome_dep, fac);
        if (result) {
            System.out.println("Departamento inserido");
        } else {
            System.out.println("Departamento ja existente");
        }
    }
    
    /**
     * Metodo utilizado para alterar os dados de um dado departamento
     * @throws RemoteException 
     */
    public void alteraDepartamento() throws RemoteException {
    	boolean result = false;
    	String nome_dep,novo_nome;
    	System.out.println("\nNome do departamento a alterar: ");
    	nome_dep=input.nextLine();
    	System.out.println("\nNovo nome do departamento: ");
    	novo_nome=input.nextLine();
        result = serverInterface.alteraDepartamento(nome_dep, novo_nome);
        if (result) {
            System.out.println("Departamento alterado");
        } else {
            System.out.println("N„o È possÌvel alterar o departamento");
        }
    }
    
    /**
     * Metodo utilizado para remover um dado departamento
     * @throws RemoteException 
     */
    public void removeDepartamento() throws RemoteException {
    	boolean done = false, result = false;
    	String nome_dep;
    	System.out.println("\nNome do departamento a remover: ");
    	nome_dep=input.nextLine();
        result = serverInterface.removeDepartamento(nome_dep);
        if (result) {
            System.out.println("Departamento removido");
        } else {
            System.out.println("N„o È possÌvel remover o departamento");
        }
    }
    
    /**
     * Metodo utilizado para pedir os dados necessarios a criacao de uma faculdade ao administrador e proceder a criacao da mesma
     * @throws NotBoundException
     * @throws RemoteException 
     */
    private void insereFaculdade()throws NotBoundException, RemoteException{
        String nome;
        boolean conf;
        System.out.println("Nome da faculdade:");
        nome = input.nextLine();
        conf = serverInterface.insereFaculdade(nome);
        if(conf){
            System.out.println("Faculdade inserida");
        }
        else{
            System.out.println("Faculdade ja existente");
        }
        
    }
    
    /**
     * Metodo para alterar os dados de uma faculdade
     * @throws RemoteException 
     */
    public void alteraFaculdade() throws RemoteException {
    	boolean done = false, result = false;
    	String nome_fac,novo_nome;
    	System.out.println("\nNome da faculdade a alterar: ");
    	nome_fac=input.nextLine();
    	System.out.println("\nNovo nome da faculdade: ");
    	novo_nome=input.nextLine();
        result = serverInterface.alteraFaculdade(nome_fac, novo_nome);
        if (result) {
            System.out.println("Faculdade alterada");
        } else {
            System.out.println("N„o È possÌvel alterar a faculdade");
        }
    }
    
    /**
     * Metodo utilizado para remover uma dada faculdade
     * @throws RemoteException 
     */
    public void removeFaculdade() throws RemoteException {
    	boolean done = false, result = false;
    	String nome_fac;
    	System.out.println("\nNome da faculdade a remover: ");
    	nome_fac=input.nextLine();
        result = serverInterface.removeFaculdade(nome_fac);
        if (result) {
            System.out.println("Faculdade removida");
        } else {
            System.out.println("N„o È possÌvel remover a faculdade");
        }
    }
    
    /**
     * Metodo para alterar as propriedades de uma eleicao 
     * @throws RemoteException 
     */
    public void alteraEleicao() throws RemoteException {
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	boolean result = false;
        Eleicao eleicao = null;
    	String titulo,novo_tit,nova_desc,data;
    	Date date = new Date();
        do{
            System.out.println("\nTitulo da eleicao a alterar: ");
            titulo=input.nextLine();
            eleicao =serverInterface.eleicaoExistente(titulo);
        }while(serverInterface.eleicaoExistente(titulo)==null);
    	String opcao;
    	System.out.println("\nPropriedade a alterar:");
    	System.out.println(" 1. Titulo:");
        System.out.println(" 2. Descricao");
        System.out.println(" 3. Data inicial:");
        System.out.println(" 4. Data final:");
        System.out.println("Opção: ");
        opcao=input.nextLine();
        switch(opcao) {
        	case "1":
                        do{
                            System.out.println("\nNovo titulo: ");
                            novo_tit=input.nextLine();
                        }while(!nomes(novo_tit));
                        result = serverInterface.alteraPropriedadesTextuais(eleicao,1,novo_tit);
        		break;
        	case "2":
                        do{
                            System.out.println("\nNova descricao: ");
                            nova_desc=input.nextLine();
                        }while(!nomes(nova_desc));
                        result = serverInterface.alteraPropriedadesTextuais(eleicao,2,nova_desc);
        		break;
        	case "3":
                        do{
                            System.out.println("\nNova data de inicio: ");
                            data = input.nextLine();
                        }while(!dataEleicao(data));
        		try {
                            date = formatter.parse(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        result = serverInterface.alteraDatas(eleicao,1,date);
        		break;
        	case "4":
                        do{
                            System.out.println("\nNova data de fim: ");
                            data = input.nextLine();
                        }while(!dataEleicao(data));
        		try {
                            date = formatter.parse(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        result = serverInterface.alteraDatas(eleicao,2,date);
        		break;
        }
        if (result) {
            System.out.println("Eleicao alterada");
        } else {
            System.out.println("Nao e possivel alterar a eleicao em causa");
        }
    }
    
    /**
     * Metodo utilizado para verificar o local e data de voto de um utilizador numa dada eleicao
     * @throws RemoteException 
     */
    public void localVoto() throws RemoteException{
        String userName,titulo;
        User user;
        Eleicao eleicao;
        do{
            System.out.println("Nome de utilizador:");
            userName = input.nextLine();
            user = serverInterface.userExists(userName);
        }while(serverInterface.userExists(userName)==null);
        do{
            System.out.println("Titulo da eleicao:");
            titulo = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(titulo);
        }while(serverInterface.eleicaoExistente(titulo)==null);
        System.out.println(serverInterface.localVoto(eleicao, user));
    }
    
    /**
     * Metodo utilizado para consultar os detalhes de uma eleicao ja concluida
     * @throws RemoteException 
     */
    public void detalhesEleicaoPassada() throws RemoteException{
        String titulo;
        Eleicao eleicao;
        do{
            System.out.println("Titulo da eleicao:");
            titulo = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(titulo);
        }while(serverInterface.eleicaoExistente(titulo)==null);
        System.out.println(serverInterface.detalhesEleicaoPassada(eleicao));
    }
    
    /**
     * Metodo para listar os tcp servers activos no momento
     * @throws RemoteException 
     */
    public void listaConsolasOn() throws RemoteException{
        System.out.println(serverInterface.listaConsolasOn());
    }
    
    /**
     * Metodo utilizado para que o administrador tenha nocao do numero de votos por departamento de uma eleicao
     * @throws RemoteException 
     */
    public void eleicoesTempoReal() throws RemoteException{
        String titulo;
        Eleicao eleicao;
        do{
            System.out.println("Titulo da eleicao:");
            titulo = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(titulo);
        }while(serverInterface.eleicaoExistente(titulo)==null);
        System.out.println(serverInterface.eleitoresTempoReal(eleicao));
    }
    
    /**
     * Metodo para listar as listas candidatas a uma dada eleicao
     * @throws RemoteException 
     */
    public void listasCandidatasEleicao() throws RemoteException{
        String titulo;
        Eleicao eleicao;
        do{
            System.out.println("Titulo da eleicao:");
            titulo = input.nextLine();
            eleicao = serverInterface.eleicaoExistente(titulo);
        }while(serverInterface.eleicaoExistente(titulo)==null);
        System.out.println(serverInterface.listasEleicao(eleicao));
    }
    
    /**
     * Metodo utilizado como protecao para o tipo de utilizador
     * @param tipo
     * @return 
     */
    public boolean tipoUtilizador(String tipo){
        if(tipo.equals("Estudante") || tipo.equals("Docente") || tipo.equals("Funcionario")){
            return true;
        }
        else{
            System.out.println("Tipo de utilizador invalido");
            return false;
        }
    }
    
    /**
     * Metodo utilizado como protecao para o tipo de eleicao
     * @param tipo
     * @return 
     */
    public boolean tipoEleicao(String tipo){
        if(tipo.equals("Nucleo de estudantes") || tipo.equals("Conselho geral")){
            return true;
        }
        else{
            System.out.println("Tipo de eleicao invalido");
            return false;
        }
    }
    
    /**
     * Metodo utilizado como protecao para todos os dados textuais
     * @param nome
     * @return 
     */
    public boolean nomes(String nome){
        if(nome.matches("[a-zA-Z\\s\'\"]+")){
            return true;
        }
        else{
            return false;
        }
    }
    /*ver*/
    public boolean dadosAutenticacao(String nome) {
        if(nome.contains(";") || nome.contains("|")){
            System.out.println("Dado invalido");
            return false;
        }   
        return true;
    }
    
    /**
     * Metodo utilizado para protecao do numero de telemovel de um utilizador
     * @param telemovel
     * @return 
     */
    public boolean telemovelUtilizador(String telemovel) {
        if (telemovel.matches("[0-9]+") && telemovel.length()==9) {
            return true;
        } else {
            System.out.println("Numero de telemovel invalido");
            return false;
        }
    }
    
    /**
     * Metodo para protecao do numero de cartao de cidadao de um utilizador
     * @param cc
     * @return 
     */
    public boolean ccUtilizador(String cc) {
        if (cc.matches("[0-9]+") && cc.length()==10) {
            return true;
        } else {
            System.out.println("Numero de cartao de cidadao invalido");
            return false;
        }
    }
   
    /**
     * Metodo para protecao da data de validade do cartao de cidadao de um utilizador 
     * @param validade
     * @return 
     */
    public boolean validadeUtilizador(String validade) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        try {
            date = formatter.parse(validade);
            return true;
        } catch (Exception e) {
            System.out.println("Data invalida");
            return false;
        }
    }
   
    /**
     * Metodo utilizado para protecao referente a datas
     * @param data
     * @return 
     */
    public boolean dataEleicao(String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        try {
            date = formatter.parse(data);
            return true;
        } catch (Exception e) {
            System.out.println("Data invalida");
            return false;
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
                serverInterface =(RMIInterface) Naming.lookup("RMIServer");
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
    private void run() throws NotBoundException, RemoteException, MalformedURLException {
    	String opcao="1";
    	Scanner input=new Scanner(System.in);
    	while(!"0".equals(opcao)) {
            menu();
            opcao=input.nextLine();
            switch(opcao) {
                case "1":
                    try{
                      this.registaUser();  
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
    		case "2":
                    try{
                      this.insereFaculdade();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "3":
                    try{
                      this.alteraFaculdade();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "4":
                    try{
                      this.removeFaculdade();  
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "5":
                    try{
                      this.insereDepartamento();  
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "6":
                    try{
                      this.alteraDepartamento();  
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "7":
                    try{
                      this.removeDepartamento();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "8":
                    try{
                      this.criaEleicao();  
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "9":
                    try{
                      this.criaListaCandidatos();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "10":
                    try{
                      this.removeListaCandidatos();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "11":
                    try{
                      this.adicionaMesaVoto();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "12":
                    try{
                      this.removeMesaVoto();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "13":
                    try{
                      this.localVoto();  
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "14":
                    try{
                      this.listaConsolasOn();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "15":
                    try{
                      this.detalhesEleicaoPassada();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "16":
                    try{
                      this.alteraEleicao();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "17":
                    try{
                      this.adicionaListaCandidatos();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "18":
                    try{
                        System.out.println(serverInterface.listaUsers());
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "19":
                    try{
                        System.out.println(serverInterface.listaEleicoes());
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "20":
                    try{
                        listasCandidatasEleicao();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "21":
                    try{
                      this.eleicoesTempoReal();
                    }catch(RemoteException e){
                        RMIServerUp();
                    }
                    break;
                case "0":
                    return;
            }
    	}  	
    }
    
    /**
     * Menu onde sao apresentadas as diferentes opcoes existentes de consola de administrador
     */
    private void menu() {
        System.out.println("|--------------------------------------|");
        System.out.println("|               MENU                   |");
        System.out.println("|--------------------------------------|");
        System.out.println(" 1. Registar utilizador");
        System.out.println(" 2. Inserir faculdade");
        System.out.println(" 3. Alterar faculdade");
        System.out.println(" 4. Remover faculdade");
        System.out.println(" 5. Inserir departamento");
        System.out.println(" 6. Alterar departamento");
        System.out.println(" 7. Remover departamento");
        System.out.println(" 8. Criar eleicao");
        System.out.println(" 9. Criar lista de candidatos");
        System.out.println(" 10. Remover lista de candidatos");
        System.out.println(" 11. Adicionar mesa de voto a uma eleicao");
        System.out.println(" 12. Remover mesa de voto a uma eleicao");
        System.out.println(" 13. Local de voto de um eleitor");
        System.out.println(" 14. Listar mesas de voto activas");
        System.out.println(" 15. Detalhes eleicao finalizada");
        System.out.println(" 16. Alterar propriedades de uma eleicao");
        System.out.println(" 17. Adicionar lista de candidatos a uma eleicao");
        System.out.println(" 18. Listar utilizadores");
        System.out.println(" 19. Listar eleicoes");
        System.out.println(" 20. Listar listas candidatas a uma eleicao");
        System.out.println(" 21. Votos de uma eleicao em tempo real");
        System.out.print(" Opcao: ");
    }
}