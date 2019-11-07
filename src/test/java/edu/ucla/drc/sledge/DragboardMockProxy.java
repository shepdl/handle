package edu.ucla.drc.sledge;

import com.sun.javafx.tk.TKClipboard;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.util.Set;

/**
 * Mock for Dragboard, used to test dragging and dropping files into a window
 * <p>
 * This is a hack that uses the Reflection API. It may very well break in future
 * versions of Java or JavaFX.
 */
public class DragboardMockProxy {

    private Dragboard db;

    public DragboardMockProxy(Clipboard clipboard) {
        Constructor<Dragboard> dragboardConstructor;
        try {
            dragboardConstructor = Dragboard.class.getDeclaredConstructor(TKClipboard.class);
            dragboardConstructor.setAccessible(true);

            this.db = dragboardConstructor.newInstance(new TKClipboard() {
                @Override
                public void setSecurityContext(AccessControlContext ctx) {

                }

                @Override
                public Set<DataFormat> getContentTypes() {
                    return clipboard.getContentTypes();
                }

                @Override
                public boolean putContent(Pair<DataFormat, Object>... content) {
                    return true;
                }

                @Override
                public Object getContent(DataFormat dataFormat) {
                    return clipboard.getContent(dataFormat);
                }

                @Override
                public boolean hasContent(DataFormat dataFormat) {
                    return clipboard.hasContent(dataFormat);
                }

                @Override
                public Set<TransferMode> getTransferModes() {
                    return null;
                }

                @Override
                public void setDragView(Image image) {

                }

                @Override
                public void setDragViewOffsetX(double offsetX) {

                }

                @Override
                public void setDragViewOffsetY(double offsetY) {

                }

                @Override
                public Image getDragView() {
                    return null;
                }

                @Override
                public double getDragViewOffsetX() {
                    return 0;
                }

                @Override
                public double getDragViewOffsetY() {
                    return 0;
                }
            });
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
            db = null;
        }
    }

    public Dragboard getDragboard() {
        return db;
    }
}
