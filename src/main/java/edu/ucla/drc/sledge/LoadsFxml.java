package edu.ucla.drc.sledge;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public interface LoadsFxml {

    default void loadFxml() {
        Class implementor = getClass();
        String[] classNameParts = implementor.getName().split("\\.");
        FXMLLoader fxmlLoader = new FXMLLoader(implementor.getResource(classNameParts[classNameParts.length - 1] + ".fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
