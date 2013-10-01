package info.buchmann.peb.domain;

import java.io.Serializable;

/**
 * Created by peter on 9/1/13.
 */
public class Song implements Serializable{
    private String title = "?";
    private String artist = "?";
    private String album = "?";
    private String playedDate = "?";
    private boolean isPlaying = false;




    public Song(String pTitle,String pArtist, String pPlayedDate,boolean pIsPlaying){
        this.title=pTitle;
        this.artist = pArtist;
        this.playedDate = pPlayedDate;
        this.isPlaying = pIsPlaying;

    }

    public String formatPlayStatus(){
        if(isPlaying){
            return "Now on SRF 3";
        }
        return playedDate;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPlayedDate() {
        return playedDate;
    }

    public void setPlayedDate(String playedDate) {
        this.playedDate = playedDate;
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
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
