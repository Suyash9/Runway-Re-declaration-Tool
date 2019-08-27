package Model;

import java.util.ArrayList;
import java.util.List;

public class Airport {
    private String name;
    private List<Runway> runways;

    public Airport(String name, List<Runway> runways) {
        this.name = name;
        this.runways = runways;
    }

    public Airport(String name) {
        this.name = name;
        this.runways = new ArrayList<>();
        this.runways.add(new Runway("09R",3660,3660,3660,3353,307));
        this.runways.add(new Runway("27L",3660,3660,3660,3660,0));
        this.runways.add(new Runway("09L",3902,3902,3902,3595,306));
        this.runways.add(new Runway("27R",3884,3962,3884,3884,0));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
            return name;
    }

    public List<Runway> getRunways() {
        return runways;
    }

    public void addRunway(Runway r) {
        runways.add(r);
    }

    public Runway getRunwayByDesignator(String designator){
        for(Runway r : runways){
            if(r.getDesignator().equals(designator)){
                return r;
            }
        }

        System.err.println("Runway not found");
        return null;
    }

    public List<String> getRunwayNames(){
        List<String> result = new ArrayList<>();

        for(Runway r : this.runways){
            result.add(r.getDesignator());
        }
        return result;
    }

    public void removeRunway(Runway r){
        this.runways.remove(r);
    }

    public void clearRunwayList(){
        this.runways.clear();
    }

    public void addRunways(List<Runway> newRunways) {
        runways = newRunways;
    }
}
