import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class MouseSquaresTest {
    @BeforeClass
    public static void testVersionWithReflection() {
        String updateGooey = "download a newer version of gooey.jar from <https://github.com/robertoaflores/Gooey>";
        try {
            Class<?> gooey    = Class.forName("edu.cnu.cs.gooey.Gooey");
            Method   version  = gooey.getMethod( "getVersion" );
            String   actual   = (String) version.invoke( null );
            String   expected = "1.8.0";
            assertTrue( "", actual.startsWith( expected ));
        } catch (ClassNotFoundException |
                 NoSuchMethodException  | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            fail( updateGooey );
        }
    }
    @BeforeClass
    public static void kickStart() {
        JFrame f = new MouseSquares();
        f.setVisible(true);
        f.dispose();
    }
    // structural
    @Test
    public void testStructural_ClassHasPrivateFieldsOnly() {
        Class<?> iClass  = MouseSquares.class;
        Field[]  iFields = iClass.getDeclaredFields();
        for (Field f : iFields) {
            if (!f.isSynthetic()) {
                assertTrue ("Field '" + f.getName() + "' should be private",       Modifier.isPrivate(f.getModifiers()));
                assertFalse("Field '" + f.getName() + "' can't be static",         Modifier.isStatic (f.getModifiers()));
                assertFalse("Field '" + f.getName() + "' can't be of type JFrame", f.getType().isAssignableFrom( JFrame.class ));
            }
        }
    }
    // non-functional
    @Test
    public void testNonFunctional_WindowHasTypeAndTitle() throws NoSuchMethodException, SecurityException {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame f) {
                {
                    Class<?> isa    = MouseSquares.class.getSuperclass();
                    assertTrue( "Class should extend JFrame", isa == JFrame.class );
                }
                {
                    Class<?> expected = MouseSquares.class;
                    Class<?> actual   = f.getClass();
                    assertEquals( "Frame isn't of expected type", expected, actual );
                }{
                    String expected = "MouseSquares";
                    String actual   = f.getTitle();
                    assertEquals( "Unexpected window title", expected, actual );
                }
            }
        });
    }
    @Test
    public void testNonFunctional_WindowHasLocationAndSize() throws NoSuchMethodException, SecurityException {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame f) {
                {
                    Dimension actual   = f.getSize();
                    Dimension expected = new Dimension( 500, 500 );
                    assertEquals( "Window has incorrect dimensions", expected, actual );
                }{
                    Point actual   = f.getLocation();
                    f.setLocationRelativeTo( null );
                    Point expected = f.getLocation();
                    assertEquals( "Window not centered on screen", expected, actual );
                }
            }
        });
    }
    @Test
    public void testNonFunctional_WindowHasMenus() throws NoSuchMethodException, SecurityException {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame f) {
                {
                    JMenuBar     menubar  = Gooey.getMenuBar( f );
                    List<JMenu>  actual   = Gooey.getMenus( menubar );
                    List<String> expected = new ArrayList<>(Arrays.asList( new String[]{ "Program", "Edit" } )); 
                    for (JMenu menu : actual) {
                        String label = menu.getText();
                        if (expected.contains(label)) {
                            expected.remove  (label);
                        } else {
                            fail( "Unexpected menu found: " + label );
                        }
                    }
                    if (!expected.isEmpty()) {
                        fail( "Expected menus not found: " + expected );
                    }
                }{
                    JMenuBar        menubar = Gooey.getMenuBar( f );
                    JMenu           program = Gooey.getMenu( menubar, "Program" );
                    List<JMenuItem> actual  = Gooey.getMenus( program );
                    assertEquals( "'Program' menu should have one menu item", 1, actual.size() );
                    assertEquals( "'Program' menu has unexpected menu item", "Exit", actual.get(0).getText() );
                    assertTrue  ( "'Exit' menu item should be enabled",              actual.get(0).isEnabled() );
                }{
                    JMenuBar            menubar  = Gooey.getMenuBar( f );
                    JMenu               edit     = Gooey.getMenu( menubar, "Edit" );
                    List<JMenuItem>     actual   = Gooey.getMenus( edit );
                    Map<String,Boolean> expected = new HashMap<>();
                    expected.put( "Undo", false );
                    expected.put( "Redo", false );
                    expected.put( "List", true  );
                    
                    assertEquals( "'Edit' menu should have two menu items", expected.size(), actual.size() );

                    for (JMenuItem menu : actual) {
                        String label = menu.getText();
                        if (expected.containsKey(label)) {
                            boolean on = expected.get( label );
                            if (on) assertTrue ( "'"+label+"' menu item should be enabled",  menu.isEnabled() );
                            else    assertFalse( "'"+label+"' menu item should be disabled", menu.isEnabled() );
                            expected.remove(label);
                        } else {
                            fail( "Unexpected menu item found: " + label );
                        }
                    }
                    if (!expected.isEmpty()) {
                        fail( "Expected menu items not found: " + expected );
                    }
                }
            }
        });
    }
    // functional
    @Test
    public void testFunctional_WindowClosesFromMenuAndCloseIcon() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame f) {
                int actual   = f.getDefaultCloseOperation();
                int expected = JFrame.DISPOSE_ON_CLOSE;
                assertTrue( "Window doesn't implement DISPOSE_ON_CLOSE", expected == actual );

                f.dispatchEvent(new WindowEvent( f, WindowEvent.WINDOW_CLOSING ));
                assertFalse( "Window should close when clicking on its close icon", f.isShowing() );
            }
        });
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame f) {
                int actual   = f.getDefaultCloseOperation();
                int expected = JFrame.DISPOSE_ON_CLOSE;
                assertTrue( "Window doesn't implement DISPOSE_ON_CLOSE", expected == actual );

                JMenuBar  menubar = Gooey.getMenuBar(f);
                JMenu     song    = Gooey.getMenu( menubar, "Program" );
                JMenuItem exit    = Gooey.getMenu( song,    "Exit" );
                exit.doClick();
                assertFalse( "Window should close when using the 'Exit' menu", f.isShowing() );
            }
        });
    }
    private static void mouseClick(JFrame frame, JPanel panel, int x, int y, int mouseButton) {
        try {
            SwingUtilities.invokeAndWait( ()-> {
                int eventModifier = mouseButton == MouseEvent.BUTTON1 ? MouseEvent.BUTTON1_MASK : MouseEvent.BUTTON2_MASK;
                panel.dispatchEvent( new MouseEvent( 
                                 frame,                      // event source 
                                 MouseEvent.MOUSE_CLICKED,   // event id 
                                 System.currentTimeMillis(), // time when it happened
                                 eventModifier,              // event modifiers
                                 x, y,                       // (x,y) location
                                 1,                          // click count
                                 false,                      // triggers popup
                                 mouseButton ));             // button
                });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void assertEqualContent(String expected, ListModel<?> model) {
        StringBuilder actual = new StringBuilder(); 
        for (int i = 0; i < model.getSize(); i++) {
            actual.append( model.getElementAt( i ).toString()).append('\n');
        }
        assertEquals( "unexpected result", expected, actual.toString() );
    }
    @Test
    public void testFunctional_AddSquaresThenDisplayDialog() {
        // 1. (mouse: add two squares)
        // 2. (get squares)[BLUE,ORANGE]
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame frame) {
                JMenuBar     menubar = Gooey.getMenuBar( frame );
                JMenu        edit    = Gooey.getMenu( menubar, "Edit" );
                JMenuItem    list    = Gooey.getMenu( edit,    "List" );
                List<JPanel> panels  = Gooey.getComponents( frame, JPanel.class, a->a.getParent().getClass() == JPanel.class );
                assertEquals( "unexpected number of JPanel objects in frame", 1, panels.size() );
                JPanel       panel   = panels.get(0);
                
                // 1. (mouse: add two squares)
                mouseClick ( frame, panel, 20, 20, MouseEvent.BUTTON1 );
                mouseClick ( frame, panel, 40, 40, MouseEvent.BUTTON1 );
                mouseClick ( frame, panel,  1,  1, MouseEvent.BUTTON2 );
                // 2. (get squares)[BLUE,ORANGE]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "[color=BLUE,x=20,y=20]\n"+
                                                       "[color=GREEN,x=40,y=40]\n";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
            }
        });
    }
    @Test
    public void testFunctional_AddSquareThenUndoThenRedo() {
        // 1. (mouse: add one square)[undo !redo]
        // 2. (get squares)[BLUE]
        // 3. (menu: undo)[!undo redo]
        // 4. (get squares)[empty]
        // 5. (menu: redo)[undo !redo]
        // 6. (get squares)[BLUE]
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame frame) {
                JMenuBar     menubar = Gooey.getMenuBar( frame );
                JMenu        edit    = Gooey.getMenu( menubar, "Edit" );
                JMenuItem    undo    = Gooey.getMenu( edit,    "Undo" );
                JMenuItem    redo    = Gooey.getMenu( edit,    "Redo" );
                JMenuItem    list    = Gooey.getMenu( edit,    "List" );
                List<JPanel> panels  = Gooey.getComponents( frame, JPanel.class, a->a.getParent().getClass() == JPanel.class );
                assertEquals( "unexpected number of JPanel objects in frame", 1, panels.size() );
                JPanel       panel   = panels.get(0);
                
                // 1. (mouse: add one square)[undo !redo]
                mouseClick ( frame, panel, 50, 80, MouseEvent.BUTTON1 );
                assertTrue ( "'Undo' menu should be enabled",  undo.isEnabled() );
                assertFalse( "'Redo' menu should be disabled", redo.isEnabled() );
                // 2. (get squares)
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "[color=BLUE,x=50,y=80]\n";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
                // 3. (menu: undo)[!undo redo]
                undo.doClick();
                assertFalse( "'Undo' menu should be disabled", undo.isEnabled() );
                assertTrue ( "'Redo' menu should be enabled",  redo.isEnabled() );
                // 4. (get squares)[empty]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
                // 5. (menu: redo)[undo !redo]
                redo.doClick();
                assertTrue ( "'Undo' menu should be enabled",  undo.isEnabled() );
                assertFalse( "'Redo' menu should be disabled", redo.isEnabled() );
                // 6. (get squares)[equal copy]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "[color=BLUE,x=50,y=80]\n";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
            }
        });
    }
    @Test
    public void testFunctional_AddSquareThenChangeToEveryColorThenUndoAll() {
        // 1. (mouse: add one square)[undo !redo]
        // 2. for each COLOR in [BLUE,GREEN,MAGENTA,ORANGE,RED,YELLOW,BLUE] do 
        //    a. (get squares)[COLOR]
        //    b. (mouse: change color)[undo !redo]
        // 3. for each COLOR in [BLUE,YELLOW,RED,ORANGE,MAGENTA,GREEN,BLUE] do 
        //    a. (get squares)[COLOR]
        //    b. (menu: undo)[undo redo]
        // 4. (menu: undo)[undo !redo]
        // 5. (get squares)[empty]
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame frame) {
                JMenuBar     menubar = Gooey.getMenuBar( frame );
                JMenu        edit    = Gooey.getMenu( menubar, "Edit" );
                JMenuItem    undo    = Gooey.getMenu( edit,    "Undo" );
                JMenuItem    redo    = Gooey.getMenu( edit,    "Redo" );
                JMenuItem    list    = Gooey.getMenu( edit,    "List" );
                List<JPanel> panels  = Gooey.getComponents( frame, JPanel.class, a->a.getParent().getClass() == JPanel.class );
                assertEquals( "unexpected number of JPanel objects in frame", 1, panels.size() );
                JPanel       panel   = panels.get(0);

                // 1. (mouse: add one square)[undo !redo]
                mouseClick ( frame, panel, 42, 121, MouseEvent.BUTTON1 );
                assertTrue ( "'Undo' menu should be enabled",  undo.isEnabled() );
                assertFalse( "'Redo' menu should be disabled", redo.isEnabled() );
                // 2. for each COLOR in [BLUE,GREEN,MAGENTA,ORANGE,RED,YELLOW,BLUE] do 
                //    a. (get squares)[COLOR]
                //    b. (mouse: change color)[undo !redo]
                for (String color : new String[]{"BLUE","GREEN","MAGENTA","ORANGE","RED","YELLOW","BLUE"}) {
                    Gooey.capture(new GooeyDialog() {
                        @Override
                        public void invoke() {
                            list.doClick();
                        }
                        @Override
                        public void test(JDialog dialog) {
                            JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                            assertNotNull( list );
                            String              expected = "[color="+color+",x=42,y=121]\n";
                            assertEqualContent( expected, list.getModel() );
                        }
                    });
                    mouseClick ( frame, panel, 0, 0, MouseEvent.BUTTON2 );
                    assertTrue ( "'Undo' menu should be enabled",  undo.isEnabled() );
                    assertFalse( "'Redo' menu should be disabled", redo.isEnabled() );
                }
                // 3. for each COLOR in [GREEN,BLUE,YELLOW,RED,ORANGE,MAGENTA,GREEN] do 
                //    a. (get squares)[COLOR]
                //    b. (menu: undo)[undo redo]
                for (String color : new String[]{"GREEN","BLUE","YELLOW","RED","ORANGE","MAGENTA","GREEN"}) {
                    Gooey.capture(new GooeyDialog() {
                        @Override
                        public void invoke() {
                            list.doClick();
                        }
                        @Override
                        public void test(JDialog dialog) {
                            JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                            assertNotNull( list );
                            String              expected = "[color="+color+",x=42,y=121]\n";
                            assertEqualContent( expected, list.getModel() );
                        }
                    });
                    undo.doClick();
                    assertTrue ( "'Undo' menu should be enabled", undo.isEnabled() );
                    assertTrue ( "'Redo' menu should be enabled", redo.isEnabled() );
                }
                // 4. (menu: undo)[undo !redo]
                undo.doClick();
                assertFalse( "'Undo' menu should be disabled", undo.isEnabled() );
                assertTrue ( "'Redo' menu should be enabled",  redo.isEnabled() );
                // 5. (get squares)[empty]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
            }
        });
    }
    @Test
    public void testFunctional_AddThreeSquaresThenUndoTwoThenAddOne() {
        // 1. (mouse: add 3 squares)[undo !redo]
        // 2. (get squares)[has 3]
        // 3. (menu: undo twice)[undo redo]
        // 4. (get squares)[has 1]
        // 5. (mouse: add 1 square)[undo !redo]
        // 6. (get squares)[has 2]
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame frame) {
                JMenuBar     menubar = Gooey.getMenuBar( frame );
                JMenu        edit    = Gooey.getMenu( menubar, "Edit" );
                JMenuItem    undo    = Gooey.getMenu( edit,    "Undo" );
                JMenuItem    list    = Gooey.getMenu( edit,    "List" );
                List<JPanel> panels  = Gooey.getComponents( frame, JPanel.class, a->a.getParent().getClass() == JPanel.class );
                assertEquals( "unexpected number of JPanel objects in frame", 1, panels.size() );
                JPanel       panel   = panels.get(0);

                Point[] mouse = { new Point(30,130), new Point(142,42), new Point(7,11) };
                // 1. (mouse: add 3 squares)[undo !redo]
                for (int i = 0; i < mouse.length; i++) {
                    mouseClick ( frame, panel, mouse[i].x, mouse[i].y, MouseEvent.BUTTON1 );
                }
                // 2. (get squares)[has 3]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "[color=BLUE,x=30,y=130]\n"+
                                                       "[color=BLUE,x=142,y=42]\n"+
                                                       "[color=BLUE,x=7,y=11]\n";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
                // 3. (menu: undo twice)[undo redo]
                undo.doClick();
                undo.doClick();
                // 4. (get squares)[has 1]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "[color=BLUE,x=30,y=130]\n";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
                // 5. (mouse: add 1 square)[undo !redo]
                mouseClick  ( frame, panel, 250, 186, MouseEvent.BUTTON1 );
                // 6. (get squares)[has 2]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "[color=BLUE,x=30,y=130]\n"+
                                                       "[color=BLUE,x=250,y=186]\n";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
            }
        });
    }
    @Test
    public void testFunctional_AddManySquaresThenUndoAllThenRedoAll() {
        // 1. (mouse: add squares)[undo !redo]
        // 2. (get squares)[has all]
        // 3. (menu: undo all)[!undo redo]
        // 4. (get squares)[empty]
        // 5. (menu: redo all)[undo !redo]
        // 6. (get squares)[has all]
        final String expectedInDialog = 
                "[color=BLUE,x=250,y=200]\n"+
                "[color=BLUE,x=260,y=200]\n"+
                "[color=BLUE,x=270,y=200]\n"+
                "[color=BLUE,x=280,y=200]\n"+
                "[color=BLUE,x=290,y=200]\n"+
                "[color=BLUE,x=300,y=200]\n"+
                "[color=BLUE,x=310,y=200]\n"+
                "[color=BLUE,x=320,y=200]\n"+
                "[color=BLUE,x=330,y=200]\n"+
                "[color=BLUE,x=340,y=200]\n"+
                "[color=BLUE,x=340,y=210]\n"+
                "[color=BLUE,x=340,y=220]\n"+
                "[color=BLUE,x=340,y=230]\n"+
                "[color=BLUE,x=340,y=240]\n"+
                "[color=BLUE,x=340,y=250]\n"+
                "[color=BLUE,x=340,y=260]\n"+
                "[color=BLUE,x=340,y=270]\n"+
                "[color=BLUE,x=340,y=280]\n"+
                "[color=BLUE,x=340,y=290]\n"+
                "[color=BLUE,x=330,y=290]\n"+
                "[color=BLUE,x=320,y=290]\n"+
                "[color=BLUE,x=310,y=290]\n"+
                "[color=BLUE,x=300,y=290]\n"+
                "[color=BLUE,x=290,y=290]\n"+
                "[color=BLUE,x=280,y=290]\n"+
                "[color=BLUE,x=270,y=290]\n"+
                "[color=BLUE,x=260,y=290]\n"+
                "[color=BLUE,x=250,y=290]\n"+
                "[color=BLUE,x=250,y=280]\n"+
                "[color=BLUE,x=250,y=270]\n"+
                "[color=BLUE,x=250,y=260]\n"+
                "[color=BLUE,x=250,y=250]\n"+
                "[color=BLUE,x=250,y=240]\n"+
                "[color=BLUE,x=250,y=230]\n"+
                "[color=BLUE,x=250,y=220]\n"+
                "[color=BLUE,x=250,y=210]\n"+
                "[color=BLUE,x=260,y=210]\n"+
                "[color=BLUE,x=270,y=210]\n"+
                "[color=BLUE,x=280,y=210]\n"+
                "[color=BLUE,x=290,y=210]\n"+
                "[color=BLUE,x=300,y=210]\n"+
                "[color=BLUE,x=310,y=210]\n"+
                "[color=BLUE,x=320,y=210]\n"+
                "[color=BLUE,x=330,y=210]\n"+
                "[color=BLUE,x=330,y=220]\n"+
                "[color=BLUE,x=330,y=230]\n"+
                "[color=BLUE,x=330,y=240]\n"+
                "[color=BLUE,x=330,y=250]\n"+
                "[color=BLUE,x=330,y=260]\n"+
                "[color=BLUE,x=330,y=270]\n"+
                "[color=BLUE,x=330,y=280]\n"+
                "[color=BLUE,x=320,y=280]\n"+
                "[color=BLUE,x=310,y=280]\n"+
                "[color=BLUE,x=300,y=280]\n"+
                "[color=BLUE,x=290,y=280]\n"+
                "[color=BLUE,x=280,y=280]\n"+
                "[color=BLUE,x=270,y=280]\n"+
                "[color=BLUE,x=260,y=280]\n"+
                "[color=BLUE,x=260,y=270]\n"+
                "[color=BLUE,x=260,y=260]\n"+
                "[color=BLUE,x=260,y=250]\n"+
                "[color=BLUE,x=260,y=240]\n"+
                "[color=BLUE,x=260,y=230]\n"+
                "[color=BLUE,x=260,y=220]\n"+
                "[color=BLUE,x=270,y=220]\n"+
                "[color=BLUE,x=280,y=220]\n"+
                "[color=BLUE,x=290,y=220]\n"+
                "[color=BLUE,x=300,y=220]\n"+
                "[color=BLUE,x=310,y=220]\n"+
                "[color=BLUE,x=320,y=220]\n"+
                "[color=BLUE,x=320,y=230]\n"+
                "[color=BLUE,x=320,y=240]\n"+
                "[color=BLUE,x=320,y=250]\n"+
                "[color=BLUE,x=320,y=260]\n"+
                "[color=BLUE,x=320,y=270]\n"+
                "[color=BLUE,x=310,y=270]\n"+
                "[color=BLUE,x=300,y=270]\n"+
                "[color=BLUE,x=290,y=270]\n"+
                "[color=BLUE,x=280,y=270]\n"+
                "[color=BLUE,x=270,y=270]\n"+
                "[color=BLUE,x=270,y=260]\n"+
                "[color=BLUE,x=270,y=250]\n"+
                "[color=BLUE,x=270,y=240]\n"+
                "[color=BLUE,x=270,y=230]\n"+
                "[color=BLUE,x=280,y=230]\n"+
                "[color=BLUE,x=290,y=230]\n"+
                "[color=BLUE,x=300,y=230]\n"+
                "[color=BLUE,x=310,y=230]\n"+
                "[color=BLUE,x=310,y=240]\n"+
                "[color=BLUE,x=310,y=250]\n"+
                "[color=BLUE,x=310,y=260]\n"+
                "[color=BLUE,x=300,y=260]\n"+
                "[color=BLUE,x=290,y=260]\n"+
                "[color=BLUE,x=280,y=260]\n"+
                "[color=BLUE,x=280,y=250]\n"+
                "[color=BLUE,x=280,y=240]\n"+
                "[color=BLUE,x=290,y=240]\n"+
                "[color=BLUE,x=300,y=240]\n"+
                "[color=BLUE,x=300,y=250]\n"+
                "[color=BLUE,x=290,y=250]\n";
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                MouseSquares.main( new String[]{} );
            }
            @Override
            public void test(JFrame frame) {
                JMenuBar     menubar = Gooey.getMenuBar( frame );
                JMenu        edit    = Gooey.getMenu( menubar, "Edit" );
                JMenuItem    undo    = Gooey.getMenu( edit,    "Undo" );
                JMenuItem    redo    = Gooey.getMenu( edit,    "Redo" );
                JMenuItem    list    = Gooey.getMenu( edit,    "List" );
                List<JPanel> panels  = Gooey.getComponents( frame, JPanel.class, a->a.getParent().getClass() == JPanel.class );
                assertEquals( "unexpected number of JPanel objects in frame", 1, panels.size() );
                JPanel       panel   = panels.get(0);

                // 1. (mouse: add several squares)[undo !redo]
                final int x = 250;
                final int y = 200;
                int rowBegin =  0;
                int colBegin =  0;
                int rowEnd   = 10;
                int colEnd   = 10;
                for (int times = 0; times < 5; times++) {
                    // top row
                    for (int i = colBegin; i < colEnd; i++) {
//                      array[ rowBegin ][ i ] = letter.next();
                        mouseClick( frame, panel, x+(i)*10,        y+(rowBegin)*10, MouseEvent.BUTTON1 );
                    }
                    // right column
                    for (int i = rowBegin+1; i < rowEnd; i++) {
//                      array[ i ][ colEnd-1 ] = letter.next();
                        mouseClick( frame, panel, x+(colEnd-1)*10, y+(i)*10,        MouseEvent.BUTTON1 );
                    }
                    // bottom row
                    for (int i = colEnd-2; i >= colBegin; i--) {
//                      array[ rowEnd-1 ][ i ] = letter.next();
                        mouseClick( frame, panel, x+(i)*10,        y+(rowEnd-1)*10, MouseEvent.BUTTON1 );
                    }
                    // left column
                    for (int i = rowEnd-2; i > rowBegin; i--) {
//                      array[ i ][ colBegin ] = letter.next();
                        mouseClick( frame, panel, x+(colBegin)*10, y+(i)*10,        MouseEvent.BUTTON1 );
                    }
                    rowBegin++;
                    colBegin++;
                    rowEnd--;
                    colEnd--;
                }
                // 2. (get squares)[has all]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = expectedInDialog;
                        assertEqualContent( expected, list.getModel() );
                    }
                });
                // 3. (menu: undo all)[!undo redo]
                for (int i = 0; i < 99; i++) {
                    undo.doClick();
                    assertTrue ( "'Undo' menu should be enabled", undo.isEnabled() );
                    assertTrue ( "'Redo' menu should be enabled", redo.isEnabled() );
                }
                undo.doClick();
                assertFalse ( "'Undo' menu should be disabled", undo.isEnabled() );
                assertTrue  ( "'Redo' menu should be enabled",  redo.isEnabled() );
                // 4. (get squares)[empty]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = "";
                        assertEqualContent( expected, list.getModel() );
                    }
                });
                // 5. (menu: redo all)[undo !redo]
                for (int i = 0; i < 99; i++) {
                    redo.doClick();
                    assertTrue ( "'Undo' menu should be enabled", undo.isEnabled() );
                    assertTrue ( "'Redo' menu should be enabled", redo.isEnabled() );
                }
                redo.doClick();
                assertTrue  ( "'Undo' menu should be enabled",  undo.isEnabled() );
                assertFalse ( "'Redo' menu should be disabled", redo.isEnabled() );
                // 6. (get squares)[has all]
                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        list.doClick();
                    }
                    @Override
                    public void test(JDialog dialog) {
                        JList<?>       list   = Gooey.getComponent( dialog, JList.class );
                        assertNotNull( list );
                        String              expected = expectedInDialog;
                        assertEqualContent( expected, list.getModel() );
                    }
                });
            }
        });
    }
}