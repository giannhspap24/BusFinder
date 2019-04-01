public class Bus {
    String lineNumber, routeCode, vehicleId, lineName, busLineId, info, timeStamp;
    double lat, lon;

    public Bus(String lineNumber, String routeCode, String vehicleId, String lineName, String busLineId, String info, double lat, double lon) {
        this.lineNumber = lineNumber;
        this.routeCode = routeCode;
        this.vehicleId = vehicleId;
        this.lineName = lineName;
        this.busLineId = busLineId;
        this.info = info;
        this.lat = lat;
        this.lon = lon;
    }

    public Bus(String busLineId, String routeCode, String vehicleId, double lat, double lon, String timeStamp) {
        this.busLineId = busLineId;
        this.routeCode = routeCode;
        this.vehicleId = vehicleId;
        this.lat = lat;
        this.lon = lon;
        this.timeStamp = timeStamp;

    }

}
