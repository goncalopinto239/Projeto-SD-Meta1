
package meta1;

public class mesaVoto implements java.io.Serializable{
    private Departamento departamento;
    
    public mesaVoto(Departamento departamento){
        this.departamento = departamento;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}
