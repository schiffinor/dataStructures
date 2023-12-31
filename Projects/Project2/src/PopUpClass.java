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
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
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
        JPanel settingPanel = gameSettings();
        JPanel sizePanel = gameSize();
        label = new JLabel("Click Reset once you've input your desired settings.", JLabel.CENTER);

        //Places them in the panel.
        Border padding = BorderFactory.createEmptyBorder(20,20,5,20);
        settingPanel.setBorder(padding);
        sizePanel.setBorder(padding);

        //Creates tabbed pane and panes.
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Game Settings", null, settingPanel);
        tabbedPane.addTab("Game Size", null, sizePanel);

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
     * Creates the panel used in the first page of the tabbed pane structure.
     * <p>
     * Creates a bunch of toggle buttons and appends them to an array.
     * Then creates a submit button that fetches the state of the buttons, constructs an array of booleans and
     * passes values as a boolean array. Never implemented this as game rules would've taken too long.
     */
    private JPanel gameSettings() {
        //Button definitions.
        JToggleButton[] toggleButtons = new JToggleButton[3];

        JButton resetButton;

        toggleButtons[0] = new JToggleButton("Enable Predator Cells");

        toggleButtons[1] = new JToggleButton("Enable Protector Cells");

        toggleButtons[2] = new JToggleButton("Enable Alternative Cells");

        //Creates the reset button and ties action.
        resetButton = new JButton("Reset?");
        resetButton.addActionListener(e -> {
            Boolean[] dataArray = new Boolean[3];
            for (int i = 0, toggleButtonsLength = toggleButtons.length; i < toggleButtonsLength; i++) {
                JToggleButton toggleButton = toggleButtons[i];
                dataArray[i] = toggleButton.isSelected();
            }
            landData.reset();
            System.out.println(Arrays.toString(dataArray));
        });

        return panelConstructor(toggleButtons, resetButton);
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
        SpinBox rows = new SpinBox("Rows: ");

        SpinBox columns = new SpinBox("Columns: ");

        //Creates the boxes.
        Box[] container = new Box[2];
        container[0] = rows.getBox();
        container[1] =columns.getBox();

        //Creates the submit button and ties action.
        resizeButton = new JButton("Resize?");
        resizeButton.addActionListener(e -> {
            Integer[] sizeArray = new Integer[2];
            sizeArray[0] = (int) rows.getSpinner().getValue();
            sizeArray[1] = (int) columns.getSpinner().getValue();
            System.out.println(Arrays.toString(sizeArray));
            landData.setRows(sizeArray[0]);
            landData.setCols(sizeArray[1]);
            SwingUtilities.invokeLater(landData::reset);

            SwingUtilities.invokeLater(parentFrame::repaint);
        });

        return panelConstructor(container, resizeButton);
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
        public SpinBox(String text) {
            SpinnerNumberModel spin = new SpinnerNumberModel(100,1, 1000000, 1);
            this.panel = Box.createHorizontalBox();
            this.spinner = new JSpinner(spin);
            panel.add(Box.createHorizontalGlue());
            panel.add(new JLabel(text));
            panel.add(Box.createHorizontalGlue());
            panel.add(spinner);
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
        panel.add(items[0]);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        for (int i = 1; i < items.length; i++) {
            panel.add(items[i]);
            panel.add(Box.createRigidArea(new Dimension(0, 2)));
        }
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(button);
        return panel;
    }
}
