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
public class NapWallet {
    
    private List<Nap> wallet;
    private double naps = 0.0;
    
    public NapWallet(Nap initialNap){
        wallet = new ArrayList();
        wallet.add(initialNap);
    }
    
    public void addNaps(double napstobeadded){
        Nap newNap = new Nap(napstobeadded);
        wallet.add(newNap);
    }
    
    // withdraw naps from the wallet naps until the entire amount is subtracted
    public void deleteNaps(Nap nap){
        double withdrawamout = nap.getAmount();
        for (Nap anap:wallet){
            double difference = anap.amount - withdrawamout;
            
            if (difference < 0 )
                anap.setAmount(0);
            else{
               anap.setAmount(difference);
               break;
            }   
            
            withdrawamout = Math.abs(difference);
        }
    }
    
    public double getNapWealth(){
        double wealth = 0.0;
        for (Nap nap : wallet) {
             wealth = wealth + nap.getAmount();
         }
        return wealth;
    }
    
    public void addNap(Nap nap){
        wallet.add(nap);
    }
}
