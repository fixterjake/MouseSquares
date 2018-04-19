import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class MouseSquaresDialog extends JDialog {
    
    private MouseSquaresDialog(JFrame parent, List<String> strings) {
        super( parent );
        
        setLayout( new BorderLayout( 5, 5 ));
        
        String[]      array = new String[ strings.size() ];
        JList<String> list  = new JList<>( strings.toArray( array ));
        JButton       done  = new JButton( "Done" );
        
        JPanel north  = new JPanel( new FlowLayout( FlowLayout.LEFT ));
        JPanel center = new JPanel( new GridLayout( 1, 1 ));
        JPanel south  = new JPanel();

        north .add( new JLabel( "Squares" ));
        center.add( new JScrollPane( list ));
        south .add( done );

        add( north,  BorderLayout.NORTH );
        add( center, BorderLayout.CENTER );
        add( south,  BorderLayout.SOUTH );

        add( new JPanel(),  BorderLayout.EAST );
        add( new JPanel(),  BorderLayout.WEST );

        done.addActionListener( e->dispose() );
        
        setTitle( "List of Squares" );
        setSize ( 400, 200 );
        setLocationRelativeTo( parent );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    }
    
    public static void showListDialog(JFrame parent, List<String> squares) {
        MouseSquaresDialog dialog = new MouseSquaresDialog( parent, squares );
        dialog.setModal  ( true );
        dialog.setVisible( true );
    }
    public static void main(String[] args) {
        System.out.print  ("begin...");
        showListDialog( null, Arrays.asList("0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18"));
        System.out.println("done!");
    }
}