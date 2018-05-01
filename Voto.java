
package meta1;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Voto implements java.io.Serializable{
    private Eleicao eleicao;
    private HashMap<User,Date> eleitores;
    private mesaVoto mesa;
    private int nulos;
    private int brancos;
    private HashMap<Lista,Integer> votosListas;
    
    public Voto(Eleicao eleicao,mesaVoto mesa,HashMap <Lista,Integer> votosListas){
        this.eleicao = eleicao;
        this.mesa = mesa;
        this.eleitores = new HashMap<>();
        this.nulos = 0;
        this.brancos = 0;
        this.votosListas = votosListas; 
    }

    public Eleicao getEleicao() {
        return eleicao;
    }

    public void setEleicao(Eleicao eleicao) {
        this.eleicao = eleicao;
    }

    public HashMap<User, Date> getEleitores() {
        return eleitores;
    }

    public void seteleitores(User user,Date date) {
        this.eleitores.put(user, date);
    }

    public mesaVoto getMesa() {
        return mesa;
    }

    public void setMesa(mesaVoto mesa) {
        this.mesa = mesa;
    }

    public int getNulos() {
        return nulos;
    }

    public void setNulos(int nulos) {
        this.nulos = nulos;
    }

    public int getBrancos() {
        return brancos;
    }

    public void setBrancos(int brancos) {
        this.brancos = brancos;
    }

    public HashMap<Lista, Integer> getVotosListas() {
        return votosListas;
    }

    public void setVotosListas(HashMap<Lista, Integer> votosListas) {
        this.votosListas = votosListas;
    }
    
    /**
     * Metodo utilizado para verificar a opcao de voto de um determinado utilizador e contabilizar a mesma
     * @param voto
     * @param listas
     * @param user 
     */
    public void confirmaVoto(String voto,ArrayList<Lista> listas,User user){
        Calendar cal = Calendar.getInstance();
        Lista lista = null;
        for(int i=0;i<listas.size();i++){
            if(listas.get(i).getNome().equals(voto)){
                lista = listas.get(i);
                break;
            }
        }
        if(voto.equals("")){
            this.brancos++;
        }
        else if(votosListas.containsKey(lista)){
            votosListas.put(lista, votosListas.get(lista)+1);
        }
        else{
            this.nulos++;
        }
        Date data = cal.getTime();
        eleitores.put(user, data);
    }
    
    /**
     * Metodo utilizado para contabilizar o numero de votos totais obtidos numa eleicao
     * @return 
     */
    public int contaVotos(){
        int cont = 0;
        cont+=this.brancos;
        cont+=this.nulos;
        for (Integer votos : this.getVotosListas().values()) {
            cont+=votos;
        }
        return cont;
    }
}
