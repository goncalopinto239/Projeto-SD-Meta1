
package meta1;

public class Lista implements java.io.Serializable{
    private String tipo;
    private String nome;
    
    public Lista(String tipo,String nome){
        this.tipo = tipo;
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
