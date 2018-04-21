import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class MouseSquaresPanel extends JPanel implements Command {

    private List<String> squares = new ArrayList<String>();
    private ArrayList<Rect> rectList = new ArrayList<Rect>();
    private int x, y, count;
    private boolean left, right = false;
    private Rect rect;
    Stack<Command> redoStack = new Stack<Command>();
    Stack<Command> undoStack = new Stack<Command>();

    public MouseSquaresPanel() {

        setSize(500, 500);

        count = 0;

        addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    count++;
                    x = e.getX();
                    y = e.getY();
                    left = true;
                    right = false;
                    repaint();
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    count++;
                    left = false;
                    right = true;
                    repaint();
                }

            }

        });

    }

    public Stack<Command> getUndo() {
        return undoStack;
    }

    public Stack<Command> getRedo() {
        return redoStack;
    }

    @Override
    public void paint(Graphics g) {
        if (count == 0) {

        }
        else if (left == true && right == false) {
            rect = new Rect(x, y, 10, 10, Color.blue);
            rectList.add(rect);
        }
        else {
            System.out.println("Got into right click, now calling setNextColor");
            rect.setNextColor();
            rectList.set(rectList.size() - 1, rect);
        }

        drawSquares(g);

    }

    private void drawSquares(Graphics g) {
        if (count != 0) {
            for (int i = 0; i < rectList.size(); i++) {
                rectList.get(i).draw(g);
            }
            
        }

    }
    
    public List<String> initilizeList() {
        squares = new ArrayList<String>();
        System.out.println(squares.size());
        for (int i = 0; i < rectList.size(); i++) {
            System.out.println("Number of times through loop: " + (i + 1));
            squares.add("[color="
                    + rectList.get(i).getColorString()
                    + ",x=" + rectList.get(i).getX() + ",y="
                    + rectList.get(i).getY() + "]");
        }
        return squares;
    }

    @Override
    public Command execute() {
        undoStack.push(this);
        return null;
    }

    @Override
    public Command undo() {
        redoStack.push(this);
        return this;
    }
    
    public Command redo() {
        return null;
    }

}
