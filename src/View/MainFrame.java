package View;

import Controller.MainController;
import Exceptions.InvalidArgumentException;
import com.sun.deploy.panel.JSmartTextArea;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* JFrame that represents the main display of the redeclaration tool. From here, one can configure the airport, runways and
 * obstacles, view the redeclared distances and calculation breakdown and see the visualations of the redeclared distances.*/
public class MainFrame extends JFrame {
    private MainController controller;

    private static final String FRAME_TITLE = "Runway Redeclaration Tool";
    private static final Dimension FRAME_PREFERRED_SIZE = new Dimension(1024, 768);
    private static final Dimension FRAME_MINIMUM_SIZE = new Dimension(800, 600);
    private static final Dimension FRAME_MAXIMUM_SIZE = new Dimension(2160, 1440);

    private static final Color BACKGROUND_COLOUR = Color.white;

    private static final Font TITLE_FONT = (new JLabel()).getFont().deriveFont(Font.BOLD, 20.0f);

    private static final Dimension VERTICAL_BTN_SEPERATION = new Dimension(0, 5);
    private static final Dimension HORIZONTAL_BTN_SEPERATION = new Dimension(5, 0);

    private SideView sideView;
    private TopView topView;
    private JTabbedPane viewSelect;
    private JComboBox thresholdSelect;

    private JLabel originalTORA, originalTODA, originalLDA, originalASDA;
    private JLabel recalculatedTORA, recalculatedTODA, recalculatedLDA, recalculatedASDA;
    private JSmartTextArea breakdown;
    private JLabel obstacleName, obstacleHeight, obstacleWidth, obstacleLength, obstacleCentreLine, obstacleLeftTHS, obstacleRightTHS;
    private JButton addObstacle, editObstacle, removeObstacle, exportObstacle;
    private JTextField slopeRatioBox, blastProtectBox;
    private JComboBox airportComboBox;


    private JRadioButton takeoff;
    private JRadioButton landing;
    ButtonGroup situationBG;

    private JCheckBox rotation;

    /* Allows us access to the various methods of the controller.*/
    public MainFrame(MainController c) {
        super(FRAME_TITLE);
        controller = c;
    }

    /* Initialises various properties of the JFrame and adds all the sub-panels in the appropriate layout*/
    public void init(Integer blastProtection, Integer slopeRatio) {
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setContentPane(contentPane);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMinimumSize(FRAME_MINIMUM_SIZE);
        setMaximumSize(FRAME_MAXIMUM_SIZE);
        setPreferredSize(FRAME_PREFERRED_SIZE);
        setSize(FRAME_PREFERRED_SIZE);

        setLayout(new GridLayout(2, 2));
        setBackground(BACKGROUND_COLOUR);

        JPanel topLeftQuarter = new JPanel();
        topLeftQuarter.setLayout(new BoxLayout(topLeftQuarter, BoxLayout.LINE_AXIS));
        topLeftQuarter.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension spacer = new Dimension(40, 0);

        topLeftQuarter.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        topLeftQuarter.add(createSpecificationButtons());
        topLeftQuarter.add(Box.createRigidArea(spacer));
        topLeftQuarter.add(createObstaclePanel());
        topLeftQuarter.add(Box.createHorizontalGlue());

        add(topLeftQuarter); //Order specific to Grid layout
        add(createValuesStack());
        add(createRunwayView());
        add(createCalculationBreakdown());

        blastProtectBox.setText(blastProtection.toString());
        slopeRatioBox.setText(slopeRatio.toString());

        setVisible(true);
    }

    /* Creates a JPanel that is displays the various parameters of the obstacle on the currently selected runway,
     * seperated into 2 columns (titles and values).*/
    private JPanel createObstaclePanel() {
        JPanel obstaclePanel = new JPanel();
        obstaclePanel.setLayout(new BoxLayout(obstaclePanel, BoxLayout.PAGE_AXIS));

        JLabel title = new JLabel("Obstacle specification");
        title.setFont(TITLE_FONT);

        Dimension TEXT_BOX_SIZE = new Dimension(60, 25);

        JPanel obstacleSpecificationGrid = new JPanel();
        obstacleSpecificationGrid.setLayout(new BoxLayout(obstacleSpecificationGrid, BoxLayout.LINE_AXIS));

        JPanel obstacleColumn1 = new JPanel();
        JPanel obstacleColumn2 = new JPanel();
        obstacleColumn1.setLayout(new BoxLayout(obstacleColumn1, BoxLayout.PAGE_AXIS));
        obstacleColumn2.setLayout(new BoxLayout(obstacleColumn2, BoxLayout.PAGE_AXIS));

        obstacleColumn1.add(new JLabel("Name: "));
        obstacleColumn1.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstacleName = new JLabel();
        obstacleColumn2.add(obstacleName);
        obstacleColumn2.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        obstacleColumn1.add(new JLabel("Height (m): "));
        obstacleColumn1.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstacleHeight = new JLabel();
        obstacleColumn2.add(obstacleHeight);
        obstacleColumn2.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        obstacleColumn1.add(new JLabel("Width (m): "));
        obstacleColumn1.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstacleWidth = new JLabel();
        obstacleColumn2.add(obstacleWidth);
        obstacleColumn2.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        obstacleColumn1.add(new JLabel("Length (m): "));
        obstacleColumn1.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstacleLength = new JLabel();
        obstacleColumn2.add(obstacleLength);
        obstacleColumn2.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        obstacleColumn1.add(new JLabel("Distance from Centreline (m): "));
        obstacleColumn1.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstacleCentreLine = new JLabel();
        obstacleColumn2.add(obstacleCentreLine);
        obstacleColumn2.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        obstacleColumn1.add(new JLabel("Distance from L Threshold (m): "));
        obstacleColumn1.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstacleLeftTHS = new JLabel();
        obstacleColumn2.add(obstacleLeftTHS);
        obstacleColumn2.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        obstacleColumn1.add(new JLabel("Distance from R Threshold (m): "));
        obstacleColumn1.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstacleRightTHS = new JLabel();
        obstacleColumn2.add(obstacleRightTHS);
        obstacleColumn2.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        obstacleSpecificationGrid.add(obstacleColumn1);
        obstacleSpecificationGrid.add(obstacleColumn2);
        obstacleSpecificationGrid.add(Box.createHorizontalGlue());

        JPanel blastProtection = new JPanel();
        blastProtection.setLayout(new BoxLayout(blastProtection, BoxLayout.LINE_AXIS));
        JLabel blastProtectLabel = new JLabel("Blast protection:");
        blastProtectBox = new JTextField("");
        blastProtectBox.setMaximumSize(TEXT_BOX_SIZE);
        blastProtectBox.setPreferredSize(TEXT_BOX_SIZE);
        blastProtectBox.setMinimumSize(TEXT_BOX_SIZE);
        blastProtection.add(blastProtectLabel);
        blastProtection.add(blastProtectBox);
        blastProtection.add(Box.createHorizontalGlue());

        JPanel slopeRatio = new JPanel();
        slopeRatio.setLayout(new BoxLayout(slopeRatio, BoxLayout.LINE_AXIS));
        JLabel slopeRatioLabel = new JLabel("Slope ratio (1:n):");
        slopeRatioBox = new JTextField();
        slopeRatioBox.setMaximumSize(TEXT_BOX_SIZE);
        slopeRatioBox.setPreferredSize(TEXT_BOX_SIZE);
        slopeRatio.setMinimumSize(TEXT_BOX_SIZE);
        slopeRatio.add(slopeRatioLabel);
        slopeRatio.add(slopeRatioBox);

        JButton applyChanges = new JButton("Apply");
        applyChanges.addActionListener(e -> {
            try {
                Integer sr = Integer.parseInt(slopeRatioBox.getText());
                controller.setSlopeRatio(sr);
            } catch (NumberFormatException ex) {
                createErrorDialog("Slope ratio must be a number");
            } catch (InvalidArgumentException ex) {
                createErrorDialog(ex.getMessage());
            }

            try {
                Integer bp = Integer.parseInt(blastProtectBox.getText());
                controller.setBlastProtection(bp);
            } catch (NumberFormatException ex) {
                createErrorDialog("Blast protection must be a number");
            } catch (InvalidArgumentException ex) {
                createErrorDialog(ex.getMessage());
            }

            JOptionPane.showMessageDialog(this,"Changes applied successfully!");

            updateViews();

        });
        slopeRatio.add(applyChanges);
        slopeRatio.add(Box.createHorizontalGlue());

        obstaclePanel.add(title);
        obstaclePanel.add(obstacleSpecificationGrid);
        obstaclePanel.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        obstaclePanel.add(Box.createVerticalGlue());
        obstaclePanel.add(blastProtection);
        obstaclePanel.add(slopeRatio);
        obstaclePanel.add(Box.createVerticalGlue());
        return obstaclePanel;
    }

    /* Creates a panel that contains both the original and recalculated values of the various runway parameters e.g
     * TODA, ASDA, TORA, LDA*/
    private JPanel createValuesStack() {
        JPanel valuesStack = new JPanel();
        valuesStack.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        valuesStack.setLayout(new BoxLayout(valuesStack, BoxLayout.PAGE_AXIS));
        valuesStack.setAlignmentX(Box.LEFT_ALIGNMENT);

        valuesStack.add(createOriginalValuesPanel());
        valuesStack.add(createRecalculatedValuesPanel());
        valuesStack.add(Box.createVerticalGlue());

        return valuesStack;
    }

    /* Creates the various components (in a panel) for displaying the original values of the distances for the runway,
     * without considering the effect of the current obstacle*/
    private JPanel createOriginalValuesPanel() {
        JPanel origValsPanel = new JPanel();
        origValsPanel.setLayout(new BoxLayout(origValsPanel, BoxLayout.PAGE_AXIS));
        origValsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Original runway distances");
        title.setFont(TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel origVals = new JPanel();
        origVals.setLayout(new GridLayout(2, 4));
        origVals.add(new JLabel("TORA (m):"));
        originalTORA = new JLabel();
        origVals.add(originalTORA);

        origVals.add(new JLabel("TODA (m):"));
        originalTODA = new JLabel();
        origVals.add(originalTODA);

        origVals.add(new JLabel("ASDA (m):"));
        originalASDA = new JLabel();
        origVals.add(originalASDA);

        origVals.add(new JLabel("LDA (m):"));
        originalLDA = new JLabel();
        origVals.add(originalLDA);

        origValsPanel.add(title);
        origValsPanel.add(origVals);

        return origValsPanel;
    }

    /* Creates the various components (in a panel) for displaying the recalculated values of the selected runway, with
     * the current obstacle*/
    private JPanel createRecalculatedValuesPanel() {
        JPanel recalcValsPanel = new JPanel();
        recalcValsPanel.setLayout(new BoxLayout(recalcValsPanel, BoxLayout.PAGE_AXIS));
        recalcValsPanel.setAlignmentX(Box.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Recalculated runway distances");
        title.setFont(TITLE_FONT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JPanel recalcVals = new JPanel();
        recalcVals.setLayout(new GridLayout(2, 4));

        recalcVals.setLayout(new GridLayout(2, 4));
        recalcVals.add(new JLabel("TORA (m):"));
        recalculatedTORA = new JLabel();
        recalcVals.add(recalculatedTORA);

        recalcVals.add(new JLabel("TODA (m):"));
        recalculatedTODA = new JLabel();
        recalcVals.add(recalculatedTODA);

        recalcVals.add(new JLabel("ASDA (m):"));
        recalculatedASDA = new JLabel();
        recalcVals.add(recalculatedASDA);

        recalcVals.add(new JLabel("LDA (m):"));
        recalculatedLDA = new JLabel();
        recalcVals.add(recalculatedLDA);

        recalcValsPanel.add(title);
        recalcValsPanel.add(recalcVals);

        return recalcValsPanel;
    }

    /* Creates the various components (in a panel) for displaying the breakdown of the calculations*/
    private JPanel createCalculationBreakdown() {
        JPanel calcBreakdownPanel = new JPanel();
        calcBreakdownPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        calcBreakdownPanel.setLayout(new BoxLayout(calcBreakdownPanel, BoxLayout.PAGE_AXIS));
        calcBreakdownPanel.setAlignmentX(Box.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Calculation breakdown");
        title.setFont(TITLE_FONT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        breakdown = new JSmartTextArea();

        calcBreakdownPanel.add(title);
        calcBreakdownPanel.add(breakdown);

        return calcBreakdownPanel;
    }

    /* Creates a set of components for the changing the view displayed in the tabbed pane, including whether you are
     * landing or taking off, and whether to rotate the top view to runway heading*/
    private JPanel createRadioButtons(){
        rotation = new JCheckBox("Rotate to runway heading");
        takeoff = new JRadioButton("Take off");
        landing = new JRadioButton("Landing");

        takeoff.setSelected(true);

        situationBG = new ButtonGroup();
        situationBG.add(takeoff);
        situationBG.add(landing);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.LINE_AXIS));
        radioPanel.add(rotation);
        radioPanel.add(takeoff);
        radioPanel.add(landing);
        radioPanel.add(Box.createHorizontalGlue());

        rotation.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                topView.rotateImage(rotation.isSelected());
            }
        });

        takeoff.addActionListener(e -> {
            sideView.setLabel("TORA,TODA,ASDA");
            topView.setTakeoff(true);

            if (controller.getRunway().getObstacle() != null) {
                boolean obstacleIsLeft = this.controller.getRunway().getObstacle().getDistanceLeftTHS() < this.controller.getRunway().getObstacle().getDistanceRightTHS();

                if ((Integer.parseInt(String.valueOf(sideView.getCurrentThreshold().charAt(0))) == 1 && Integer.parseInt(String.valueOf(sideView.getCurrentThreshold().charAt(1))) == 9)
                        || (Integer.parseInt(String.valueOf(sideView.getCurrentThreshold().charAt(0))) > 1)) {
                    sideView.setLOorTOT();

                    if (obstacleIsLeft) {
                        sideView.setLOorTOT();
                        topView.setLOorTOT();
                    } else {
                        sideView.setTOA();
                        topView.setTOA();
                    }

                    sideView.setDirection(0);
                    topView.setDirection(0);
                    repaint();
                } else {
                    sideView.setTOA();

                    if (obstacleIsLeft) {
                        sideView.setTOA();
                        topView.setTOA();
                    } else {
                        sideView.setLOorTOT();
                        topView.setLOorTOT();
                    }

                    sideView.setDirection(1);
                    topView.setDirection(1);
                    repaint();
                }
            }
        });

        landing.addActionListener(e -> {
            sideView.setLabel("LDA");
            topView.setTakeoff(false);
            if (controller.getRunway().getObstacle() != null) {
                boolean obstacleIsLeft = this.controller.getRunway().getObstacle().getDistanceLeftTHS() < this.controller.getRunway().getObstacle().getDistanceRightTHS();

                if ((Integer.parseInt(String.valueOf(sideView.getCurrentThreshold().charAt(0))) == 1 && Integer.parseInt(String.valueOf(sideView.getCurrentThreshold().charAt(1))) == 9)
                        || (Integer.parseInt(String.valueOf(sideView.getCurrentThreshold().charAt(0))) > 1)) {
                    sideView.setLT();

                    if (obstacleIsLeft) {
                        sideView.setLT();
                        topView.setLT();
                    } else {
                        sideView.setLOorTOT();
                        topView.setLOorTOT();
                    }

                    sideView.setDirection(0);
                    topView.setDirection(0);
                    repaint();
                } else {
                    sideView.setLOorTOT();
                    if (obstacleIsLeft) {
                        sideView.setLOorTOT();
                        topView.setLOorTOT();
                    } else {
                        sideView.setLT();
                        topView.setLT();
                    }

                    sideView.setDirection(1);
                    topView.setDirection(1);
                    repaint();
                }
            }
        });
        return radioPanel;
    }

    /* Creates and returns a JPanel containing the components to display the views in a tabbed format. Also includes
    * controls for selecting airport, threshold, colourblind mode and exporting the current view.*/
    private JPanel createRunwayView() {
        JPanel runwayView = new JPanel();
        runwayView.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        runwayView.setLayout(new BoxLayout(runwayView, BoxLayout.PAGE_AXIS));
        runwayView.setAlignmentX(Box.LEFT_ALIGNMENT);

        final Dimension VIEW_SELECT_SIZE = new Dimension(400, 250);

        viewSelect = new JTabbedPane();

        JPanel exportBtnPanel = new JPanel();
        exportBtnPanel.setLayout(new BoxLayout(exportBtnPanel, BoxLayout.LINE_AXIS));
        JButton exportButton = new JButton("Export View");
        exportButton.addActionListener(new ExportViewListener(this));

        // Turns the Green background in the Top-down view to Yellow
        JCheckBox colourblindMode = new JCheckBox("Colourblind Mode");
        colourblindMode.addActionListener(e -> {
            topView.setColourblindMode();
            topView.invalidate();
            this.repaint();
        });

        exportBtnPanel.add(colourblindMode);
        exportBtnPanel.add(Box.createHorizontalGlue());
        exportBtnPanel.add(exportButton);

        sideView = new SideView(this,this.controller);
        topView = new TopView(this,this.controller);

        viewSelect.addTab("Top-Down View", topView); //JPanels are placeholders for actual views
        viewSelect.addTab("Side View", sideView);
        viewSelect.setSelectedIndex(1);

        JPanel airportTHSSelect = new JPanel();
        airportTHSSelect.setLayout(new BoxLayout(airportTHSSelect, BoxLayout.LINE_AXIS));
        createAirportSelect();
        createRunwaySelect();
        airportTHSSelect.add(Box.createRigidArea(HORIZONTAL_BTN_SEPERATION));
        airportTHSSelect.add(airportComboBox);
        airportTHSSelect.add(Box.createRigidArea(HORIZONTAL_BTN_SEPERATION));
        airportTHSSelect.add(thresholdSelect);
        airportTHSSelect.add(Box.createHorizontalGlue());

        runwayView.add(airportTHSSelect);
        runwayView.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));
        runwayView.add(createRadioButtons());
        runwayView.add(viewSelect);
        runwayView.add(exportBtnPanel);

        return runwayView;
    }

    /* Initialises the combobox for selecting an airport*/
    private void createAirportSelect() {
        final Dimension RUNWAY_SELECT_SIZE = new Dimension(135, 25);

        airportComboBox = new JComboBox();
        airportComboBox.setEditable(false);
        airportComboBox.setMinimumSize(RUNWAY_SELECT_SIZE);
        airportComboBox.setPreferredSize(RUNWAY_SELECT_SIZE);
        airportComboBox.setMaximumSize(RUNWAY_SELECT_SIZE);

        for (String airport : this.controller.getAirportNames()) {
            airportComboBox.addItem(airport);
        }

        airportComboBox.addActionListener(e -> {
            if (airportComboBox.getSelectedItem() != null) {
                this.controller.handleChangeAirport((String) airportComboBox.getSelectedItem());
                controller.setThresholds();
            }
        });
    }

    /* Initialises a combobox with the selection for the current runway */
    private void createRunwaySelect() {
        final Dimension RUNWAY_SELECT_SIZE = new Dimension(135, 25);

        this.thresholdSelect = new JComboBox();
        thresholdSelect.setEditable(false);
        thresholdSelect.setMinimumSize(RUNWAY_SELECT_SIZE);
        thresholdSelect.setPreferredSize(RUNWAY_SELECT_SIZE);
        thresholdSelect.setMaximumSize(RUNWAY_SELECT_SIZE);

        for (String d : this.controller.getDefaultRunways()) {
            thresholdSelect.addItem(d);
        }

        sideView.setCurrentThreshold(this.controller.getDefaultRunways().get(0));
        topView.setCurrentThreshold(this.controller.getDefaultRunways().get(0));
        thresholdSelect.addActionListener(e -> {
            if (thresholdSelect.getSelectedItem() != null) {
                this.controller.handleRunwayChange(thresholdSelect.getSelectedItem().toString());
                sideView.setCurrentThreshold(thresholdSelect.getSelectedItem().toString());
                topView.setCurrentThreshold(thresholdSelect.getSelectedItem().toString());
                controller.recalculateAndUpdateDistances();

                if (controller.hasObstacle()) {
                    addObstacle.setEnabled(false);
                    editObstacle.setEnabled(true);
                    removeObstacle.setEnabled(true);
                    exportObstacle.setEnabled(true);
                } else {
                    addObstacle.setEnabled(true);
                    editObstacle.setEnabled(false);
                    removeObstacle.setEnabled(false);
                    exportObstacle.setEnabled(false);
                }

                if(takeoff.isSelected()){
                    takeoff.doClick();
                }else{
                    landing.doClick();
                }

                sideView.repaint();
                topView.repaint();
            }
        });
    }

    /* Creates a set of buttons for changing the specification of the airport and runway including import and export
     * buttons, adding, editing and removal of the obstacle and editing the airport specification*/
    private JPanel createSpecificationButtons() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.PAGE_AXIS));

        final Dimension BUTTON_SIZE = new Dimension(135, 25);
        final Dimension CLUSTER_SPACING = new Dimension(0, 15);

        JButton exportSituation = createSpecificationButton("Export Situation", true, BUTTON_SIZE);
        exportSituation.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
            fc.setDialogTitle("Export situation");
            int returnVal = fc.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.getName().toLowerCase().endsWith(".txt")) {
                    controller.writeSituationToTextFile(file.getPath());
                } else {
                    controller.writeSituationToTextFile(file.getPath() + ".txt");
                }
                JOptionPane.showMessageDialog(this,"Situation exported successfully!");

            }
        });

        addObstacle = createSpecificationButton("Add Obstacle", true, BUTTON_SIZE);
        addObstacle.addActionListener(e -> {
            AddObstacleDialog addObstacleDialog = new AddObstacleDialog(this, controller);
        });
        editObstacle = createSpecificationButton("Edit Obstacle", false, BUTTON_SIZE);
        editObstacle.addActionListener(e -> {
            EditObstacleDialog editObstacleDialog = new EditObstacleDialog(this, controller);
        });
        removeObstacle = createSpecificationButton("Remove Obstacle", false, BUTTON_SIZE);
        removeObstacle.addActionListener(e -> {
            controller.removeObstacle();
            clearObstacleSpec();
            setAddObstacleEnabled(true);
            setEditObstacleEnabled(false);
            setRemoveObstacleEnabled(false);
            setExportObstacleEnabled(false);
            JOptionPane.showMessageDialog(this,"Obstacle removed!");

        });

        exportObstacle = createSpecificationButton("Export Obstacle", false, BUTTON_SIZE);
        exportObstacle.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
            fc.setDialogTitle("Export Obstacle");
            int returnVal = fc.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    controller.exportXMLObstacle(file.getPath());
                } else {
                    controller.exportXMLObstacle(file.getPath() + ".xml");
                }
                JOptionPane.showMessageDialog(this,"Obstacle exported successfully!");
            }

        });


        JButton importAirport = createSpecificationButton("Import Airport", true, BUTTON_SIZE);
        importAirport.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
            fc.setDialogTitle("Import Airport");
            int returnVal = fc.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    controller.importAirportFromXML(file.getPath());
                } else {
                    controller.importAirportFromXML(file.getPath() + ".xml");
                }
            }

        });

        JButton editAirport = createSpecificationButton("Edit Airport", true, BUTTON_SIZE);
        editAirport.addActionListener(e -> {
            EditAirportDialog editAirportDialog = new EditAirportDialog(this, controller);
            editAirportDialog.init();
        });
        JButton exportAirport = createSpecificationButton("Export Airport", true, BUTTON_SIZE);
        exportAirport.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
            fc.setDialogTitle("Export Airport");
            int returnVal = fc.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    controller.exportXMLAirport(file.getPath());
                } else {
                    controller.exportXMLAirport(file.getPath() + ".xml");
                }
                JOptionPane.showMessageDialog(this,"Airport exported successfully!");

            }

        });

        addButtonWSpacing(buttonsPanel, exportSituation, VERTICAL_BTN_SEPERATION);

        buttonsPanel.add(Box.createRigidArea(CLUSTER_SPACING));

        addButtonWSpacing(buttonsPanel, addObstacle, VERTICAL_BTN_SEPERATION);
        addButtonWSpacing(buttonsPanel, editObstacle, VERTICAL_BTN_SEPERATION);
        addButtonWSpacing(buttonsPanel, removeObstacle, VERTICAL_BTN_SEPERATION);
        addButtonWSpacing(buttonsPanel, exportObstacle, VERTICAL_BTN_SEPERATION);

        buttonsPanel.add(Box.createRigidArea(CLUSTER_SPACING));

        addButtonWSpacing(buttonsPanel, importAirport, VERTICAL_BTN_SEPERATION);
        addButtonWSpacing(buttonsPanel, editAirport, VERTICAL_BTN_SEPERATION);
        addButtonWSpacing(buttonsPanel, exportAirport, VERTICAL_BTN_SEPERATION);

        buttonsPanel.add(Box.createRigidArea(CLUSTER_SPACING));

        buttonsPanel.add(Box.createRigidArea(VERTICAL_BTN_SEPERATION));

        buttonsPanel.add(Box.createVerticalGlue());
        return buttonsPanel;
    }

    /* Adds the button to the specified panel, followed by a specified amount of space following the button*/
    private void addButtonWSpacing(JPanel panel, JButton button, Dimension spacing) {
        panel.add(button);
        panel.add(Box.createRigidArea(spacing));
    }

    /* Creates and returns a button with the given label and size, and set to be enabled/disabeld, as per the boolean
     * flag*/
    private JButton createSpecificationButton(String label, Boolean enabled, Dimension size) {
        JButton b = new JButton(label);
        b.setEnabled(enabled);

        b.setPreferredSize(size);
        b.setSize(size);
        b.setMaximumSize(size);

        return b;
    }

    /* Adds the list of airports to the airport selection combobox and selected one of the airports from the new list*/
    public void setAirports(List<String> airports) {
        airportComboBox.removeAllItems();
        for (String name: airports) {
            airportComboBox.addItem(name);
        }

        controller.handleChangeAirport((String)airportComboBox.getSelectedItem());
        controller.setThresholds();
    }

    @Override
    /* Repaints the frame and both views as necessary*/
    public void repaint() {
        super.repaint();
        if (topView != null && sideView != null) {
            topView.repaint();
            sideView.repaint();
        }
    }

    /* Sets the obstacle's name in the obstacle specification section of the GUI*/
    public void setObstacleName(String name) {
        obstacleName.setText(name);
    }

    /* Sets the obstacle's height in the obstacle specification section of the GUI*/
    public void setObstacleHeight(Integer height) {
        obstacleHeight.setText(height.toString());
    }

    /* Sets the obstacle's width in the obstacle specification section of the GUI*/
    public void setObstacleWidth(Integer width) {
        obstacleWidth.setText(width.toString());
    }

    /* Sets the obstacle's length in the obstacle specification section of the GUI*/
    public void setObstacleLength(Integer length) {
        obstacleLength.setText(length.toString());
    }

    /* Sets the obstacle's distance from the runways centreline in the obstacle specification section of the GUI*/
    public void setObstacleCentreline(Integer centreline) {
        obstacleCentreLine.setText(centreline.toString());
    }

    /* Sets the obstacle's distance from the views leftmost threshold in the obstacle specification section of the GUI*/
    public void setObstacleLeftTHS(Integer height) {
        obstacleLeftTHS.setText(height.toString());
    }

    /* Sets the obstacle's distance from the views rightmost threshold in the obstacle specification section of the GUI*/
    public void setObstacleRightTHS(Integer height) {
        obstacleRightTHS.setText(height.toString());
    }

    /* Gets the values from the various obstacle specification labels from the obstacle specification section of the GUI*/
    public String getObstacleNameText() {
        return obstacleName.getText();
    }

    public String getObstacleHeightText() {
        return obstacleHeight.getText();
    }

    public String getObstacleWidthText() {
        return obstacleWidth.getText();
    }

    public String getObstacleLengthText() {
        return obstacleLength.getText();
    }

    public String getObstacleCentrelineText() {
        return obstacleCentreLine.getText();
    }

    public String getObstacleLeftTHSText() {
        return obstacleLeftTHS.getText();
    }

    public String getObstacleRightTHSText() {
        return obstacleRightTHS.getText();
    }

    /* Clears the values of all the obstacle specification labels in the obstacle specification part of the GUI*/
    public void clearObstacleSpec() {
        obstacleHeight.setText("");
        obstacleWidth.setText("");
        obstacleLength.setText("");
        obstacleName.setText("");
        obstacleRightTHS.setText("");
        obstacleLeftTHS.setText("");
        obstacleCentreLine.setText("");
    }

    public void setThresholds(ArrayList<String> thresholds) {
        thresholdSelect.removeAllItems();
        for (String s : thresholds) {
            thresholdSelect.addItem(s);
        }
    }

    // Gets the blast protection from the blast protection textbox - throwing an error dialog if the parsing fails
    public Integer getBlastProtection() {
        try {
            return Integer.parseInt(blastProtectBox.getText());
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Blast protection must be a number");
        }
    }

    // Gets the slope ration from the textbox - throwing an error dialog if the parsing fails
    public Integer getSlopeRatio() {
        try {
            return Integer.parseInt(slopeRatioBox.getText());
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Slope ratio must be a number");
        }
    }

    /* Sets the blast protection in the GUI*/
    public void setBlastProtection(Integer blastProtection) {
        blastProtectBox.setText(blastProtection.toString());
    }

    /* Sets the slope ration in the GUI*/
    public void setSlopeRatio(Integer slopeRatio) {
        blastProtectBox.setText(slopeRatio.toString());
    }

    /* Sets the various runway parameters in the GUI for both original and recalulated values*/
    public void setOriginalTORA(Integer TORA) { originalTORA.setText(TORA.toString()); }

    public void setOriginalTODA(Integer TODA) {
        originalTODA.setText(TODA.toString());
    }

    public void setOriginalASDA(Integer ASDA) {
        originalASDA.setText(ASDA.toString());
    }

    public void setOriginalLDA(Integer LDA) {
        originalLDA.setText(LDA.toString());
    }

    public void setRecalculatedTORA(Integer TORA) {
        recalculatedTORA.setText(TORA.toString());
    }

    public void setRecalculatedTODA(Integer TODA) {
        recalculatedTODA.setText(TODA.toString());
    }

    public void setRecalculatedASDA(Integer ASDA) {
        recalculatedASDA.setText(ASDA.toString());
    }

    public void setRecalculatedLDA(Integer LDA) {
        recalculatedLDA.setText(LDA.toString());
    }

    /* Adds the calculation breakdown to the GUI */
    public void setBreakdown(String calcBreak) {
        breakdown.setText(calcBreak);
    }

    /* Creates an error dialog with the given error message*/
    private void createErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage,
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /*Setters for whether the "Add Obstacle", "Edit Obstacle* etc. buttons are enabled or disabled*/
    public void setAddObstacleEnabled(Boolean b) {
        addObstacle.setEnabled(b);
    }

    public void setEditObstacleEnabled(Boolean b) {
        editObstacle.setEnabled(b);
    }

    public void setRemoveObstacleEnabled(Boolean b) {
        removeObstacle.setEnabled(b);
    }

    public void setExportObstacleEnabled(Boolean b) {
        exportObstacle.setEnabled(b);
    }

    /* Updates the views and makes sure that the scenario is set for the views as necessary e.g when an update is made
     * to the obstacle specification or a new obstacle is added to the runway*/
    public void updateViews() {
        if (takeoff != null && landing != null) {
            if (takeoff.isSelected()) {
                takeoff.doClick();
            } else {
                landing.doClick();
            }
        }
        repaint();
    }

    /* Listener for the export view button. Creates a dialog for the user to select the location to save the view and
     * the location to store the image.*/
    class ExportViewListener implements ActionListener {
        MainFrame parent;

        /* Initialises the parent for the dialog that is created*/
        public ExportViewListener(MainFrame parent) {
            this.parent = parent;
        }

        @Override
        /* Displays a file chooser for exporting and saving the currently selected view to file. Saves the image in the
         * specified location, with specified name, with the selected format*/
        public void actionPerformed(ActionEvent e) {
            BufferedImage i;

            final JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            //Our choice of common image file formats
            fc.addChoosableFileFilter(new FileNameExtensionFilter("JPEG", "jpg", "jpeg"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG", "png"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("GIF", "gif"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("BMP", "bmp"));

            if (viewSelect.getSelectedComponent() == sideView) { //Gets the currently selected view
                i = sideView.getView();
                fc.setDialogTitle("Export runway side view");
            } else {
                i = topView.getView();
                fc.setDialogTitle("Export runway top view");
            }

            int returnVal = fc.showSaveDialog(parent);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String fileName = file.getName().toLowerCase();

                FileFilter ff = fc.getFileFilter();

                switch (ff.getDescription()) { //Appends the file extension if not included in the file name for the exported view
                    case "JPEG":
                        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))) {
                            file = new File(file.getPath() + ".jpeg");
                        }
                        controller.writeImageToFile(file, "jpg", i);
                        break;
                    case "PNG":
                        if (!fileName.endsWith(".png")) {
                            file = new File(file.getPath() + ".png");
                        }
                        controller.writeImageToFile(file, "png", i);
                        break;
                    case "GIF":
                        if (!fileName.endsWith(".gif")) {
                            file = new File(file.getPath() + ".gif");
                        }
                        controller.writeImageToFile(file, "gif", i);
                        break;
                    case "BMP":
                        if (!fileName.endsWith(".bmp")) {
                            file = new File(file.getPath() + ".bmp");
                        }
                        controller.writeImageToFile(file, "bmp", i);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}