package View;

import Controller.MainController;
import Model.Obstacle;
import Model.Runway;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/* Extension of JPanel to display a top-down view of the runway strip currently selected. The redeclared distances are
 * displayed if necessary.*/
public class TopView extends JPanel {
    private BufferedImage runwayView = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    private MainController controller;

    private Image runwayImage = null;
    private Image bgImage = null;

    private Image scaledRunway = null;
    private Image scaledBG = null;
    private int paddingSideTop = 15;
    private int paddingSide = 60;
    private boolean colourBlindMode = false;

    private int initialX,initialY;
    private int currOffsetX,currOffsetY;
    private double zoomRatio = 1;

    private float ratio;

    private boolean LOorTOT = false;
    private boolean LT = false;
    private boolean TOA = true;

    private String currentThreshold;

    private boolean isTakeoff = true;

    int arrowDirection = 1;

    private MainFrame mainFrame;
    private Integer runwayLength;
    private float scaleFactor;
    private int origX;

    private int arrowFontSize = 12;

    private Color centrelineColour = Color.red;
    private Color distanceArrowColour = Color.BLACK;

    private Color colourblindBG = new Color(255, 216, 0);
    private Color normalBG = new Color(0, 197, 3);

    private Boolean rotate = false;

    /* Initialises the view*/
    public TopView(MainFrame frame, MainController controller) {
        this.mainFrame = frame;
        this.controller = controller;

        this.addListeners();
        setBackground(normalBG);

        try {
            InputStream i = getClass().getResourceAsStream("/runway.png");
            runwayImage = ImageIO.read(i);
            origX = runwayImage.getWidth(null);
            i = getClass().getResourceAsStream("/bgImage.png");
            bgImage = ImageIO.read(i);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.runwayLength = this.controller.getRunway().getRunwayStrip();
    }

    /* Returns a buffered image for the view, to enable exporting of the view.*/
    public BufferedImage getView() {
        return runwayView;
    }

    @Override
    /* Resizes the buffered image as necessary upon resizing of the panel itself.*/
    public void invalidate() {
        super.invalidate();
        int width = getWidth();
        int height = getHeight();

        this.ratio = ((float) this.runwayLength / this.getWidth());
        int stripEnd = (int) (this.controller.getRunway().getStripEnd() / ratio);

        //scale the images
        if (width > 0 && height > 0) {
            scaledRunway = runwayImage.getScaledInstance(getWidth() - 2 * stripEnd, getHeight() / 4,
                    Image.SCALE_SMOOTH);
            scaledBG = bgImage.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
            scaleFactor = scaledRunway.getWidth(null) / (float) origX;
        }
    }


    /* Draws the various graphic elements onto the image of the runway strip in the correct positions. The distances
     * are drawn onto the runway strip if necessary. Direction of travel is also drawn onto the runway strip.*/
    public void drawRunway(Graphics g) {
        Runway runway = controller.getRunway();

        int stripEnd = (int) (this.controller.getRunway().getStripEnd() / ratio);

        g.drawImage(scaledBG, 0, 0, this);
        g.drawImage(scaledRunway, stripEnd, this.getHeight() / 2 - this.getHeight() / 8, this);

        int fontSize = this.getHeight() / 11;

        //rotate the string
        Font font = new Font(null, Font.PLAIN, fontSize);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(90), 0, 0);
        Font rotatedFont = font.deriveFont(affineTransform);
        g.setFont(rotatedFont);
        g.setColor(Color.WHITE);

        String leftThreshold = this.controller.getRunway().getLowerThreshold();
        String rightThreshold = this.controller.getRunway().getHigherThreshold();

        drawLeftThreshold(g, leftThreshold);
        drawRightThreshold(g, fontSize, rightThreshold);

        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        g.setFont(font.deriveFont(affineTransform));

        g.setColor(centrelineColour);
        g.drawLine(0, getHeight() / 2 - 1, getWidth(), getHeight() / 2 - 1);
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        drawDirection(g, arrowDirection);
        drawDistances(g, runway);
    }

    /* Draws arrows onto the top down view of the runway to represent the redefined distances of the runway, when an obstacle
     * is present (otherwise, no arrows are drawn). Breaks down the calculation into its resultant components, e.g
     * blast protection, RESA, slope calculation etc.*/
    private void drawDistances(Graphics g, Runway runway) {
        if (runway.getObstacle() != null) {
            Obstacle obs = runway.getObstacle();
            int slope = (int) (obs.getHeight() * runway.getSlopeRatio() / ratio);
            int RESA = (int) (runway.getRESA() / ratio);
            int blastProtection = (int) (runway.getBlastProtection() / ratio);
            int stripEnd = (int) (runway.getStripEnd() / ratio);
            int startBlastProtectionX = 0;
            int endBlastProtectionX = 0;
            int startSlopeX = 0;
            int endSlopeX = 0;

            int startResaX;
            int endResaX;

            int startStripEndX;
            int endStripEndX;

            int LTHSStart = stripEnd + (int) (60 * scaleFactor);
            int RTHSStart = getWidth() - stripEnd - (int) (80 * scaleFactor);

            int orientation = Integer.parseInt(currentThreshold.substring(0, 2));
            int LTHSDisp, RTHSDisp;

            String opposDesignator = controller.oppositeThreshold(orientation);

            if (currentThreshold.endsWith("L")) {
                opposDesignator += "R";
            } else if (currentThreshold.endsWith("C")) {
                opposDesignator += "C";
            } else if (currentThreshold.endsWith("R")) {
                opposDesignator += "L";
            }

            Runway opposTHS = controller.getRunwayByDesignator(opposDesignator);

            //Draw displaced Thresholds
            if (orientation >= 19) {
                LTHSDisp = opposTHS.getDisplacedThreshold();
                RTHSDisp = controller.getRunwayByDesignator(currentThreshold).getDisplacedThreshold();
            } else {
                LTHSDisp = controller.getRunwayByDesignator(currentThreshold).getDisplacedThreshold();
                RTHSDisp = opposTHS.getDisplacedThreshold();
            }

            //Draws an arrow to display the distances for each threshold (i.e how much it is displaced by or not e.g 307m
            drawDistanceArrow(g, stripEnd, LTHSStart, getHeight() / 2 + 20, Integer.toString(LTHSDisp) + "m");
            drawDistanceArrow(g, getWidth() - stripEnd, RTHSStart, getHeight() / 2 + 20, Integer.toString(RTHSDisp) + "m");

            int arrowPadding = (int) (arrowFontSize * 1.7);

            /* Enumerates all 8 scenarios for drawing and draws out the appropriate distances for each scenario*/
            if (obs.getDistanceLeftTHS() < obs.getDistanceRightTHS()) {

                g.setColor(Color.white);
                g.drawLine(stripEnd + (int) (60 * scaleFactor), 10, stripEnd + (int) (60 * scaleFactor), 300);

                if (TOA) { //Take Off Away from the left threshold on the view
                    startBlastProtectionX = LTHSStart + (int) (obs.getDistanceLeftTHS() / ratio);
                    endBlastProtectionX = startBlastProtectionX + blastProtection;

                    int endTODAX = endBlastProtectionX + (int) (runway.getUpdatedTODA() / ratio);
                    int endTORAX = endBlastProtectionX + (int) (runway.getUpdatedTORA() / ratio);
                    int endASDAX = endBlastProtectionX + (int) (runway.getUpdatedASDA() / ratio);

                    drawDistanceArrow(g, startBlastProtectionX, endBlastProtectionX, getHeight() / 2 - arrowPadding * 4, "Blast Protection");
                    drawDistanceArrow(g, endBlastProtectionX, endTODAX, getHeight() / 2 - arrowPadding * 3, "TODA");
                    drawDistanceArrow(g, endBlastProtectionX, endTORAX, getHeight() / 2 - arrowPadding * 2, "TORA");
                    drawDistanceArrow(g, endBlastProtectionX, endASDAX, getHeight() / 2 - arrowPadding, "ASDA");

                    drawObstacleLTHS(g, obs, LTHSStart);
                } else if (LOorTOT && !isTakeoff) { //Landing over the obstacle onto the left threshold on the view (e.g 09R)
                    int obsPos = LTHSStart + (int) (obs.getDistanceLeftTHS() / ratio);

                    startResaX = obsPos;
                    endResaX = startResaX + RESA;

                    startSlopeX = obsPos;
                    endSlopeX = startSlopeX + slope;

                    if (endSlopeX > endResaX)
                        startStripEndX = endSlopeX;
                    else
                        startStripEndX = endResaX;

                    endStripEndX = startStripEndX + stripEnd;

                    int startLDA = endStripEndX;
                    int endLDA = startLDA + (int) (runway.getUpdatedLDA() / ratio);
                    if (endLDA < (getWidth() - stripEnd)) {
                        endLDA = getWidth() - stripEnd;
                    }

                    drawDistanceArrow(g, startResaX, endResaX, getHeight() / 2 - arrowPadding + 4, "RESA");
                    drawDistanceArrow(g, startSlopeX, endSlopeX, getHeight() / 2 - arrowPadding * 3, "Slope");
                    drawDistanceArrow(g, startStripEndX, endStripEndX, getHeight() / 2 - arrowPadding * 2, "Strip End");
                    drawDistanceArrow(g, startLDA, endLDA, getHeight() / 2 - arrowPadding, "LDA");

                    drawObstacleLTHS(g, obs, LTHSStart);
                } else if (LOorTOT) { //Takeoff Towards obstacle on the right threshold
                    int obsPos = RTHSStart - (int) (obs.getDistanceRightTHS() / ratio);

                    startResaX = obsPos;
                    endResaX = startResaX + RESA;

                    startSlopeX = obsPos;
                    endSlopeX = startSlopeX + slope;


                    if (endSlopeX > endResaX)
                        startStripEndX = endSlopeX;
                    else
                        startStripEndX = endResaX;
                    endStripEndX = startStripEndX + stripEnd;

                    drawDistanceArrow(g, startResaX, endResaX, getHeight() / 2 - arrowPadding * 4, "RESA");
                    drawDistanceArrow(g, startSlopeX, endSlopeX, getHeight() / 2 - arrowPadding * 3, "Slope");
                    drawDistanceArrow(g, startStripEndX, endStripEndX, getHeight() / 2 - arrowPadding * 2, "Strip End");
                    drawDistanceArrow(g, LTHSStart, endStripEndX, getHeight() / 2 - arrowPadding, "TODA, TORA, ASDA");

                    drawObstacleRTHS(g, obs, RTHSStart);
                } else if (LT) { //Landing Towards the obstacle on the right threshold
                    g.setColor(Color.white);
                    int LDAEnd = RTHSStart - (int) (runway.getUpdatedLDA() / ratio);
                    startResaX = LDAEnd - stripEnd;
                    endResaX = startResaX - RESA;

                    drawDistanceArrow(g, LTHSStart, LDAEnd, getHeight() / 2 - arrowPadding, "LDA");
                    drawDistanceArrow(g, LDAEnd, startResaX, getHeight() / 2 - arrowPadding * 2, "Strip End");
                    drawDistanceArrow(g, startResaX, endResaX, getHeight() / 2 - arrowPadding * 3, "RESA");

                    drawObstacleRTHS(g, obs, RTHSStart);
                }
            } else {
                if (LT) { //Landing Towards the obstacle onto the left threshold
                    g.setColor(Color.white);
                    int LDAEnd = LTHSStart + (int) (runway.getUpdatedLDA() / ratio);
                    startResaX = LDAEnd + stripEnd;
                    endResaX = startResaX + RESA;

                    drawDistanceArrow(g, LTHSStart, LDAEnd, getHeight() / 2 - arrowPadding, "LDA");
                    drawDistanceArrow(g, LDAEnd, startResaX, getHeight() / 2 - arrowPadding * 2, "Strip End");
                    drawDistanceArrow(g, startResaX, endResaX, getHeight() / 2 - arrowPadding * 3, "RESA");
                    drawObstacleLTHS(g, obs, LTHSStart);
                } else if (isTakeoff && LOorTOT) { //Takeoff towards the obstacle on the left threshold
                    int obsPos = LTHSStart + (int) (obs.getDistanceLeftTHS() / ratio);

                    startResaX = obsPos;
                    endResaX = startResaX - RESA;

                    startSlopeX = obsPos;
                    endSlopeX = startSlopeX - slope;

                    if (endSlopeX < endResaX)
                        startStripEndX = endSlopeX;
                    else
                        startStripEndX = endResaX;
                    endStripEndX = startStripEndX - stripEnd;

                    drawDistanceArrow(g, startResaX, endResaX, getHeight() / 2 - arrowPadding * 4, "RESA");
                    drawDistanceArrow(g, startSlopeX, endSlopeX, getHeight() / 2 - arrowPadding * 3, "Slope");
                    drawDistanceArrow(g, startStripEndX, endStripEndX, getHeight() / 2 - arrowPadding * 2, "Strip End");
                    drawDistanceArrow(g, LTHSStart, endStripEndX, getHeight() / 2 - arrowPadding, "TODA, TORA, ASDA");

                    drawObstacleLTHS(g, obs, LTHSStart);
                } else if (LOorTOT) { //Landing over the obstacle on the right threshold
                    int obsPos = RTHSStart - (int) (obs.getDistanceRightTHS() / ratio);

                    startResaX = obsPos;
                    endResaX = startResaX - RESA;

                    startSlopeX = obsPos;
                    endSlopeX = startSlopeX - slope;

                    if (endSlopeX < endResaX)
                        startStripEndX = endSlopeX;
                    else
                        startStripEndX = endResaX;

                    endStripEndX = startStripEndX - stripEnd;

                    int startLDA = endStripEndX;
                    int endLDA = startLDA - (int) (runway.getUpdatedLDA() / ratio);
                    if (endLDA < stripEnd) {
                        endLDA = stripEnd;
                    }

                    drawDistanceArrow(g, startResaX, endResaX, getHeight() / 2 - arrowPadding * 4, "RESA");
                    drawDistanceArrow(g, startSlopeX, endSlopeX, getHeight() / 2 - arrowPadding * 3, "Slope");
                    drawDistanceArrow(g, startStripEndX, endStripEndX, getHeight() / 2 - arrowPadding * 2, "Strip End");
                    drawDistanceArrow(g, startLDA, endLDA, getHeight() / 2 - arrowPadding, "LDA");
                    drawObstacleRTHS(g, obs, RTHSStart);
                } else if (TOA) { //Takeoff Away from the obstacle on the right threshold
                    startBlastProtectionX = RTHSStart - (int) (obs.getDistanceRightTHS() / ratio);
                    endBlastProtectionX = startBlastProtectionX - blastProtection;

                    int endTODAX = endBlastProtectionX - (int) (runway.getUpdatedTODA() / ratio);
                    int endTORAX = endBlastProtectionX - (int) (runway.getUpdatedTORA() / ratio);
                    int endASDAX = endBlastProtectionX - (int) (runway.getUpdatedASDA() / ratio);

                    drawDistanceArrow(g, startBlastProtectionX, endBlastProtectionX, getHeight() / 2 - arrowPadding * 4, "Blast Protection");
                    drawDistanceArrow(g, endBlastProtectionX, endTODAX, getHeight() / 2 - arrowPadding * 3, "TODA");
                    drawDistanceArrow(g, endBlastProtectionX, endTORAX, getHeight() / 2 - arrowPadding * 2, "TORA");
                    drawDistanceArrow(g, endBlastProtectionX, endASDAX, getHeight() / 2 - arrowPadding, "ASDA");
                    drawObstacleRTHS(g, obs, RTHSStart);
                }

            }
        }
        //No distances drawn when no obstacle is present
    }

    /* Toggles the colourblind background of the topdown view, as per the checkbox on the main frame*/
    public void setColourblindMode() {
        try {
            if (!colourBlindMode) {
                InputStream i = getClass().getResourceAsStream("/bgImageColourblind.png");
                bgImage = ImageIO.read(i);
                colourBlindMode = true;
                setBackground(colourblindBG);
            } else if (colourBlindMode) {
                InputStream i = getClass().getResourceAsStream("/bgImage.png");
                bgImage = ImageIO.read(i);
                colourBlindMode = false;
                setBackground(normalBG);
            }
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Draws a black arrow onto the top view between the x-coords provided, at the given height.*/
    private void drawDistanceArrow(Graphics g, int x1, int x2, int y) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }

        g.setColor(Color.black);
        Polygon rightArrowEnd = new Polygon();
        rightArrowEnd.addPoint(x2, y);
        rightArrowEnd.addPoint(x2 - 5, y + 3);
        rightArrowEnd.addPoint(x2 - 5, y - 3);

        Polygon leftArrowEnd = new Polygon();
        leftArrowEnd.addPoint(x1, y);
        leftArrowEnd.addPoint(x1 + 5, y + 3);
        leftArrowEnd.addPoint(x1 + 5, y - 3);

        g.fillPolygon(rightArrowEnd);
        g.drawLine(x1, y, x2, y);
        g.fillPolygon(leftArrowEnd);
    }

    /* Draws a black arrow onto the top view between the x-coords provided, at the given height, with a label
     * at the leftmost point of the arrow*/
    private void drawDistanceArrow(Graphics g, int x1, int x2, int y, String label) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        drawDistanceArrow(g, x1, x2, y);

        g.setFont(new Font(null, Font.PLAIN, arrowFontSize));
        if (x1 < 0) {
            g.drawString(label, 0, y + arrowFontSize + 5);
        } else {
            g.drawString(label, x1, y + arrowFontSize + 5);
        }
    }

    /* Draws the designator for the leftmost threshold (of the view) onto the runway strip*/
    private void drawLeftThreshold(Graphics g, String threshold) {
        //this.getHeight()/20 is a magic number
        g.drawString(threshold, this.getWidth() / 6, this.getHeight() / 2 - this.getHeight() / 8 + this.getHeight() / 20);
    }

    /* Draws the designator for the rightmost threshold (of the view) onto the runway strip*/
    private void drawRightThreshold(Graphics g, int fontSize, String threshold) {
        //this.getHeight()/20 is a magic number
        g.drawString(threshold, (5 * this.getWidth()) / 6 - fontSize, this.getHeight() / 2 - this.getHeight() / 8 + this.getHeight() / 20);
    }

    /* Draws the obstacle onto the runway strip, relative to the left threshold of the view*/
    private void drawObstacleLTHS(Graphics g, Obstacle obs, int LTHSStart) {
        int obsPos = LTHSStart + (int) (obs.getDistanceLeftTHS() / ratio);
        drawObstacle(g, obsPos);
    }

    /* Draws the obstacle onto the runway strip, relative to the right threshold of the view*/
    private void drawObstacleRTHS(Graphics g, Obstacle obs, int RTHSStart) {
        int obsPos = RTHSStart - (int) (obs.getDistanceRightTHS() / ratio);
        drawObstacle(g, obsPos);
    }

    /* Draws a magenta circle on the centreline of the runway at the given x position, to represent the obstacle
     * on the runway currently*/
    private void drawObstacle(Graphics g, int xpos) {
        final int OBS_RADIUS = 8;
        final int STROKE_WIDTH = 2;

        int height = (getHeight() / 2) - OBS_RADIUS;

        g.setColor(Color.black);
        g.fillOval(xpos - OBS_RADIUS - STROKE_WIDTH, height - STROKE_WIDTH,
                2 * OBS_RADIUS + 2 * STROKE_WIDTH, 2 * OBS_RADIUS + 2 * STROKE_WIDTH);

        g.setColor(Color.MAGENTA);
        g.fillOval(xpos - OBS_RADIUS, height, 2 * OBS_RADIUS, 2 * OBS_RADIUS);

    }

    /* Draws a small arrow in the bottom right of the top view, in black, to indicate the direction of travel on the runway
     * strip*/
    private void drawDirection(Graphics g, int arrowDirection) {
        g.setColor(distanceArrowColour);

        int fontHeight = getHeight() * 9 / 10;
        int arrowLength = 100;

        g.setFont(new Font(null, Font.PLAIN, 12));
        g.drawString("Direction", getWidth() - paddingSide - arrowLength, fontHeight - 5);
        g.drawLine(getWidth() - paddingSide, fontHeight, getWidth() - paddingSide - arrowLength, fontHeight);

        if (arrowDirection == 0) {
            g.drawLine(getWidth() - paddingSide - arrowLength, fontHeight, getWidth() - paddingSide - arrowLength + 15, fontHeight + 10);
            g.drawLine(getWidth() - paddingSide - arrowLength, fontHeight, getWidth() - paddingSide - arrowLength + 15, fontHeight - 10);

        } else {
            g.drawLine(getWidth() - paddingSide, fontHeight, getWidth() - paddingSide - 15, fontHeight + 10);
            g.drawLine(getWidth() - paddingSide, fontHeight, getWidth() - paddingSide - 15, fontHeight - 10);
        }
    }

    /* Sets the scenario to be Landing Over the obstacle or Taking Off towards the obstacle*/
    public void setLOorTOT() {
        this.LOorTOT = true;
        this.LT = false;
        this.TOA = false;
    }

    /* Sets the scenario to be Landing towards the obstacle*/
    public void setLT() {
        this.LT = true;
        this.LOorTOT = false;
        this.TOA = false;
    }

    /* Sets the scenario to be taking off away from the obstacle*/
    public void setTOA() {
        this.TOA = true;
        this.LT = false;
        this.LOorTOT = false;
    }

    /*Sets the designator for the currently selected threshold*/
    public void setCurrentThreshold(String currentThreshold) {
        this.currentThreshold = currentThreshold;
    }

    /*Gets the designator for the currently displayed threshold*/
    public String getCurrentThreshold() {
        return currentThreshold;
    }

    /*Sets the direction of travel - 1 = left to right
     *                              - 0 = right to left*/
    public void setDirection(int diection) {
        this.arrowDirection = diection;
    }

    /* Sets a flag to indicate if we are taking off or landing*/
    public void setTakeoff(boolean b) {
        isTakeoff = b;
    }

    /* Sets a flag for rotating the runway to runway heading*/
    public void rotateImage(Boolean rotate) {
        this.rotate = rotate;
        repaint();
    }

    @Override
    /* Paints the top view to the panel with the appropriate arrows, if necessary*/
    public void paint(Graphics g) {
        runwayView = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics i = runwayView.createGraphics();

        drawRunway(i);

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform aT = new AffineTransform();

        // we apply the affine transformation
        aT.scale(zoomRatio,zoomRatio);
        aT.translate(currOffsetX/zoomRatio, currOffsetY/zoomRatio);

        if (runwayView != null) {
            if (rotate) {
                String orient = currentThreshold.substring(0, 2);
                int angle = Integer.parseInt(orient);
                if (angle >= 19) {
                    angle = angle - 18;
                }
                aT.rotate(Math.toRadians(angle*10 - 90), this.getWidth() / 2, this.getHeight() / 2);
            }
        }

        //we draw the image with the affine transform in mind
        g2d.drawImage(runwayView,aT,this);
    }

    private void addListeners() {

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                //we get the coordinates of the initial press and save them as a field
                initialX = e.getX();
                initialY = e.getY();
                setCursor(new Cursor(13));


            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                //We get the difference of the initial coordinates and the current drag coordinates.
                int diffX = e.getX() - initialX;
                int diffY = e.getY() - initialY;

                //We add the difference to the current Offset so that Affine transform can translate it to every pixel
                currOffsetX = currOffsetX + diffX;
                currOffsetY = currOffsetY + diffY;

                //We update the initial coordinates while dragging
                initialX = initialX + diffX;
                initialY = initialY + diffY;

                repaint();
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                    zoomRatio+=1*(.1 * -(double)e.getWheelRotation());
                }

                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

    }
}
