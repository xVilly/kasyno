package com.casino.Controllers;

import com.casino.Connection.ConnectionManager;
import com.casino.Connection.IncomingMessage;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class ChatController implements IController {

    @FXML
    private VBox chatBox;

    @FXML
    private TextArea inputBox;

    @FXML
    private ScrollPane chatBoxScroll;


    @FXML
    void onSend(ActionEvent event) {
        if (inputBox.getText().isEmpty()) {
            return;
        }
        ConnectionManager.getInstance().GetConnection().getMessageSender().sendChatMessage(inputBox.getText());
        inputBox.clear();
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    int messageIndex = 1;
    boolean connected = false;
    
    public void AddChatMessage(String message, String color) {
        Label newMessage = new Label();
        newMessage.setText(message);
        newMessage.setTextFill(Paint.valueOf(color));
        chatBox.getChildren().add(newMessage);
        chatBoxScroll.setVvalue(1.0);

        messageIndex++;
    }
    
    public void onActivate() {
        if (!connected) {
            AddChatMessage("Connecting to the casino chat...", "#000000");
        }

        ConnectionManager.getInstance().GetConnection().getMessageSender().sendChatJoin();

        ConnectionManager.getInstance().GetConnection().registerCallback((byte)0x03, (IncomingMessage msg) -> {
            onChatMessage(msg);
        });
        connected = true;
    }

    public void onChatMessage(IncomingMessage msg) {
        String message = msg.getString();
        int messageType = msg.getInt();

        Platform.runLater(() -> {
            switch(messageType) {
                case 0:
                    AddChatMessage(message, "#0dd1cb");
                    break;
                case 1:
                    AddChatMessage(message, "#9c8a3b");
                    break;
                case 2:
                    AddChatMessage(message, "#9e6208");
                    break;
                case 3:
                    AddChatMessage(message, "#696969");
                    break;
                default:
                    AddChatMessage(message, "#000000");
                    break;
            }
        });
    }
}
