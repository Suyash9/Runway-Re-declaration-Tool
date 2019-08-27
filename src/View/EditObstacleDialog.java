package View;

import Controller.MainController;
import Controller.XMLExporter;
import Model.Obstacle;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditObstacleDialog extends JDialog {

    private MainController controller;
    private MainFrame parent;
    private JDialog dialogParent;
    private String type;
    private List<String> parameters;
    private static final Dimension DIALOG_PREFERRED_SIZE = new Dimension(600, 450);
    private static final Dimension LABEL_SEPERATION = new Dimension(0,5);
    private static final Dimension TEXT_FIELD_SIZE = new Dimension(100, 25);

    private JTextField nameTextBox, lengthTextBox, widthTextBox, heightTextBox, centreLineTextBox, thresholdLTextBox, thresholdRTextBox;

    public EditObstacleDialog(MainFrame frame, JDialog d, MainController c){
        super(d, "Create obstacle");
        this.dialogParent = d;
        this.parent = frame;
        this.type = "add";
        controller = c;
        init();
    }

    public EditObstacleDialog(MainFrame f, MainController c) {
        super(f, "Edit obstacle specification");
        parent = f;
        this.type = "edit";
        controller = c;
        init();

        getObstacleValues();
    }

    public void getObstacleValues() {
        nameTextBox.setText(parent.getObstacleNameText());
        parameters.set(0,nameTextBox.getText());
        heightTextBox.setText(parent.getObstacleHeightText());
        parameters.set(1,heightTextBox.getText());
        widthTextBox.setText(parent.getObstacleWidthText());
        parameters.set(2,widthTextBox.getText());
        lengthTextBox.setText(parent.getObstacleLengthText());
        parameters.set(3,lengthTextBox.getText());

        centreLineTextBox.setText(parent.getObstacleCentrelineText());
        parameters.set(4,centreLineTextBox.getText());
        thresholdLTextBox.setText(parent.getObstacleLeftTHSText());
        parameters.set(5,thresholdLTextBox.getText());
        thresholdRTextBox.setText(parent.getObstacleRightTHSText());
        parameters.set(6,thresholdRTextBox.getText());
    }

    public void init() {
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setContentPane(contentPane);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(createLabelStack());

        JButton saveObs = new JButton("Save to XML");
        saveObs.setEnabled(true);

        saveObs.addActionListener(e -> {
                        final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
            fc.setDialogTitle("Save Obstacle");
            int returnVal = fc.showSaveDialog(this);

            for (int i = 0; i < parameters.size(); i++) {
                if(i != 0){
                    try{
                        Integer x = Integer.parseInt(parameters.get(i));
                        if (x < 0 && i > 0 && i < 5) throw new NumberFormatException();
                    }catch (NumberFormatException nfe){
                        JOptionPane.showMessageDialog(this,"Invalid parameters");
                        return;
                    }
                }
            }

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                controller.saveObstacle(fc.getSelectedFile(), parameters);
            } else{
                return;
            }
            JOptionPane.showMessageDialog(this,"Obstacle saved successfully!");

        });

        JButton applyObsChanges = new JButton("Apply changes");

        applyObsChanges.addActionListener(e -> {
            boolean exceptionThrown = false;

            for (int i = 0; i < parameters.size(); i++) {
                if(i != 0){
                    try{
                        Integer x = Integer.parseInt(parameters.get(i));
                        if (x < 0 && i > 0 && i < 5) throw new NumberFormatException();
                    }catch (NumberFormatException nfe){
                        JOptionPane.showMessageDialog(this,"Invalid parameters");
                        exceptionThrown = true;
                        return;
                    }
                }
            }

            switch (this.type){
                case "add":
                    if(!exceptionThrown){
                        this.controller.handleNewObstacle(this.parameters);
                        this.dispose();
                        this.dialogParent.dispose();
                        this.parent.setAddObstacleEnabled(false);
                        this.parent.setEditObstacleEnabled(true);
                        this.parent.setRemoveObstacleEnabled(true);
                        parent.updateViews();
                        JOptionPane.showMessageDialog(this, "Successfully added obstacle " + this.parameters.get(0) + "!");
                    }
                    break;
                case "edit":
                    if(!exceptionThrown){
                        this.controller.handleEditObstacle(this.parameters);
                        parent.updateViews();
                        this.dispose();
                    }
                    break;
            }
            JOptionPane.showMessageDialog(this,"Changes applied successfully!");

        });

        add(saveObs);
        add(applyObsChanges);

        setResizable(false);
        pack();
        setVisible(true);

        this.parameters = new ArrayList<>(7);

        for(int i = 0; i < 7; i++){
            this.parameters.add("");
        }
    }

    private JPanel createLabelStack() {
        JPanel labelStack = new JPanel();

        labelStack.setLayout(new BoxLayout(labelStack, BoxLayout.PAGE_AXIS));
        labelStack.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelStack.add(Box.createVerticalGlue());

        JPanel name = new JPanel();
        name.setLayout(new BoxLayout(name, BoxLayout.LINE_AXIS));
        name.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel nameLabel = new JLabel("Name: ");
        nameTextBox = createTextBox();
        nameTextBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { parameters.set(0,nameTextBox.getText());
            }
        });
        
        name.add(nameLabel);
        name.add(nameTextBox);

        JPanel height = new JPanel();
        height.setLayout(new BoxLayout(height, BoxLayout.LINE_AXIS));
        height.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel heightLabel = new JLabel("Height (m): ");
        heightTextBox = createTextBox();
        heightTextBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { parameters.set(1,heightTextBox.getText());
            }
        });

        height.add(heightLabel);
        height.add(heightTextBox);

        JPanel width = new JPanel();
        width.setLayout(new BoxLayout(width, BoxLayout.LINE_AXIS));
        width.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel widthLabel = new JLabel("Width (m): ");
        widthTextBox = createTextBox();
        widthTextBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { parameters.set(2, widthTextBox.getText());
            }
        });

        width.add(widthLabel);
        width.add(widthTextBox);


        JPanel length = new JPanel();
        length.setLayout(new BoxLayout(length, BoxLayout.LINE_AXIS));
        length.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel lengthLabel = new JLabel("Length (m): ");
        lengthTextBox = createTextBox();
        lengthTextBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { parameters.set(3,lengthTextBox.getText());
            }
        });

        length.add(lengthLabel);
        length.add(lengthTextBox);

        JPanel centreLine = new JPanel();
        centreLine.setLayout(new BoxLayout(centreLine, BoxLayout.LINE_AXIS));
        centreLine.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel centreLineLabel = new JLabel("Distance from centreline (m): ");
        centreLineTextBox = createTextBox();
        centreLineTextBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { parameters.set(4,centreLineTextBox.getText());
            }
        });

        centreLine.add(centreLineLabel);
        centreLine.add(centreLineTextBox);

        JPanel thresholdL = new JPanel();
        thresholdL.setLayout(new BoxLayout(thresholdL, BoxLayout.LINE_AXIS));
        thresholdL.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel thresholdLLabel = new JLabel("Distance from L Threshold (m): ");
        thresholdLTextBox = createTextBox();
        thresholdLTextBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { parameters.set(5,thresholdLTextBox.getText());
            }
        });

        thresholdL.add(thresholdLLabel);
        thresholdL.add(thresholdLTextBox);

        JPanel thresholdR = new JPanel();
        thresholdR.setLayout(new BoxLayout(thresholdR, BoxLayout.LINE_AXIS));
        thresholdR.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel thresholdRLabel = new JLabel("Distance from R Threshold (m): ");
        thresholdRTextBox = createTextBox();
        thresholdRTextBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { parameters.set(6,thresholdRTextBox.getText());}});

        thresholdR.add(thresholdRLabel);
        thresholdR.add(thresholdRTextBox);

        addWSeperation(labelStack, name);
        addWSeperation(labelStack, height);
        addWSeperation(labelStack, width);
        addWSeperation(labelStack, length);
        addWSeperation(labelStack, centreLine);
        addWSeperation(labelStack, thresholdL);
        addWSeperation(labelStack, thresholdR);
        labelStack.add(Box.createVerticalGlue());

        return labelStack;
    }
    
    private JTextField createTextBox() {
        JTextField text = new JTextField();
        text.setPreferredSize(TEXT_FIELD_SIZE);
        text.setMinimumSize(TEXT_FIELD_SIZE);
        text.setMaximumSize(TEXT_FIELD_SIZE);
        return text;
    }

    private void addWSeperation(JPanel panel, Component p) {
        panel.add(p);
        panel.add(Box.createRigidArea(LABEL_SEPERATION));
    }
}
