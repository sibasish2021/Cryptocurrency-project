package DSCoinPackage;
import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) 
  {
    //System.out.println("Insertion starting");
    String s="1000000001";
    String s1;
    CRF obj=new CRF(64);
    //Setting the dgst
    if(lastBlock==null)
    {
      s1=start_string;
    }
    else
    {
      s1=lastBlock.dgst;
    }
    //Setting the nonce
    while(true)
    {
      if(obj.Fn(s1+"#" + newBlock.trsummary+ "#"+ s).substring(0,4).equals("0000"))
      {
        newBlock.nonce=s;
        break;
      }
      else
      {
        s=Long.toString(Long.parseLong(s)+1);//error

      }
    }
    if(lastBlock!=null)
    {
    //System.out.println("Insertion continuing");
    newBlock.dgst=obj.Fn(s1+"#" + newBlock.trsummary+ "#"+ newBlock.nonce);
    //System.out.println("Insertion continuing1");
    newBlock.previous=lastBlock;
    //System.out.println("Insertion continuing2");
    newBlock.next=null;
    //System.out.println("Insertion continuing3");
    lastBlock.next=newBlock;
    //System.out.println("Insertion continuing4");
    lastBlock=newBlock;
    }
    else
    {
      newBlock.dgst=obj.Fn(s1+"#" + newBlock.trsummary+ "#"+ newBlock.nonce);
      newBlock.previous=null;
      newBlock.next=null;
      lastBlock=newBlock;


    }

    //System.out.println("Inserted lastBlock"+lastBlock.dgst+" "+lastBlock.nonce);

  }
}
