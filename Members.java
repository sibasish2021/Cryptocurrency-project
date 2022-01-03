package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void addTrans(Transaction t, Transaction[] tn)
  {
    Transaction[] tar=new Transaction[tn.length+1];
    for(int i=0;i<tn.length;i++)
    {
      tar[i]=tn[i];
    }
    tar[tn.length]=t;
    tn=tar;
  }
  public void remTrans(Transaction[] tn)
  {
    Transaction[] tar=new Transaction[tn.length-1];
    for(int i=1;i<tn.length;i++)
    {
      tar[i-1]=tn[i];
    }
    tn=tar;
  }

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) 
  {
    Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(mycoins.get(0).get_first(),mycoins.get(0).get_second());
    mycoins.remove(0);//Removing a coin from mycoins list

    //Creating a transaction obj
    Transaction tobj=new Transaction();
    
    tobj.coinID=p.get_first();
    tobj.coinsrc_block=p.get_second();
    tobj.next=null;
    tobj.previous=null;
    
    tobj.Source=this;//Setting source attribute
    tobj.Source.mycoins=this.mycoins;
    tobj.Source.in_process_trans=this.in_process_trans;
    
    for(int i=0;i<DSobj.memberlist.length;i++)//Setting destination attribute
    {
      if(DSobj.memberlist[i].UID.equals(destUID))
      {
        tobj.Destination=DSobj.memberlist[i];
      }
    }
    
    //Adding the transaction to senders transaction array
    addTrans(tobj,in_process_trans);
    //Adding the trasaction to pending transactions list
    DSobj.pendingTransactions.AddTransactions(tobj);
  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException 
  {
    
    TransactionBlock temp = DSObj.bChain.lastBlock;
	  int k=0;
	  List<TransactionBlock> Blocks = new ArrayList<TransactionBlock>();
	  boolean l = true;
	int ff=0;
	while(l){
		Blocks.add(0,temp);
		for(k=0;k<temp.trarray.length;k++){
			if(temp.trarray[k].Source!=null)
			if(temp.trarray[k].coinID.equals(tobj.coinID)&&temp.trarray[k].Source.equals(tobj.Source)&&temp.trarray[k].Destination.equals(tobj.Destination)){//if(temp.trarray[k]==tobj){//dfhklkzhflhlahklfhlhhlhh
				ff=1;
				break;
			}
		}
		if(ff==1)
			break;
		temp=temp.previous;
		if(temp==null)
			throw new MissingTransactionException();
	}
	int height = 1;
	int tempss = temp.trarray.length;//curr.mtree.rootnode.numberLeaves;
	while(tempss>1){
		tempss/=2;
		height+=1;
	}
	int left=0;
	int right=temp.trarray.length-1;
	TreeNode curr = temp.Tree.rootnode;
	for(int j=0;j<height-1;j++){
		if(k>(left+right)/2){
			curr = curr.right;
			left = (left+right)/2+1;
		}
		else{
			curr=curr.left;
			right = (right+left)/2;
		}
	}
	List<Pair<String,String>> Path = new ArrayList<Pair<String,String>>();
	while(curr!=temp.Tree.rootnode){
		Path.add(new Pair<String,String>(curr.parent.left.val,curr.parent.right.val));
		curr=curr.parent;
	}
	Path.add(new Pair<String,String>(curr.val,null));
	List<Pair<String,String>> Path2 = new ArrayList<Pair<String,String>>();
	if(temp.previous==null){
		Path2.add(new Pair<String,String>(BlockChain_Honest.start_string,null));
		Path2.add(new Pair<String,String>(temp.dgst,BlockChain_Honest.start_string+"#"+temp.trsummary+"#"+temp.nonce));
	}
	else{
		Path2.add(new Pair<String,String>(temp.previous.dgst,null));
		Path2.add(new Pair<String,String>(temp.dgst,temp.previous.dgst+"#"+temp.trsummary+"#"+temp.nonce));
	}
	for(int i=1;i<Blocks.size();i++)
		Path2.add(new Pair<String,String>(Blocks.get(i).dgst,Blocks.get(i).previous.dgst+"#"+Blocks.get(i).trsummary+"#"+Blocks.get(i).nonce));
	int index=0;
	for(int i=0;i<in_process_trans.length;i++){
		if(in_process_trans[i]==tobj)
			break;
		else
			index++;
	}
	for(int i=index;i<in_process_trans.length-1;i++)
		in_process_trans[i]=in_process_trans[i+1];
	in_process_trans[in_process_trans.length-1]=null;
	if(Long.parseLong(tobj.Destination.mycoins.get(0).get_first())>Long.parseLong(tobj.coinID))
		tobj.Destination.mycoins.add(0,new Pair<String,TransactionBlock>(tobj.coinID,temp));
	else if(Long.parseLong(tobj.Destination.mycoins.get(tobj.Destination.mycoins.size()-1).get_first())<Long.parseLong(tobj.coinID))
		tobj.Destination.mycoins.add(new Pair<String,TransactionBlock>(tobj.coinID,temp));
	else{
		for(int i=0;i<tobj.Destination.mycoins.size()-1;i++){
			if((Long.parseLong(tobj.Destination.mycoins.get(i).get_first())<Long.parseLong(tobj.coinID))&&(Long.parseLong(tobj.Destination.mycoins.get(i+1).get_first())>Long.parseLong(tobj.coinID))){
				tobj.Destination.mycoins.add(i+1,new Pair<String,TransactionBlock>(tobj.coinID,temp));
				break;
			}
		}
	}
    return new Pair<List<Pair<String,String>>,List<Pair<String,String>>>(Path,Path2);
    
  }

  public void MineCoin(DSCoin_Honest DSObj) 
  {
    //Initialising the transaction array
    int l=DSObj.bChain.tr_count;
    Transaction[] tr=new Transaction[l];
    int s=0,i=0;
    Transaction curr=new Transaction();
    
    //Building the transaction array
    while(s!=l-2)
    {
      curr=DSObj.pendingTransactions.firstTransaction;
      if(DSObj.bChain.lastBlock.checkTransaction(curr))//Checking if transaction is valid
      {
        for(i=0;i<s;i++)//Checking if transaction is already there in our new Block
        {
          if(tr[i].coinID.equals(curr.coinID))
          {
            try
            {
              DSObj.pendingTransactions.RemoveTransaction();
            }
            catch(Exception e)
            {
          
            }
            break;
          }
        }
        if(!(tr[i].coinID.equals(curr.coinID)))//if transaction is not present anywhere in our block
        {
          tr[s]=curr;
          try
          {
            DSObj.pendingTransactions.RemoveTransaction();
          }
          catch(Exception e)
          {
          
          }
          s=s+1;
        }
      }
      else
      {
        try
        {
          DSObj.pendingTransactions.RemoveTransaction();
        }
        catch(Exception e)
        {
          
        }
      }
    }
    //Adding the miner reward
    Transaction mrt =new Transaction();
    DSObj.latestCoinID=Integer.toString(Integer.parseInt(DSObj.latestCoinID)+1);//Updating the latest coin 
    mrt.coinID=DSObj.latestCoinID;
    mrt.Source=null;
    mrt.Destination=this;
    mrt.next=null;
    mrt.previous=null;
    mrt.coinsrc_block=null;
    tr[l-1]=mrt;
    //Creating the Transaction block
    TransactionBlock tB=new TransactionBlock(tr);
    //Adding the transaction block at the end of the Blockchain
    DSObj.bChain.InsertBlock_Honest(tB);
    //Adding the coin to miners wallet Doubt


  }
  


  public void MineCoin(DSCoin_Malicious DSObj) 
  {
    //Initialising the transaction array
    int l=DSObj.bChain.tr_count;
    Transaction[] tr=new Transaction[l];
    int s=0,i=0;
    Transaction curr=new Transaction();
    
    //Building the transaction array
    while(s!=l-2)
    {
      curr=DSObj.pendingTransactions.firstTransaction;
      if(DSObj.bChain.lastBlocksList[0].checkTransaction(curr))//Checking if transaction is valid
      {
        for(i=0;i<s;i++)//Checking if transaction is already there in our new Block
        {
          if(tr[i].coinID==curr.coinID)
          {
            try
            {
              DSObj.pendingTransactions.RemoveTransaction();
            }
            catch(Exception e)
            {
              System.out.println(e);
            }
            break;
          }
        }
        if(i>=s || tr[i].coinID!=curr.coinID)//if transaction is not present anywhere in our block
        {
          tr[s]=curr;
          try
          {
            DSObj.pendingTransactions.RemoveTransaction();
          }
          catch(Exception e)
          {

          }
          s=s+1;
        }
      }
      else
      {
        try
        {
        
          DSObj.pendingTransactions.RemoveTransaction();
        }
        catch(Exception e)
        {

        }
      }
    }
    //Adding the miner reward
    Transaction mrt =new Transaction();
    DSObj.latestCoinID=Integer.toString(Integer.parseInt(DSObj.latestCoinID)+1);//Updating the latest coin 
    mrt.coinID=DSObj.latestCoinID;
    mrt.Source=null;
    mrt.Destination=this;
    mrt.next=null;
    mrt.previous=null;
    mrt.coinsrc_block=null;
    tr[l-1]=mrt;
    //Creating the Transaction block
    TransactionBlock tB=new TransactionBlock(tr);
    //Adding the transaction block at the end of the Blockchain
    DSObj.bChain.InsertBlock_Malicious(tB);
    //Adding the coin to miners wallet Doubt

  }  
}
