/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchainhashtest;

import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author Peter Winzell
 */
public class DataBlock {
    
    private String name;
    private String ownerId;
    private long value;
    private int transactionType;
    private int flag;
    private String proofKey;
    private String key;

    private int hash;

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setProofKey(String proofKey) {
        this.proofKey = proofKey;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public long getValue() {
        return value;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public int getFlag() {
        return flag;
    }

    public String getProofKey() {
        return proofKey;
    }

    public String getKey() {
        return key;
    }

    public int getHash() {
        return hash;
    }
    
    
    int getBlockHash_1(){
        
        int[] hash = {name != null ? name.hashCode() : 0,
                ownerId != null ? ownerId.hashCode() : 0,
                Long.toString(value).hashCode(),
                Integer.toString(transactionType).hashCode(),
                Integer.toString(flag).hashCode(),
                proofKey != null ? proofKey.hashCode() : 0,
                key != null ? key.hashCode() : 0};
        
        return Arrays.hashCode(hash);
    }
     
    String getData(){
        String[] strings ={ name != null ? name.concat("/") : " ",
                            ownerId != null ? ownerId.concat("/"): " ",
                Long.toString(value),
                Integer.toString(transactionType),
                Integer.toString(flag),
                proofKey != null ? proofKey.concat("/") : " ",
                key != null ? key.concat("/") : " "};
        String str="";
        
        for (int i = 0; i < strings.length; i++)
            str = str.concat(strings[i]);
       
        return str;
    }
    
    String getSHA256Hash(){
        String result = null;
        
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte [] hash = digest.digest(getData().getBytes("UTF-8"));
            result = DatatypeConverter.printHexBinary(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return result;

    }
    
    
}
