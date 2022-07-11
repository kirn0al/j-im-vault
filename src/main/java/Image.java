import java.time.LocalDateTime;

public class Image {

    private String name;
    private LocalDateTime timeOfCreating;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimeOfCreating() {
        return timeOfCreating;
    }

    public void setTimeOfCreating(LocalDateTime timeOfCreating) {
        this.timeOfCreating = timeOfCreating;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
