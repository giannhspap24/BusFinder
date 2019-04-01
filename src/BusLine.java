import java.io.Serializable;

public class BusLine implements Serializable
{
    public String lineID, lineCode, description;
    public int hashCode;


    public BusLine(String lineCode, String lineID, String description, int hashCode) {
        this.lineID = lineID;
        this.lineCode = lineCode;
        this.description = description;
        this.hashCode = hashCode;
    }
}
