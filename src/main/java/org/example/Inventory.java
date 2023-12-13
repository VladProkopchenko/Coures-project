package org.example;

public class Inventory {
    private String type;
    private String number;
    private int roomNumber;
    private int x;
    private int y;

    public Inventory() {
    }

    public Inventory(String type, String number, int roomNumber, int x, int y) {
        this.type = type;
        this.number = number;
        this.roomNumber = roomNumber;
        this.x = x;
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Number:  " + number + " Room: " +roomNumber+ " Type:" + type;
    }
}
