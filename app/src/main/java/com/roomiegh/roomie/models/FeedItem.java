package com.roomiegh.roomie.models;

import android.text.Spanned;

/**
 * Created by KayO on 15/08/2016.
 */
public class FeedItem {
    String title;
    String link;
    Spanned description;
    String pubDate;
    String stringDescription;
    String thumbnailUrl;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Spanned getDescription() {
        return description;
    }

    public void setDescription(Spanned description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getStringDescription() {
        return stringDescription;
    }

    public void setStringDescription(String stringDescription) {
        this.stringDescription = stringDescription;
    }
}
