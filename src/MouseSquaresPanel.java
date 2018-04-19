import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MouseSquaresPanel extends JPanel {
    
    private List<String> squares = new ArrayList<String>();
    private int x, y;
    private boolean left, right = false;
    private String current;
    
    Stack<ActionEvent> redoStack = new Stack<ActionEvent>();
    Stack<ActionEvent> undoStack = new Stack<ActionEvent>();
    
    public MouseSquaresPanel() {
        
        setSize(500, 500);
        
        addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    left = true;
                    right = false;
                    repaint();
                }
                
                if (e.getButton() == MouseEvent.BUTTON3) {
                    left = false;
                    right = true;
                    repaint();
                }
                
            }
        });
        
    }
    
    public List<String> getList() {
        return squares;
    }
    
    public Stack<ActionEvent> getUndo() {
        return undoStack;
    }
    
    public Stack<ActionEvent> getRedo() {
        return redoStack;
    }
    
    private void drawSquare(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 10, 10);
        current = "BLUE"; 
    }
    
    private void cycleColor(Graphics g) {
//        if (current.equals("BLUE")) {
//            g.setColor(Color.magenta);
//            g.fillRect(x, y, 10, 10);
//            current = "MAGENTA";
//        }
//        else if (current.equals("MAGENTA")) {
//            g.setColor(Color.orange);
//            g.fillRect(x, y, 10, 10);
//            current = "ORANGE";
//        }
//        else if (current.equals("ORANGE")) {
//            g.setColor(Color.red);
//            g.fillRect(x, y, 10, 10);
//            current = "RED";
//        }
//        else if (current.equals("RED")) {
//            g.setColor(Color.yellow);
//            g.fillRect(x, y, 10, 10);
//            current = "YELLOW";
//        }
//        else {
//            g.setColor(Color.blue);
//            g.fillRect(x, y, 10, 10);
//            current = "BLUE";
//        }
        
    }

    @Override
    public void paint(Graphics g) {
//        if (left == true && right == false) {
//            drawSquare(g);
//        }
//        
//        if (left == false && right == true) {
//            cycleColor(g);
//        }
        
        drawSquare(g);
        
        if (current == null) {
            
        }
        else {
            squares.add("[color=" + current + ",x=" + x + ",y=" + y + "]");
        }

    }

}
