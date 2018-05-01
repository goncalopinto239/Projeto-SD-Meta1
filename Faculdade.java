
package meta1;

public class Faculdade implements java.io.Serializable {
    private String nome;
    
    public Faculdade(String nome){
        this.nome = nome;
    }
    
    public String getNome(){
        return this.nome;
    }
    
    public void setNome(String nome){
        this.nome = nome;
    }
}
