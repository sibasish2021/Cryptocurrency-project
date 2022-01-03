package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.*;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public TransactionBlock next;//Added attribute
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) 
  {
    int trc=t.length;
    this.trarray=new Transaction[trc];
    for(int i=0;i<trc;i++)
    {
      this.trarray[i]=t[i];
    }
    this.Tree=new MerkleTree();
    this.Tree.Build(trarray);
    this.previous=null;
    this.next=null;
    this.trsummary=this.Tree.rootnode.val;
    this.dgst=null;

    
  }

  public boolean checkTransaction (Transaction t) 
  {
    boolean b1=true;
    boolean b2=true;
    TransactionBlock tb=t.coinsrc_block;
    tb=t.coinsrc_block;
    int s=tb.trarray.length;
    //Checking that the source and destination match
    if(tb==null)//coinsource null case
    {
      b1=true;
    }
    else
    {
      
      for(int i=0;i<s;i++)
      {
        if(tb.trarray[i].coinID.equals(t.coinID))
        {
          if(!(tb.trarray[i].Destination.UID.equals(t.Source.UID)))
          {
            return false;
            
          }
        }
      }
    }

    TransactionBlock curr=this;//new TransactionBlock(this.trarray);
    // curr.trarray=this.trarray;
    // curr.previous=this.previous;
    // curr.next=this.next;
    // curr.trsummary=this.trsummary;
    // curr.Tree=this.Tree;
    // curr.nonce=this.nonce;
    // curr.dgst=this.dgst;
    int count =0;//keeping count of transaction t in this block

    for(int i=0;i<curr.trarray.length;i++)
    {
      if(curr.trarray[i].coinID.equals(t.coinID))
      {
        count++;
      }


    }
    if(count>1)
    {
      b2=false;
    }

    curr=curr.previous;//initialisation
    while(curr!=t.coinsrc_block)//condition
    {
      for(int i=0;i<curr.trarray.length;i++)
      {
        if(curr.trarray[i].coinID.equals(t.coinID))
        {
          b2=false;
          break;
        }

      }
      curr=curr.previous;//updation
    }



    return (b1 && b2);
  }
}
