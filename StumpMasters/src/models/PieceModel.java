package models;

import java.util.List;

public abstract class PieceModel {
	public final static int BLACK = 0;
	public final static int WHITE = 1;
	private int xpos;
	private int ypos;
	private int color;
	
	public PieceModel(){
		xpos = -1;
		ypos = -1;
	}
	
	public PieceModel(int xpos, int ypos, int color){
		this.xpos = xpos;
		this.ypos = ypos;
		this.color = color;
	}
	
	public int getXpos() {
		return this.xpos;
	}
	
	public int getYpos() {
		return this.ypos;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public void setXpos(int xPos) {
		this.xpos = xPos;
	}
	
	public void setYpos(int yPos) {
		this.ypos = yPos;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	public void setPosition(int xpos, int ypos){
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	public abstract List<Integer[]> getValidMoves(PieceModel[][] board);
	
}
