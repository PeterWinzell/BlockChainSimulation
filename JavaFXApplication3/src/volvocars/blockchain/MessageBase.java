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
public abstract class MessageBase<KEY> implements Transaction{

    private String signature;
    public String getSignature(){ return signature;} 
        
}
