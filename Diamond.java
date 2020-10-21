/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author johna
 */
public class Diamond extends Shape{
    
    private int vertDiagonal, horiDiagonal;
    
    public Diamond () {
        super();
    }
    
    public Diamond (int x, int y, int, vertDiagonal, int horiDiagonal) {
        super(x,y);
        this.vertDiagonal = vertDiagonal;
        this.horiDiagonal = horiDiagonal;
        
    }

    public int getVertDiagonal() {
        return vertDiagonal;
    }

    public void setVertDiagonal(int vertDiagonal) {
        this.vertDiagonal = vertDiagonal;
    }

    public int getHoriDiagonal() {
        return horiDiagonal;
    }

    public void setHoriDiagonal(int horiDiagonal) {
        this.horiDiagonal = horiDiagonal;
    }
    
    
    
}
