package DSCoinPackage;
import java.util.*;
import HelperClasses.*;
public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) 
  {
    Members mod= new Members();
    mod.UID="Moderator";
    
    String cid="100000";
    String cid1="100000";
    int l=DSObj.memberlist.length;
    //int i=0;
    Transaction[] ta=new Transaction[coinCount];
    
    //Distributing coins in round robin
    for(int i=0;i<coinCount;i++)
    {
      Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(cid1,null);
      DSObj.memberlist[i%l].mycoins.add(p);
      cid1=Integer.toString(Integer.parseInt(cid1)+1);//updation of cid
    }
    //Setting the last coinId attribute
    DSObj.latestCoinID=cid1;

    //Creating the transactions
    for(int i=0;i<coinCount;i++)
    {
      ta[i]=new Transaction();
      ta[i].Source=mod;
      ta[i].Destination=DSObj.memberlist[i%l];//.UID;
      ta[i].coinID=cid;
      cid=Integer.toString(Integer.parseInt(cid)+1);//updation of cid
      ta[i].next=null;
      ta[i].previous=null;
    }
    
    
    //Creating the transaction blocks
    int b=0;
    int e=DSObj.bChain.tr_count;
    int cc=coinCount;
    while(cc>0)
    {
      TransactionBlock tB=new TransactionBlock(Arrays.copyOfRange(ta,b,e));
      b=e;
      e=e+DSObj.bChain.tr_count;
      if(e>cc)
      {
        e=cc;//case when end overshoots
      }
      //System.out.println("Inserting bloock");
      DSObj.bChain.InsertBlock_Honest(tB);
      cc=cc-DSObj.bChain.tr_count;
    }
    //Updating the attributes


    

  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) 
  {
    Members mod= new Members();
    mod.UID="Moderator";
    
    String cid="100000";
    String cid1="100000";
    int l=DSObj.memberlist.length;
    //int i=0;
    Transaction[] ta=new Transaction[coinCount];
    
    // //Distributing coins in round robin
    // for(int i=0;i<coinCount;i++)
    // {
    //   Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(cid1,null);
    //   DSObj.memberlist[i%l].mycoins.add(p);
    //   cid1=Integer.toString(Integer.parseInt(cid1)+1);//updation of cid
    // }
    //Setting the last coinId attribute
    DSObj.latestCoinID=cid1;//Integer.toString(Integer.parseInt(cid1)+1);

    //Creating the transactions
    for(int i=0;i<coinCount;i++)
    {
      ta[i]=new Transaction();
      ta[i].Source=mod;
      ta[i].Destination=DSObj.memberlist[i%l];//.UID;
      ta[i].coinID=cid;
      ta[i].coinsrc_block=null;
      cid=Integer.toString(Integer.parseInt(cid)+1);//updation of cid
      ta[i].next=null;
      ta[i].previous=null;
    }
    
    
    //Creating and inserting the transaction blocks in the blockchain
    int b=0;
    int e=DSObj.bChain.tr_count;
    int cc=coinCount;
    TransactionBlock[] tba=new TransactionBlock[cc/DSObj.memberlist.length];
    int x=0;
    while(cc>0)
    {
      TransactionBlock tB=new TransactionBlock(Arrays.copyOfRange(ta,b,e));
      b=e;
      e=e+DSObj.bChain.tr_count;
      if(e>cc)
      {
        e=cc;//case when end overshoots
      }
      DSObj.bChain.InsertBlock_Malicious(tB);
      cc=cc-DSObj.bChain.tr_count;
      tba[x]=tB;
      x++;
    }
    int y=0;
    //Distributing coins in round robin
    for(int i=0;i<coinCount;i++)
    {
      Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(cid1,tba[y/DSObj.bChain.tr_count]);
      DSObj.memberlist[i%l].mycoins.add(p);
      cid1=Integer.toString(Integer.parseInt(cid1)+1);//updation of cid
    }
    //Updating the attributes
    

  }
}
