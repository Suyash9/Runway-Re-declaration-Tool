package View;

import Controller.MainController;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class AddObstacleDialog extends JDialog {

    private MainController controller;
    private MainFrame parent;
    private String selectedObstacle;
    private static final Dimension DIALOG_PREFERRED_SIZE = new Dimension(600, 450);
    private static final Dimension ITEM_SEPERATION = new Dimension(0,30);


    public AddObstacleDialog(MainFrame f, MainController c) {
        super(f, "Add obstacle");
        parent = f;
        controller = c;
        init();
    }

    public void init() {
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setContentPane(contentPane);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        add(createNewObstacleButton());
        add(Box.createRigidArea(ITEM_SEPERATION));
        add(createPredefinedObstacleSelect());
        add(Box.createRigidArea(ITEM_SEPERATION));
        add(createImportObstacle());
        add(Box.createRigidArea(ITEM_SEPERATION));

        setResizable(false);
        pack();
        setVisible(true);
    }

    //TODO: align left
    private JButton createAddBtn() {
        JButton addPredefinedObs = new JButton("Add");
        addPredefinedObs.addActionListener(e -> {
            controller.handleSetPredefinedObstacle(this.selectedObstacle);
            this.parent.setAddObstacleEnabled(false);
            this.parent.setEditObstacleEnabled(true);
            this.parent.setRemoveObstacleEnabled(true);
            this.parent.setExportObstacleEnabled(true);
            parent.updateViews();
            this.dispose();
            JOptionPane.showMessageDialog(this,"Obstacle added successfully!");

        });

        return addPredefinedObs;
    }

    private JPanel createNewObstacleButton() {
        JPanel newObstacle = new JPanel();
        newObstacle.setLayout(new BoxLayout(newObstacle, BoxLayout.LINE_AXIS));

        JLabel newObsLabel = new JLabel("New Obstacle: ");

        JButton newObsButton = new JButton("Create obstacle");
        newObsButton.addActionListener(e -> {
            EditObstacleDialog editObstacleDialog = new EditObstacleDialog(parent,this, controller);
        });

        newObstacle.add(newObsLabel);
        newObstacle.add(Box.createHorizontalGlue());
        newObstacle.add(newObsButton);

        return newObstacle;
    }

    private JPanel createPredefinedObstacleSelect() {
        JPanel predefinedObs = new JPanel();
        predefinedObs.setLayout(new BoxLayout(predefinedObs, BoxLayout.LINE_AXIS));

        JLabel predefinedLabel = new JLabel("Predefined Obstacle: ");

        JComboBox predefinedSelect = new JComboBox();

        for(Object item: controller.getPredefinedObstacles()){
            predefinedSelect.addItem(item);
        }

        predefinedSelect.setEditable(false);
        //TODO: have a controller method to get the name
        this.selectedObstacle = this.controller.getPredefinedObstacles().get(0).getName();

        predefinedObs.add(predefinedLabel);
        predefinedObs.add(Box.createRigidArea(new Dimension(5,0)));
        predefinedObs.add(predefinedSelect);
        predefinedObs.add(createAddBtn());

        predefinedSelect.addActionListener(e -> selectedObstacle = String.valueOf(predefinedSelect.getSelectedItem()));

        return predefinedObs;
    }

    private JPanel createImportObstacle() {
        JPanel importObs = new JPanel();
        importObs.setLayout(new BoxLayout(importObs, BoxLayout.LINE_AXIS));

        JLabel importObsLabel = new JLabel("Import Obstacle: ");

        JButton importObsButton = new JButton("Load XML");
        importObsButton.setEnabled(true);
        importObsButton.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
            fc.setDialogTitle("Import Obstacle");
            int returnVal = fc.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    controller.importObstacle(file.getPath());
                } else {
                    controller.importObstacle(file.getPath() + ".xml");
                }
                JOptionPane.showMessageDialog(this,"Obstacle imported successfully!");

                parent.updateViews();
            }
        });

        importObs.add(importObsLabel);
        importObs.add(Box.createHorizontalGlue());
        importObs.add(importObsButton);

        return importObs;
    }
}
