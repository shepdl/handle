package edu.ucla.drc.sledge.documentimport;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class ImportWarningDialog extends VBox {

    private Consumer yesHandler;
    private Consumer noHandler;

    public void setCallbacks (Consumer yesHandler, Consumer noHandler) {
        this.yesHandler = yesHandler;
        this.noHandler = noHandler;
    }

    @FXML private void yesClicked (MouseEvent event) {
        event.consume();
        yesHandler.accept(null);
    }

    @FXML private void noClicked (MouseEvent event) {
        event.consume();
        noHandler.accept(null);
    }
}
