import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 23.03.2018.
 */
public class ChooseGame extends Application{

    private  ListView<String> listView;
    private VBox vBox;
    private Button buttonSubmit;
    private List<String> hosts;
    private Scene scene;
    private Label label;
    private TCPClient tcpClient = new TCPClient();
    private TCPServer tcpServer = new TCPServer();
    private String nickName;
    private Host enemy = new Host();
    @Override
    public void start(Stage primaryStage) throws Exception {
        nickName = DataStorage.nickName;

        buttonSubmit = new Button("Play");
        buttonSubmit.setDisable(true);
        hosts = new ArrayList<>();


        label = new Label("Choose Game to Play");

        listView = new ListView<>();

        vBox = new VBox(15);
        vBox.getChildren().addAll(label, listView, buttonSubmit);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(20));
        vBox.setPrefSize(400, 250);




        scene = new Scene(vBox);

        primaryStage.setScene(scene);
        primaryStage.show();




        Thread thread = new Thread(() -> {
            getHosts();
        });

        thread.start();
        buttonSubmit.setOnAction(event -> {
            thread.interrupt();
            DataStorage.enemy = enemy;
         //   Thread threadStart = new Thread(()->{
           // while(true) {
                try {
                    //Thread.sleep(1000);
                    while (true) {
                        boolean created = tcpClient.tryCreatClientSocket(Config.PORT_SERVER);
                        if (created) break;
                    }
                    tcpClient.sendMessage("start");
                   // break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
          //  }

                GetStarted getStarted = new GetStarted();
                try {
                    tcpClient.closeClientSocket();
                    tcpServer.closeServerSocket();
                    getStarted.start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

           // threadStart.start();


       // });

    }

    public void getHosts(){
        while (true) {
            boolean created = tcpServer.tryCreateServerSocket(Config.PORT_CLIENT);
            if (created) break;
        }
        while (true) {
            boolean created = tcpClient.tryCreatClientSocket(Config.PORT_SERVER);
            if (created) break;
        }

                    try {
                        tcpClient.sendMessage(Config.SEND_NAME);
                        enemy.setMessage(tcpServer.getMessage());
                        hosts.add(enemy.getMessage());
                        while (true) {
                            boolean created = tcpClient.tryCreatClientSocket(Config.PORT_SERVER);
                            if (created) break;
                        }
                        tcpClient.sendMessage(nickName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            Platform.runLater(() -> {
                listView.getItems().removeAll();
                listView.getItems().addAll(hosts);
                listView.getSelectionModel().select(0);
                listView.refresh();
                buttonSubmit.setDisable(false);
            });



       // }

    }



}
