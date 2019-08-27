package Controller;

import Exceptions.InvalidArgumentException;
import Model.Airport;
import Model.Obstacle;
import Model.ObstacleManager;
import Model.Runway;
import View.EditAirportDialog;
import View.MainFrame;
import View.RunwayPanel;
import jdk.nashorn.internal.scripts.JO;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public  class MainController {
    private ObstacleManager obstacleManager = ObstacleManager.getInstance();
    private MainFrame frame;
    private Airport airport; //Currently selected airport
    private Runway runway; //Currently selected runway
    private Obstacle obstacle; //Currently selected obstacle
    private WriteFile writeFile;
    private static final Integer INITIAL_BLAST_PROTECTION = 300;
    private static final Integer INITIAL_SLOPE_RATIO = 50;

    public static void main(String[] args) {
        MainController c = new MainController();
        c.init();
    }
    private List<Airport> airports = new ArrayList<>();

    public MainController() {
        frame = new MainFrame(this);
        airport = new Airport("Heathrow Airport");
        runway = this.airport.getRunways().get(0);
        airports.add(airport);
    }

    public void init() {
        obstacle = null;
        runway.setObstacle(obstacle);


        frame.init(INITIAL_BLAST_PROTECTION, INITIAL_SLOPE_RATIO);
        recalculateAndUpdateDistances();

        try {
            setBlastProtection(INITIAL_BLAST_PROTECTION);
            setSlopeRatio(INITIAL_SLOPE_RATIO);
        } catch (InvalidArgumentException ex) { }
    }

    public void handleImportSituation(){}
    public void handleExportSituation(){ }
    public void handleImportAirport(){}
    public void handleConfigAirport(){}
    public void handleExportAirport(){}
    public void handleRunwayChange(){}
    public void handleRotation(){}
    public void handleSideView(){}
    public void handleTopView(){}
    public void handleExportView(){}

    public List<Obstacle> getPredefinedObstacles(){
        return this.obstacleManager.getPredefinedObstacles();
    }

    public void recalculateAndUpdateDistances() {
        runway.calculate();
        updateOriginalDistances(runway);
        updateRecalculatedDistances(runway);
        updateCalculationBreakdown(runway);
        frame.repaint();
    }

    public void updateOriginalDistances(Runway r) {
        frame.setOriginalTODA(r.getTODA());
        frame.setOriginalTORA(r.getTORA());
        frame.setOriginalASDA(r.getASDA());
        frame.setOriginalLDA(r.getLDA());
    }

    public void updateRecalculatedDistances(Runway r) {
        frame.setRecalculatedTODA(r.getUpdatedTODA());
        frame.setRecalculatedTORA(r.getUpdatedTORA());
        frame.setRecalculatedASDA(r.getUpdatedASDA());
        frame.setRecalculatedLDA(r.getUpdatedLDA());
    }

    public void updateCalculationBreakdown(Runway r){
        frame.setBreakdown(r.getCalcBreakdown());
    }

    public void handleSetPredefinedObstacle(String name){
        this.obstacle = this.obstacleManager.getPredefinedObstacleByName(name);
        this.airport.getRunwayByDesignator(runway.getDesignator()).setObstacle(this.obstacleManager.getPredefinedObstacleByName(name));
        recalculateAndUpdateDistances();
        updateObstacleSpec();
    }

    public void handleNewObstacle(List<String> parameters){
        this.airport.getRunwayByDesignator(runway.getDesignator()).setObstacle(this.obstacleManager.createObstacle(parameters));
        recalculateAndUpdateDistances();
        updateObstacleSpec();
    }

    public void handleEditObstacle(List<String> parameters){
        this.obstacleManager.modifyObstacle(parameters.get(0),parameters);
        this.airport.getRunwayByDesignator(runway.getDesignator()).setObstacle(this.obstacleManager.getPredefinedObstacleByName(parameters.get(0)));
        recalculateAndUpdateDistances();
        updateObstacleSpec();
    }

    public void saveObstacle(File file, List<String> parameters) {
        Obstacle obs = this.obstacleManager.createObstacle(parameters);

        XMLExporter exporter = new XMLExporter(obs, file.getPath());
        //todo here is the problem
        if (file.getName().toLowerCase().endsWith(".xml")) {
            exporter.exportEditObstacle(file.getPath());
        } else {
            exporter.exportEditObstacle(file.getPath() + ".xml");
        }
    }

    public List<Runway> getRunways() {
        return airport.getRunways();
    }

    public void setAirportName(String name) {
        airport.setName(name);
    }

    public String getAirportName() {
        return airport.getName();
    }

    public boolean editRunways(List<RunwayPanel> runwayPanels, EditAirportDialog parent) {
        List<Runway> oldRunways = airport.getRunways();
        ArrayList<Runway> newRunways = new ArrayList<>();


        Integer result = JOptionPane.showConfirmDialog(parent, "Obstacles from edited runways will be deleted. Is this OK?",
                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (!(result == JOptionPane.YES_OPTION)) {
            return false;
        }


        for (RunwayPanel r : runwayPanels) {
            try {
                int runwayLength = r.getRunwayLength();
                int RESA = r.getRESA();

                char THS1Pos = r.getTHS1Position();
                int THS1Dir = r.getTHS1Direction();
                int THS1Disp = r.getTHS1DisplacedThreshold();
                int THS1Clearway = r.getTHS1Clearway();
                int THS1Stopway = r.getTHS1Stopway();
                int THS1Stripend = r.getTHS1Stripend();

                char THS2Pos = r.getTHS2Position();
                int THS2Dir = r.getTHS2Direction();
                int THS2Disp = r.getTHS2DisplacedThreshold();
                int THS2Clearway = r.getTHS2Clearway();
                int THS2Stopway = r.getTHS2Stopway();
                int THS2Stripend = r.getTHS2Stripend();

                String THS1Desig = createDesignator(THS1Dir, THS1Pos);
                String THS2Desig = createDesignator(THS2Dir, THS2Pos);

                int THS1TORA = runwayLength;
                int THS1TODA = runwayLength + THS1Clearway;
                int THS1ASDA = runwayLength + THS1Stopway;
                int THS1LDA = runwayLength - THS1Disp;

                int THS2TORA = runwayLength;
                int THS2TODA = runwayLength + THS2Clearway;
                int THS2ASDA = runwayLength + THS2Stopway;
                int THS2LDA = runwayLength - THS2Disp;

                Runway THS1 = new Runway(THS1Desig, THS1TORA, THS1TODA, THS1ASDA, THS1LDA, THS1Disp, THS1Stopway, THS2Clearway, RESA, THS1Stripend);
                Runway THS2 = new Runway(THS2Desig, THS2TORA, THS2TODA, THS2ASDA, THS2LDA, THS2Disp, THS2Stopway, THS2Clearway, RESA, THS2Stripend);


                if (r.isEdited() || r.isNew()) {
                    THS1.setObstacle(null);
                    THS2.setObstacle(null);
                } else {
                    Obstacle THS1Obs = getRunwayByDesignator(THS1Desig).getObstacle();
                    Obstacle THS2Obs = getRunwayByDesignator(THS2Desig).getObstacle();

                    THS1.setObstacle(THS1Obs);
                    THS2.setObstacle(THS2Obs);
                }

                newRunways.add(THS1);
                newRunways.add(THS2);
            } catch (InvalidArgumentException | NumberFormatException ex) {
                createErrorDialog(ex.getMessage(), parent);
                return false;
            }
        }
        airport.clearRunwayList();
        airport.addRunways(newRunways);
        airport.setName(parent.getAirportName());

        setThresholds();

        return true;
    }

    public void setThresholds() {
        ArrayList<String> thresholds = new ArrayList<>();
        for (Runway r : airport.getRunways()) {
            thresholds.add(r.getDesignator());
        }

        frame.setThresholds(thresholds);
    }

    private String createDesignator(int dir, char pos) {
        String desig;
        if (dir < 10) {
            desig = "0" + String.valueOf(dir) + String.valueOf(pos);
        } else {
            desig = String.valueOf(dir) + String.valueOf(pos);
        }
        return desig;
    }

    private void createErrorDialog(String errorMessage, EditAirportDialog parent) {
        JOptionPane.showMessageDialog(parent, errorMessage,
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public List<RunwayPanel> getRunwayPanels(EditAirportDialog caller) {
        List<RunwayPanel> runwayPanels = new ArrayList<>();

        List<Runway> added = new ArrayList<>();

        for (Runway THS1 : airport.getRunways()) {
            String THS1designator = THS1.getDesignator();
            Runway THS2;

            int THS1Dir = Integer.parseInt(THS1designator.substring(0,2));

            String THS2Dir = oppositeThreshold(THS1Dir);

            char THS1Pos = THS1designator.substring(2,3).toCharArray()[0];
            char THS2Pos = Character.MIN_VALUE;
            if (THS1Pos == 'L') {
                THS2Pos = 'R';
            } else if (THS1Pos == 'R') {
                THS2Pos = 'L';
            } else if (THS1Pos == 'C') {
                THS2Pos = 'C';
            }
            if (THS1Pos == Character.MIN_VALUE) {
                THS2 = getRunwayByDesignator(THS2Dir);
            } else {
                THS2 = getRunwayByDesignator(THS2Dir + String.valueOf(THS2Pos));
            }

            if (THS2 == null) {
                //ERROR CASE
                return new ArrayList<>();
            }

            if (!added.contains(THS1) && !added.contains(THS2)) {
                added.add(THS1);
                added.add(THS2);

                int RESA = THS1.getRESA();
                int RunwayLength = THS1.getTORA();

                int THS1Disp = THS1.getDisplacedThreshold();
                int THS2Disp = THS2.getDisplacedThreshold();

                int THS1Stopway = THS1.getSTOPWAY();
                int THS1StripEnd = THS1.getStripEnd();
                int THS1Clearway = THS1.getCLEARWAY();

                int THS2Stopway = THS2.getSTOPWAY();
                int THS2StripEnd = THS2.getStripEnd();
                int THS2Clearway = THS2.getCLEARWAY();

                RunwayPanel rp = new RunwayPanel(this, caller, THS1Pos, THS2Pos, THS1Dir, Integer.parseInt(THS2Dir), RESA,
                                    THS1StripEnd, THS2StripEnd, THS1Clearway, THS2Clearway, THS1Stopway, THS2Stopway,
                                    THS1Disp, THS2Disp, RunwayLength);

                runwayPanels.add(rp);
            }
        }
        return runwayPanels;
    }

    public Runway getRunwayByDesignator(String designator) {
        for (Runway r : airport.getRunways()) {
            if (r.getDesignator().equals(designator)) {
                return r;
            }
        }
        return null;
    }

    public String oppositeThreshold(int i) {
        i = ((i + 17) % (36))+ 1;
        if (i < 10) {
            return "0" + String.valueOf(i);
        } else {
            return  String.valueOf(i);
        }
    }

    public void updateObstacleSpec() {
        Obstacle o = runway.getObstacle();
        if (o != null) {
            frame.setObstacleHeight(o.getHeight());
            frame.setObstacleName(o.getName());
            frame.setObstacleWidth(o.getWidth());
            frame.setObstacleCentreline(o.getDistanceFromCenterline());
            frame.setObstacleLength(o.getLength());
            frame.setObstacleLeftTHS(o.getDistanceLeftTHS());
            frame.setObstacleRightTHS(o.getDistanceRightTHS());
        }
    }

    public boolean hasObstacle() {
        return (runway.getObstacle() != null);
    }

    public void setBlastProtection(Integer blastProtection) throws InvalidArgumentException {
        if (runway != null) {
            if (blastProtection >= 0) {
                runway.setBlastProtection(blastProtection);
                recalculateAndUpdateDistances();
            } else {
                throw new InvalidArgumentException("Blast protection cannot be negative");
            }
        }
    }

    public void setSlopeRatio(Integer slopeRatio) throws InvalidArgumentException {
        if (runway != null) {
            if (slopeRatio > 0) {
                runway.setSlopeRatio(slopeRatio);
                recalculateAndUpdateDistances();
            } else {
                throw new InvalidArgumentException("Slope ratio (1:n) must be greater than zero");
            }
        }
    }



    public void handleChangeAirport(String name){
        this.airport = this.getAirportByName(name);
    }

    public String getDefaultAirport(){
        return airport.getName();
    }

    public List<String> getDefaultRunways(){
        return this.airport.getRunwayNames();
    }

    private Airport getAirportByName(String name){
        for(Airport a: this.airports){
            if (a.getName().equals(name)){
                return a;
            }
        }

        System.err.println("Airport not found");
        return null;
    }

    public List<String> getAirportNames(){
        List<String> result = new ArrayList<>();

        for(Airport a : this.airports){
            result.add(a.getName());
        }

        return result;
    }

    public void handleRunwayChange(String designator){
        this.runway = this.airport.getRunwayByDesignator(designator);
        if(this.runway.getObstacle() == null){
            this.runway.setObstacle(this.obstacle);
        }
    }

    public Obstacle getObstacle(){
        return this.obstacle;
    }

    public void removeObstacle() {
        runway.setObstacle(null);
        recalculateAndUpdateDistances();
    }

    public Runway getRunway() {
        return this.runway;
    }

    public void setRunway(Runway r){
        this.runway = r;
    }



    public void writeImageToFile(File file, String formatName, BufferedImage i) {
        formatName = formatName.toLowerCase();
        try {
            Object[] options = {"OK"};
            if (!ImageIO.write(i, formatName, file)) {
                JOptionPane error = new JOptionPane("Format unsupported", JOptionPane.WARNING_MESSAGE);

                JOptionPane.showOptionDialog(frame, "Format unsupported", "Warning",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,null, options, options[0]);
            }
        } catch (IOException ex) {

        }
    }

    public void exportXMLAirport(String filepath){
        XMLExporter e = new XMLExporter(airport, obstacleManager, runway, filepath);
        e.exportAirport();
    }

    public void exportXMLObstacle(String filepath){
        XMLExporter e = new XMLExporter(airport, obstacleManager, runway, filepath);
        e.exportSingleObstacle(runway.getObstacle());
    }

    public void importAirportFromXML(String filepath){
        XMLImporter i = new XMLImporter(filepath);
        if (validateXML("/Airport.xsd", filepath)) {
            try {
                Airport importedAirport = i.importAirport();
                airports.add(importedAirport);

                ArrayList<String> airportNames = new ArrayList<>();
                for (Airport a : airports) {
                    airportNames.add(a.getName());
                }
                frame.setAirports(airportNames);
                recalculateAndUpdateDistances();

                JOptionPane.showMessageDialog(frame, "Airport imported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid parameters: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid XML file structure.");
        }
    }

    // Validates a given XML file against an XSD Schema.
    // Source: https://journaldev.com/895/how-to-validate-xml-against-xsd-in-java
    public boolean validateXML(String schemaPath, String XMLPath){
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            InputStream i = getClass().getResourceAsStream(schemaPath);
            Schema schema = factory.newSchema(new StreamSource(i));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(XMLPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
    }

    public void importObstacle(String filepath){
        XMLImporter i = new XMLImporter(filepath);

        try {
            Obstacle obs = i.importObstacle();

            if (obs.getHeight() >= 0 && obs.getLength() >= 0 && obs.getWidth() >= 0 && obs.getDistanceRightTHS() >= 0 && obs.getDistanceLeftTHS() >= 0 && obs.getDistanceFromCenterline() >= 0) {
                runway.setObstacle(obs);

                if (runway.getObstacle().equals(obs)){
                    frame.setAddObstacleEnabled(false);
                    frame.setEditObstacleEnabled(true);
                    frame.setRemoveObstacleEnabled(true);
                    frame.setExportObstacleEnabled(true);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Negative parameters are not permitted. ");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid parameters: " + e.getMessage());
        }

        updateObstacleSpec();
    }

    //Prints relevant data to .txt file
    public void writeSituationToTextFile(String filePath){
        writeFile = new WriteFile(filePath);
        if(obstacle != null && runway != null) {
            try {
                writeFile.writeToFile("Original Runway Distances" +
                        "\nTORA = " + runway.getTORA() +
                        "\nTODA = " + runway.getTODA() +
                        "\nASDA = " + runway.getASDA() +
                        "\nLDA  = " + runway.getLDA() +

                        "\n\nRecalculated Runway Distances" +
                        "\nUpdated TORA = " + runway.getUpdatedTORA() +
                        "\nUpdated TODA = " + runway.getUpdatedTODA() +
                        "\nUpdated ASDA = " + runway.getUpdatedASDA() +
                        "\nUpdated LDA  = " + runway.getUpdatedLDA() +

                        "\n\nObstacle Specification" +
                        "\nName = " + obstacle.getName() +
                        "\nWidth (m) = " + obstacle.getWidth() +
                        "\nLength (m) = " + obstacle.getLength() +
                        "\nHeight (m) = " + obstacle.getHeight() +
                        "\nDistance from Centreline (m) = " + obstacle.getDistanceFromCenterline() +
                        "\nDistance from Left Threshold (m) = " + obstacle.getDistanceLeftTHS() +
                        "\nDistance from Right Threshold (m) = " + obstacle.getDistanceRightTHS() +
                        "\nBlast Protection = " + runway.getBlastProtection() +
                        "\n\nAirport Data " +
                        "\nName = " + airport.getName() +
                        "\nRunways = " + airport.getRunwayNames() +

                        "\n\nMore Runway Data" +
                        "\nDisplaced Threshold = " + runway.getDisplacedThreshold() +
                        "\nStopway = " + runway.getSTOPWAY() +
                        "\nClearway = " + runway.getCLEARWAY() +
                        "\nRESA = " + +runway.getRESA() +
                        "\nALS = " + runway.getALS() +
                        "\nTOCS = " + runway.getTOCS() +
                        "\nRunway Strip = " + runway.getRunwayStrip()

                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (runway != null) {
            try {
                writeFile.writeToFile("Original Runway Distances" +
                        "\nTORA = " + runway.getTORA() +
                        "\nTODA = " + runway.getTODA() +
                        "\nASDA = " + runway.getASDA() +
                        "\nLDA  = " + runway.getLDA() +

                        "\n\nNo obstacle not present" +

                        "\n\nMore Runway Data" +
                        "\nDisplaced Threshold = " + runway.getDisplacedThreshold() +
                        "\nStopway = " + runway.getSTOPWAY() +
                        "\nClearway = " + runway.getCLEARWAY() +
                        "\nRESA = " + +runway.getRESA() +
                        "\nALS = " + runway.getALS() +
                        "\nTOCS = " + runway.getTOCS() +
                        "\nRunway Strip = " + runway.getRunwayStrip()

                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Must have a runway to output a situation",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
