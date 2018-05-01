
package meta1;

import java.util.Date;
import java.util.ArrayList;

public class Eleicao implements java.io.Serializable{
    private String tipo;
    private Date inicio;
    private Date fim;
    private String titulo;
    private String descricao;
    private boolean finalizada;
    private ArrayList<Lista> listas;
    private ArrayList<mesaVoto> mesas;
    private ArrayList<Voto> votos;
    
    public Eleicao(String tipo, Date inicio, Date fim, String titulo, String descricao
            ){
        this.tipo = tipo;
        this.inicio = inicio;
        this.fim = fim;
        this.titulo = titulo;
        this.descricao = descricao;
        this.finalizada = false;
        this.listas = new ArrayList<>();
        this.mesas = new ArrayList<>();
        this.votos = new ArrayList<>();
    }
    
    public String getTipo(){
        return this.tipo;
    }
    
    public Date getInicio(){
        return this.inicio;
    }
    
    public Date getFim(){
        return this.fim;
    }
    
    public String getTitulo(){
        return this.titulo;
    }
    
    public String getDescricao(){
        return this.descricao;
    }
    
    public ArrayList<Lista> getListas(){
        return this.listas;
    }
    
    public ArrayList<mesaVoto> getmesasVoto(){
        return this.mesas;
    }
    
    public ArrayList<Voto> getVotos(){
        return this.votos;
    }
    
    public boolean getFinalizada(){
        return this.finalizada;
    }
    
    public void setTipo(String tipo){
        this.tipo = tipo;
    }
    
    public void setInicio(Date inicio){
        this.inicio = inicio;
    }
    
    public void setFim(Date fim){
        this.fim = fim;
    }
    
    public void setTitulo(String titulo){
        this.titulo = titulo;
    }
    
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
    
    public void setListas(ArrayList<Lista> listas){
        this.listas = listas;
    } 
    
    public void setmesasVoto(ArrayList<mesaVoto> mesas){
        this.mesas = mesas;
    }
    
    public void setVotos(ArrayList<Voto> votos){
        this.votos = votos;
    }
    
    public void setFinalizada(boolean finalizada){
        this.finalizada = finalizada;
    }
}
