package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    String s1="";
    if(tB.previous==null)
    {
      s1=start_string;

    }
    else
    {
      s1=tB.previous.dgst;
    }
    CRF obj=new CRF(64);
    String s=obj.Fn(s1+ "#" + tB.trsummary+"#" + tB.nonce);
    if(!(tB.dgst.substring(0,4).equals("0000") && tB.dgst.equals(s)))
    {
      return false;

    }
    for(int i=0;i<tB.trarray.length;i++)
    {
      if(!(tB.checkTransaction(tB.trarray[i])))
      {
        return false;
      }
    }
    MerkleTree mtree=new MerkleTree();
    mtree.Build(tB.trarray);
    if(mtree.rootnode.val!=tB.trsummary)
    {
      return false;
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain () 
  {
    int size=0;
    int s1=0;
    if(lastBlocksList.length==0)
    {
      return null;
    }

    TransactionBlock curr=new TransactionBlock(lastBlocksList[0].trarray);//iterator block
    TransactionBlock temp=new TransactionBlock(lastBlocksList[0].trarray);//temporary last block
    TransactionBlock last=null;//required last block
    boolean ind=false;//indicator if last block is already is in list or need to be added
    
    for(int i=0;i<lastBlocksList.length;i++)
    {
      curr=lastBlocksList[i];
      temp=lastBlocksList[i];
      s1=0;
      while(curr!=null)
      {
        if(checkTransactionBlock(curr))
        {
         s1=s1+1;
         curr=curr.previous;
        }
        else
        {
          if(s1>size)
          {
            size=s1;
            last=temp;
            temp=curr.previous;
            s1=0;
          }
          else
          {
            temp=curr.previous;
            s1=0;
          }
          curr=curr.previous;
        }
      }
      if(s1>size)
      {
        size=s1;
        last=temp;
      }
    }

    return last;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) 
  {
    TransactionBlock lastBlock=this.FindLongestValidChain();//new TransactionBlock(this.FindLongestValidChain());
    String s="1000000001";
    String s1="";
    CRF obj=new CRF(64);
    if(lastBlock==null)
    {
      s1=start_string;
    }
    else
    {
      s1=lastBlock.dgst;
    }
    while(true)//Finding the nonce
    {
      if(obj.Fn(s1+"#" + newBlock.trsummary+ "#"+ s).substring(0,4)=="0000")
      {
        newBlock.nonce=s;
        break;
      }
      else
      {
        s=Long.toString(Long.parseLong(s)+1);//error

      }
    }
    newBlock.dgst=obj.Fn(s1+"#" + newBlock.trsummary+ "#"+ newBlock.nonce);
    newBlock.previous=lastBlock;
    newBlock.next=null;
    boolean b=false;//indicator whether the last block of longest valid chain is in the member list or not
    for(int i=0;i<lastBlocksList.length;i++)//Updating last block list
    {
      if(lastBlocksList[i]==null)
      {
        break;
      }
      if(lastBlocksList[i]==lastBlock)
      {
        lastBlocksList[i]=newBlock;
        b=true;
        break;
      }

    }
    if(b==false)//case when last block is not in list
    {
      for(int i=0;i<lastBlocksList.length;i++)
      {
        if(lastBlocksList[i]==null)
        {
          lastBlocksList[i]=newBlock;
        }
      }
    }
    if(lastBlock!=null)
    {
      lastBlock.next=newBlock;
    
    }
    lastBlock=newBlock;

  }
}
