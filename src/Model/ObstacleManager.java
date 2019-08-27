package Model;

import java.util.ArrayList;
import java.util.List;

public final class ObstacleManager {
    private static final ObstacleManager INSTANCE = new ObstacleManager();


    private List<Obstacle> predefinedObstacles;

    public ObstacleManager(){
        this.predefinedObstacles = new ArrayList<>();

        //Airplanes as obstacles
        //predefinedObstacles.add(new Obstacle("Boeing 737-100", 12, 28, 29, 0,-50, 3646));
        predefinedObstacles.add(new Obstacle("test", 25, 35, 37, 20,2853, 500));

        predefinedObstacles.add(new Obstacle("Airbus A320", 25, 35, 37, 20,500, 2853));
        predefinedObstacles.add(new Obstacle("Boeing 747SP", 25, 35, 37, 20,200, 3153));
        predefinedObstacles.add(new Obstacle("Boeing 777-200", 25, 35, 37, 20,100, 3253));

//        predefinedObstacles.add(new Obstacle("Boeing 747SP", 15, 59, 56, 60,3203, 150));
//        predefinedObstacles.add(new Obstacle("Boeing 777-200", 20, 60 , 63, 20,3546, 50));

//        predefinedObstacles.add(new Obstacle("Obstacle1", 25, 28, 29, 10,250, 500));
//        predefinedObstacles.add(new Obstacle("Obstacle2", 11, 35, 37, 10,2600, 3200));
//        predefinedObstacles.add(new Obstacle("Obstacle3", 25, 59, 56, 10,2500, 300));
//        predefinedObstacles.add(new Obstacle("Obstacle4", 18, 60 , 63, 10,150, 500));
    }

    public static ObstacleManager getInstance(){
        return INSTANCE;
    }

    public void addObstacles(List<Obstacle> obstacles) {
        this.predefinedObstacles.addAll(obstacles);
    }

    public void addObstacle(Obstacle obstacle){
        this.predefinedObstacles.add(obstacle);
    }

    public List<Obstacle> getPredefinedObstacles() {
        return predefinedObstacles;
    }

    public Obstacle getPredefinedObstacleByName(String name){
        for(Obstacle o : this.predefinedObstacles){
            if(o.getName().equals(name)){
                return o;
            }
        }

        System.err.println("Obstacle not found");
        return null;
    }

    public Obstacle createObstacle(List<String> parameters){
        Obstacle newObstacle = new Obstacle(
                parameters.get(0),
                Integer.parseInt(parameters.get(1)),
                Integer.parseInt(parameters.get(2)),
                Integer.parseInt(parameters.get(3)),
                Integer.parseInt(parameters.get(4)),
                Integer.parseInt(parameters.get(5)),
                Integer.parseInt(parameters.get(6)));

        this.addObstacle(newObstacle);

        return newObstacle;
    }

    public void modifyObstacle(String name, List<String> parameters){
        Obstacle current = null;

        for(Obstacle obstacle: this.predefinedObstacles){
            if(obstacle.getName().equals(name)){
                current = obstacle;
            }
        }

        if (current != null) {
            this.predefinedObstacles.set(this.predefinedObstacles.indexOf(current), this.createObstacle(parameters));
        } else {
            this.createObstacle(parameters);
        }
    }

    public void clearPredefinedList(){
        this.predefinedObstacles.clear();
    }
}
