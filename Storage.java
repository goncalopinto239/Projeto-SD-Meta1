
package meta1;

import java.util.ArrayList;
import java.io.Serializable;

public class Storage implements Serializable {
    protected ArrayList<User> lista_users;
    protected ArrayList<Eleicao> lista_eleicoes;
    protected ArrayList<Departamento> lista_departamentos;
    protected ArrayList<Faculdade> lista_faculdades;
    protected ArrayList<Lista> lista_candidatos;
	
    public Storage(){
	this.lista_users=new ArrayList<>();
        this.lista_eleicoes=new ArrayList<>();
	this.lista_departamentos=new ArrayList<>();
	this.lista_faculdades=new ArrayList<>();
        this.lista_candidatos=new ArrayList<>();
    }

    public ArrayList<User> getLista_users() {
        return lista_users;
    }

    public void setLista_users(ArrayList<User> lista_users) {
        this.lista_users = lista_users;
    }

    public ArrayList<Eleicao> getLista_eleicoes() {
        return lista_eleicoes;
    }

    public void setLista_eleicoes(ArrayList<Eleicao> lista_eleicoes) {
        this.lista_eleicoes = lista_eleicoes;
    }

    public ArrayList<Departamento> getLista_departamentos() {
        return lista_departamentos;
    }

    public void setLista_departamentos(ArrayList<Departamento> lista_departamentos) {
        this.lista_departamentos = lista_departamentos;
    }

    public ArrayList<Faculdade> getLista_faculdades() {
        return lista_faculdades;
    }

    public void setLista_faculdades(ArrayList<Faculdade> lista_faculdades) {
        this.lista_faculdades = lista_faculdades;
    }

    public ArrayList<Lista> getLista_candidatos() {
        return lista_candidatos;
    }

    public void setLista_candidatos(ArrayList<Lista> lista_candidatos) {
        this.lista_candidatos = lista_candidatos;
    }
    
}
