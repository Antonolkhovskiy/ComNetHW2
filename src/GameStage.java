import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;


public class GameStage extends Application {

    private VBox playerVBox, enemyVBox, gameVBox, vBoxReady;
    private HBox playersBoard, enemyBoard;
    private Label playersShipsLabel, enemyShipsLabel, turnLabel, playerLabel, enemyLabel, labelReady;
    private Board board;
    private BorderPane mainLayout;
    private ArrayList<Cell> playersShips, enemyShips;
    private Host enemy;
    private boolean turn, server;
    private String yourShips, enemiesShips;
    private String cellsLeft = " Cells Left -> ";
    private byte enemiesShipsLeft = 10;
    private byte yourShipsLeft = 10;
    private Stage stage;
    private TCPServer tcpServer = new TCPServer();
    private TCPClient tcpClient = new TCPClient();
    private boolean ready = false;
    private Scene gettingReadyScene , scene;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        yourShips = new String("Your Cells Left -> 10");
        enemiesShips = DataStorage.enemy.getMessage() + "'s Cells Left -> " + String.valueOf(enemiesShipsLeft);
        playersShipsLabel = new Label(yourShips);
        enemyShipsLabel = new Label(enemiesShips);
        playersShipsLabel.setPadding(new Insets(30));
        enemyShipsLabel.setPadding(new Insets(30));
        turnLabel = new Label("Your Turn");
        playerLabel = new Label("Your Ships");
        enemyLabel = new Label("Enemies Ships");
        board = new Board();

        enemyShips = board.getCells();
        playersShips = DataStorage.ships;
        enemy = DataStorage.enemy;
        turn = DataStorage.turn;
        playersBoard = new HBox();
        enemyBoard =  new HBox();
        playersBoard.getChildren().addAll(playersShips);
        enemyBoard.getChildren().addAll(enemyShips);

        playersBoard.setDisable(true);

        playerVBox = new VBox(15);
        enemyVBox = new VBox(15);
        gameVBox = new VBox(20);
        enemyVBox.setAlignment(Pos.CENTER);
        playerVBox.setAlignment(Pos.CENTER);
        playerVBox.getChildren().addAll(playersShipsLabel);
        enemyVBox.getChildren().addAll(enemyShipsLabel);

        gameVBox.getChildren().addAll(
                turnLabel,
                playerLabel,
                playersBoard,
                enemyLabel,
                enemyBoard);
        gameVBox.setAlignment(Pos.CENTER);
        gameVBox.setPadding(new Insets(0,0,15,0));

        mainLayout = new BorderPane();

        mainLayout.setLeft(playerVBox);
        mainLayout.setRight(enemyVBox);
        mainLayout.setBottom(gameVBox);

        mainLayout.setPrefSize(500, 300);

        scene = new Scene(mainLayout);
        getReadyScene();

        primaryStage.setScene(gettingReadyScene);
        primaryStage.show();
        Thread getReadyThread = new Thread(()->{


            while(true){
                    if(ready){
                        Platform.runLater(()->{
                            primaryStage.setScene(scene);
                            primaryStage.show();
                            setListeners();
                        });
                        if(turn){
                            myTurn();
                        }else {
                            enemiesTurn();
                        }
                        break;
                    }
            }
        });
        createClientServer();
        getReadyThread.start();





    }

    public void setListeners(){
        for(int i = 0; i < 20; i++){
            final int index = i;
            Cell singleCell = (Cell)enemyShips.get(i);
            singleCell.setOnMouseClicked(e -> fire(index));
            singleCell.setOnMouseEntered(e ->colorYellow(index));
            singleCell.setOnMouseExited(e ->colorBack(index));
        }
    }

    public void colorYellow(int cellNumer){
        Cell singleCell = (Cell)enemyShips.get(cellNumer);
        if(!singleCell.isHit()){
            singleCell.setFill(Color.YELLOW);
        }

    }
    public void colorBack(int cellNumer){
        Cell singleCell = (Cell)enemyShips.get(cellNumer);
        if(!singleCell.isHit()){
            singleCell.setFill(Color.LIGHTGRAY);
        }
    }
    public void fire(int cellNumber){
        Cell singleCell = (Cell)enemyShips.get(cellNumber);
        Thread thread = new Thread(()->{
            try {
                getClient();
                tcpClient.sendMessage(String.valueOf(cellNumber));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String message = null;

            try {
                message = tcpServer.getMessage();
                if(message.equals(Config.HIT)){
                    singleCell.setFill(Color.RED);
                    singleCell.setHit(true);
                    enemiesShipsLeft--;
                    endGame();
                    Platform.runLater(()->{
                        enemiesShips = DataStorage.enemy.getMessage() + "'s Cells Left -> " + String.valueOf(enemiesShipsLeft);
                        enemyShipsLabel.setText(enemiesShips);
                    });


                    //turnLabel.setText("Your Turn");
                    enemiesTurn();
                }else{
                    singleCell.setFill(Color.BLACK);
                    singleCell.setHit(true);
                    //turnLabel.setText("Your Turn");
                    enemiesTurn();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        thread.start();

    }

    public void myTurn(){
        Platform.runLater(()->{
            turnLabel.setText("Your Turn");
            enemyBoard.setDisable(false);
        });



    }

    public void enemiesTurn(){
        Platform.runLater(()->{
            turnLabel.setText("Enemies Turn");
            enemyBoard.setDisable(true);
        });

        Thread thread = new Thread(()->{
            defend();
            myTurn();

        });
        thread.start();
    }

    public void defend(){
        boolean flagMissed = true;
        try {

            int shipIndex = Integer.valueOf(tcpServer.getMessage());
                Cell cell = (Cell)playersShips.get(shipIndex);
                if(cell.isHasShip()){
                    getClient();
                    tcpClient.sendMessage(Config.HIT);
                    cell.setFill(Color.RED);
                    yourShipsLeft--;
                    endGame();
                    Platform.runLater(()->{
                        yourShips = "Your Cells Left -> "  + String.valueOf(enemiesShipsLeft);
                        playersShipsLabel.setText(yourShips);
                    });

                    cell.setHit(true);
                    flagMissed = false;
                }
            if(flagMissed){
                cell.setFill(Color.BLUE);
                cell.setHit(true);
                getClient();
                tcpClient.sendMessage(Config.MISSED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        turn = true;
    }

    public void endGame(){
        VBox vBox = new VBox();
        Label label = new Label();
        vBox.getChildren().add(label);
        vBox.setPrefSize(300, 200);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);
        if(yourShipsLeft == 0){
            Platform.runLater(()->{
                label.setTextFill(Color.RED);
                label.setText("You losed the game\n" + DataStorage.enemy.getMessage() + " won!");
                stage.setScene(scene);
            });

        }
        if(enemiesShipsLeft == 0){
            Platform.runLater(()->{
                label.setTextFill(Color.GREEN);
                label.setText("You WON the game!\n" + DataStorage.enemy.getMessage() + " losed");
                stage.setScene(scene);
            });

        }
    }

    public void createClientServer(){
        Thread thread = new Thread(()->{
            if(turn){
            while (true) {
                boolean created = tcpServer.tryCreateServerSocket(Config.PORT_SERVER);
                if (created) break;
            }
                ready = true;
        }else{
            while (true) {
                boolean created = tcpServer.tryCreateServerSocket(Config.PORT_CLIENT);
                if (created) break;
            }
                ready = true;
        }
        ready = true;
        });
        server = turn;
       thread.start();

    }

    public void getReadyScene(){
        vBoxReady = new VBox();
        vBoxReady.setPrefSize(500,300);
        vBoxReady.setAlignment(Pos.CENTER);
        labelReady = new Label("Getting Ready");
        vBoxReady.getChildren().add(labelReady);
        gettingReadyScene = new Scene(vBoxReady);

    }

    public void getClient(){
        if(server){
            while (true) {
                boolean created = tcpClient.tryCreatClientSocket(Config.PORT_CLIENT);
                if (created) break;
            }
        }else{
            while (true) {
                boolean created = tcpClient.tryCreatClientSocket(Config.PORT_SERVER);
                if (created) break;
            }
        }
    }
}

