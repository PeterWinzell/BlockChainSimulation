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
public interface TransactionMemoryPool<KEY> {
    
    void addTransaction(TransactionMessage transaction);
    void deleteTransaction(TransactionMessage transaction);
    void deleteAll();
    void signTransaction(TransactionMessage transaction,KEY priavteKey);
}
