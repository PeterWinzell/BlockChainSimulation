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
public class Message extends MessageBase{

    private BroadCastListener sender;
    private BroadCastListener reciever;
    private Transaction_type  transaction;
    private Nap amount;
            
   
    public void sign(Object privateKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Message(BroadCastListener sender,BroadCastListener reciever,Transaction_type t,Nap amount){
        
        this.sender = sender;
        this.reciever = reciever;
        this.transaction = t;
        this.amount = amount;      
        
    }

    public BroadCastListener getSender() {
        return sender;
    }

    public BroadCastListener getReciever() {
        return reciever;
    }

    public Transaction_type getTransaction() {
        return transaction;
    }

    public Nap getAmount() {
        return amount;
    }

    public void setSender(BroadCastListener sender) {
        this.sender = sender;
    }

    public void setReciever(BroadCastListener reciever) {
        this.reciever = reciever;
    }

    public void setTransaction(Transaction_type transaction) {
        this.transaction = transaction;
    }

    public void setAmount(Nap amount) {
        this.amount = amount;
    }
    
    
    
}
