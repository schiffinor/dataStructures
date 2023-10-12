/*
This class is used to create a pop-up window that allows the user to set the game settings. It's pretty simple. I used
some default Java Swing Demo from Oracle to teach myself how all the components work, but this is my own creation.
I hate coding GUIs, but here it is.
*/
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
/**
 * The `PopUpClass` is used to create a pop-up window for configuring game settings in a Java Swing-based application.
 * This class provides an interface for users to set various game parameters such as landscape size, sleep time, and
 * agent parameters. It encapsulates the layout and interaction components of the settings window.
 * <p>
 * The `PopUpClass` class creates a tabbed window with multiple tabs, and the main "Game Settings" tab contains input fields for setting
 * various game parameters. Users can modify settings and apply changes to the simulation by clicking the "Resize?" button.
 * A custom icon is set for the pop-up window, and the layout is designed to be user-friendly.
 * <p>
 * The class also includes a utility method for creating image icons and a sub-class `SpinBox` for creating spinner-box components.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class PopUpClass extends JPanel {
    final JLabel label;
    final ImageIcon icon = createImageIcon("gura2.png");
    final JDialog frame;
    final Landscape landData;
    final LandscapeFrame parentFrame;


    /**
     * Creates the pop-up window off of a provided Dialog frame. Then tags on some properties and the cute icon.
     */
    public PopUpClass(JDialog frame, Landscape landscape, LandscapeFrame parent) {
        //Creates the base panel.
        super(new BorderLayout());
        //Icon for the pop-up window.
        this.frame = frame;
        assert icon != null;
        this.frame.setIconImage(icon.getImage());
        //Sets landscape data and parent frame.
        this.landData = landscape;
        this.parentFrame = parent;

        //Creates the panel components.
        JPanel sizePanel = gameSize();
        label = new JLabel("Click Reset once you've input your desired settings.", JLabel.CENTER);

        //Places them in the panel.
        Border padding = BorderFactory.createEmptyBorder(20,20,5,20);
        sizePanel.setBorder(padding);

        //Creates tabbed pane and panes.
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Game Settings", null, sizePanel);

        //Adds tabbed panes and labels to window.
        add(tabbedPane, BorderLayout.CENTER);
        add(label, BorderLayout.PAGE_END);
        label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    /**
     * Creates an image icon object from a provided image path.
     * <p>
     * If the file does not exist, will return null.
     * @param path image file path.
     * @return ImageIcon object to use as icon for the window.
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = PopUpClass.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Creates the panel used in the second page of the tabbed pane structure.
     * <p>
     * Creates two spinner boxes using a class I outline below.
     * There is basically a Box and a JSpinner all in one.
     * Then creates a submit button that fetches the state of the spinners, constructs an array of ints and
     * passes values as an int array.
     */
    private JPanel gameSize() {
        //Button declarations.
        JButton resizeButton;

        //Creates the spinners.
        SpinBox height = new SpinBox("Height: ",500,10,1000000,10);

        SpinBox width = new SpinBox("Width: ",500,10,1000000,10);

        SpinBox sleep = new SpinBox("Sleep: ",100,0,1000000,1);

        SpinBox agents = new SpinBox("Agents: ",100,1,1000000,1);

        SpinBox sRad = new SpinBox("Social \nRadius: ",25,1,1000000,1);

        SpinBox aRad = new SpinBox("AntiSocial \nRadius: ",25,1,1000000,1);

        //Creates the boxes.
        Box[] container = new Box[6];
        container[0] = height.getBox();
        container[1] =width.getBox();
        container[2] =sleep.getBox();
        container[3] =agents.getBox();
        container[4] =sRad.getBox();
        container[5] =aRad.getBox();

        //Creates the submit button and ties action.
        resizeButton = new JButton("Resize?");
        resizeButton.addActionListener(e -> {
            Integer[] sizeArray = new Integer[2];
            sizeArray[0] = (int) height.getSpinner().getValue();
            sizeArray[1] = (int) width.getSpinner().getValue();
            if ((sizeArray[0] % 10 != 0) || (sizeArray[1] % 10 != 0)) {
                sizeArray[0] = sizeArray[0] - (sizeArray[0] % 10);
                sizeArray[1] = sizeArray[1] - (sizeArray[1] % 10);
            }
            System.out.println(Arrays.toString(sizeArray));
            landData.setHeight(sizeArray[0]);
            landData.setWidth(sizeArray[1]);
            landData.setSleepTime((int) sleep.getSpinner().getValue());
            landData.setAgents((int) agents.getSpinner().getValue());
            landData.setSocialRadius((int) sRad.getSpinner().getValue());
            landData.setAntiSocialRadius((int) aRad.getSpinner().getValue());
            SwingUtilities.invokeLater(landData::reset);

            SwingUtilities.invokeLater(parentFrame::repaint);
        });

        return panelConstructor(container, resizeButton);
    }

    /**
     * Utility class for going through a list of Components and creating a combined JPanel with a submit button.
     *
     * @param items Components to be added.
     * @param button submit button to be added.
     * @return constructed JPanel to be displayed.
     */
    private JPanel panelConstructor(Component[] items, JButton button) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS ));
        panel.setBorder(new EmptyBorder(new Insets(50, 75, 50, 75)));
        panel.add(Box.createVerticalGlue());
        panel.add(items[0]);
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        for (int i = 1; i < items.length; i++) {
            panel.add(Box.createVerticalGlue());
            panel.add(items[i]);
            panel.add(Box.createVerticalGlue());
            panel.add(Box.createRigidArea(new Dimension(0, 2)));
        }
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(button);
        for (Component item : items) {
            item.setMaximumSize(new Dimension(1920, item.getHeight()+40));
            item.setPreferredSize(new Dimension((int) item.getPreferredSize().getWidth(), item.getHeight()+40));
        }
        return panel;
    }

    /**
     * Aforementioned SpinBox Class, basically a JSpinner-Box AIO structure.
     * <p>
     * Creates Box and JSpinner puts JSpinner in Box, adds Label, and makes values individually fetch-able.
     */
    public static class SpinBox {

        public final Box panel;
        public final JSpinner spinner;

        /**
         * Constructor for the class does all the important set-up.
         * @param text text to put in Label.
         */
        public SpinBox(String text, int defaultValue, int min, int max, int step) {
            SpinnerNumberModel spin = new SpinnerNumberModel(defaultValue,min, max, step);
            this.panel = Box.createHorizontalBox();
            this.spinner = new JSpinner(spin);
            this.panel.add(new JLabel(text));
            this.panel.add(Box.createHorizontalGlue());
            this.panel.add(spinner);
        }

        //Getter for spinner.
        public JSpinner getSpinner() {
            return this.spinner;
        }

        //Getter for Box.
        public Box getBox() {
            return this.panel;
        }

    }
}
