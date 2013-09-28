/* graphical representation of binary trees

   To use, this file and "node.java" must be in same directory:
  
   treegraph TG = new treegraph(1024,768); // specify window size
   node T = ... // construct binary tree
   TG.drawtree(T);

   Everytime drawtree is called, the background is cleared, so you
   don't have to create a new treegraph object to draw a new tree.
*/
package edu.puc.concurrentavl;

import java.awt.*;
import java.awt.Graphics;
import javax.swing.*;

public class treegraph extends JFrame
{

    public int XDIM, YDIM;
    public Graphics display;

    public void paint(Graphics g) {} // override method

    // constructor sets window dimensions
    public treegraph(int x, int y)
    {
	XDIM = x;  YDIM = y;
	this.setBounds(0,0,XDIM,YDIM);
	this.setVisible(true); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	display = this.getGraphics();
	// draw static background as a black rectangle
	display.setColor(Color.black);
	display.fillRect(0,0,x,y);
        display.setColor(Color.red);
	try{Thread.sleep(500);} catch(Exception e) {} // Synch with system
    }  // drawingwindow


    public static int depth(RBNode N)  // find max depth of tree
    {
	if (N==null) return 0;
        int l = depth(N.left);
        int r = depth(N.right);
        if (l>r) return l+1; else return r+1;
    }

    // internal vars used by drawtree routines:
    private int bheight = 50; // branch height
    private int yoff = 30;  // static y-offset

    // l is level, lb,rb are the bounds (position of left and right child)
    private void drawnode(RBNode N,int l, int lb, int rb)
    {
	if (N==null) return;
	//try{Thread.sleep(1000);} catch(Exception e) {} // slow down
	if(N.color == color.BLACK)
		display.setColor(Color.black);
	else //if(N.color == color.RED)
		display.setColor(Color.red);
	
	display.fillOval(((lb+rb)/2)-10,yoff+(l*bheight),20,20);
	if(N.color == color.BLACK)
		display.setColor(Color.red);
	else //if(N.color == color.RED)
		display.setColor(Color.blue);
	//display.setColor(Color.red);
	display.drawString(N.value+"",((lb+rb)/2)-5,yoff+15+(l*bheight));
	display.setColor(Color.blue); // draw branches
        if (N.left!=null)
	    {
   	       display.drawLine((lb+rb)/2,yoff+10+(l*bheight),
			((3*lb+rb)/4),yoff+(l*bheight+bheight));
               drawnode(N.left,l+1,lb,(lb+rb)/2);
	    }
        if (N.right!=null)
	    {
               display.drawLine((lb+rb)/2,yoff+10+(l*bheight),
			((3*rb+lb)/4),yoff+(l*bheight+bheight));
               drawnode(N.right,l+1,(lb+rb)/2,rb);
	    }
    } // drawnode

    public void drawtree(RBNode T)
    {
        if (T==null) return;
	int d = depth(T);
	bheight = (YDIM/d);
	display.setColor(Color.white);
	display.fillRect(0,0,XDIM,YDIM);  // clear background
        drawnode(T,0,0,XDIM);
    }

    /* sample use:  (put this in another file) **************
    public static void main(String[] args)
    {
      treegraph W = new treegraph(1024,768);
      node T = 
       new node(9,
         new node(5,null,new node(10)),
         new node(15,
		  null,
		  new node(18,new node(3),null)));
      W.drawtree(T);
      W.display.drawString("Do you like my tree?",20,W.YDIM-50);
      try{Thread.sleep(5000);} catch(Exception e) {} // 5 sec delay
    }  // main
    ********************/

} // treegraph

