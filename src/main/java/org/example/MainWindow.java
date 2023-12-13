package org.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class MainWindow extends ApplicationAdapter {
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private TextField findFurnitureIdTextField;
    private TextField addFurnitureIdTextField;
    private TextField addRoomNumberTextField;
    private TextField deleteFurnitureTextField;
    private Button searchFurnitureButton;
    private Button setPositionButton;
    private Button addButton;
    private Button deleteButton;
    private Label findLabel;
    private Label furnitureIdLabel;
    private Label deleteLabel;
    private Label roomNumberLabel;
    private Label furnitureTypeLabel;
    private Label infoLabel;
    private Label listLabel;
    private Table findFurnitureTable;
    private Table addFurnitureTable;
    private Table deleteFurnitureTable;
    private Table controlTable;
    private SelectBox addFurnitureTypeSelectBox;
    private Stage stage;
    private Skin skin;
    private ScrollPane scrollPane;
    private Room room;
    private RoomRenderer roomRenderer;
    private ArrayList<Zone> zones;
    private ZoneRenderer zoneRenderer;
    private DBController dbController;
    private int searchPointX = 0;
    private int searchPointY = 0;
    private int addPointX = 0;
    private int addPointY = 0;
    private boolean isSearchDrawing;
    private boolean isAdded;
    private boolean isAddDrawing;

    @Override
    public void create(){
        font = new BitmapFont(Gdx.files.internal("src\\assets\\default.fnt"), Gdx.files.internal("src\\assets\\default.png"), false);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font.getData().scale(5);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        room = new Room(10,10,540,620);
        zones = new ArrayList<>();
        zones.add(new Zone(200,200,160,300));
        zones.add(new Zone(20,200,160,300));
        zones.add(new Zone(20,520,520,100));
        zones.add(new Zone(380,200,160,300));
        zones.add(new Zone(200,80,340,100));
        roomRenderer = new RoomRenderer();
        zoneRenderer = new ZoneRenderer();

        skin = new Skin(Gdx.files.internal("src\\assets\\uiskin.json"));
        dbController = new DBController();
        dbController.connect();

        findFurnitureTable = new Table();
        findLabel = new Label("Id:  ",skin);
        findFurnitureIdTextField = new TextField("", skin);
        searchFurnitureButton = new TextButton("Find", skin);
        searchFurnitureButton.setSize(75, 50);

        addFurnitureTable = new Table();
        furnitureIdLabel = new Label("Id: ", skin);
        roomNumberLabel = new Label("Room number",skin);
        furnitureTypeLabel = new Label("Type",skin);
        addFurnitureIdTextField = new TextField("",skin);
        addRoomNumberTextField = new TextField("",skin);
        setPositionButton = new TextButton("Set position",skin);
        addButton = new TextButton("Add",skin);
        addButton.setSize(75,50);

        addFurnitureTypeSelectBox = new SelectBox(skin);
        addFurnitureTypeSelectBox.setItems("","Chair","Table","Sofa","Cupboard","Monitor","Board","TV","Mouse","KeyBoard",
                "System unit","Router");

        deleteFurnitureTable = new Table();
        deleteLabel = new Label("Id: ",skin);
        deleteFurnitureTextField = new TextField("",skin);
        deleteButton = new TextButton("Delete: ",skin);

        controlTable = new Table();
        controlTable.defaults().space(100);

        infoLabel = new Label("...",skin);
        infoLabel.setPosition(10,650);
        listLabel = new Label("reg",skin);
        //listLabel.setPosition(1100,500);
        scrollPane = new ScrollPane(listLabel,skin);
        scrollPane.setPosition(1100,350);
        scrollPane.setSize(400,300);
        updateList();

        searchFurnitureButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String number = findFurnitureIdTextField.getText();
                if(number.isEmpty()){
                    infoLabel.setText("The search field is empty");
                }
                else {
                    Inventory inventory = dbController.selectInventoryByNumber(number);
                    if(inventory.getNumber() == null){
                        infoLabel.setText("No such furniture");
                    }
                    else {
                        isSearchDrawing = true;
                        searchPointX = inventory.getX();
                        searchPointY = inventory.getY();
                        infoLabel.setText(inventory.toString());
                    }
                }
            }
        });
        setPositionButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                isSearchDrawing = false;
                isAddDrawing = true;
                isAdded = true;
            }
        });
        addButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                isSearchDrawing = false;
                isAddDrawing = false;
                String number = addFurnitureIdTextField.getText();
                String roomNumber = addRoomNumberTextField.getText();
                String type = (String)addFurnitureTypeSelectBox.getSelected();
                if(number.isEmpty() || roomNumber.isEmpty() || type.isEmpty()){
                    infoLabel.setText("Some add field is empty");
                    infoLabel.setText(addPointX + "|"+addPointY);
                }
                else{
                    dbController.insertFurnitureByNumber(number,roomNumber,type,addPointX,addPointY);
                    infoLabel.setText("Added");
                    updateList();
                }
            }
        });
        deleteButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                isSearchDrawing = false;
                isAddDrawing = false;
                String number = deleteFurnitureTextField.getText();
                if(number.isEmpty()){
                    infoLabel.setText("Input field is empty");
                }
                else {
                    dbController.deleteFurnitureByNumber(number);
                    infoLabel.setText("Deleted");
                    updateList();
                }
            }
        });

        findFurnitureTable.add(findLabel);
        findFurnitureTable.add(findFurnitureIdTextField).row();
        findFurnitureTable.add(searchFurnitureButton);

        addFurnitureTable.add(furnitureIdLabel);
        addFurnitureTable.add(addFurnitureIdTextField).row();
        addFurnitureTable.add(roomNumberLabel);
        addFurnitureTable.add(addRoomNumberTextField).row();
        addFurnitureTable.add(furnitureTypeLabel);
        addFurnitureTable.add(addFurnitureTypeSelectBox).row();
        addFurnitureTable.add(setPositionButton);
        addFurnitureTable.add(addButton);

        deleteFurnitureTable.add(deleteLabel);
        deleteFurnitureTable.add(deleteFurnitureTextField).row();
        deleteFurnitureTable.add(deleteButton);

        controlTable.add(findFurnitureTable).row();
        controlTable.add(addFurnitureTable).row();
        controlTable.add(deleteFurnitureTable).row();
        controlTable.setPosition(800,400);

        stage.addActor(controlTable);
        stage.addActor(infoLabel);
        stage.addActor(scrollPane);
    }
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        roomRenderer.render(shapeRenderer,room);
        zoneRenderer.render(shapeRenderer,zones);
        if(isSearchDrawing){
            drawPoint(searchPointX, searchPointY,shapeRenderer);
        }
        if (isAdded) {
            if (Gdx.input.justTouched()) {
                addPointX = Gdx.input.getX();
                addPointY = Gdx.graphics.getHeight() - Gdx.input.getY();
                isAdded = false;
            }
        }
        if(isAddDrawing){
            drawPoint(addPointX, addPointY,shapeRenderer);
        }
        shapeRenderer.end();
        spriteBatch.begin();
        //spriteRenderer.render(font,spriteBatch);
        spriteBatch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    @Override
    public void dispose() {
        font.dispose();
        spriteBatch.dispose();
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
    public void updateList(){
        ArrayList<Inventory> inventory = dbController.selectAllInventory();
        StringBuilder stringBuilder = new StringBuilder();
        for(Inventory furn : inventory){
            stringBuilder.append(furn.toString());
            stringBuilder.append("\n");
        }
        listLabel.setText(stringBuilder);
    }
}
