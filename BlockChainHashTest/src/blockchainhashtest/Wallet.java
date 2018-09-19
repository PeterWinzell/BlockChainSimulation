/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchainhashtest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

/**
 *
 * @author Peter Winzell
 */
import java.security.NoSuchAlgorithmException;/**
 *
 * @author Peter Winzell
 */
public class Wallet {
    KeyPair keypair;
    
    Wallet() throws NoSuchAlgorithmException{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keypair = keyGen.genKeyPair();
    }
    
    public PublicKey getPublicKey(){
        return keypair.getPublic();
    }
    
   
    
}
