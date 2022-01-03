package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) 
  {
    Transaction t=new Transaction();
    t.Source=transaction.Source;
    t.Destination=transaction.Destination;
    t.coinID=transaction.coinID;
    t.coinsrc_block=transaction.coinsrc_block;
    
    
    if(numTransactions==0)
    {
      t.next=null;
      t.previous=null;
      firstTransaction=t;
      lastTransaction=t;
    }
    else
    {
      t.next=null;
      t.previous=lastTransaction;
      lastTransaction.next=t;
      lastTransaction=t;
    }
    numTransactions++;

  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException
  {
    Transaction t=new Transaction();
    if(firstTransaction==null)//Empty Queue
    {
      throw new EmptyQueueException();
    }
    else if(numTransactions==1)//Queue having one element
    {
      t=firstTransaction;
      firstTransaction=null;
      lastTransaction=null;
      numTransactions=0;

    }
    else//Queue having more than one element
    {
      t=firstTransaction;
      firstTransaction=firstTransaction.next;
      firstTransaction.previous=null;
      numTransactions--;
      
    }
    return t;
  }

  public int size() 
  {
    return numTransactions;
  }
}
