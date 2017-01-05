package projetrev.com.personcapparis8.dataBase;


public class personClass {
    private long id;
    private String distance;
    private String date;
    private String image_name;

    public personClass(String distance, String date, String image_name) {
        this.distance = distance;
        this.date = date;
        this.image_name = image_name;
    }

    public personClass(long id, String distance, String date, String image_name) {
        this.id = id;
        this.distance = distance;
        this.date = date;
        this.image_name = image_name;
    }

    public personClass() {

    }

    public long getId() {
        return id;
    }

    public String getDistance() {
        return distance;
    }

    public String getDate() {
        return date;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
}