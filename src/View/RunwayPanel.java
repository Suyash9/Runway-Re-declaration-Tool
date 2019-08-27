package View;

import Controller.MainController;
import Exceptions.InvalidArgumentException;
import javax.swing.*;
import java.awt.*;

public class RunwayPanel extends JPanel {
    private JTextField RESATextBox;
    private JTextField runwayLengthTextBox;

    private MainController controller;
    private EditAirportDialog parent;

    private static final Dimension SIDE_SEPERATION = new Dimension(35, 0);
    private static final Dimension TEXT_FIELD_SIZE = new Dimension(80,18);

    private ThresholdPanel THS1;
    private ThresholdPanel THS2;

    private boolean edited;
    private boolean isNew;

    private char initTHSPos1;
    private char initTHSPos2;
    private int initTHSDir1;
    private int initTHSDir2;
    private int initRESA;
    private int initTHS1Stripend;
    private int initTHS2Stripend;
    private int initTHS1Clearway;
    private int initTHS2Clearway;
    private int initTHS1Stopway;
    private int initTHS2Stopway;
    private int initTHS1Disp;
    private int initTHS2Disp;
    private int initRunwayLength;

    public RunwayPanel(MainController controller, EditAirportDialog parent){
        this.controller = controller;
        this.parent = parent;
        init();
        setDefaultValues();
        edited = true;
        isNew = true;
    }

    public RunwayPanel(MainController controller, EditAirportDialog parent, char THSPos1, char THSPos2, int THSDir1, int THSDir2,
                       int RESA, int THS1Stripend, int THS2Stripend, int THS1Clearway, int THS2Clearway, int THS1Stopway,
                       int THS2Stopway, int THS1Disp, int THS2Disp, int RunwayLength) {
        initTHSPos1 = THSPos1;
        initTHSPos2 = THSPos2;
        initTHSDir1 = THSPos1;
        initTHSDir2 = THSPos2;
        initRESA = RESA;
        initTHS1Stripend = THS1Stripend;
        initTHS2Stripend = THS2Stripend;
        initTHS1Clearway = THS1Clearway;
        initTHS2Clearway = THS2Clearway;
        initTHS1Stopway = THS1Stopway;
        initTHS2Stopway = THS2Stopway;
        initTHS1Disp = THS1Disp;
        initTHS2Disp = THS2Disp;
        initRunwayLength = RunwayLength;
        
        this.controller = controller;
        this.parent = parent;
        init();

        THS1.setDirection(THSDir1);
        THS1.setPosition(THSPos1);
        THS1.setClearway(THS1Clearway);
        THS1.setStopway(THS1Stopway);
        THS1.setStripend(THS1Stripend);
        THS1.setDisplacedThreshold(THS1Disp);

        THS2.setDirection(THSDir2);
        THS2.setPosition(THSPos2);
        THS2.setClearway(THS2Clearway);
        THS2.setStopway(THS2Stopway);
        THS2.setStripend(THS2Stripend);
        THS2.setDisplacedThreshold(THS2Disp);

        RESATextBox.setText(Integer.toString(RESA));
        runwayLengthTextBox.setText(Integer.toString(RunwayLength));
        revalidate();
        edited = false;
        isNew = false;
    }

    private void init() {
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel runwaySpecs = new JPanel();
        runwaySpecs.setLayout(new BoxLayout(runwaySpecs, BoxLayout.LINE_AXIS));

        JLabel RESALabel = new JLabel("RESA (m): ");
        RESATextBox = new JTextField();
        setAllSizes(RESATextBox, TEXT_FIELD_SIZE);

        JLabel runwayLengthLabel = new JLabel("Runway Length (m): ");
        runwayLengthTextBox = new JTextField();
        setAllSizes(runwayLengthTextBox, TEXT_FIELD_SIZE);


        runwaySpecs.add(Box.createRigidArea(SIDE_SEPERATION));
        runwaySpecs.add(RESALabel);
        runwaySpecs.add(RESATextBox);
        runwaySpecs.add(Box.createHorizontalGlue());
        runwaySpecs.add(runwayLengthLabel);
        runwaySpecs.add(runwayLengthTextBox);
        runwaySpecs.add(Box.createRigidArea(SIDE_SEPERATION));

        JButton removeRunway = new JButton("Remove runway");
        removeRunway.addActionListener(e -> {
            parent.removeRunwayPanel(this);
        });

        THS1 = new ThresholdPanel();
        THS2 = new ThresholdPanel(THS1);
        THS1.setCorrespondingTHS(THS2);

        add(runwaySpecs);
        add(Box.createRigidArea(new Dimension(0,10)));
        add(THS1);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(THS2);
        add(removeRunway);
    }

    private void setDefaultValues() {
        RESATextBox.setText("240");

    }

    private void setAllSizes(JComponent c, Dimension size) {
        c.setMinimumSize(size);
        c.setMaximumSize(size);
        c.setPreferredSize(size);
        c.setSize(size);
    }


    public int getRunwayLength() throws NumberFormatException, InvalidArgumentException {
        String s = runwayLengthTextBox.getText();
        try {
            int runwayLength = Integer.parseInt(s);
            if (runwayLength < 0) {
                throw new InvalidArgumentException("Runway length must be at least zero metres");
            }
            return runwayLength;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Runway length must be a number");
        }
    }

    public int getRESA() throws NumberFormatException, InvalidArgumentException {
        String s = RESATextBox.getText();
        try {
            int RESA = Integer.parseInt(s);
            if (RESA < 0) {
                throw new InvalidArgumentException("RESA must be at least zero metres");
            }
            return RESA;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("RESA must be a number");
        }
    }


    public int getTHS2Clearway() throws NumberFormatException, InvalidArgumentException {
        String s = THS2.getClearwayText();
        try {
            int clearway = Integer.parseInt(s);
            if (clearway < 0) {
                throw new InvalidArgumentException("Clearway must be at least zero metres");
            }
            return clearway;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Clearway must be a number");
        }
    }

    public char getTHS2Position() {
        String s = THS2.getPositionValue();
        char pos = s.toCharArray()[0];
        return pos;
    }

    public int getTHS2Direction() {
        String s = THS2.getDirectionValue();
        int dir = Integer.parseInt(s);
        return dir;
    }

    public int getTHS2Stripend() throws NumberFormatException, InvalidArgumentException {
        String s = THS2.getStripendText();
        try {
            int stripend = Integer.parseInt(s);
            if (stripend < 0) {
                throw new InvalidArgumentException("Stripend must be at least zero metres");
            }
            return stripend;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Stripend must be a number");
        }
    }

    public int getTHS2DisplacedThreshold() throws NumberFormatException, InvalidArgumentException  {
        String s = THS2.getDisplacedThresholdText();
        try {
            int disp = Integer.parseInt(s);
            if (disp < 0) {
                throw new InvalidArgumentException("Threshold displacement must be at least zero metres");
            }
            return disp;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Threshold displacement must be a number");
        }
    }

    public int getTHS2Stopway() throws NumberFormatException, InvalidArgumentException {
        String s = THS2.getStopwayText();
        try {
            int stopway = Integer.parseInt(s);
            if (stopway < 0) {
                throw new InvalidArgumentException("Stopway must be at least zero metres");
            }
            return stopway;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Stopway must be a number");
        }
    }

    public int getTHS1Clearway() throws NumberFormatException, InvalidArgumentException {
        String s = THS1.getClearwayText();
        try {
            int clearway = Integer.parseInt(s);
            if (clearway < 0) {
                throw new InvalidArgumentException("Clearway must be at least zero metres");
            }
            return clearway;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Clearway must be a number");
        }
    }

    public char getTHS1Position() {
        String s = THS1.getPositionValue();
        char pos = s.toCharArray()[0];
        return pos;
    }

    public int getTHS1Direction() {
        String s = THS1.getDirectionValue();
        int dir = Integer.parseInt(s);
        return dir;
    }

    public int getTHS1Stripend() throws NumberFormatException, InvalidArgumentException {
        String s = THS1.getStripendText();
        try {
            int stripend = Integer.parseInt(s);
            if (stripend < 0) {
                throw new InvalidArgumentException("Stripend must be at least zero metres");
            }
            return stripend;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Stripend must be a number");
        }
    }

    public int getTHS1DisplacedThreshold() throws NumberFormatException, InvalidArgumentException  {
        String s = THS1.getDisplacedThresholdText();
        try {
            int disp = Integer.parseInt(s);
            if (disp < 0) {
                throw new InvalidArgumentException("Threshold displacement must be at least zero metres");
            }
            return disp;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Threshold displacement must be a number");
        }
    }

    public int getTHS1Stopway() throws NumberFormatException, InvalidArgumentException {
        String s = THS1.getStopwayText();
        try {
            int stopway = Integer.parseInt(s);
            if (stopway < 0) {
                throw new InvalidArgumentException("Stopway must be at least zero metres");
            }
            return stopway;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Stopway must be a number");
        }
    }

    public boolean isEdited() {
        if (isNew) {
            return edited;
        } else {
            return !((String.valueOf(initTHSPos1).equals(THS1.getPositionValue())) &&
                    (String.valueOf(initTHSDir1).equals(THS1.getDirectionValue())) &&
                    (String.valueOf(initRESA).equals(RESATextBox.getText())) &&
                    (String.valueOf(initTHS1Stripend).equals(THS1.getStripendText())) &&
                    (String.valueOf(initTHS1Clearway).equals(THS1.getClearwayText())) &&
                    (String.valueOf(initTHS1Stopway).equals(THS1.getStopwayText())) &&
                    (String.valueOf(initTHS1Disp).equals(THS1.getDisplacedThresholdText())) &&
                    (String.valueOf(initTHSPos2).equals(THS2.getPositionValue())) &&
                    (String.valueOf(initTHSDir2).equals(THS2.getDirectionValue())) &&
                    (String.valueOf(initRESA).equals(RESATextBox.getText())) &&
                    (String.valueOf(initTHS2Stripend).equals(THS2.getStripendText())) &&
                    (String.valueOf(initTHS2Clearway).equals(THS2.getClearwayText())) &&
                    (String.valueOf(initTHS2Stopway).equals(THS2.getStopwayText())) &&
                    (String.valueOf(initTHS2Disp).equals(THS2.getDisplacedThresholdText())) &&
                    (String.valueOf(initRunwayLength).equals(runwayLengthTextBox.getText())));
        }
    }

    public boolean isNew() {
        return isNew;
    }

    class ThresholdPanel extends JPanel {
        JSpinner dirOfTravelSpinner;
        JTextField stopwayTextBox;
        JTextField stripendTextBox;
        JComboBox positionDropDown;
        JTextField clearwayTextBox;
        JTextField THSDispTextBox;

        private ThresholdPanel correspondingTHS;

        private ThresholdPanel(ThresholdPanel t) {
            this();
            correspondingTHS = t;
            positionDropDown.setSelectedItem("C");
        }

        private ThresholdPanel() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

            JPanel column1 = new JPanel();
            column1.setLayout(new BoxLayout(column1, BoxLayout.PAGE_AXIS));
            JPanel column2 = new JPanel();
            column2.setLayout(new BoxLayout(column2, BoxLayout.PAGE_AXIS));
            JPanel column3 = new JPanel();
            column3.setLayout(new BoxLayout(column3, BoxLayout.PAGE_AXIS));
            JPanel column4 = new JPanel();
            column4.setLayout(new BoxLayout(column4, BoxLayout.PAGE_AXIS));

            JLabel dirOfTravelLabel = new JLabel("Direction of travel: ");
            SpinnerNumberModel model = new SpinnerNumberModel(1,1,36,1);
            dirOfTravelSpinner = new JSpinner(model);
            dirOfTravelSpinner.setEditor(new JSpinner.DefaultEditor(dirOfTravelSpinner));
            dirOfTravelSpinner.addChangeListener(e -> {
                if (correspondingTHS != null) {
                    JSpinner s = (JSpinner)e.getSource();

                    int i = (int)s.getValue();
                    i = i + 18;
                    if (i > 36) {
                        i = i - 36;
                    }
                    correspondingTHS.setDirection(i);
                }
            });
            setAllSizes(dirOfTravelSpinner, TEXT_FIELD_SIZE);

            JLabel stopwayLabel = new JLabel("Stopway (m): ");
            stopwayTextBox = new JTextField();
            setAllSizes(stopwayTextBox, TEXT_FIELD_SIZE);

            JLabel stripendLabel = new JLabel("Stripend (m): ");
            stripendTextBox = new JTextField();
            setAllSizes(stripendTextBox, TEXT_FIELD_SIZE);

            JLabel positionLabel = new JLabel("Position: ");

            String[] items = {"L", "C", "R"};
            positionDropDown = new JComboBox(items);
            positionDropDown.setSelectedItem("C");
            positionDropDown.addActionListener( e -> {
                String s = (String)positionDropDown.getSelectedItem();
                if (correspondingTHS != null) {
                    if (s.equals("L")) {
                        correspondingTHS.setPosition('R');
                    } else if (s.equals("R")) {
                        correspondingTHS.setPosition('L');
                    } else {
                        correspondingTHS.setPosition('C');
                    }
                }
            });
            setAllSizes(positionDropDown, TEXT_FIELD_SIZE);

            JLabel clearwayLabel = new JLabel("Clearway (m): ");
            clearwayTextBox = new JTextField();
            setAllSizes(clearwayTextBox, TEXT_FIELD_SIZE);

            JLabel THSDispLabel = new JLabel("Threshold displacement (m): ");
            THSDispTextBox = new JTextField();
            setAllSizes(THSDispTextBox, TEXT_FIELD_SIZE);

            column1.add(dirOfTravelLabel);
            column1.add(stopwayLabel);
            column1.add(stripendLabel);

            column2.add(dirOfTravelSpinner);
            column2.add(stopwayTextBox);
            column2.add(stripendTextBox);

            column3.add(positionLabel);
            column3.add(clearwayLabel);
            column3.add(THSDispLabel);

            column4.add(positionDropDown);
            column4.add(clearwayTextBox);
            column4.add(THSDispTextBox);

            add(Box.createRigidArea(SIDE_SEPERATION));
            add(column1);
            add(column2);
            add(Box.createHorizontalGlue());
            add(column3);
            add(column4);
            add(Box.createRigidArea(SIDE_SEPERATION));
        }

        private void setCorrespondingTHS(ThresholdPanel t) {
            this.correspondingTHS = t;
        }

        private void setPosition(char pos) {
            positionDropDown.setSelectedItem(String.valueOf(pos));
        }

        private void setDirection (int direction) {
            dirOfTravelSpinner.setValue(direction);
        }

        private void setStripend (int stripend) {
            stripendTextBox.setText(String.valueOf(stripend));
        }

        private void setClearway (int clearway) {
            clearwayTextBox.setText(String.valueOf(clearway));
        }

        private void setStopway (int stopway) {
            stopwayTextBox.setText(String.valueOf(stopway));
        }

        private void setDisplacedThreshold(int disp) {
            THSDispTextBox.setText(String.valueOf(disp));
        }


        private String getClearwayText() {
            return clearwayTextBox.getText();
        }

        private String getPositionValue() {
            return (String)positionDropDown.getSelectedItem();
        }

        private String getDirectionValue() {
            return dirOfTravelSpinner.getValue().toString();
        }

        private String getStripendText() {
            return stripendTextBox.getText();
        }

        private String getDisplacedThresholdText() {
            return THSDispTextBox.getText();
        }

        private String getStopwayText() {
            return stopwayTextBox.getText();
        }
    }

}
