package Model;

public class Runway {

    private String designator;

    private Integer TORA, TODA, ASDA, LDA, displacedThreshold = 0, runwayStrip;

    private Integer STOPWAY = 0;
    private Integer CLEARWAY = 0;
    private Integer RESA = 240;
    private Integer stripEnd = 60;
    private Integer ALS = 50;
    private Integer TOCS = 50;
    private Integer blastProtection = 300;

    private Integer updatedTORA = 0;
    private Integer updatedTODA = 0;
    private Integer updatedASDA = 0;
    private Integer updatedLDA = 0;

    private Integer direction = 0;

    private Obstacle obstacle;

    private String calcBreakdown = "";

    private String currentThreshold;
    private String oppositeThreshold;

    public Runway(String designator, Integer TORA, Integer TODA, Integer ASDA, Integer LDA,
                  Integer displacedThreshold) {
        this.designator = designator;
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA = LDA;
        this.displacedThreshold = displacedThreshold;
        this.runwayStrip = 2*STOPWAY + TORA;

        if(designator.startsWith("0")){
            currentThreshold = designator.substring(1);
        }else{
            currentThreshold = designator;
        }

        this.oppositeThreshold = this.getOpposite(designator);
    }

    public Runway( String designator, Integer TORA, Integer TODA, Integer ASDA, Integer LDA,
                   Integer displacedThreshold, Integer STOPWAY, Integer CLEARWAY, Integer RESA,
                   Integer stripEnd) {
        this.designator = designator;
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA = LDA;
        this.displacedThreshold = displacedThreshold;
        this.runwayStrip = 2*STOPWAY + TORA;

        this.STOPWAY = STOPWAY;
        this.CLEARWAY = CLEARWAY;
        this.RESA = RESA;
        this.stripEnd = stripEnd;

        if(designator.startsWith("0")){
            currentThreshold = designator.substring(1);
        }else{
            currentThreshold = designator;
        }

        this.oppositeThreshold = this.getOpposite(designator);
    }

    public Runway( String designator, Integer TORA, Integer TODA, Integer ASDA, Integer LDA,
                   Integer displacedThreshold, Integer STOPWAY, Integer CLEARWAY, Integer RESA,
                   Integer stripEnd, Integer ALS, Integer TOCS) {
        this.designator = designator;
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA = LDA;
        this.displacedThreshold = displacedThreshold;
        this.STOPWAY = STOPWAY;
        this.CLEARWAY = CLEARWAY;
        this.RESA = RESA;
        this.stripEnd = stripEnd;
        this.ALS = ALS;
        this.TOCS = TOCS;
        this.runwayStrip = 2*STOPWAY + TORA;

        if(designator.startsWith("0")){
            currentThreshold = designator.substring(1);
        }else{
            currentThreshold = designator;
        }

        this.oppositeThreshold = this.getOpposite(designator);
    }

    public void calculate(){
        Integer runwayNo = 0;

        if (designator.matches(".*[a-zA-Z]+.*")){
            runwayNo = Integer.parseInt(designator.replaceAll("([a-zA-Z])", ""));
        } else{
            runwayNo = Integer.parseInt(designator);
        }

        calcBreakdown = "";

        if (obstacle == null){
            // No obstacle
            updatedTORA = TORA;
            updatedTODA = TODA;
            updatedASDA = ASDA;
            updatedLDA = LDA;

            calcBreakdown += "No obstacles: TORA, TODA, ASDA and LDA remain unchanged\n";
            calcBreakdown += "TORA = " + updatedTORA + "\n";
            calcBreakdown += "TODA = " + updatedTODA + "\n";
            calcBreakdown += "ASDA = " + updatedASDA + "\n";
            calcBreakdown += "LDA  = " + updatedLDA;
        }else if (obstacle.getDistanceRightTHS() < obstacle.getDistanceLeftTHS()){
            // Runway R is used for Take Off Away and Landing Over
            // Runway L is used for Take off Towards and Landing Towards

            if (runwayNo >= 19){
                // Take Off Away

                updatedTORA = TORA - obstacle.getDistanceRightTHS() - blastProtection - displacedThreshold;
                calcBreakdown += "TORA = TORA - obstacle distance from threshold - blast protection\n";
                calcBreakdown += "\t = " + TORA + " - " + obstacle.getDistanceRightTHS() + " - " + blastProtection + "\n";
                calcBreakdown += "\t = " + updatedTORA + "\n";
                calcBreakdown += "\n";

                if(CLEARWAY>0){
                    updatedTODA = updatedTORA + CLEARWAY;
                    calcBreakdown += "TODA = recalculated TORA + Clearway\n";
                    calcBreakdown += "\t = " + updatedTORA + " + " + CLEARWAY + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";
                }else{
                    updatedTODA = TODA - obstacle.getDistanceRightTHS() - blastProtection - displacedThreshold;
                    calcBreakdown += "TODA = TODA - obstacle distance from threshold - blast protection\n";
                    calcBreakdown += "\t = " + TODA  + " - " + obstacle.getDistanceRightTHS() + " - " + blastProtection + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";
                }

                if(STOPWAY>0){
                    updatedASDA = updatedTORA + STOPWAY;
                    calcBreakdown += "ASDA = recalculated TORA + Stopway\n";
                    calcBreakdown += "\t = " + updatedTORA + " + " + STOPWAY + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                }else {
                    updatedASDA = ASDA - obstacle.getDistanceRightTHS() - blastProtection - displacedThreshold;
                    calcBreakdown += "ASDA = ASDA - obstacle distance from threshold - blast protection\n";
                    calcBreakdown += "\t = " + ASDA + " - " + obstacle.getDistanceRightTHS() + " - " + blastProtection + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                }

                // Landing Over
                Integer slopeCalc = (obstacle.getHeight()*ALS);

                if((slopeCalc+stripEnd)>blastProtection) {
                    calcBreakdown += "LDA  = original LDA - obstacle distance from threshold - slope calculation - strip end\n";
                    updatedLDA = LDA - obstacle.getDistanceRightTHS() - slopeCalc - stripEnd;
                    calcBreakdown += "\t = " + LDA + " - " + obstacle.getDistanceRightTHS() + " - " + slopeCalc + " - " + stripEnd + "\n";
                }else {
                    calcBreakdown += "LDA  = original LDA - obstacle distance from threshold - blast protection\n";
                    updatedLDA = LDA - obstacle.getDistanceRightTHS() - blastProtection;
                    calcBreakdown += "\t = " + LDA + " - " + obstacle.getDistanceRightTHS() + " - " + blastProtection + "\n";
                }
                calcBreakdown += "\t = " + updatedLDA;
                calcBreakdown += "\n";
            }

            if (runwayNo < 19){
                // Take Off Towards

                Integer slopeCalc2 = (obstacle.getHeight() * TOCS);

                if (slopeCalc2>RESA) {
                    updatedASDA = updatedTODA = updatedTORA = obstacle.getDistanceLeftTHS() + displacedThreshold - slopeCalc2 - stripEnd;

                    calcBreakdown += "TORA = obstacle distance from threshold + displaced threshold - slope calculation - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceLeftTHS() + " + " + displacedThreshold + " - " + slopeCalc2 + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTORA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "TODA = obstacle distance from threshold + displaced threshold - slope calculation - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceLeftTHS() + " + " + displacedThreshold + " - " + slopeCalc2 + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "ASDA = obstacle distance from threshold + displaced threshold - slope calculation - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceLeftTHS() + " + " + displacedThreshold + " - " + slopeCalc2 + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                }else{
                    updatedASDA = updatedTODA = updatedTORA = obstacle.getDistanceLeftTHS() + displacedThreshold - RESA - stripEnd;

                    calcBreakdown += "TORA = obstacle distance from threshold + displaced threshold - RESA - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceLeftTHS() + " + " + displacedThreshold + " - " + RESA + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTORA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "TODA = obstacle distance from threshold + displaced threshold - RESA - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceLeftTHS() + " + " + displacedThreshold + " - " + RESA + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "ASDA = obstacle distance from threshold + displaced threshold - RESA - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceLeftTHS() + " + " + displacedThreshold + " - " + RESA + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                }
                // Landing Towards

                calcBreakdown += "LDA  = obstacle distance from threshold - RESA - strip end\n";
                updatedLDA = obstacle.getDistanceLeftTHS() - RESA - stripEnd;
                calcBreakdown += "\t = " + obstacle.getDistanceLeftTHS() + " - " + RESA + " - " + stripEnd + "\n";
                calcBreakdown += "\t = " + updatedLDA;
            }

        }else if (obstacle.getDistanceLeftTHS() < obstacle.getDistanceRightTHS()) {

            // Runway L is used for Take Off Away and Landing Over
            // Runway R is used for Take off Towards and Landing Towards

            if (runwayNo < 19) {
                // Take Off Away

                updatedTORA = TORA - obstacle.getDistanceLeftTHS() - blastProtection - displacedThreshold;
                calcBreakdown += "TORA = TORA - obstacle distance from threshold - blast protection\n";
                calcBreakdown += "\t = " + TORA + " - " + obstacle.getDistanceLeftTHS() + " - " + blastProtection + "\n";
                calcBreakdown += "\t = " + updatedTORA + "\n";
                calcBreakdown += "\n";

                if (CLEARWAY > 0) {
                    updatedTODA = updatedTORA + CLEARWAY;
                    calcBreakdown += "TODA = recalculated TORA + Clearway\n";
                    calcBreakdown += "\t = " + updatedTORA + " + " + CLEARWAY + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";
                } else {
                    updatedTODA = TODA - obstacle.getDistanceLeftTHS() - blastProtection - displacedThreshold;
                    calcBreakdown += "TODA = TODA - obstacle distance from threshold - blast protection\n";
                    calcBreakdown += "\t = " + TODA + " - " + obstacle.getDistanceLeftTHS() + " - " + blastProtection + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";
                }

                if (STOPWAY > 0) {
                    updatedASDA = updatedTORA + STOPWAY;
                    calcBreakdown += "ASDA = recalculated TORA + Stopway\n";
                    calcBreakdown += "\t = " + updatedTORA + " + " + STOPWAY + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                } else {
                    updatedASDA = ASDA - obstacle.getDistanceLeftTHS() - blastProtection - displacedThreshold;
                    calcBreakdown += "ASDA = ASDA - obstacle distance from threshold - blast protection\n";
                    calcBreakdown += "\t = " + ASDA + " - " + obstacle.getDistanceLeftTHS() + " - " + blastProtection + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                }

                // Landing over

                Integer slopeCalc = (obstacle.getHeight() * ALS);

                if ((slopeCalc + stripEnd) > blastProtection) {
                    calcBreakdown += "LDA  = original LDA - obstacle distance from threshold - slope calculation - strip end\n";
                    updatedLDA = LDA - obstacle.getDistanceLeftTHS() - slopeCalc - stripEnd;
                    calcBreakdown += "\t = " + LDA + " - " + obstacle.getDistanceLeftTHS() + " - " + slopeCalc + " - " + stripEnd + "\n";
                } else {
                    calcBreakdown += "LDA  = original LDA - obstacle distance from threshold - blast protection\n";
                    updatedLDA = LDA - obstacle.getDistanceLeftTHS() - blastProtection;
                    calcBreakdown += "\t = " + LDA + " - " + obstacle.getDistanceLeftTHS() + " - " + blastProtection + "\n";
                }
                calcBreakdown += "\t = " + updatedLDA;
                calcBreakdown += "\n";
            }

            if (runwayNo >= 19) {
                // Take Off Towards
                Integer slopeCalc2 = (obstacle.getHeight() * TOCS);

                if (slopeCalc2>RESA) {
                    updatedASDA = updatedTODA = updatedTORA = obstacle.getDistanceRightTHS() + displacedThreshold - slopeCalc2 - stripEnd;

                    calcBreakdown += "TORA = obstacle distance from threshold + displaced threshold - slope calculation - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceRightTHS() + " + " + displacedThreshold + " - " + slopeCalc2 + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTORA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "TODA = obstacle distance from threshold + displaced threshold - slope calculation - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceRightTHS() + " + " + displacedThreshold + " - " + slopeCalc2 + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "ASDA = obstacle distance from threshold + displaced threshold - slope calculation - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceRightTHS() + " + " + displacedThreshold + " - " + slopeCalc2 + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                }else{
                    updatedASDA = updatedTODA = updatedTORA = obstacle.getDistanceRightTHS() + displacedThreshold - RESA - stripEnd;

                    calcBreakdown += "TORA = obstacle distance from threshold + displaced threshold - RESA - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceRightTHS() + " + " + displacedThreshold + " - " + RESA + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTORA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "TODA = obstacle distance from threshold + displaced threshold - RESA - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceRightTHS() + " + " + displacedThreshold + " - " + RESA + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedTODA + "\n";
                    calcBreakdown += "\n";

                    calcBreakdown += "ASDA = obstacle distance from threshold + displaced threshold - RESA - strip end\n";
                    calcBreakdown += "\t = " + obstacle.getDistanceRightTHS() + " + " + displacedThreshold + " - " + RESA + " - " + stripEnd + "\n";
                    calcBreakdown += "\t = " + updatedASDA + "\n";
                    calcBreakdown += "\n";
                }

                // Landing Towards

                calcBreakdown += "LDA  = obstacle distance from threshold - RESA - strip end\n";
                updatedLDA = obstacle.getDistanceRightTHS() - RESA - stripEnd;
                calcBreakdown += "\t = " + obstacle.getDistanceRightTHS() + " - " + RESA + " - " + stripEnd + "\n";
                calcBreakdown += "\t = " + updatedLDA;
            }
        }
    }

    public String getLowerThreshold(){
        return Integer.parseInt(this.currentThreshold.substring(0,this.currentThreshold.length()-1)) < Integer.parseInt(this.oppositeThreshold.substring(0,this.oppositeThreshold.length()-1)) ? this.designator : this.oppositeThreshold ;
    }

    public String getHigherThreshold(){
        return Integer.parseInt(this.currentThreshold.substring(0,this.currentThreshold.length()-1)) < Integer.parseInt(this.oppositeThreshold.substring(0,this.oppositeThreshold.length()-1))  ? this.oppositeThreshold : this.designator;

    }

    // method to get the opposite runway
    private String getOpposite(String designator) {
        int number = Integer.parseInt(this.currentThreshold.substring(0,this.currentThreshold.length()-1));

        int opposite;

        if(number > 18){
            opposite = number - 18;
        }else{
            opposite = number + 18;
        }

        String oppositeDesignator;

        if(designator.charAt(designator.length()-1) == 'L'){
            oppositeDesignator = "R";
        }else{
            oppositeDesignator = "L";
        }

        if(opposite < 10){
            return "0" + opposite + oppositeDesignator;
        }
        return opposite + oppositeDesignator;
    }

    public String getDesignator() {
        return designator;
    }

    public Integer getTORA() {
        return TORA;
    }

    public Integer getTODA() {
        return TODA;
    }

    public Integer getASDA() {
        return ASDA;
    }

    public Integer getLDA() {
        return LDA;
    }

    public void setBlastProtection(Integer blastProtection) {
        this.blastProtection = blastProtection;
    }

    public void setSlopeRatio(Integer slopeRatio) {
        this.ALS = slopeRatio;
        this.TOCS = slopeRatio;
    }

    public int getSlopeRatio() {
        return ALS;
    }

    public Integer getUpdatedTORA() {
        return updatedTORA;
    }

    public Integer getUpdatedTODA() {
        return updatedTODA;
    }

    public Integer getUpdatedASDA() {
        return updatedASDA;
    }

    public Integer getUpdatedLDA() {
        return updatedLDA;
    }

    public Integer getDisplacedThreshold() {
        return displacedThreshold;
    }

    public Integer getSTOPWAY() {
        return STOPWAY;
    }

    public Integer getCLEARWAY() {
        return CLEARWAY;
    }

    public Integer getRESA() {
        return RESA;
    }

    public Integer getStripEnd() {
        return stripEnd;
    }

    public Integer getALS() {
        return ALS;
    }

    public Integer getTOCS() {
        return TOCS;
    }

    public Integer getRunwayStrip() {
        return runwayStrip;
    }

    public Integer getBlastProtection() {
        return blastProtection;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getCalcBreakdown() { return calcBreakdown;}
}
