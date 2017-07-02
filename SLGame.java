import javax.swing.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.util.*;

public class SLGame{
	Snake snakes[] = new Snake[14];
	Ladder ladders[] = new Ladder[14];
	Trap traps[] = new Trap[5];
	String name[] = new String[2];

	int snakesCount = 0;
	int laddersCount = 0;
	int trapsCount = 0;

	int usedTiles[] = new int[25];
	int nextIndex = 0;
	
	Board bd = new Board();
	Dice dice = bd.getDice();
	Scanner scan = new Scanner(System.in);
	
	public void setup(Board bd){
		int snakeHead = 0;
		int snakeTail = 0;
		int ladderTop = 0;
		int ladderBottom = 0;
		int trapDuration = 0;
		int trapTile = 0;
		
		boolean snakeErrorFlag = false;
		boolean ladderErrorFlag = false;
		boolean trapErrorFlag = false;
		
		snakesCount = getInt("Enter number of snakes", 1, 14);
		for(int i = 0; i < snakesCount; i++){
			do{
				snakeErrorFlag = false;
				snakeHead = getInt("Tile of snake head " + (i + 1), 2, 99);
				if(isUsed(snakeHead) == false){
					snakeTail = getInt("Tile of snake tail " + (i + 1), 1, 100);
					if(snakeTail < snakeHead){
						if(isUsed(snakeTail) == false){
							usedTiles[nextIndex] = snakeTail;
							nextIndex++;
							usedTiles[nextIndex] = snakeHead;
							nextIndex++;
							
							Snake s = new Snake(snakeHead, snakeTail);
							bd.add(s);
							snakes[i] = s;
						}
						else{
							plainMessage("Error");
							snakeErrorFlag = true;
						}
					}
					else{
						plainMessage("Error");
						snakeErrorFlag = true;
					}
				}
				else{
					plainMessage("Error");
					snakeErrorFlag = true;
				}
			}
			while(snakeErrorFlag);
		}
		
		snakesCount = getInt("Enter number of ladders", 1, 14);
		for(int i = 0; i < snakesCount; i++){
			do{
				ladderErrorFlag = false;
				ladderTop = getInt("Tile of ladder top " + (i + 1), 2, 99);
				if(isUsed(ladderTop) == false){
					ladderBottom = getInt("Tile of ladder bottom " + (i + 1), 2, 98);
					if(ladderBottom < ladderTop){
						if(isUsed(ladderBottom) == false){
							usedTiles[nextIndex] = ladderBottom;
							nextIndex++;
							usedTiles[nextIndex] = ladderTop;
							nextIndex++;
							
							Ladder l = new Ladder(ladderBottom, ladderTop);
							bd.add(l);
							ladders[i] = l;
						}
						else{
							plainMessage("Error");
							ladderErrorFlag = true;
						}
					}
					else{
						plainMessage("Error");
						ladderErrorFlag = true;
					}
				}
				else{
					plainMessage("Error");
					ladderErrorFlag = true;
				}
			}
			while(ladderErrorFlag);
		}
		
		trapsCount = getInt("Enter number of traps", 1, 5);
		for(int i = 0; i < trapsCount; i++){
			do{
				trapTile = getInt("Tile of trap " + (i + 1), 2, 99);
				if(isUsed(trapTile) == false){
					trapDuration = getInt("Duration of trap " + (i + 1), 3, 5);
					
					usedTiles[nextIndex] = trapTile;
					nextIndex++;
					
					Trap t = new Trap(trapTile, trapDuration);
					bd.add(t);
					traps[i] = t;
				}
				else {
					plainMessage("Error");
					trapErrorFlag = true;
				}
			}
			while(trapErrorFlag);
		}
	}
	
	public boolean isUsed(int tile){
		for(int i = 0; i < usedTiles.length; i++){
			if(usedTiles[i] == tile){
				return true;
			}
		}
		return false;
	}
	
	int getInt(String message, int from, int to){
		String s;
		int n = 0;
		boolean invalid;
		do{
			invalid = false;
			s = (String)JOptionPane.showInputDialog(
			bd,  message,  "Customized Dialog",
			JOptionPane.PLAIN_MESSAGE);
			try{
				n = Integer.parseInt(s);
				if (n < from || n > to )
				plainMessage("Re-enter: Input not in range " + from + " to " + to);
			}
			catch (NumberFormatException nfe){
				plainMessage("Re-enter: Invalid number");
				invalid = true;
			}
		}
		while(invalid || n < from || n > to);
		return n;
	}

	String getString(String message){
		String s = (String)JOptionPane.showInputDialog(bd, message, "Customized Dialog", JOptionPane.PLAIN_MESSAGE);	
		return s;
	}
	
	void plainMessage(String message){
		JOptionPane.showMessageDialog(bd,
		message, "A prompt message",
		JOptionPane.PLAIN_MESSAGE);
	}
	
	public void control(){
		int numPlayers = 2;
		
		setup(bd);
		
		bd.clearMessages();
		
		String name1 = getString("Player 1 name : ");
		String name2 = getString("Player 2 name : ");
		bd.clearMessages();
		bd.addMessage("Current Players are");
		bd.addMessage("Player 1 : " + name1);
		bd.addMessage("Player 2 : " + name2);
		int p1Location = 1;
		int p2Location = 1;
		bd.setPiece(1,p1Location);
		bd.setPiece(2,p2Location);
		int val = 0;

		int p1TrapDelay = 0, p2TrapDelay = 0;
		
		while(true){
			do{
				if(p1TrapDelay == 0){
					val = getInt(name1 + ": Enter 0 to throw dice. Enter 1 - 6 for Testing.", 0, 6);
					
					if(val == 0){
						val = dice.roll();
					}
					else{
						dice.set(val);
					}
					
					if((p1Location + val) < 101){
						p1Location += val;
						plainMessage(name1 + ": moving to " + p1Location);
						bd.setPiece(1, p1Location);
						
						for(int i = 0; i < ladders.length; i++){
							if(ladders[i] != null){
								if(p1Location == ladders[i].getBottom()){
									p1Location = ladders[i].getTop();
									plainMessage(name1 + ": is about to climb up a ladder to " + p1Location);
									bd.setPiece(1,  p1Location);
								}
							}
						}
						
						for(int i = 0; i < snakes.length; i++){
							if(snakes[i] != null){
								if(p1Location == snakes[i].getHead()){
									p1Location = snakes[i].getTail();
									plainMessage(name1 + ": is about to slide down a snake to " + p1Location);
									bd.setPiece(1,  p1Location);
								}
							}
						}
						
						for(int i = 0; i < traps.length; i++){
							if(traps[i] != null){
								if(p1Location == traps[i].getLocation()){
									plainMessage(name1 + " has landed in a trap. Miss " + traps[i].getDuration() + " turns.");
									p1TrapDelay = traps[i].getDuration();
								}
							}
						}
						
						if(p1Location == 100){
							break;
						}
						
						if(val == 6){
							plainMessage(name1 + " rolled a six and has gained a turn!" );
						}
					}
					else{
						plainMessage("Sorry! " + name1 + " must roll the exact number to land on 100 to win." );
					}
				}
				else{
					p1TrapDelay--;
					plainMessage("Sorry! " + name1 + " is trapped." );
				}
			}
			while(val == 6);
			
			if(p1Location == 100){
				plainMessage("Congratulations! " + name1 + " has won!" );
				break;
			}
			
			do{
				if(p2TrapDelay == 0){
					val = getInt(name2 + ": Enter 0 to throw dice. Enter 1 - 6 for Testing.", 0, 6);
					
					if(val == 0){
						val = dice.roll();
					}
					else{
						dice.set(val);
					}
					
					if((p2Location + val) < 101){
						p2Location += val;
						plainMessage(name2 + ": moving to " + p2Location);
						bd.setPiece(2, p2Location);
						
						for(int i = 0; i < ladders.length; i++){
							if(ladders[i] != null){
								if(p2Location == ladders[i].getBottom()){
									p2Location = ladders[i].getTop();
									plainMessage(name2 + ": is about to climb up a ladder to " + p2Location);
									bd.setPiece(2,  p2Location);
								}
							}
						}
						
						for(int i = 0; i < snakes.length; i++){
							if(ladders[i] != null){
								if(p2Location == snakes[i].getHead()){
									p2Location = snakes[i].getTail();
									plainMessage(name2 + ": is about to slide down a snake to " + p2Location);
									bd.setPiece(2,  p2Location);
								}
							}
						}
						
						for(int i = 0; i < traps.length; i++){
							if(traps[i] != null){
								if(p2Location == traps[i].getLocation()){
									plainMessage(name2 + " has landed in a trap. Miss " + traps[i].getDuration() + " turns.");
									p2TrapDelay = traps[i].getDuration();
								}
							}
						}
						
						if(p2Location == 100){
							break;
						}
						
						if(val == 6){
							plainMessage(name2 + " rolled a six and has gained a turn!" );
						}
					}
					else {
						plainMessage("Sorry! " + name2 + " must roll the exact number to land on 100 to win." );
					}
				}
				else{
					p2TrapDelay--;
					plainMessage("Sorry! " + name2 + " is trapped." );
				}
			}
			while(val == 6);
			
			if(p2Location == 100){
				plainMessage("Congratulations! " + name2 + " has won!" );
				break;
			}
		}
	}
	
	public static void main(String args[]){
		SLGame slg = new SLGame();
		slg.control();
	}
}
