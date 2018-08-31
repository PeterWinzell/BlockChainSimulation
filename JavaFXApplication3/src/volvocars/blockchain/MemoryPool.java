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

    

    public void MemoryPool(){
        list = new ArrayList();
    }
    
    @Override
    public void deleteTransaction(TransactionMessage transaction) {
        list.remove(transaction);
    }

    @Override
    public void deleteAll() {
        list.removeAll(list);
    }

    @Override
    public void signTransaction(TransactionMessage transaction, Object priavteKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTransaction(TransactionMessage transaction) {
        list.add(transaction);
    }
}
