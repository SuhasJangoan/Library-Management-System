/**
 * @author
 * 
 * Suhas Jangoan as author
 *  
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class ABGame{

char [] inBoard = new char [23];
char [] outBoard = new char [23];
String inPath;
String outPath;	
int treeDepth=0;
int inWhiteCount=0;
int inBlackCount=0;
TreeNode root;
int nodesEvaluated=0;

public static void main(String[] args){

	ABGame gameOpening = new ABGame(); 
	gameOpening.moveCoin(args);
}

public void moveCoin(String [] args){
		inPath = args[0];
		outPath = args[1];
		treeDepth = Integer.parseInt(args[2]);
	
	try{
		BufferedReader reader = new BufferedReader(new FileReader(inPath));
		String line = reader.readLine();
		char c;
			for(int i=0;i<23;i++){
				c = line.charAt(i);
				if(c=='W' ||c=='w' ){
					inBoard[i] = 'W';
					inWhiteCount++;
				}else if(c=='B' ||c=='b'){
					inBoard[i] = 'B';
					inBlackCount++;
				}else{
					inBoard[i] = 'x';
				}
			}
		
		if(inWhiteCount<3){
			System.out.println("Sorry dude you lost the game .. !!");
		}
		
		System.out.println("Printing input board");
		root =new TreeNode();
		root.setDepth(0);
		root.setBoard(getBoardCopy(inBoard));
		buildMoveTree(root);
		applyABMinMax(root);
		
		//System.out.println("Printing Tree");
		//printTreeNode(root);
		
		System.out.print("Input board is: ");
		System.out.print(inBoard);
		System.out.println();
		
		System.out.print("Board Position: ");
		for(int i=0;i<23;i++){
			System.out.print(root.getBoard()[i]);
		}
		System.out.println();
		
		System.out.println("Positions evaluated by static estimation:"+nodesEvaluated);
		System.out.println("MINMAX estimate: "+root.getStatEst());
		System.out.println("Output board is");
		
		
		writeToFile(root.getBoard());
		
	}catch (Exception e){
		System.out.println("Exception Occurred !! ");
		e.printStackTrace();	
	}

}


public void writeToFile(char[] board){
	
	File outFile =  new File(outPath);
	String strContent  = "";
	
	
	for(int i=0;i<23;i++){
		strContent += board[i];
	}
	try{
		
		if(!outFile.exists())
			outFile.createNewFile();
		
		FileOutputStream outStream = new FileOutputStream(outPath);
		
		outStream.write(strContent.getBytes());
		outStream.close();
		
	}catch(Exception exp){
		System.out.println("Exception Occurred");
		exp.printStackTrace();
	}
}


char[] getBoardCopy(char[] board){
	char [] copy = new char [23];
	//System.out.println("inside print board printing original board");
	//printBoard(board);
	for(int i=0;i<23;i++){
		copy[i] = board[i];
	}
	
	//System.out.println("printing copy of board ");
	//printBoard(copy);
	return copy;
}

public void applyABMinMax(TreeNode node){
	//System.out.println("applyABMinMax" + node.getDepth());
	
	if(node.getChilds() != null &&node.getChilds().size()>0){
		
		if(node.statEval){
			
			if(node.getDepth()%2==0){
				if(node.getStatEst() < node.getParent().getStatLess() ){
					node.getParent().setStatLess(node.getStatEst());
				}
			}else{
				if(node.getStatEst() > node.getParent().getStatGreat() ){
					node.getParent().setStatGreat(node.getStatEst());
				}
			}
			
			
			node.statEval = true;
			return;
		}else{
			for(int i=0;i<node.getChilds().size();i++){
					
				
				applyABMinMax(node.getChilds().get(i));
				
				
				if(node.getDepth()!=0){
					if(node.getDepth()%2==0 ){
						
						//System.out.println("Node parent is " + node.getParent());
						
						if(node.getStatGreat()>node.getParent().getStatLess()){
							return;
						}
					}else{
						
						if(node.getStatLess()<node.getParent().getStatGreat()){
							return;
						}
						
						
					}
				}	
			}	
			
			
				if(node.getDepth()%2==0 ){
					
					node.setStatEst(node.getStatGreat());
					node.statEval = true;
	
					if(node.getDepth()!=0){
						if(node.getStatEst() < node.getParent().getStatLess() ){
							node.getParent().setStatLess(node.getStatEst());
						}
					}	
					
				}else{
					
					node.setStatEst(node.getStatLess());
					node.statEval = true;
					
					if(node.getDepth()!=0){
						if(node.getStatEst() > node.getParent().getStatGreat() ){
							node.getParent().setStatGreat(node.getStatEst());
						}
					}	
					
				}
			
		}
		
		if(node.getDepth()==0){
			for(int i=0;i<node.getChilds().size();i++){
				if(node.getChilds().get(i).getStatEst() == node.getStatEst()){
					node.setBoard(node.getChilds().get(i).getBoard());
					break;
				}
			}
		}
		
		
		
	}else{
		//System.out.println("Evaluating leaf");
		getMidStatEst(node);
		if(node.getDepth()%2==0){
			if(node.getStatEst() < node.getParent().getStatLess() ){
				node.getParent().setStatLess(node.getStatEst());
			}
		}else{
			if(node.getStatEst() > node.getParent().getStatGreat() ){
				node.getParent().setStatGreat(node.getStatEst());
			}
		}
		
		
		nodesEvaluated++;
	}
}


public void buildMoveTree(TreeNode node){
	char c;
	int whiteCount=0,blackCount=0;
	ArrayList<Integer> whiteInd = new ArrayList<Integer>();
	ArrayList<Integer> blackInd = new ArrayList<Integer>();
	ArrayList<Integer> itrInd = new ArrayList<Integer>();
	
	// System.out.println("looking for empty spaces");
	 for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i];
			//System.out.println("c is "+ c + " node value "+node.getBoard()[i]);
			if(c=='x' || c=='X' || c==' '){
			}else if(c=='w' || c=='W'){
				whiteCount++;
				whiteInd.add(i);
			}else if(c=='b' || c=='B'){
				blackCount++;
				blackInd.add(i);
			}
		
		}

	if(node.getDepth() == treeDepth || whiteCount <=2 || blackCount <=2){
	}else{
		double statEst;
		
		 //System.out.println("inside else ");
		 ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
		 ArrayList<char[]> allBoards = new ArrayList<char[]>();
		 char[] bestBoard = null;
		 
		 
		 if(node.getDepth()%2==0){
				c='W';
				itrInd = whiteInd;
				statEst=-1000000000;
			}else{
				c='B';
				itrInd=blackInd;
				statEst=1000000000;
			}
		 
		 
			 for(int i=0;i<itrInd.size();i++){
				 	allBoards = new ArrayList<char[]>();
				 
				 	
				 	generateMove(c,node.getBoard(),itrInd.get(i),allBoards);
				 
					//System.out.println("all Boards size is " + allBoards.size());
				
					 for(int j=0;j<allBoards.size();j++){
		
							//building tree
							TreeNode newNode = new TreeNode();
							newNode.setDepth(node.getDepth()+1);
							newNode.setParent(node);
							
							
							newNode.setBoard(allBoards.get(j));
							//System.out.println("Add new board to child " + allBoards.get(j));
							buildMoveTree(newNode);
							
							if(node.getDepth()%2==0){
								if(newNode.getStatEst() > statEst){
									statEst = newNode.getStatEst(); 
									bestBoard = newNode.getBoard();
								}
		
							}else{
								if(newNode.getStatEst() < statEst){
									statEst = newNode.getStatEst(); 
									bestBoard = newNode.getBoard();
								}
							}
							
							childs.add(newNode);
						 
					 }
					 
			 }
		 
		 
		node.setChilds(childs);
		node.setStatEst(statEst);
		if(node.getDepth() ==0){
			//node.setIndex(bestInd);
			node.setBoard(bestBoard);
		}
	}
	
	
	
}

public ArrayList<Integer> getNeighbour(char[] board,int ind){
	ArrayList<Integer> ret = new ArrayList<Integer>();
	

	switch(ind){
	
				case 0: ret.add(1);
						ret.add(3);
						ret.add(8);
						break;
						
				case 1: ret.add(0);
						ret.add(2);
						ret.add(4);
						break;
						
				case 2: ret.add(1);
						ret.add(5);
						ret.add(13);
						break;
						
				case 3: ret.add(0);
						ret.add(4);
						ret.add(6);
						ret.add(9);
						break;
						
				case 4: ret.add(1);
						ret.add(3);
						ret.add(5);
						break;
						
				case 5: ret.add(2);
						ret.add(4);
						ret.add(7);
						ret.add(12);
						break;
						
				case 6: ret.add(3);
						ret.add(7);
						ret.add(10);
						break;	
						
				case 7: ret.add(5);
						ret.add(6);
						ret.add(11);
						break;
			
				case 8: ret.add(0);
						ret.add(9);
						ret.add(20);
						break;
						
				case 9: ret.add(3);
						ret.add(8);
						ret.add(10);
						ret.add(17);
						break;
						
				case 10:ret.add(6);
						ret.add(9);
						ret.add(14);	
						break;
						
				case 11:ret.add(7);
						ret.add(12);
						ret.add(16);
						break;
						
				case 12:ret.add(5);
						ret.add(11);
						ret.add(13);
						ret.add(19);
						break;
						
				case 13:ret.add(2);
						ret.add(12);
						ret.add(22);
						break;
						
				case 14:ret.add(10);
						ret.add(15);
						ret.add(17);
						break;		
						
				case 15:ret.add(14);
						ret.add(16);
						ret.add(18);
						break;
						
				case 16:ret.add(11);
						ret.add(15);
						ret.add(19);
						break;
						
				case 17:ret.add(9);
						ret.add(14);
						ret.add(18);
						ret.add(20);
						break;
						
				case 18:ret.add(15);
						ret.add(17);
						ret.add(19);
						ret.add(21);
						break;	
						
				case 19:ret.add(12);
						ret.add(16);
						ret.add(18);
						ret.add(22);
						break;
						
						
				case 20:ret.add(8);
						ret.add(17);
						ret.add(21);
						break;
						
				case 21:ret.add(18);
						ret.add(20);
						ret.add(22);
						break;	
						
				case 22:ret.add(13);
						ret.add(19);
						ret.add(21);
						break;
	
	}
	
	
	
	return ret;
}


public void generateMove(char c,char[] board,int ind,ArrayList<char[]> allBoard){
	char[] newBoard;
	char[] tempBoard;
	ArrayList<Integer> NighInd;
	int tempWhite = 0;
	int tempBlack = 0;
	
	char temp;
	ArrayList<Integer> emptyInd = new ArrayList<Integer>();
	int whiteCount=0,blackCount=0;
	//System.out.println("looking for empty spaces");
			 for(int i = 0; i < 23; i++) {
				 temp = board[i];
					//System.out.println("c is "+ c + " node value "+node.getBoard()[i]);
					if(temp=='x' || temp=='X' || temp==' '){
						emptyInd.add(i);
					}else if(temp=='w' || temp=='W'){
						whiteCount++;
					}else if(temp=='b' || temp=='B'){
						blackCount++;
					}
				
				}

			 //System.out.println("Empty spaces are " + emptyInd);
	
	if(whiteCount < 3){
		return;
	}else if((whiteCount == 3 && c =='W') || (blackCount == 3 && c =='B') ){
		//System.out.println("Entered into endgame");
		NighInd = emptyInd;
		//System.out.println("Empty Ind are : " + emptyInd);
	}else{
	
		NighInd = getNeighbour(board,ind);
	
	}
	for(int j=0;j<NighInd.size();j++){
		
		if(board[NighInd.get(j)] == 'x'){	
			newBoard = getBoardCopy(board);
			
			newBoard[NighInd.get(j)] = c;
			newBoard[ind] = 'x';
			//System.out.println("Moving " + c + " from " + ind + " to ind " +  NighInd.get(j)  );
			
				if(isCloseMill(NighInd.get(j),newBoard)){
					for(int i=0;i<23;i++){
						if(newBoard[i]!=c && newBoard[i]!='x'){
							 //System.out.println("Mill Done");
							tempBoard = getBoardCopy(newBoard);
							if(!isCloseMill(i,tempBoard)){
								tempBoard[i] = 'x';
								//System.out.println("Removing ind " + i);
							  allBoard.add(tempBoard);
							 
							}else{
								tempWhite = 0;
								tempBlack = 0;
								
								for(int k = 0; k < 23; k++) {
									 temp = tempBoard[k];
										if(temp=='w' || temp=='W'){
											tempWhite++;
										}else if(temp=='b' || temp=='B'){
											tempBlack++;
										}
									
									}
								
								if((tempBlack == 3 && c=='W')||(tempWhite == 3 && c=='B') ){
									tempBoard[i] = 'x';
									//System.out.println("Removing ind " + i);
								  allBoard.add(tempBoard);
								}
								
								//System.out.println("mill encountered so not removing");
							}
						}
					}
					
				}else{
					allBoard.add(newBoard);
				}
		}
	}
	
}

public boolean isCloseMill(int ind,char[] board){
	boolean ret = false;
	switch(ind){
	
				case 0: if(board[ind] == board[1] && board[ind] == board[2] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[8] && board[ind] == board[20] && board[ind]!='x'){
							ret = true;
						}else if(board[ind] == board[3] && board[ind] == board[6] && board[ind]!='x'){
							ret = true;
						}
						break;
						
				case 1: if(board[ind] == board[0] && board[ind] == board[2] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 2: if(board[ind] == board[0] && board[ind] == board[1] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[13] && board[ind] == board[22] && board[ind]!='x'){
							ret = true;
						}else if(board[ind] == board[5] && board[ind] == board[7] && board[ind]!='x'){
							ret = true;
						}
						break;
						
				case 3: if(board[ind] == board[0] && board[ind] == board[6] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[4] && board[ind] == board[5] && board[ind]!='x'){
							ret = true;
						}else if(board[ind] == board[9] && board[ind] == board[17] && board[ind]!='x'){
							ret = true;
						}
						break;
						
				case 4: if(board[ind] == board[3] && board[ind] == board[5] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 5: if(board[ind] == board[7] && board[ind] == board[2] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[3] && board[ind] == board[4] && board[ind]!='x'){
							ret = true;
						}else if(board[ind] == board[12] && board[ind] == board[19] && board[ind]!='x'){
							ret = true;
						}
						break;
						
				case 6: if(board[ind] == board[10] && board[ind] == board[14] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[0] && board[ind] == board[3] && board[ind]!='x'){
							ret =true;
						}
						break;	
						
				case 7: if(board[ind] == board[11] && board[ind] == board[16] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[2] && board[ind] == board[5] && board[ind]!='x'){
							ret =true;
						}
						break;
			
				case 8: if(board[ind] == board[9] && board[ind] == board[10] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[0] && board[ind] == board[20] && board[ind]!='x'){
							ret = true;
						}
						break;
						
				case 9: if(board[ind] == board[3] && board[ind] == board[17] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[8] && board[ind] == board[10] && board[ind]!='x'){
							ret = true;
						}
						break;
						
				case 10: if(board[ind] == board[8] && board[ind] == board[9] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[6] && board[ind] == board[14] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 11: if(board[ind] == board[12] && board[ind] == board[13] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[7] && board[ind] == board[16] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 12: if(board[ind] == board[11] && board[ind] == board[13] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[5] && board[ind] == board[19] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 13: if(board[ind] == board[11] && board[ind] == board[12] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[2] && board[ind] == board[22] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 14: if(board[ind] == board[15] && board[ind] == board[16] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[17] && board[ind] == board[20] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[6] && board[ind] == board[10] && board[ind]!='x'){
							ret =true;
						}
						break;		
						
				case 15: if(board[ind] == board[18] && board[ind] == board[21] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[14] && board[ind] == board[16] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 16: if(board[ind] == board[7] && board[ind] == board[11] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[19] && board[ind] == board[22] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[14] && board[ind] == board[15] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 17: if(board[ind] == board[3] && board[ind] == board[9] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[14] && board[ind] == board[20] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[18] && board[ind] == board[19] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 18: if(board[ind] == board[15] && board[ind] == board[21] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[17] && board[ind] == board[19] && board[ind]!='x'){
							ret =true;
						}
						break;	
						
				case 19: if(board[ind] == board[5] && board[ind] == board[12] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[16] && board[ind] == board[22] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[17] && board[ind] == board[18] && board[ind]!='x'){
							ret =true;
						}
						break;
						
						
				case 20: if(board[ind] == board[0] && board[ind] == board[8] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[14] && board[ind] == board[17] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[21] && board[ind] == board[22] && board[ind]!='x'){
							ret =true;
						}
						break;
						
				case 21: if(board[ind] == board[15] && board[ind] == board[18] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[20] && board[ind] == board[22] && board[ind]!='x'){
							ret =true;
						}
						break;	
						
				case 22: if(board[ind] == board[2] && board[ind] == board[13] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[16] && board[ind] == board[19] && board[ind]!='x'){
							ret =true;
						}else if(board[ind] == board[20] && board[ind] == board[21] && board[ind]!='x'){
							ret =true;
						}
						break;
			
	
	}
	return ret;
}

public void printTreeNode(TreeNode node){
	//printing when node attribute = 0
	//System.out.println("Printing Tree");
	for(int i=0;i<node.getDepth();i++){
		System.out.print("|");
	}
	System.out.print("StatEst:" +node.getStatEst() + ";Board is:");
	for(int i=0;i<23;i++){
		System.out.print(node.getBoard()[i]);
	}
	System.out.println();
	
	if(node.getChilds()!=null){
		//System.out.println("Printing Childs");
			for(int i=0;i<node.getChilds().size();i++)
				printTreeNode(node.getChilds().get(i));
	}	
}

public void getMidStatEst(TreeNode node){
	int whites =0;
	int blacks =0;
	char c;
	int statEst;
	int blackMovesNo=0;
	ArrayList<char[]> allBoards;
	
	 ArrayList<Integer>blackInd = new ArrayList<Integer>();
	//System.out.print("Inside static est board is " );
	/*for(int i=0;i<23;i++){
		System.out.print("*"+node.getBoard()[i]);
	}
	System.out.println();
	*/

	for(int i = 0; i < 23; i++) {
		c = node.getBoard()[i];
		//System.out.println("c is " +c);
		if(c=='W' ||c=='w' ){
			whites++;
		}else if(c=='B' ||c=='b'){
			blacks++;
			blackInd.add(i);
		}
	
	}
	
	allBoards = new ArrayList<char[]>();
	for(int i=0;i<blackInd.size();i++){
	 	generateMove('B',node.getBoard(),blackInd.get(i),allBoards);
	}	
	
	blackMovesNo = allBoards.size();
	
	if(blacks<=2){
		statEst = 10000;
	}else if(whites<=2){
		statEst = -10000; 
	}else if(blackMovesNo ==0){
		statEst = 10000;
	}else{
		statEst = 1000*(whites - blacks);
		statEst = statEst - blackMovesNo;
	}
	
	//System.out.println("Whites :"+whites + "Blacks :"+blacks);
	node.setStatEst(statEst);
}

}