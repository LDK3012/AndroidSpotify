package Models;
// class artists goi ten nhac si, ten bai hat, anh artists
public class Artists {

    String Id_artists;
    String name_artists;
    String images_albums;


    public Artists(){

    }

    public Artists(String id_artists, String name_artists, String images_albums) {
        Id_artists = id_artists;
        this.name_artists = name_artists;
        this.images_albums = images_albums;
    }


    public String getId_artists() {
        return Id_artists;
    }

    public void setId_artists(String id_artists) {
        Id_artists = id_artists;
    }

    public String getName_artists() {
        return name_artists;
    }

    public void setName_artists(String name_artists) {
        this.name_artists = name_artists;
    }

    public String getImages_albums() {
        return images_albums;
    }

    public void setImages_albums(String images_albums) {
        this.images_albums = images_albums;
    }
}
