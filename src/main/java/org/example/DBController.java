package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBController {

    private Connection connection;
    private final List<String> furnitureTypes = Arrays.asList("Chair","Table","Sofa","Board");

    public void connect() {
        try {
            final String password = "12345678";
            final String user = "postgres";
            final String url = "jdbc:postgresql://localhost:5432/inventory";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected");
        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public ArrayList<Inventory> selectAllInventory() {
        ArrayList<Inventory> inventory = new ArrayList<>();
        try {
            String sqlQuery = "SELECT name, inventory_no, position_x, position_y, number " +
                    "FROM (" +
                    "SELECT furniture.name, furniture.inventory_no, furniture.position_x, furniture.position_y, room.number " +
                    "FROM furniture " +
                    "JOIN room ON furniture.room_id = room.id " +
                    "UNION ALL " +
                    "SELECT equipment.name, equipment.inventory_no, equipment.position_x, equipment.position_y, room.number " +
                    "FROM equipment " +
                    "JOIN room ON equipment.room_id = room.id " +
                    ") AS combined_query";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Inventory inventoryItem = new Inventory();
                inventoryItem.setNumber(resultSet.getString("inventory_no"));
                inventoryItem.setRoomNumber(resultSet.getInt("number"));
                inventoryItem.setType(resultSet.getString("name"));
                inventoryItem.setX(resultSet.getInt("position_x"));
                inventoryItem.setY(resultSet.getInt("position_y"));
                inventory.add(inventoryItem);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return inventory;
    }
    public ArrayList<Inventory> selectInventoryByType(String type){
        ArrayList<Inventory> inventory = new ArrayList<>();
        String sqlQuery;
        try{
            if (furnitureTypes.contains(type)){
                sqlQuery = "SELECT furniture.name, furniture.inventory_no, furniture.position_x, furniture.position_y, room.number \n" +
                        "FROM furniture\n" +
                        "JOIN room ON furniture.room_id = room.id\n" +
                        "WHERE furniture.name = ?";
            }else {
                sqlQuery = "SELECT equipment.name, equipment.inventory_no, equipment.position_x, equipment.position_y, room.number \n" +
                        "FROM equipment\n" +
                        "JOIN room ON equipment.room_id = room.id\n" +
                        "WHERE equipment.name = ?";
            }
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1,type);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Inventory inventoryItem = new Inventory();
                inventoryItem.setNumber(resultSet.getString("inventory_no"));
                inventoryItem.setRoomNumber(resultSet.getInt("number"));
                inventoryItem.setType(resultSet.getString("name"));
                inventoryItem.setX(resultSet.getInt("position_x"));
                inventoryItem.setY(resultSet.getInt("position_y"));
                inventory.add(inventoryItem);
            }
            statement.close();
            resultSet.close();
        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return inventory;
    }

    public Inventory selectInventoryByNumber(String number) {
        Inventory inventoryItem = new Inventory();
        try {
            String sqlQuery = "SELECT name, inventory_no, position_x, position_y, number " +
                    "FROM (" +
                    "    SELECT furniture.name, furniture.inventory_no, furniture.position_x, furniture.position_y, room.number " +
                    "    FROM furniture " +
                    "    JOIN room ON furniture.room_id = room.id " +
                    "    WHERE furniture.inventory_no = ? " +
                    "    UNION ALL " +
                    "    SELECT equipment.name, equipment.inventory_no, equipment.position_x, equipment.position_y, room.number " +
                    "    FROM equipment " +
                    "    JOIN room ON equipment.room_id = room.id " +
                    "    WHERE equipment.inventory_no = ? " +
                    ") AS combined_query";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, number);
            statement.setString(2, number);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                inventoryItem.setNumber(resultSet.getString("inventory_no"));
                inventoryItem.setRoomNumber(resultSet.getInt("number"));
                inventoryItem.setType(resultSet.getString("name"));
                inventoryItem.setX(resultSet.getInt("position_x"));
                inventoryItem.setY(resultSet.getInt("position_y"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return inventoryItem;
    }

    public void insertFurnitureByNumber(String number, String roomNumber, String type, int x, int y) {
        try {
            int id = 0;
            String sqlQuery = "SELECT id FROM room\n" +
                    "WHERE number = ?";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1,roomNumber);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                id = resultSet.getInt("id");
            }
            if(furnitureTypes.contains(type)){
                sqlQuery = "INSERT INTO furniture (name,room_id,type_id,inventory_no,position_x,position_y,acceptance_year,working)\n" +
                        "VALUES(?,?,?,?,?,?,2020,true)";
            }
            else {
                sqlQuery = "INSERT INTO equipment (name,room_id,type_id,inventory_no,position_x,position_y,acceptance_year,working)\n" +
                        "VALUES(?,?,?,?,?,?,2020,true)";
            }
            statement = connection.prepareStatement(sqlQuery);
            statement.setString(1,type);
            statement.setInt(2,id);
            statement.setInt(3,1);
            statement.setString(4,number);
            statement.setInt(5,x);
            statement.setInt(6,y);
            int rows = statement.executeUpdate();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public void deleteFurnitureByNumber(String number) {
        try {
            String furnitureQuery = "DELETE FROM furniture WHERE inventory_no = ?";
            String equipmentQuery = "DELETE FROM equipment WHERE inventory_no = ?";

            PreparedStatement statement = connection.prepareStatement(furnitureQuery);
            statement.setString(1, number);
            int furnitureRows = statement.executeUpdate();
            statement.close();

            PreparedStatement equipmentStatement = connection.prepareStatement(equipmentQuery);
            equipmentStatement.setString(1, number);
            int equipmentRows = equipmentStatement.executeUpdate();
            equipmentStatement.close();

            int totalRowsDeleted = furnitureRows + equipmentRows;
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            connection.close();
            System.out.println("Database disconnected");
        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}
