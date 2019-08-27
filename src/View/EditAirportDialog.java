package View;

import Controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EditAirportDialog extends JDialog {
    private MainController controller;
    private JFrame parent;
    private JPanel runwaysPanel;
    JScrollPane scrollPane;
    private ArrayList<RunwayPanel> runways;
    private JTextField name;

    private static final Dimension RUNWAY_SEPARATION = new Dimension(0, 20);
    private static final Dimension DIALOG_SIZE = new Dimension(720, 480);

    private static Component verticalGlue = Box.createVerticalGlue();

    public EditAirportDialog(JFrame f, MainController c) {
        super(f, "Edit airport configuration");
        parent = f;
        controller = c;
        runways = new ArrayList<>(); //Get current list of runways
    }

    public void init() {
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setContentPane(contentPane);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        setPreferredSize(new Dimension(DIALOG_SIZE));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.LINE_AXIS));

        namePanel.add(new JLabel("Airport Name: "));
        namePanel.add(Box.createRigidArea(new Dimension(20,0)));

        name = new JTextField();

        Dimension size = new Dimension(200,18);

        name.setMinimumSize(size);
        name.setMaximumSize(size);
        name.setPreferredSize(size);
        name.setSize(size);
        namePanel.add(name);
        namePanel.add(Box.createHorizontalGlue());


        runwaysPanel = new JPanel();
        runwaysPanel.setLayout(new BoxLayout(runwaysPanel, BoxLayout.PAGE_AXIS));
        runwaysPanel.add(verticalGlue);
        scrollPane = new JScrollPane(runwaysPanel);

        JButton addNew = new JButton("Add new runway");
        JButton apply = new JButton("Apply changes");

        addNew.addActionListener(e-> {
            runways.remove(verticalGlue);
            RunwayPanel newRunway = new RunwayPanel(controller, this);
            runwaysPanel.add(newRunway);
            runwaysPanel.add(Box.createRigidArea(RUNWAY_SEPARATION));
            runwaysPanel.add(verticalGlue);
            runways.add(newRunway);

            revalidate();
            scrollPane.getViewport().setViewPosition(new Point(0,runwaysPanel.getSize().height));

            JOptionPane.showMessageDialog(this,"Runway added successfully!");

        });

        apply.addActionListener(e -> {
            if (controller.editRunways(runways, this)) {
                this.dispose();
            }
            JOptionPane.showMessageDialog(this,"Changes applied successfully!");

        });


        runwaysPanel.setLayout(new BoxLayout(runwaysPanel, BoxLayout.Y_AXIS));

        add(new JLabel("Use the controls below to add, remove and modify the runways of the current airport"));
        add(Box.createRigidArea(new Dimension(10,0)));
        add(namePanel);
        add(scrollPane);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        buttonsPanel.add(addNew);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10,0)));
        buttonsPanel.add(apply);
        buttonsPanel.add(Box.createHorizontalGlue());

        add(buttonsPanel);

        setAirportName(controller.getAirportName());

        if (controller.getRunwayPanels(this).size() != 0) {
            addRunwayPanels(controller.getRunwayPanels(this));
        }

        setResizable(false);
        pack();
        setVisible(true);
    }

    public void addRunwayPanels(List<RunwayPanel> panels) {
        for (RunwayPanel r : panels) {
            runways.remove(verticalGlue);
            runwaysPanel.add(r);
            runwaysPanel.add(Box.createRigidArea(RUNWAY_SEPARATION));
            runwaysPanel.add(verticalGlue);
            runways.add(r);
        }
        revalidate();
        repaint();
    }

    public void addRunwayPanels() {
        for (RunwayPanel r : runways) {
            runwaysPanel.add(r);
            runwaysPanel.add(Box.createRigidArea(RUNWAY_SEPARATION));
            runwaysPanel.add(verticalGlue);
        }
        runwaysPanel.add(verticalGlue);
        scrollPane = new JScrollPane(runwaysPanel);
        revalidate();
        repaint();
    }

    public void removeRunwayPanel(RunwayPanel rp) {
        runways.remove(rp);

        runwaysPanel = new JPanel();
        runwaysPanel.setLayout(new BoxLayout(runwaysPanel, BoxLayout.PAGE_AXIS));
        runwaysPanel.add(verticalGlue);

        addRunwayPanels();
    }

    public void setAirportName(String name) {
        this.name.setText(name);
    }

    public String getAirportName() {
        return name.getText();
    }
}
