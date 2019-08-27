package Model;

public class Obstacle {
    private String name;
    private Integer height, width, length;
    private Integer distanceFromCenterline, distanceLeftTHS, distanceRightTHS;

    public Obstacle(String name, Integer height, Integer width, Integer length, Integer distanceFromCenterline, Integer distanceLeftTHS, Integer distanceRightTHS) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        this.distanceFromCenterline = distanceFromCenterline;
        this.distanceLeftTHS = distanceLeftTHS;
        this.distanceRightTHS = distanceRightTHS;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getDistanceFromCenterline() {
        return distanceFromCenterline;
    }

    public Integer getDistanceLeftTHS() {
        return distanceLeftTHS;
    }

    public Integer getDistanceRightTHS() {
        return distanceRightTHS;
    }
}
