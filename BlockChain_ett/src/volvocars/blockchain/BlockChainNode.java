/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Peter Winzell
 * BlockChaiNodes are either forgers or users with a wallet.
 */
public class BlockChainNode<M,L> implements  Runnable, BroadCastListener<M,L>,BroadCastReceiver<M>{
    //id
    protected int index;
    
    //2d reps.
    private int x;
    private int y;
    
    private MemoryPool memPool;
    private boolean isForger = false;
    private List<BlockChainNode> forgerS;
    private ArrayList<Block> blockChain;
    
    private boolean isedge = false;
    
    private boolean visited = false;
    private L listener; 
    
    private NapWallet wallet = null;
    private final int minsize = 5;
    private double odometersetting = 0.0; // the car is never driven 
    
    public BlockChainNode(List<BlockChainNode> forgerList, int index){
        
        try {
            this.wallet = new NapWallet(new Nap(1000*Math.random()));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlockChainNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.index = index;
        this.forgerS = forgerList;
        
        memPool = new MemoryPool();
        addGenesisBlock();
    }
    
    private void addGenesisBlock(){
        if (isForger()){
            Block block = Block.getGenesisBlock();
            blockChain.add(0,block);
        }
    }
    
    public void setEdgeMatrixIndex(int i) {
        this.index = i;
    }

    

    public int getEdgeMatrixIndex() {
        return index;
    }
    
    public boolean getVisitited(){
        return visited;
    }

    public void setVisitied(boolean v){
        this.visited = v;
    }

    @Override
    public void MessageNotification(M message) {
       
    }

    @Override
    public void addBroadCastListener(L listener) {
        this.listener = listener;
    }
    
    
    public void broadCastMessage(Transaction_type m){
        ((BroadCastListener)listener).MessageNotification(m);
    }
   
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public boolean isForger(){
        return isForger;
    }
    
    public boolean hasEdgeTo(BlockChainNode node){
        return true;
    }
    
    public double getSize(){
        return minsize + Math.log(wallet.getNapWealth());
    }

    private boolean stopped = false;
    
    @Override
    public void run() {
        stopped = false;
        
        while(!stopped){
            if (!isForger()){
                TransactionMessage transactionMessage = null;
                if (Math.random() < .5){
                
                    Transaction transaction = getTransaction(Transaction_type.ODOMETER);
                    transactionMessage = new TransactionMessage();
                    transactionMessage.setTransaction(transaction);
                    memPool.addTransaction(transactionMessage);
                
                //message = new Message(this,null,Transaction_type.BUY,new Nap(10));
                //wallet.addNap(nap);
                }
                else{
                //message = new Message(this,null,Transaction_type.SELL,new Nap(10));
                //wallet.deleteNaps(nap);
                }
                ((BroadCastListener)listener).MessageNotification(transactionMessage);
            }    
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BlockChainNode.class.getName()).log(Level.SEVERE, null, ex);
            }
                
        }
        System.out.println(" Thread is done " + index);
    }
    
    private Transaction getTransaction(Transaction_type type){
        if( type == Transaction_type.ODOMETER)
            return new OdometerTransaction(this,this,Math.rint(Math.random() * 100));
        else
            return new WalletTransaction(this,this,Math.rint(Math.random() * 50));
    }
    
    public void stop(){
        stopped = true;
    }

    @Override
    public void receiveMessage(M message) {
        if (isForger()){
            memPool.addTransaction((TransactionMessage) message);
            System.out.println(" mess recieved");
            if (memPool.timeToForge() && findMe()){
                System.out.println(this.index + " is forging...");
                addNewBlock(createNewBlock());
                
            }
        }    
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    
    private boolean findMe(){
        int forgerIndex = (BlockChainNetwork.blockHeight+1) % forgerS.size();
        return (forgerS.get(forgerIndex) == this);
    }
    
    public NapWallet getWallet(){
        return wallet;
    }
    
    public double getOdometer(){
        return odometersetting;
    }
    
    public void setForger(){
        isForger = true;
        forgerS.add(this);
    }
    
    
    public void addNewBlock(Block b){
        int votes = 0;
        for(BlockChainNode forger: forgerS){
            if (forger != this){
                if (forger.voteOnBlock(b)) votes++;
            }
        }
        
        double voting = (double)votes/(double)forgerS.size();
        // 2/3 majorioty vote
        if (voting > 0.66667){
            // only keep two in chain, print the others on file.
            for(BlockChainNode forger: forgerS){
                forger.addAndPrint(b); // send message across forgers and add to self
                forger.removeTransactions(memPool);
                memPool.clearAll(); // do this last
            }
            
        }
            
    }
    
    public void addAndPrint(Block b){
        if (blockChain.size() >= 2){
                Block prevNode = blockChain.get(1);
                Block printNode = blockChain.get(0);
                blockChain.clear();
                blockChain.add(0,prevNode);
                blockChain.add(1,b);
                printNode.print();
            }
            else{
                blockChain.add(1,b);
            }
    }
    
    private Block createNewBlock(){
        return memPool.getNewBlockFromTransactions(blockChain.get(blockChain.size() - 1));   
    }
    
    public boolean voteOnBlock(Block block){
        // validate block here and if ok vote yes.
        return true;
    }

    private void removeTransactions(MemoryPool memPool) {
        if (memPool != this.memPool){
            memPool.clearAll(memPool);
        }
    }
}
