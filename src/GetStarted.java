import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Stack;


public class GetStarted extends Application {

    private VBox vBox;
    private Button buttonStart;
    private Label mainLabel, shipsToPlace;
    private int shipsLeft = 6;
    private Board board;
    private HBox cells;
    private byte[] arrayCells = new byte[22];
    private Stack<Integer> shipTypeStack;
    private int shipType = 3;
    private ArrayList<Cell> ships;


    @Override
    public void start(Stage primaryStage) throws Exception {
        vBox = new VBox(50);
        buttonStart = new Button("Start Game");
        buttonStart.setDisable(true);
        mainLabel = new Label("You Have to Place All Ships to Start the Game");
        shipsToPlace = new Label("Ships left: " + String.valueOf(shipsLeft));
        board = new Board();
        cells = board.gethBox();
        arrayCells[20] = 1;
        arrayCells[21] = 1;
        Thread thread = new Thread(()-> setCellListeners());
        thread.run();
        ships = new ArrayList<>(20);

        buttonStart.setOnAction(e -> {
            GameStage gameStage = new GameStage();
            try {
                thread.stop();
                gameStage.start(new Stage());
                primaryStage.close();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        fullFillStack();
        shipType = shipTypeStack.pop();

        vBox.setPrefSize(700,250);
        vBox.getChildren().addAll(mainLabel, shipsToPlace, cells, buttonStart);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);

        primaryStage.setScene(scene);
        primaryStage.show();



    }

    public void setCellListeners(){

        for(int i = 0; i < 20; i++){
            final int index = i;

            Cell singleCell = (Cell)cells.getChildren().get(i);
            singleCell.setOnMouseClicked(e ->{
                if(shipTypeStack.isEmpty()){
                    buttonStart.setDisable(false);
                    cells.setDisable(true);
                    for(int j = 0; j <20; j++){
                        Cell cell = (Cell) cells.getChildren().get(j);
                        ships.add(cell);
                        DataStorage.ships = ships;
                    }

                }else if(validPlace(index)){
                    placeShip(index);
                    shipType = shipTypeStack.pop();
                }
            });

            singleCell.setOnMouseEntered(e -> visCells(index));

            singleCell.setOnMouseExited(e -> backColor(index));
        }

    }
    //filling ship type stack
    public void fullFillStack(){
        shipTypeStack = new Stack<>();
        shipTypeStack.push(1);
        shipTypeStack.push(1);
        shipTypeStack.push(1);
        shipTypeStack.push(1);
        shipTypeStack.push(2);
        shipTypeStack.push(2);
        shipTypeStack.push(3);
    }
    //checking valid place
    public boolean validPlace(int cellNumber){

        switch (shipType){
            case 1: {
                    if (arrayCells[cellNumber] == 0) {
                        return true;
                    } else {
                        return false;
                    }

            }
            case 2:{
                if ((arrayCells[cellNumber] == 0) && (arrayCells[cellNumber + 1] == 0)) {
                    return true;
                } else {
                    return false;
                }

            }
            case 3:{
                if ((arrayCells[cellNumber] == 0) && (arrayCells[cellNumber + 1] == 0) && (arrayCells[cellNumber + 2] == 0)) {
                    return true;
                } else {
                    return false;
                }

            }
            default:{
                return false;

            }
        }
    }
    //coloring cells
    public void visCells(int cellNumber) {
        boolean valid = validPlace(cellNumber);

        if (valid) {
            for (int i = cellNumber; i < cellNumber + shipType; i++) {
                Cell cell = (Cell) cells.getChildren().get(i);
                cell.setFill(Color.LIGHTGREEN);
            }
        } else {
            Cell cell = (Cell) cells.getChildren().get(cellNumber);
            cell.setFill(Color.RED);

        }


    }
    //coloring back
    public void backColor(int cellNumber){
        int endBoard = 20;
        if((cellNumber + shipType) <= 20){
            endBoard = cellNumber + shipType;
        }

        for (int i = cellNumber; i < endBoard; i++) {
            if(arrayCells[i] != 1){
                Cell cell = (Cell) cells.getChildren().get(i);
                cell.setFill(Color.LIGHTGRAY);
            }
        }
        Cell cell = (Cell) cells.getChildren().get(cellNumber);
        if(cell.isHasShip()){
            cell.setFill(Color.LIGHTGREEN);
        }
    }
    //placing ships
    public void placeShip(int cellNumber){
        boolean valid = validPlace(cellNumber);
        if(valid){
            for (int i = cellNumber; i < cellNumber + shipType; i++) {
                Cell cell = (Cell) cells.getChildren().get(i);
                cell.setHasShip(true);
                arrayCells[i] = 1;
            }
            shipsLeft--;
            shipsToPlace.setText("Ships left: " + String.valueOf(shipsLeft));
        }else{

        }
    }
}
