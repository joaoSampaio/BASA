package pt.ulisboa.tecnico.mybasaclient.model.firebase;

public class FirebaseFileLink {

    private String url;
    private String pathFile;

    private String thumbnail;
    private String pathThumbnail;
    private int duration;
    private long createdAt;

    public FirebaseFileLink() {
    }

    public FirebaseFileLink(String url, String pathFile) {
        this.url = url;
        this.pathFile = pathFile;
    }

    public FirebaseFileLink(String url, String pathFile, String thumbnail, String pathThumbnail, int duration, long createdAt) {
        this.url = url;
        this.pathFile = pathFile;
        this.thumbnail = thumbnail;
        this.pathThumbnail = pathThumbnail;
        this.duration = duration;
        this.createdAt = createdAt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPathThumbnail() {
        return pathThumbnail;
    }

    public void setPathThumbnail(String pathThumbnail) {
        this.pathThumbnail = pathThumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}