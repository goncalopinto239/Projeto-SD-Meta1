
package meta1;


public class Departamento implements java.io.Serializable {
    private Faculdade faculdade;
    private String nome;
    
    public Departamento(String nome, Faculdade faculdade){
        this.nome = nome;
        this.faculdade = faculdade;
    }
    
    public String getNome(){
        return this.nome;
    }
    
    public void setNome(String nome){
        this.nome = nome;
    }
    
    public Faculdade getFaculdade(){
        return this.faculdade;
    }
    
    public void setFaculdade(Faculdade faculdade){
        this.faculdade = faculdade;
    }
}
