/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

/**
 *
 * @author Peter Winzell
 */
public class TransactionMessage extends MessageBase{

    private Transaction transaction;
    
    public void sign(Object privateKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setTransaction(Transaction transaction){
        this.transaction = transaction;
    }
    
    public Transaction getTransation(){
        return transaction;
    }
}
