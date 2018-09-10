import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Anton on 21.03.2018.
 */

public class StartingStage extends Application {

    private Button createButton;
    private Button joinButton;
    private TextField nickNameTextField;
    private Label typeNickNameLabel;
    private VBox vBox;
    private Scene startingScene;
    private TCPClient tcpClient = new TCPClient();
    private TCPServer tcpServer = new TCPServer();
    private Host host, enemy;



    public static void main(String[] args){
        launch(args);
    }


  @Override
 public void start(Stage primaryStage) throws Exception {
        createButton = new Button("Create Game");
        joinButton = new Button("Join Game");
        nickNameTextField = new TextField();
        typeNickNameLabel = new Label("Type NickName");
        vBox = new VBox(25);

        nickNameTextField.setMaxWidth(80);

        vBox.getChildren().addAll(typeNickNameLabel, nickNameTextField, joinButton, createButton);
        vBox.setAlignment(Pos.CENTER);

        vBox.setPrefSize(400, 250);

        startingScene = new Scene(vBox);

      primaryStage.setScene(startingScene);
      primaryStage.setTitle("Welcome to One-Dimentional Battleship Game");
      primaryStage.show();






        joinButton.setOnAction(e ->{
            DataStorage.turn = false;
            ChooseGame chooseGame = new ChooseGame();

            if(nickNameTextField.getText().trim().equals("")){
                typeNickNameLabel.setText("Write Your Nick!");
            } else {
                DataStorage.nickName = nickNameTextField.getText().trim();
                try {
                    chooseGame.start(primaryStage);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }




        });

        createButton.setOnAction(e ->{
            DataStorage.turn = true;
            AtomicBoolean flag = new AtomicBoolean(false);
            GetStarted getStarted = new GetStarted();
            if(nickNameTextField.getText().trim().equals("")){
                typeNickNameLabel.setText("Write Your Nick!");
            }else {
                DataStorage.nickName = nickNameTextField.getText().trim();
                typeNickNameLabel.setText("waiting...");
                nickNameTextField.setDisable(true);
                Thread thread = new Thread(()->{
                if(createGame()){
                    try {
                        String message = tcpServer.getMessage();
                        flag.set(true);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }else{
                    typeNickNameLabel.setText("there is no players, please try later");
                    nickNameTextField.setDisable(false);
                }

            });
                thread.start();
                Thread getStartedThread = new Thread(()->{
                    while(true){
                        if(flag.get()){
                            thread.interrupt();
                            try {
                                Platform.runLater(()->{
                                    try {
                                        tcpServer.closeServerSocket();
                                        tcpClient.closeClientSocket();
                                        getStarted.start(primaryStage);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                });

                                break;
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

                getStartedThread.start();

            }

        });

    }

    public boolean createGame() {
        while (true) {
            boolean created = tcpServer.tryCreateServerSocket(Config.PORT_SERVER);
            if (created) break;
        }
        while (true) {
            boolean created = tcpClient.tryCreatClientSocket(Config.PORT_CLIENT);
            if (created) break;
        }
        try {
            String msg = tcpServer.getMessage();
            if (msg.equals(Config.SEND_NAME)) {
                tcpClient.sendMessage(nickNameTextField.getText());
                while (true) {
                    boolean created = tcpClient.tryCreatClientSocket(Config.PORT_CLIENT);
                    if (created) break;
                }
                enemy = new Host();
                enemy.setMessage(tcpServer.getMessage());
                DataStorage.enemy = enemy;
                return true;
            }


        } catch (Exception e1) {
            // e1.printStackTrace();
            return false;
        }
        return false;


    }







}
