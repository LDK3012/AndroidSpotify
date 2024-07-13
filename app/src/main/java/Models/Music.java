package Models;
// class Albums goi ten albums, ten bai hat
public class Music {

    public Music(){

    }

    String images;
    String tracks_item;
    String track;
    String track_name;
    String album_name;


    public Music(String images, String tracks_item, String track, String track_name, String album_name) {
        this.images = images;
        this.tracks_item = tracks_item;
        this.track = track;
        this.track_name = track_name;
        this.album_name = album_name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getTracks_item() {
        return tracks_item;
    }

    public void setTracks_item(String tracks_item) {
        this.tracks_item = tracks_item;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTrack_name() {
        return track_name;
    }

    public void setTrack_name(String track_name) {
        this.track_name = track_name;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }
}
