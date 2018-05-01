
package meta1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Files {
    private ObjectOutputStream oS;
    private ObjectInputStream iS;
	
    public boolean abreLeitura(String nomeDoFicheiro) {
        try{
            iS = new ObjectInputStream(new FileInputStream(nomeDoFicheiro));
            return true;
        }
        catch (IOException e){
            return false;
        }
    }
    
    public void abreEscrita(String nomeDoFicheiro) throws IOException{
        oS = new ObjectOutputStream(new FileOutputStream(nomeDoFicheiro));
    }
    
    public Object leObjecto() throws IOException,ClassNotFoundException{
        return iS.readObject();
    }
    
    public void escreveObjecto(Object o) throws IOException{
        oS.writeObject(o);
    }
    
    public void fechaLeitura() throws IOException{
        iS.close();
    }
    
    public void fechaEscrita() throws IOException{
        oS.close();
    }
	
}
