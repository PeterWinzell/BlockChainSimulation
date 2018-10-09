/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 *
 * @author pwinzell
 */
public abstract class BaseTransaction  {

    

    
    
    protected String transactionId; //Contains a hash of transaction*
    
    protected PublicKey sender; //Senders address/public key.
    protected PublicKey reciepient; //Recipients address/public key.
    
    protected BlockChainNode senderNode;
    protected BlockChainNode reciepientNode;
    protected double value; //Contains the amount we wish to send to the recipient.
    protected byte[] signature; //This is to prevent anybody else from spending funds in our wallet.
	
	
     protected static int sequence = 0; //A rough count of how many transactions have been generated 
	
	// Constructor: 
     public BaseTransaction(BlockChainNode from, BlockChainNode to, double value) {
		this.senderNode = from;
		this.reciepientNode = to;
                
                sender = senderNode.getWallet().getPublicKey();
                reciepient = reciepientNode.getWallet().getPublicKey(); // we actually only store values 
		this.value = value;
                sequence++;
                transactionId = Integer.toString(sequence);
		
      }
	
      abstract public boolean processTransaction();
	
	
	
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Double.toString(value)	;
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
	
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Double.toString(value) + sequence
				);
	}
        
        
        public String getTransactionId() {
            return transactionId;
        }
        
        @Override
        public String toString(){
            return new String(transactionId + ":" + value);
        }
}
