/*
This class is used to create a pop-up window that allows the user to set the game settings. It's pretty simple. I used
some default Java Swing Demo from Oracle to teach myself how all the components work, but this is my own creation.
I hate coding GUIs, but here it is.
*/

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

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
    final Board landData;
    final LandscapeFrame parentFrame;


    /**
     * Creates the pop-up window off of a provided Dialog frame. Then tags on some properties and the cute icon.
     */
    public PopUpClass(JDialog frame, Board landscape, LandscapeFrame parent) {
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
        JPanel settingsPanel = gameSettings();
        JPanel settingsPanel2 = gameSettings2();
        label = new JLabel("Click Reset once you've input your desired settings.", JLabel.CENTER);

        //Places them in the panel.
        Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
        settingsPanel.setBorder(padding);
        settingsPanel2.setBorder(padding);

        //Creates tabbed pane and panes.
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Game Settings I", null, settingsPanel);
        tabbedPane.addTab("Game Settings II", null, settingsPanel2);

        //Adds tabbed panes and labels to window.
        add(tabbedPane, BorderLayout.CENTER);
        add(label, BorderLayout.PAGE_END);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Creates an image icon object from a provided image path.
     * <p>
     * If the file does not exist, will return null.
     *
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
        final ButtonGroup buttonGroup = new ButtonGroup();

        JButton resetButton;

        toggleButtons[0] = new JToggleButton("Empty Board");
        toggleButtons[0].setActionCommand("0");

        toggleButtons[1] = new JToggleButton("Random Board");
        toggleButtons[1].setActionCommand("1");

        toggleButtons[2] = new JToggleButton("Board From File");
        toggleButtons[2].setActionCommand("2");

        for (JToggleButton toggleButton : toggleButtons) {
            buttonGroup.add(toggleButton);
        }
        toggleButtons[landData.getConstructorUsed()].setSelected(true);

        //Creates the reset button and ties action.
        resetButton = new JButton("Reset?");
        resetButton.addActionListener(e -> {
            String actionCommand = buttonGroup.getSelection().getActionCommand();
            int actionNum = Integer.parseInt(actionCommand);
            if (actionNum == 1 && landData.getInitialLock() == 0) {
                landData.setInitialLock(25);
            } else if (actionNum == 2 && landData.getFileSource() == null) {
                landData.setFileSource("board1.txt");
            }
            landData.setConstructorUsed(actionNum);
            landData.reset();
            parentFrame.repaint();
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
    private JPanel gameSettings2() {
        //Button declarations.
        JButton resetButton;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        Runnable fileRun = () -> {
            int choice = fileChooser.showOpenDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                landData.setFileSource(filename);
            }
        };

        //Creates the spinners.

        SpinBox sleep = new SpinBox("Sleep: ", 10, 0, 1000000, 1);

        SpinBox initialLock = new SpinBox("Locked Cells: ", 25, 0, 40, 1);

        ButtonBox filePick = new ButtonBox("Load File: ", "File Chooser", fileRun);

        //Creates the boxes.
        Box[] container = new Box[3];
        container[0] = sleep.getBox();
        container[1] = initialLock.getBox();
        container[2] = filePick.getBox();

        //Creates the submit button and ties action.
        resetButton = new JButton("Reset?");
        resetButton.addActionListener(e -> {
            landData.setSleepTime((int) sleep.getSpinner().getValue());
            landData.setInitialLock((int) initialLock.getSpinner().getValue());
            landData.reset();
            parentFrame.repaint();
        });

        return panelConstructor(container, resetButton);
    }


    /**
     * Utility class for going through a list of Components and creating a combined JPanel with a submit button.
     *
     * @param items  Components to be added.
     * @param button submit button to be added.
     * @return constructed JPanel to be displayed.
     */
    private JPanel panelConstructor(Component[] items, JButton button) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
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
            item.setMaximumSize(new Dimension(1920, item.getHeight() + 40));
            item.setPreferredSize(new Dimension((int) item.getPreferredSize().getWidth(), item.getHeight() + 40));
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
         *
         * @param text text to put in Label.
         */
        public SpinBox(String text, int defaultValue, int min, int max, int step) {
            SpinnerNumberModel spin = new SpinnerNumberModel(defaultValue, min, max, step);
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


    /**
     * Aforementioned SpinBox Class, basically a JSpinner-Box AIO structure.
     * <p>
     * Creates Box and JSpinner puts JSpinner in Box, adds Label, and makes values individually fetch-able.
     */
    public static class ButtonBox {

        public final Box panel;
        public final JButton button;

        /**
         * Constructor for the class does all the important set-up.
         *
         * @param text text to put in Label.
         */
        public ButtonBox(String text, String buttonText, Runnable runnable) {
            this.panel = Box.createHorizontalBox();
            this.button = new JButton(buttonText);
            this.button.addActionListener(e -> runnable.run());
            this.panel.add(new JLabel(text));
            this.panel.add(Box.createHorizontalGlue());
            this.panel.add(this.button);
        }

        //Getter for spinner.
        public JButton getButton() {
            return this.button;
        }

        //Getter for Box.
        public Box getBox() {
            return this.panel;
        }

    }
}
