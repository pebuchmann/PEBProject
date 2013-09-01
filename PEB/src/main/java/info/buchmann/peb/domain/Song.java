package info.buchmann.peb.domain;

import java.io.Serializable;

/**
 * Created by peter on 9/1/13.
 */
public class Song implements Serializable{
    private String title = "?";
    private String artist = "?";



    public Song(String pTitle,String pArtist){
        this.title=pTitle;
        this.artist = pArtist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object pOther){
        if(! (pOther instanceof Song)) return false;
        Song other = (Song)pOther;
        if(other == null) return false;
        if(other.title.equals(this.title) &&  other.artist.equals(this.artist)) return true;
        return false;
    }

}
