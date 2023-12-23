package org.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class MainWindow extends ApplicationAdapter {
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private TextField findInventoryIdTextField;
    private TextField addInventoryIdTextField;
    private TextField addRoomNumberTextField;
    private TextField deleteInventoryTextField;
    private Button searchInventoryButton;
    private Button searchInventoryByTypeButton;
    private Button setPositionButton;
    private Button addButton;
    private Button deleteButton;
    private Label findLabel;
    private Label inventoryIdLabel;
    private Label deleteLabel;
    private Label roomNumberLabel;
    private Label inventoryTypeLabel;
    private Label infoLabel;
    private Label listLabel;
    private Table searchInventoryTable;
    private Table addInventoryTable;
    private Table deleteInventoryTable;
    private Table controlTable;
    private SelectBox addInventoryTypeSelectBox;
    private SelectBox searchInventoryByTypeSelectBox;
    private Stage stage;
    private Skin skin;
    private ScrollPane scrollPane;
    private Room room;
    private RoomRenderer roomRenderer;
    private ArrayList<Zone> zones;
    private ZoneRenderer zoneRenderer;
    private DBController dbController;
    private ArrayList<Inventory> inventory;
    private int searchPointX = 0;
    private int searchPointY = 0;
    private int addPointX = 0;
    private int addPointY = 0;
    private boolean isSearchDrawing;
    private boolean isAllDrawing;
    private boolean isAdded;
    private boolean isAddDrawing;

    @Override
    public void create() {
        font = new BitmapFont(Gdx.files.internal("src\\assets\\default.fnt"), Gdx.files.internal("src\\assets\\default.png"), false);
        shapeRenderer = new ShapeRenderer();
        font.getData().scale(5);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        room = new Room(10, 10, 550, 620);
        zones = new ArrayList<>();
        zones.add(new Zone(200, 200, 160, 300));
        zones.add(new Zone(20, 200, 160, 300));
        zones.add(new Zone(20, 520, 520, 100));
        zones.add(new Zone(380, 200, 160, 300));
        zones.add(new Zone(200, 80, 340, 100));
        roomRenderer = new RoomRenderer();
        zoneRenderer = new ZoneRenderer();

        skin = new Skin(Gdx.files.internal("src\\assets\\uiskin.json"));
        dbController = new DBController();
        dbController.connect();

        searchInventoryTable = new Table();
        findLabel = new Label("Id:  ", skin);
        findInventoryIdTextField = new TextField("", skin);
        searchInventoryButton = new TextButton("Search", skin);
        searchInventoryButton.setSize(75, 50);

        addInventoryTable = new Table();
        inventoryIdLabel = new Label("Id: ", skin);
        roomNumberLabel = new Label("Room number", skin);
        inventoryTypeLabel = new Label("Type", skin);
        addInventoryIdTextField = new TextField("", skin);
        addRoomNumberTextField = new TextField("", skin);
        setPositionButton = new TextButton("Set position", skin);
        addButton = new TextButton("Add", skin);
        addButton.setSize(75, 50);

        addInventoryTypeSelectBox = new SelectBox(skin);
        addInventoryTypeSelectBox.setItems("", "Chair", "Table", "Sofa", "Cupboard", "Monitor", "Board", "TV", "Mouse", "KeyBoard",
                "System unit", "Router");

        deleteInventoryTable = new Table();
        deleteLabel = new Label("Id: ", skin);
        deleteInventoryTextField = new TextField("", skin);
        deleteButton = new TextButton("Delete: ", skin);

        controlTable = new Table();
        controlTable.defaults().space(100);

        infoLabel = new Label("...", skin);
        infoLabel.setPosition(10, 650);
        listLabel = new Label("", skin);
        scrollPane = new ScrollPane(listLabel, skin);
        scrollPane.setPosition(900, 140);
        scrollPane.setSize(400, 500);

        searchInventoryByTypeButton = new TextButton("Search by type",skin);
        searchInventoryByTypeButton.setSize(120,40);
        searchInventoryByTypeButton.setPosition(1400,600);
        searchInventoryByTypeSelectBox = new SelectBox<>(skin);
        searchInventoryByTypeSelectBox.setItems("", "Chair", "Table", "Sofa", "Cupboard", "Monitor", "Board", "TV", "Mouse", "KeyBoard",
                "System unit", "Router");
        searchInventoryByTypeSelectBox.setPosition(1400,550);
        searchInventoryByTypeSelectBox.setSize(120,40);

        updateList();

        searchInventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String number = findInventoryIdTextField.getText();
                if (number.isEmpty()) {
                    infoLabel.setText("The search field is empty");
                } else {
                    Inventory inventory = dbController.selectInventoryByNumber(number);
                    if (inventory.getNumber() == null) {
                        infoLabel.setText("No such furniture");
                    } else {
                        isAllDrawing = false;
                        isSearchDrawing = true;
                        searchPointX = inventory.getX();
                        searchPointY = inventory.getY();
                        infoLabel.setText(inventory.toString());
                    }
                }
            }
        });

        searchInventoryByTypeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                String type = (String)searchInventoryByTypeSelectBox.getSelected();
                inventory = dbController.selectInventoryByType(type);
                isAllDrawing = true;
            }
        });
        setPositionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isAllDrawing = false;
                isSearchDrawing = false;
                isAddDrawing = true;
                isAdded = true;
            }
        });
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isSearchDrawing = false;
                isAddDrawing = false;
                String number = addInventoryIdTextField.getText();
                String roomNumber = addRoomNumberTextField.getText();
                String type = (String) addInventoryTypeSelectBox.getSelected();
                if (number.isEmpty() || roomNumber.isEmpty() || type.isEmpty() || addPointX == 0 || addPointY == 0) {
                    infoLabel.setText("Some add field is empty");
                } else {
                    dbController.insertFurnitureByNumber(number, roomNumber, type, addPointX, addPointY);
                    infoLabel.setText("Added");
                    addPointX = 0;
                    addPointY = 0;
                    updateList();
                }
            }
        });
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isSearchDrawing = false;
                isAddDrawing = false;
                String number = deleteInventoryTextField.getText();
                if (number.isEmpty()) {
                    infoLabel.setText("Input field is empty");
                } else {
                    dbController.deleteFurnitureByNumber(number);
                    infoLabel.setText("Deleted");
                    updateList();
                }
            }
        });

        searchInventoryTable.add(findLabel);
        searchInventoryTable.add(findInventoryIdTextField).row();
        searchInventoryTable.add(searchInventoryButton);

        addInventoryTable.add(inventoryIdLabel);
        addInventoryTable.add(addInventoryIdTextField).row();
        addInventoryTable.add(roomNumberLabel);
        addInventoryTable.add(addRoomNumberTextField).row();
        addInventoryTable.add(inventoryTypeLabel);
        addInventoryTable.add(addInventoryTypeSelectBox).row();
        addInventoryTable.add(setPositionButton);
        addInventoryTable.add(addButton);

        deleteInventoryTable.add(deleteLabel);
        deleteInventoryTable.add(deleteInventoryTextField).row();
        deleteInventoryTable.add(deleteButton);

        controlTable.add(searchInventoryTable).row();
        controlTable.add(addInventoryTable).row();
        controlTable.add(deleteInventoryTable).row();
        controlTable.setPosition(730, 400);

        stage.addActor(controlTable);
        stage.addActor(infoLabel);
        stage.addActor(scrollPane);
        stage.addActor(searchInventoryByTypeButton);
        stage.addActor(searchInventoryByTypeSelectBox);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        roomRenderer.render(shapeRenderer, room);
        zoneRenderer.render(shapeRenderer, zones);
        if (isSearchDrawing) {
            drawPoint(searchPointX, searchPointY, shapeRenderer);
        }
        if (isAdded) {
            if (Gdx.input.justTouched()) {
                addPointX = Gdx.input.getX();
                addPointY = Gdx.graphics.getHeight() - Gdx.input.getY();
                isAdded = false;
            }
        }
        if (isAddDrawing) {
            drawPoint(addPointX, addPointY, shapeRenderer);
        }
        if(isAllDrawing){
            drawPoint(inventory,shapeRenderer);
        }
        shapeRenderer.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        stage.dispose();
        dbController.disconnect();
    }

    public void drawPoint(float x, float y, ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        float pointSize = 5;
        shapeRenderer.circle(x, y, pointSize);
    }
    public void drawPoint(ArrayList<Inventory> inventory, ShapeRenderer shapeRenderer){
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        float pointSize = 5;
        for(Inventory inventoryItem : inventory){
            shapeRenderer.circle(inventoryItem.getX(), inventoryItem.getY(), pointSize);
        }
    }

    public void updateList() {
        ArrayList<Inventory> inventory = dbController.selectAllInventory();
        StringBuilder stringBuilder = new StringBuilder();
        for (Inventory furn : inventory) {
            stringBuilder.append("\n");
            stringBuilder.append(furn.toString());
            stringBuilder.append("\n");
        }
        listLabel.setText(stringBuilder);
    }
}
