/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ArrayList<Block> blockChain = new ArrayList();
    private List<Integer> kmList =  Arrays.asList(200,100,50,10,5);
    
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
            
            Block block = Block.getGenesisBlock();
            block.mineBlock(0);
            blockChain.add(0,block);
        
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
        if (isForger())
            return 30;
            
        return Math.min(odometersetting/10, 80) + Math.log(Math.log(wallet.getNapWealth() + odometersetting/4));
        //return minsize + 2*Math.log(wallet.getNapWealth() + odometersetting);
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
                    odometersetting += ((OdometerTransaction)transaction).value;
                    
                    transactionMessage = new TransactionMessage();
                    transactionMessage.setTransaction(transaction);
                    
                    
                    ((BroadCastListener)listener).MessageNotification(transactionMessage);
                }
            }    
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BlockChainNode.class.getName()).log(Level.SEVERE, null, ex);
            }
                
        }
        // System.out.println(" Thread is done " + index);
    }
    
    private Transaction getTransaction(Transaction_type type){
        if( type == Transaction_type.ODOMETER)
            return new OdometerTransaction(this,this,Math.rint(Math.random() * kmList.get(index % 5)));
        else
            return new WalletTransaction(this,this,Math.rint(Math.random() * 50));
    }
    
    public void stop(){
        stopped = true;
    }

    @Override
    public void receiveMessage(M message) {
        synchronized(this){
            if (isForger()){
                memPool.addTransaction((TransactionMessage) message);
                if (memPool.timeToForge() && findMe()){
                    addNewBlock(createNewBlock());

                }
            }   
        }
    }
        
    
    private boolean findMe(){
        int forgerIndex = BlockChainNetwork.blockHeight % forgerS.size();
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
        if (!forgerS.contains(this))
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
        if (voting >= 0.66667){
            // only keep two in chain, print the one going out.
            Block printNode = this.addAndPrint(b);
            BlockChainNetwork.blockHeight++;
            for(BlockChainNode forger: forgerS){
                if (forger != this){
                    forger.addAndPrint(b); // send message across forgers and add to self
                    forger.removeTransactions(memPool);
                }    
            }
            memPool.clearAll();
            if (printNode != null){
                printNode.print();  
            } 
        }
            
    }
    
    public Block addAndPrint(Block b){
        if (blockChain.size() >= 2){
                Block prevNode = blockChain.get(1);
                Block printNode = blockChain.get(0);
                blockChain.clear();
                blockChain.add(0,prevNode);
                blockChain.add(1,b);
                // increase blockheight
                return printNode; 
        }       
        else{
                blockChain.add(1,b);
        }
        return null;
    }
    
    private Block createNewBlock(){
        Block b = memPool.getNewBlockFromTransactions(blockChain.get(blockChain.size() - 1));
        b.mineBlock(0);
        return b;
    }
    
    
    public boolean voteOnBlock(Block block){
        // validate block here and if ok vote yes.
        return block.validate();
    }

    private void removeTransactions(MemoryPool memPool) {
        if (memPool != this.memPool){
            this.memPool.clearAll(memPool);
        }
    }
}
