/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.security.PublicKey;

/**
 *
 * @author pwinzell
 */
public class WalletTransaction extends BaseTransaction implements Transaction{

    private static int sequence = 0;
    
    
    public WalletTransaction(BlockChainNode from, BlockChainNode to, double value){
        super(from,to,value);
    }
    
    @Override
    public boolean processTransaction() {
        
        if (this.verifySignature()==false){
            System.out.println(" transaction signature failed");
            return false;
        }
        this.transactionId = calulateHash();
        
        return true;
    }
    
    public String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Double.toString(value) + sequence
				);
    }
}
