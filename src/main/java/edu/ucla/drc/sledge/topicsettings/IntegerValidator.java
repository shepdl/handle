package edu.ucla.drc.sledge.topicsettings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

class IntegerValidator implements ChangeListener<String> {

    private TextField field;
    private Consumer<Integer> property;

    public IntegerValidator(TextField field, Consumer<Integer> property) {
        this.field = field;
        this.property = property;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        try {
            int parsedValue = Integer.parseInt(newValue);
            field.setText(newValue);
            this.property.accept(parsedValue);
        } catch (NumberFormatException ex) {
            field.setText(oldValue);
        }
    }
}
