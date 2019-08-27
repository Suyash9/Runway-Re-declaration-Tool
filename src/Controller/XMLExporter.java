package Controller;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import Model.Airport;
import Model.Obstacle;
import Model.ObstacleManager;
import Model.Runway;
import org.xml.sax.*;
import org.w3c.dom.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
* This class handles exporting the details of the Airfield as XML files.
* */
public class XMLExporter {
    private Airport airport;
    private ObstacleManager om;
    private Obstacle obs;
    private Runway runway;
    private String filepath;

    private Document dom;
    private Element e = null;
    private DocumentBuilder db;

    public XMLExporter(Airport airport, ObstacleManager om, Runway runway, String filepath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }

        dom = db.newDocument();

        this.airport = airport;
        this.om = om;
        this.runway = runway;
        this.filepath = filepath;
    }

    public XMLExporter(Obstacle obs, String filepath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }

        dom = db.newDocument();
        this.obs = obs;
        this.filepath = filepath;
    }

    public void exportSingleObstacle(Obstacle obs){
        dom.appendChild(createObstacleElem(obs));
        outputToFile();
    }

    public void exportAirport() {
        Element airportElem = dom.createElement("Airport");
        airportElem.setAttribute("Name", airport.getName());

        Element obstaclesElem = dom.createElement("Obstacles");
        for (Obstacle obstacle : om.getPredefinedObstacles()) {
            obstaclesElem.appendChild(createObstacleElem(obstacle));
        }
        airportElem.appendChild(obstaclesElem);

        Element runwaysElem = dom.createElement("Runways");
        for (Runway runway : airport.getRunways()) {
            Element run = dom.createElement("Runway");
            runwaysElem.appendChild(run);

            e = dom.createElement("Designator");
            e.setTextContent(runway.getDesignator());
            run.appendChild(e);

            e = dom.createElement("TORA");
            e.setTextContent(runway.getTORA().toString());
            run.appendChild(e);

            e = dom.createElement("TODA");
            e.setTextContent(runway.getTODA().toString());
            run.appendChild(e);

            e = dom.createElement("ASDA");
            e.setTextContent(runway.getASDA().toString());
            run.appendChild(e);

            e = dom.createElement("LDA");
            e.setTextContent(runway.getLDA().toString());
            run.appendChild(e);

            e = dom.createElement("displacedThreshold");
            e.setTextContent(runway.getDisplacedThreshold().toString());
            run.appendChild(e);

            e = dom.createElement("runwayStrip");
            e.setTextContent(runway.getRunwayStrip().toString());
            run.appendChild(e);

            e = dom.createElement("STOPWAY");
            e.setTextContent(runway.getSTOPWAY().toString());
            run.appendChild(e);

            e = dom.createElement("CLEARWAY");
            e.setTextContent(runway.getCLEARWAY().toString());
            run.appendChild(e);

            e = dom.createElement("RESA");
            e.setTextContent(runway.getRESA().toString());
            run.appendChild(e);

            e = dom.createElement("stripEnd");
            e.setTextContent(runway.getStripEnd().toString());
            run.appendChild(e);

            e = dom.createElement("ALS");
            e.setTextContent(runway.getALS().toString());
            run.appendChild(e);

            e = dom.createElement("TOCS");
            e.setTextContent(runway.getRunwayStrip().toString());
            run.appendChild(e);
        }
        airportElem.appendChild(runwaysElem);

        dom.appendChild(airportElem);

        outputToFile();
    }

    public Element createObstacleElem(Obstacle obs){
        Element obstacleElem = dom.createElement("Obstacle");

        e = dom.createElement("Name");
        e.setTextContent(obs.getName());
        obstacleElem.appendChild(e);

        e = dom.createElement("Height");
        e.setTextContent(obs.getHeight().toString());
        obstacleElem.appendChild(e);

        e = dom.createElement("Width");
        e.setTextContent(obs.getWidth().toString());
        obstacleElem.appendChild(e);

        e = dom.createElement("Length");
        e.setTextContent(obs.getLength().toString());
        obstacleElem.appendChild(e);

        e = dom.createElement("DistCenterline");
        e.setTextContent(obs.getDistanceFromCenterline().toString());
        obstacleElem.appendChild(e);

        e = dom.createElement("DistLeftTHS");
        e.setTextContent(obs.getDistanceLeftTHS().toString());
        obstacleElem.appendChild(e);

        e = dom.createElement("DistRightTHS");
        e.setTextContent(obs.getDistanceRightTHS().toString());
        obstacleElem.appendChild(e);

        return obstacleElem;
    }

    public void exportEditObstacle(String filepath) {
        this.filepath = filepath;
        dom.appendChild(createObstacleElem(this.obs));
        outputToFile();
    }

    private void outputToFile() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(filepath)));

        } catch (TransformerException | IOException te) {
            System.out.println(te.getMessage());
        }
    }
}