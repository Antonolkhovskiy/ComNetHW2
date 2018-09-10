import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by Anton on 24.03.2018.
 */
public class Board{

    private ArrayList<Cell> cells;
    private HBox hBox;

    public Board (){
        hBox = new HBox(0);
        hBox.getChildren().addAll(getCells());
        hBox.setAlignment(Pos.CENTER);
    }


    public ArrayList<Cell> getCells(){
        cells = new ArrayList<>(20);
        for(int i = 0; i < 20; i++){
            cells.add(new Cell(i));
        }

        return cells;
    }

    public HBox gethBox() {
        return hBox;
    }
}
