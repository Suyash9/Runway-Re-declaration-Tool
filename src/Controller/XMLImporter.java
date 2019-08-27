package Controller;

import Model.Airport;
import Model.Obstacle;
import Model.Runway;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class XMLImporter {
    private Document dom;
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private String filepath;

    private Airport airport;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Runway> runways = new ArrayList<>();

    public XMLImporter(String filepath) {
        this.filepath = filepath;
    }

    public Obstacle importObstacle(){
        Obstacle obstacle = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.parse(filepath);

            obstacle = getObstacle(dom.getDocumentElement());

        } catch (ParserConfigurationException | SAXException pce) {
            System.out.println(pce.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return obstacle;
    }

    public Airport importAirport(){
        Airport airport = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.parse(filepath);

            Element doc = dom.getDocumentElement();

            String airportName = doc.getAttribute("Name");

            NodeList obstacleNodes = doc.getElementsByTagName("Obstacle");
            for (int i = 0; i < obstacleNodes.getLength(); i++) {
                obstacles.add(getObstacle(obstacleNodes.item(i)));
            }

            NodeList runwayNodes = doc.getElementsByTagName("Runway");
            for (int i = 0; i < runwayNodes.getLength(); i++) {
                runways.add(getRunway(runwayNodes.item(i)));
            }

            airport = new Airport(airportName, runways);

        } catch (ParserConfigurationException | SAXException pce) {
            System.out.println(pce.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return airport;
    }

    private static Obstacle getObstacle(Node node) {
        Obstacle obstacle = null;

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            String name = getTagValue("Name", element);
            Integer height = parseInt(getTagValue("Height", element));
            Integer width = parseInt(getTagValue("Width", element));
            Integer length = parseInt(getTagValue("Length", element));
            Integer distanceFromCenterline = (parseInt(getTagValue("DistCenterline", element)));
            Integer distanceLeftTHS = parseInt(getTagValue("DistLeftTHS", element));
            Integer distanceRightTHS = (parseInt(getTagValue("DistRightTHS", element)));

            obstacle = new Obstacle(name, height, width, length, distanceFromCenterline, distanceLeftTHS, distanceRightTHS);
        }
        return obstacle;
    }

    private static Runway getRunway(Node node) {
        Runway runway = null;

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            String designator = getTagValue("Designator", element);
            Integer TORA = parseInt(getTagValue("TORA", element));
            Integer TODA = parseInt(getTagValue("TODA", element));
            Integer ASDA = parseInt(getTagValue("ASDA", element));
            Integer LDA = parseInt(getTagValue("LDA", element));
            Integer displacedThreshold = parseInt(getTagValue("displacedThreshold", element));
            Integer runwayStrip = parseInt(getTagValue("runwayStrip", element));
            Integer STOPWAY = parseInt(getTagValue("STOPWAY", element));
            Integer CLEARWAY = parseInt(getTagValue("CLEARWAY", element));
            Integer RESA = parseInt(getTagValue("RESA", element));
            Integer stripEnd = parseInt(getTagValue("stripEnd", element));
            Integer ALS = parseInt(getTagValue("ALS", element));
            Integer TOCS = parseInt(getTagValue("TOCS", element));

            runway = new Runway(designator, TORA, TODA, ASDA, LDA, displacedThreshold, STOPWAY, CLEARWAY, RESA, stripEnd, ALS, TOCS);
        }
        return runway;
    }

    private static String getTagValue(String tag, Element element){
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Runway> getRunways() {
        return runways;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
