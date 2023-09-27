/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

/*
 * DialogDemo.java requires these files:
 *   CustomDialog.java
 *   images/middle.gif
 */
public class DialogDemo extends JPanel {
    JLabel label;
    ImageIcon icon = createImageIcon("gura2.png");
    JFrame frame;
    public Dialog menu;


    /** Creates the GUI shown inside the frame's content pane. */
    public DialogDemo(JFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        Dialog menu = new JDialog();
        this.frame.setIconImage(icon.getImage());
        menu.pack();

        //Create the components.
        JPanel settingPanel = gameSettings();
        JPanel sizePanel = gameSize();
        label = new JLabel("Click Reset once you've input your desired settings.", JLabel.CENTER);

        //Lay them out.
        Border padding = BorderFactory.createEmptyBorder(20,20,5,20);
        settingPanel.setBorder(padding);
        sizePanel.setBorder(padding);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Game Settings", null, settingPanel); //tooltip text

        tabbedPane.addTab("Game Size", null, sizePanel);

        add(tabbedPane, BorderLayout.CENTER);
        add(label, BorderLayout.PAGE_END);
        label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DialogDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Creates the panel shown by the first tab. */
    private JPanel gameSettings() {
        JToggleButton[] toggleButtons = new JToggleButton[3];

        JButton resetButton = null;

        toggleButtons[0] = new JToggleButton("Enable Predator Cells");

        toggleButtons[1] = new JToggleButton("Enable Protector Cells");

        toggleButtons[2] = new JToggleButton("Enable Alternative Cells");


        resetButton = new JButton("Reset?");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Boolean[] dataArray = new Boolean[3];
                for (int i = 0, toggleButtonsLength = toggleButtons.length; i < toggleButtonsLength; i++) {
                    JToggleButton toggleButton = toggleButtons[i];
                    dataArray[i] = toggleButton.isSelected();
                }
                System.out.println(Arrays.toString(dataArray));
            }
        });

        return createPane(toggleButtons, resetButton);
    }

    /** Creates the panel shown by the first tab. */
    private JPanel gameSize() {

        JSpinner[] matrixSize = new JSpinner[2];

        JButton resizeButton = null;


        SpinnerNumberModel spin = new SpinnerNumberModel(100,1, 1000000, 1);
        matrixSize[0] = new JSpinner(spin);
        Box rows = createSpinner("Rows: ", matrixSize[0]);

        matrixSize[1] = new JSpinner(spin);
        Box columns = createSpinner("Columns: ", matrixSize[1]);

        resizeButton = new JButton("Resize?");
        resizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer[] sizeArray = new Integer[2];
                sizeArray[0] = rows.;
                System.out.println(Arrays.toString(sizeArray));
            }
        });

        return createPane(toggleButtons, resizeButton);
    }

    /**
     * Used by createSimpleDialogBox and createFeatureDialogBox
     * to create a pane containing a description, a single column
     * of radio buttons, and the Show it! button.
     */

    public static class SpinBox extends Box implements JSpinner {

        public Box panel;
        public JSpinner spinner;

        public SpinBox(String text, JSpinner spinner) {
            super(BoxLayout.X_AXIS);
            this.panel = Box.createHorizontalBox();
            this.spinner = spinner;
            panel.add(Box.createHorizontalGlue());
            panel.add(new JLabel(text));
            panel.add(Box.createHorizontalGlue());
            panel.add(spinner);
        }

    }
    private JPanel createPane( JToggleButton[] toggleButtons, JButton reset) {

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS ));
        pane.setBorder(new EmptyBorder(new Insets(50, 75, 50, 75)));
        pane.add(toggleButtons[0]);
        pane.add(Box.createRigidArea(new Dimension(0, 2)));
        for (int i = 1; i < toggleButtons.length; i++) {
            pane.add(toggleButtons[i]);
            pane.add(Box.createRigidArea(new Dimension(0, 2)));
        }
        pane.add(Box.createRigidArea(new Dimension(0, 10)));
        pane.add(reset);
        return pane;
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("DialogDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        DialogDemo newContentPane = new DialogDemo(frame);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
