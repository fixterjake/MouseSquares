import java.awt.Color;
import java.awt.Graphics;

public class Rect {
    
    private int x, y;
    private int w, h;
    private Color color;
    
    public Rect(int x, int y, int h, int w, Color color) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
        this.color = color;
    }
    
    public Rect() {
        this.x = 0;
        this.y = 0;
        this.h = 0;
        this.w = 0;
        this.color = null;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getX() {
        return x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getY() {
        return y;
    }
    
    public void setH(int h) {
        this.h = h;
    }
    
    public int getH() {
        return h;
    }
    
    public void setW(int w) {
        this.w = w;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
    
    public String getColorString() {
        if (getColor() == Color.blue) {
            return "BLUE";
        }
        else if (getColor() == Color.green) {
            return "GREEN";
        }
        else if (getColor() == Color.magenta) {
            return "MAGENTA";
        }
        else if (getColor() == Color.orange) {
            return "ORANGE";
        }
        else if (getColor() == Color.red) {
            return "RED";
        }
        else {
            return "YELLOW";
        }
        
    }
    
    public void setNextColor() {
        if (color == Color.blue) {
            setColor(Color.green);
            System.out.println("Set color to green");
        }
        else if (color == Color.green) {
            setColor(Color.magenta);
            System.out.println("Set color to magenta");
        }
        else if (color == Color.magenta) {
            setColor(Color.orange);
            System.out.println("Set color to orange");
        }
        else if (color == Color.orange) {
            setColor(Color.red);
            System.out.println("Set color to red");
        }
        else if (color == Color.red) {
            setColor(Color.yellow);
            System.out.println("Set color to yellow");
        }
        else {
            setColor(Color.blue);
            System.out.println("Set color back to blue");
        }
    }
    
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillRect(x, y, w, h);
    }

}
