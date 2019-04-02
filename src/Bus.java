import java.io.Serializable;

public class Bus  implements Serializable {
    String routeCode, vehicleId,  LineCode, timeStamp;
    double lat, lon;



    public Bus(String LineCode, String routeCode, String vehicleId, double lat, double lon, String timeStamp) {
        this.LineCode = LineCode;
        this.routeCode = routeCode;
        this.vehicleId = vehicleId;
        this.lat = lat;
        this.lon = lon;
        this.timeStamp = timeStamp;

    }

    @Override
    public String toString() {
        return "Bus{" +
                "routeCode='" + routeCode + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", LineCode='" + LineCode + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
