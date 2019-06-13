package edu.ucla.drc.sledge.topicsettings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

class DoubleValidator implements ChangeListener<String> {

    private TextField field;
    private Consumer<Double> property;

    public DoubleValidator(TextField field, Consumer<Double> property) {
        this.field = field;
        this.property = property;
    }

    public DoubleValidator(TextField field) {
        this.field = field;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        try {
            double alpha = Double.parseDouble(newValue);
            field.setText(newValue);
            if (this.property != null) {
                this.property.accept(alpha);
            }
        } catch (NumberFormatException ex) {
            field.setText(oldValue);
        }
    }
}
