
package meta1;

public class User implements java.io.Serializable {
    private String tipo;
    private String username;
    private String password;
    private boolean online;
    private String telemovel;
    private String numeroCartao;
    private String morada;
    private String validadeCartao;
    private Departamento departamento;
    
    public User (String tipo, String username, String password, String telemovel, String numeroCartao, String morada,
            String validadeCartao, Departamento departamento){
        this.tipo = tipo;
        this.username = username;
        this.password = password;
        this.telemovel = telemovel;
        this.numeroCartao = numeroCartao;
        this.morada = morada;
        this.validadeCartao = validadeCartao;
        this.departamento = departamento;
        this.online = false;
    }
    
    public String getType() {
        return this.tipo;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public boolean getOnline() {
        return this.online;
    }
    
    public String getTelemovel() {
        return this.telemovel;
    }
    
    public String getNumeroCartao() {
        return this.numeroCartao;
    }
    
    public String getMorada() {
        return this.morada;
    }
    
    public String getValidadeCartao() {
        return this.validadeCartao;
    }
    
    public Departamento getDepartamento() {
        return this.departamento;
    }
    
    public void setType(String tipo) {
        this.tipo = tipo;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
   
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public void setTelemovel(String telemovel) {
        this.telemovel = telemovel;
    }
    
    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }
    
    public void setMorada(String morada) {
        this.morada = morada;
    }
    
    public void setValidadeCartao(String validadeCartao) {
        this.validadeCartao = validadeCartao;
    }
    
    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}