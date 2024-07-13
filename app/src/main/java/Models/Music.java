package Models;
// class Albums goi ten albums, ten bai hat
public class Music {

    public Music(){

    }
    String idMusic;
    String images; // anh
    String track_name; // ten bai hat
    String album_name; // ten albums
    String preview_url; // link nhac
    String artist_name; // ten nghe si
    String id_artist; // id nghe si ( dùng id để lấy ảnh nghệ sĩ )

    public Music(String idMusic, String images, String track_name, String album_name, String preview_url, String artist_name,String id_artist) {
        this.idMusic = idMusic;
        this.images = images;
        this.track_name = track_name;
        this.album_name = album_name;
        this.preview_url = preview_url;
        this.artist_name = artist_name;
        this.id_artist = id_artist;
    }

    public String getIdMusic() { return idMusic; }
    public void setIdMusic(String idMusic) { this.idMusic = idMusic; }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }
    public String getArtist_name() { return artist_name; }

    public void setArtist_name(String artist_name) { this.artist_name = artist_name; }
    public String getId_artist() { return id_artist; }
    public void setId_artist(String id_artist) { this.id_artist = id_artist; }
}
