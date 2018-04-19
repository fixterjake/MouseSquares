import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MouseSquares extends JFrame {
    
    private MouseSquaresPanel panel;
    
    public MouseSquares() {
        setSize(500, 500);
        setTitle("MouseSquares");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JMenuBar menu = new JMenuBar();
        JMenu program = new JMenu("Program");
        JMenu edit = new JMenu("Edit");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem redo = new JMenuItem("Redo");
        JMenuItem list = new JMenuItem("List");
        
        program.add(exit);
        edit.add(undo);
        edit.add(redo);
        edit.addSeparator();
        edit.add(list);
        
        menu.add(program);
        menu.add(edit);
        
        setJMenuBar(menu);
        
        panel = new MouseSquaresPanel();
        
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
        
        list.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listSquares();
            }
        });
        
        if (panel.getUndo().isEmpty()) {
            undo.setEnabled(false);
        }
        
        if (panel.getRedo().isEmpty()) {
            redo.setEnabled(false);
        }
        
        add(panel);
        
    }
    
    
    private void undo() {
        
    }
    
    private void redo() {
        
    }
    
    private void listSquares() {
        MouseSquaresDialog.showListDialog(this, panel.getList());
     }
    
    public static void main(String[] args) {
        MouseSquares s = new MouseSquares();
        s.setVisible(true);
    }

}
