/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter Winzell
 */
public class MemoryPool implements TransactionMemoryPool
{
    private List<TransactionMessage> list;

    

    public  MemoryPool(){
        list = new ArrayList();
    }
    
    @Override
    public void deleteTransaction(TransactionMessage transaction) {
        synchronized(this){
            list.remove(transaction);
        }    
    }

    @Override
    public void deleteAll() {
        synchronized(this){
            list.removeAll(list);
        }    
    }

    public void emptyTransactions(){
    
            deleteAll();
       
    }
    @Override
    public void signTransaction(TransactionMessage transaction, Object priavteKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTransaction(TransactionMessage transaction) {
        synchronized(this){
            list.add(transaction);
        }
    }
    
    public boolean timeToForge(){
        synchronized(this){
            System.out.println(" time to forge " + list.size());
            return (list.size() >= 20);
        }    
    }

    Block getNewBlockFromTransactions(Block prevBlock) {
        
        Block b = new Block(prevBlock.hash);
        
        for(TransactionMessage transMess: list){
            b.addTransaction(transMess.getTransation());
        }
        //b.calculateHash();
        
        return b;
    }
    
    public void clearAll(){
        synchronized(this){
            list.clear();
        }    
    }
    
    public void clearAll(MemoryPool mined){
        synchronized(this){
            for (TransactionMessage removee:mined.list){
                list.remove(removee);
            }
        }
    }
}
