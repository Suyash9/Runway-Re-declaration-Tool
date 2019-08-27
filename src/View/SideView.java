package View;

import Controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SideView extends JPanel {

    private BufferedImage runwayView = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
    private MainFrame mainFrame;
    private Integer runwayLength;
    private MainController controller;

    private int padding = 30;

    private int middleY;
    private int heightOfHalfLine;

    private boolean LOorTOT = false;
    private boolean LT = false;
    private boolean TOA = true;
    private int initialX,initialY;
    private int currOffsetX,currOffsetY;
    private double zoomRatio = 1;

    private String currentThreshold;

    private String label = "TORA,TODA,ASDA";

    int arrowDirection = 1;

    public SideView(MainFrame frame, MainController controller){
        this.mainFrame = frame;
        this.controller = controller;
        this.setBackground(Color.black);
        this.runwayLength = controller.getRunway().getRunwayStrip();
        addListeners();

    }

    public BufferedImage getView() {
        return runwayView;
    }

    private void drawRunway(Graphics g){
        int runwayStartX = this.padding;
        int runwayY = this.getHeight()/2;
        int runwayEndX = this.getWidth()-this.padding;

        this.middleY = this.getHeight()/2;
        this.heightOfHalfLine = this.getHeight()/15;
        g.setFont(new Font("Arial", Font.PLAIN,12));

        drawBaseRunway(g, runwayStartX, runwayY, runwayEndX);

        float ratio = ((float)this.runwayLength/(this.getWidth()- 2*padding));

        if(this.controller.getRunway().getObstacle() != null){
            int slope = (int) (this.controller.getObstacle().getHeight()*this.controller.getRunway().getSlopeRatio()/ratio);
            int RESA = (int) (this.controller.getRunway().getRESA()/ratio);
            int stripEnd = (int) (this.controller.getRunway().getStripEnd()/ratio);
            int blastProtection = (int) (this.controller.getRunway().getBlastProtection()/ratio);
            int startBlastProtectionX = 0;
            int endBlastProtectionX = 0;
            int startSlopeX = 0;
            int endSlopeX = 0;

            int startResaX;
            int endResaX;

            int startStripEndX;
            int endStripEndX;


            if(this.controller.getObstacle().getDistanceLeftTHS() < this.controller.getObstacle().getDistanceRightTHS()){

                int DLTH = (int) (this.controller.getObstacle().getDistanceLeftTHS()/ratio);

                int startDLTHX = runwayStartX;
                int endDLTHX = startDLTHX + DLTH;

                if(TOA){
                    startBlastProtectionX = endDLTHX;
                    endBlastProtectionX = startBlastProtectionX + blastProtection;
                }

                if(LOorTOT){
                    startSlopeX = endDLTHX;
                    endSlopeX = startSlopeX + slope;
                }

                startResaX = endDLTHX;
                endResaX = startResaX + RESA;
                if(LOorTOT){
                    startResaX = endSlopeX - RESA;
                    endResaX = endSlopeX;
                }

                startStripEndX = endResaX;
                endStripEndX = startStripEndX + stripEnd;

                int startLDAX = endStripEndX;
                int endLDAX = this.getWidth() - this.padding;

                int startToraX = endBlastProtectionX;
                int endToraX = this.getWidth() - this.padding;

                if(LOorTOT){
                    drawLandingTowardsOrTakeOffTowards(g, slope, label, arrowDirection, startDLTHX, endDLTHX, startSlopeX, endSlopeX, startResaX, endResaX, startStripEndX, endStripEndX, startLDAX, endLDAX);
                }else if(LT){
                    drawLandingTowards(g, slope, label, arrowDirection, startDLTHX, endDLTHX, startResaX, endResaX, startStripEndX, endStripEndX, startLDAX, endLDAX);
                }else{
                    drawTakeOffAway(g,slope,startDLTHX,endDLTHX, startBlastProtectionX,endBlastProtectionX,startToraX,endToraX, label,arrowDirection);
                }
            }else{
                int DRTH = (int) (this.controller.getObstacle().getDistanceRightTHS()/ratio);

                int endDRTHX = runwayEndX;
                int startDRTHX = runwayEndX - DRTH;

                if(TOA){
                    startBlastProtectionX = startDRTHX - blastProtection;
                    endBlastProtectionX = startDRTHX;
                }

                int TORAorLDAStartX = runwayStartX;
                int TORAorLDAEndX = startBlastProtectionX;

                startSlopeX = startDRTHX - slope;
                endSlopeX = startDRTHX;

                startResaX = startSlopeX;
                endResaX = startSlopeX + RESA;

                startStripEndX = startResaX - stripEnd;
                endStripEndX = startResaX;

                if(LOorTOT){

                    TORAorLDAEndX = startStripEndX;
                }else if(TOA){
                    drawTakeOffAwayReverse(g,slope,startDRTHX,endDRTHX,startBlastProtectionX,endBlastProtectionX,TORAorLDAStartX,TORAorLDAEndX,label,arrowDirection);
                } else{
                    startResaX = startDRTHX - RESA;
                    endResaX = startDRTHX;

                    startStripEndX = startResaX - stripEnd;
                    endStripEndX = startResaX;

                    TORAorLDAEndX = startStripEndX;

                    drawLandingTowardsReverse(g,slope,label,arrowDirection,startDRTHX,endDRTHX,startResaX,endResaX,startStripEndX,endStripEndX,TORAorLDAStartX,TORAorLDAEndX);
                }

                if(LOorTOT){
                    drawLandingTowardsOrTakeOffTowardsReverse(g,slope,label,arrowDirection,startDRTHX,endDRTHX,startSlopeX,endSlopeX,startResaX,endResaX,startStripEndX,endStripEndX,TORAorLDAStartX,TORAorLDAEndX);
                }
            }
        }

    }



    private void drawTakeOffAwayReverse(Graphics g, int height, int startDRTHX, int endDRTHX, int startBlastProtectionX, int endBlastProtectionX, int startToraX, int endToraX, String lastLabel, int arrowDirection){
        drawSection(g,startToraX,endToraX,middleY,middleY,lastLabel,heightOfHalfLine);
        drawSection(g,startBlastProtectionX,endBlastProtectionX,middleY,middleY,"B.P.",height/5);
        drawObstacleLabel(g,endBlastProtectionX,middleY,height/5,"Obstacle");
        drawSection(g,startDRTHX,endDRTHX,middleY,middleY,"DRTH",heightOfHalfLine);
        drawDirection(g,arrowDirection);
    }

    private void drawTakeOffAway(Graphics g, int height, int startDLTHX, int endDLTHX, int startBlastProtectionX, int endBlastProtectionX, int startToraX, int endToraX, String lastLabel, int arrowDirection) {
        drawSection(g,startDLTHX,endDLTHX,middleY,middleY,"DLTH",height/5);
        drawObstacleLabel(g,endDLTHX,middleY,height/5,"Obstacle");
        drawSection(g,startBlastProtectionX,endBlastProtectionX,middleY,middleY,"B.P.",heightOfHalfLine);
        drawSection(g,startToraX,endToraX,middleY,middleY,lastLabel,heightOfHalfLine);
        drawDirection(g,arrowDirection);
    }

    private void drawLandingTowardsReverse(Graphics g, int slope, String lastLabel, int arrowDirection, int startDRTHX, int endDRTHX, int startResaX, int endResaX, int startStripEndX, int endStripEndX, int startLDAX, int endLDAX) {
        drawSection(g,startLDAX,endLDAX,middleY,middleY,lastLabel,heightOfHalfLine);
        drawSection(g,startStripEndX,endStripEndX,middleY,middleY,"S.E.",heightOfHalfLine);
        drawSection(g,startResaX,endResaX,middleY,middleY,"Resa",slope/5);
        drawObstacleLabel(g,endResaX,middleY,slope/5,"Obstacle");
        drawSection(g,startDRTHX,endDRTHX,middleY,middleY,"DRTH",heightOfHalfLine);
        drawDirection(g,arrowDirection);

    }

    private void drawLandingTowards(Graphics g, int slope, String lastLabel, int arrowDirection, int startDLTHX, int endDLTHX, int startResaX, int endResaX, int startStripEndX, int endStripEndX, int startLDAX, int endLDAX) {
        drawSection(g,startDLTHX,endDLTHX,middleY,middleY,"DLTH",slope/5);
        drawObstacleLabel(g,endDLTHX,middleY,slope/5,"Obstacle");
        drawSection(g,startResaX,endResaX,middleY,middleY,"Resa",heightOfHalfLine);
        drawSection(g,startStripEndX,endStripEndX,middleY,middleY,"S.E.",heightOfHalfLine);
        drawSection(g,startLDAX,endLDAX,middleY,middleY,lastLabel,heightOfHalfLine);
        drawDirection(g,arrowDirection);
    }

    private void drawLandingTowardsOrTakeOffTowardsReverse(Graphics g, int slope, String lastLabel, int arrowDirection, int startDRTHX, int endDRTHX, int startSlopeX, int endSlopeX, int startResaX, int endResaX, int startStripEndX, int endStripEndX, int startLDAX, int endLDAX){
        drawSection(g,startLDAX,endLDAX,middleY,middleY,lastLabel,heightOfHalfLine);
        drawSection(g,startStripEndX,endStripEndX,middleY,middleY,"S.E",heightOfHalfLine);
        drawSection(g,startSlopeX,endSlopeX,middleY,middleY,"Slope",slope/5);
        drawObstacleLabel(g,endSlopeX,middleY,slope/5,"Obstacle");
        drawResa(g,startResaX,endResaX,middleY,"Resa",heightOfHalfLine);
        drawSlope(g,endSlopeX,startSlopeX,middleY - slope/5,middleY);
        drawSection(g,startDRTHX,endDRTHX,middleY,middleY,"DRTH",heightOfHalfLine);
        drawDirection(g,arrowDirection);

    }

    private void drawLandingTowardsOrTakeOffTowards(Graphics g, int slope, String lastLabel, int arrowDirection, int startDLTHX, int endDLTHX, int startSlopeX, int endSlopeX, int startResaX, int endResaX, int startStripEndX, int endStripEndX, int startLDAX, int endLDAX) {
        drawSection(g,startDLTHX,endDLTHX,middleY,middleY,"DLTH",slope/5);
        drawObstacleLabel(g,endDLTHX,middleY,slope/5,"Obstacle");
        drawSection(g,startSlopeX,endSlopeX,middleY,middleY,"Slope",heightOfHalfLine);
        drawSlope(g,endDLTHX,endResaX,middleY - slope/5,middleY);
        drawResa(g,startResaX,endResaX,middleY,"Resa",heightOfHalfLine);
        drawSection(g,startStripEndX,endStripEndX,middleY,middleY,"S.E",heightOfHalfLine);
        drawSection(g,startLDAX,endLDAX,middleY,middleY,lastLabel,heightOfHalfLine);
        drawDirection(g,arrowDirection);
    }

    private void drawDirection(Graphics g, int arrowDirection){
        g.drawString("Direction", this.getWidth()-this.padding-this.getWidth()/4 + 25,this.getHeight()*7/8 - 5);
        g.drawLine(this.getWidth()-this.padding,this.getHeight() *7/8,this.getWidth() - this.padding - this.getWidth()/4,this.getHeight() *7/8);

        if(arrowDirection == 0){
            g.drawLine(this.getWidth()-this.padding-this.getWidth()/4,this.getHeight()*7/8,this.getWidth()-this.padding - this.getWidth()/4 + 15,this.getHeight()*7/8 + 10);
            g.drawLine(this.getWidth()-this.padding-this.getWidth()/4,this.getHeight()*7/8,this.getWidth()-this.padding - this.getWidth()/4 + 15,this.getHeight()*7/8 - 10);

        }else{
            g.drawLine(this.getWidth()-this.padding,this.getHeight()*7/8,this.getWidth()-this.padding - 15,this.getHeight()*7/8 +10);
            g.drawLine(this.getWidth()-this.padding,this.getHeight()*7/8,this.getWidth()-this.padding - 15,this.getHeight()*7/8 -10);
        }
    }

    private void drawSlope(Graphics g, int endDLTHX, int endResaX, int startY, int endY){
        g.drawLine(endDLTHX,startY,endResaX,endY);
    }

    private void drawObstacleLabel(Graphics g, int endDLTHX, int middleY, int height, String name){
        g.drawString(name,endDLTHX - 25,middleY-height-5);
    }

    private void drawResa(Graphics g, int startResaX, int endResaX, int middleY, String label, int heightOfHalfLine) {
        drawSectionEndIndicator(g,startResaX,middleY,middleY,heightOfHalfLine);
        g.drawLine(startResaX,middleY-heightOfHalfLine,endResaX,middleY-heightOfHalfLine);
        g.drawString(label,startResaX,middleY-heightOfHalfLine - 5);
        g.drawLine(endResaX,middleY - heightOfHalfLine,endResaX,middleY);
    }

    private void drawSection(Graphics g, int startX, int endX, int startY, int endY, String label,int height){
        drawSectionEndIndicator(g,endX,endY + heightOfHalfLine,endY,height);
        drawSectionLabel(g,startX,endX,startY,label);
    }

    private void drawSectionLabel(Graphics g, int startX, int endX,int startY, String label) {
        //connect indicators
        g.drawLine(startX,startY + heightOfHalfLine,endX,startY + heightOfHalfLine);

        //draw connector to the label
        g.drawLine((startX + endX)/2,startY + heightOfHalfLine,(startX + endX)/2,startY + 2*heightOfHalfLine);

        //draw label
        g.drawString(label,(startX + endX)/2 - 4*label.length(), startY + 3*heightOfHalfLine);
    }

    private void drawSectionEndIndicator(Graphics g, int endX,int startY, int endY, int height){
        g.drawLine(endX,startY,endX, endY - height);
    }

    private void drawBaseRunway(Graphics g, int runwayStartX, int runwayY, int runwayEndX) {
        //draw thresholds
        g.drawString(this.controller.getRunway().getLowerThreshold(),0,middleY);
        g.drawString(this.controller.getRunway().getHigherThreshold(),this.getWidth() - padding + padding/6,middleY); //don't ask

        //left threshold
        g.drawLine(runwayStartX,middleY - heightOfHalfLine,runwayStartX,middleY + heightOfHalfLine);

        //draw runway
        g.drawLine(runwayStartX,runwayY, runwayEndX,runwayY);

        //right threshold
        g.drawLine(runwayEndX,middleY - heightOfHalfLine,runwayEndX,middleY + heightOfHalfLine);
    }

    public void setLOorTOT() {
        this.LOorTOT = true;
        this.LT = false;
        this.TOA = false;
    }

    public void setLT() {
        this.LT = true;
        this.LOorTOT = false;
        this.TOA = false;
    }

    public void setTOA() {
        this.TOA = true;
        this.LT = false;
        this.LOorTOT = false;
    }

    public void setCurrentThreshold(String currentThreshold) {
        this.currentThreshold = currentThreshold;
    }

    public String getCurrentThreshold() {
        return currentThreshold;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDirection(int diection){
        this.arrowDirection = diection;
    }

//    public void paintComponent(Graphics g){
//
//        runwayView = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_3BYTE_BGR);
//        Graphics i = runwayView.createGraphics();
//
//        //Background set???????
//
//        drawRunway(i);
//
//        //g.drawImage(runwayView, 0, 0, null);
//
//    }

    public void paint(Graphics g) {

        runwayView = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        Graphics i = runwayView.createGraphics();

        //Background set???????
        drawRunway(i);

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform aT = new AffineTransform();

        // we apply the affine transformation
        aT.scale(zoomRatio,zoomRatio);
        aT.translate(currOffsetX/zoomRatio, currOffsetY/zoomRatio);

        //we draw the image with the affine transform in mind
        g2d.drawImage(runwayView,aT,this);
    }

    private void addListeners(){

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
