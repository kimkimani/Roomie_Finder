package com.roomiegh.roomie.models;

import java.io.Serializable;

/**
 * Created by KayO on 20/05/2017.
 */

public class Room implements Serializable{
    int id,type,price, hostel_id;
    //TODO let API return array of photoPaths for a single room
    //TODO possibly add the floor the room is on
    String hostelName,photoPath, roomNum;

    public Room() {
    }

    public Room(int id, int type, int price, int hostel_id, String hostelName, String photoPath, String roomNum) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.hostel_id = hostel_id;
        this.hostelName = hostelName;
        this.photoPath = photoPath;
        this.roomNum = roomNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getHostel_id() {
        return hostel_id;
    }

    public void setHostel_id(int hostel_id) {
        this.hostel_id = hostel_id;
    }

    public String getHostelName() {
        return hostelName;
    }

    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (id != room.id) return false;
        if (type != room.type) return false;
        if (price != room.price) return false;
        if (hostel_id != room.hostel_id) return false;
        if (hostelName != null ? !hostelName.equals(room.hostelName) : room.hostelName != null)
            return false;
        if (photoPath != null ? !photoPath.equals(room.photoPath) : room.photoPath != null)
            return false;
        return roomNum != null ? roomNum.equals(room.roomNum) : room.roomNum == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + type;
        result = 31 * result + price;
        result = 31 * result + hostel_id;
        result = 31 * result + (hostelName != null ? hostelName.hashCode() : 0);
        result = 31 * result + (photoPath != null ? photoPath.hashCode() : 0);
        result = 31 * result + (roomNum != null ? roomNum.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", type=" + type +
                ", price=" + price +
                ", hostel_id=" + hostel_id +
                ", hostelName='" + hostelName + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", roomNum='" + roomNum + '\'' +
                '}';
    }
}
