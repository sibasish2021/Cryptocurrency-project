package HelperClasses;

import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;
  

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = tr.length;
    numdocs = num_trans;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);

    return rootnode.val;
  }
  
  public TreeNode getNode(Transaction t)
  {
    String s=get_str(t);
    if(rootnode==null)
    {
      return null;
    }
    else if(rootnode.val==s)
    {
      return rootnode;
    }
    MerkleTree ltree=new MerkleTree();
    MerkleTree rtree=new MerkleTree();
    ltree.rootnode=rootnode.left;
    rtree.rootnode=rootnode.right;
    ltree.numdocs=numdocs/2;
    rtree.numdocs=numdocs/2;
    if(ltree.getNode(t)!=null)
    {
      return ltree.getNode(t);
    }
    else if(rtree.getNode(t)!=null)
    {
      return rtree.getNode(t);
    }
    return null;

  }

  public List<Pair<String,String>> getProof(TreeNode tn)
  {
    List<Pair<String,String>> ar =new ArrayList<Pair<String,String>>();
    TreeNode curr=new TreeNode();
    curr=tn;
    while(curr!=rootnode)
    {
      if(curr==curr.parent.left)
      {
        Pair<String,String> p=new Pair<String,String>(curr.val,curr.parent.right.val);
        ar.add(p);
      }
      else
      {
        Pair<String,String> p=new Pair<String,String>(curr.parent.left.val,curr.val);
        ar.add(p);
      }
    }
    Pair<String,String> p=new Pair<String,String>(rootnode.val,null);
    ar.add(p);
    return ar;    
  }
  
}
