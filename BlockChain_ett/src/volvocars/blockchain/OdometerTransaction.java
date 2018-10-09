/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

/**
 *
 * @author pwinzell
 */
public class OdometerTransaction extends BaseTransaction implements Transaction{

    
    
    public OdometerTransaction(BlockChainNode from, BlockChainNode to, double value){
        super(from,to,value);
    }
    
    @Override
    public boolean processTransaction() {
        return true; // for an odometer we really dont have anything to process.
    }
    
    public String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				Double.toString(value) + sequence
				);
    }
    
    @Override
    public String toString(){
        return super.toString() + " km ";
    }
}
