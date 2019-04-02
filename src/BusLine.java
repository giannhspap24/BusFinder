import java.io.Serializable;
import java.util.ArrayList;

public class BusLine implements Serializable
{
    public String lineID, lineCode, description;
    public int hashCode;
    public ArrayList<Bus> runningBuses;


    public BusLine(String lineCode, String lineID, String description, int hashCode) {
        this.lineID = lineID;
        this.lineCode = lineCode;
        this.description = description;
        this.hashCode = hashCode;
        runningBuses = new ArrayList<>();
    }

    public void updateBus(Bus b) {
        boolean flag = true;
        for (Bus tempBus : runningBuses) {
            if (tempBus.vehicleId.equals(b.vehicleId)) {
                tempBus.lat = b.lat;
                tempBus.lon = b.lon;
                tempBus.timeStamp = b.timeStamp;
                flag = false;

                //System.out.println(this.toString() + " updated bus " + b.toString());
                break;
            }
        }
        if (flag) {
            runningBuses.add(b);
            //System.out.println(this.toString() + " added bus " + b.toString());

        }
    }

    @Override
    public String toString() {
        return "BusLine{" +
                "lineID='" + lineID + '\'' +
                ", lineCode='" + lineCode + '\'' +
                ", description='" + description + '\'' +
                ", hashCode=" + hashCode +
                ", runningBuses=" + runningBuses +
                '}';
    }
}
