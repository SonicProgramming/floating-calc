package pkg3d.control;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import pkg3d.Main;
import parse.*;

/**
 * FXML Controller class
 */
public class MainWindowController implements Initializable {

    @FXML private PerspectiveCamera sceneCamera;
    @FXML private AnchorPane rootPane;
    @FXML private Pane internalPane;
    @FXML private TextArea calcTextArea;
    
    // I made an editor folding for NetBeans to hide these with one click.
    // Feel free to delete just as a regular comment.
    // <editor-fold defaultstate="collapsed" desc="A lot of buttons">
    @FXML private Button oneBtn;
    @FXML private Button twoBtn;
    @FXML private Button threeBtn;
    @FXML private Button fourBtn;
    @FXML private Button fiveBtn;
    @FXML private Button sixBtn;
    @FXML private Button sevenBtn;
    @FXML private Button eightBtn;
    @FXML private Button nineBtn;
    @FXML private Button zeroBtn;
    @FXML private Button actionBtn;
    @FXML private Button dotBtn;
    @FXML private Button addBtn;
    @FXML private Button subBtn;
    @FXML private Button divBtn;
    @FXML private Button mulBtn;
    @FXML private Button powBtn;
    @FXML private Button rootBtn;
    @FXML private Button cubeRootBtn;
    @FXML private Button quadRootBtn;
    @FXML private Button sinBtn;
    @FXML private Button cosBtn;
    @FXML private Button tgBtn;
    @FXML private Button ctgBtn;
    // </editor-fold>
    
    private final int rootPaneW = 420, rootPaneH = 510;
    private final int internalPaneW = 405, internalPaneH = 495;
    
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    
    /**
     * Initializes the controller class and sets the whole thing up.
     * @param url Unused, does not affect anything
     * @param rb Не используется, значение ни на что не влияет
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initialized");
        sceneCamera.translateZProperty()
                .set(-500);
        
        /*
        These are here because every Pane is a subclass of Region.
        Every Region is resizable no matter what.
        So, to prevent resizing we need to capture the resizing event.
        Then manually resize the pane back to its original size.
        */
        rootPane.widthProperty().addListener(listener -> {
            rootPane.resize(this.rootPaneW, this.rootPaneH);
        });
        rootPane.heightProperty().addListener(listener -> {
            rootPane.resize(this.rootPaneW, this.rootPaneH);
        });
        internalPane.widthProperty().addListener(listener -> {
            internalPane.resize(this.internalPaneW, this.internalPaneH);
        });
        internalPane.heightProperty().addListener(listener -> {
            internalPane.resize(this.internalPaneW, this.internalPaneH);
        });
        
        // These listeners are here because Main doesn't know the layout
        // Therefore the listeners are set here, and it works.
        Main.primalStage.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            this.sceneCameraMouseDragged(event);
        });
        Main.primalStage.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            this.sceneCameraMouseDragOver(event);
        });
        Main.primalStage.addEventHandler(ScrollEvent.SCROLL, event -> {
            this.sceneCameraMouseScrolled(event);
        });
    }    
    
    @FXML private void actionBtnActionPerformed(ActionEvent evt) {        
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
                && !currentText.endsWith(".")
            ) {
            double res = new ExpressionParser().startParse(currentText);
            calcTextArea.setText("" + res);            
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Incorrect end of expression!");
            alert.showAndWait();
        }
    }
    
    @FXML private void sceneCameraMouseDragged(MouseEvent evt) {
        // It's better to have these values copied here
        // Who knows, maybe they can change before this method returns
        double currentMouseX = evt.getSceneX();
        double currentMouseY = evt.getSceneY();
        
        // We don't need to change anything if X or Y are set to 0
        if (lastMouseX == 0 ) {
            lastMouseX = currentMouseX;
            return;
        }
        if (lastMouseY == 0) {
            lastMouseY = currentMouseY;
            return;
        }
        
        // I would've made a switch statement if left button was involved
        // For now it's not very much worse to have two if blocks
        MouseButton evtButton = evt.getButton();
        if (evtButton.equals(MouseButton.SECONDARY)) {
            sceneCamera.getTransforms()
                    .add(new Rotate(
                            (lastMouseY - currentMouseY) / 2,
                            new Point3D(1, 0, 0)
                    ));
            
            // Rotation is inverted here since it will be less intuitive otherwise
            sceneCamera.getTransforms()
                    .add(new Rotate(
                            (currentMouseX - lastMouseX) / 2,
                            new Point3D(0, 1, 0)
                    ));
        }
        else if (evtButton.equals(MouseButton.MIDDLE)) {
            sceneCamera.translateXProperty()
                    .set(sceneCamera.getTranslateX() + lastMouseX - currentMouseX);
            sceneCamera.translateYProperty()
                    .set(sceneCamera.getTranslateY() + lastMouseY - currentMouseY);
        }
                
        /*
        The thing is, every next mouse movement will be relative.
        If left intact, coordinates are subtracted from absolute X and Y
        in the coordinate system of a scene (meaning that origin never changes).
        This divides the whole scene in halfs, where, ex. bottom half is negative
        for Y and top is positive.
        Also, the further from the middle, the higher the difference between
        lastMouseY and currentMouseY which leads to more translation added to
        the camera, and this may skyrocket the object somewhere in the scene.
        This is very counter-intuitive, annoying, and overall bad for user experience.
        */
        lastMouseX = currentMouseX;
        lastMouseY = currentMouseY;
    }
    
    @FXML private void sceneCameraMouseDragOver(MouseEvent evt) {
        /*
        As seen in sceneCameraMouseDragged() if last X and Y are zero
        new values are assigned - it solves the problem when you need to start
        new drag directly where you finished the last one for the camera
        to NOT fly like crazy.
        */
        lastMouseX = 0;
        lastMouseY = 0;
        System.out.println("Mouse drag over");
    }

    private void sceneCameraMouseScrolled(ScrollEvent evt) {
        // Zooming in and out, multiplied by 1.75 to increase "sensivity"
        sceneCamera.translateZProperty()
                    .set(sceneCamera.getTranslateZ() + evt.getDeltaY()*1.75);
    }
    
    /*
    All the following code is fairly simple so i didn't bother writing comments,
    and i'm sure you'd use a different approach.
    */
    // <editor-fold defaultstate="collapsed" desc="A lot of button controllers">
    @FXML private void oneBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("1");
    }

    @FXML private void twoBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("2");        
    }

    @FXML private void threeBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("3");        
    }

    @FXML private void fourBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("4");        
    }

    @FXML private void fiveBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("5");        
    }

    @FXML private void sixBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("6");
    }

    @FXML private void sevenBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("7");        
    }

    @FXML private void eightBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("8");
    }

    @FXML private void nineBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("9");
    }

    @FXML private void zeroBtnActionPerformed(ActionEvent evt) {
        calcTextArea.appendText("0");
    }

    @FXML private void dotBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(".")
                && !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            calcTextArea.appendText(".");
        }
    }

    @FXML private void addBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            calcTextArea.appendText(" + ");
        } else {
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            calcTextArea.appendText(" + ");
        }
    }

    @FXML private void subBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            calcTextArea.appendText(" - ");
        } else {
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            calcTextArea.appendText(" - ");
        }
    }

    @FXML private void mulBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            calcTextArea.appendText(" * ");
        } else {
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            String b = calcTextArea.getText();
            calcTextArea.appendText(" * ");
        }
    }

    @FXML private void divBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            calcTextArea.appendText(" / ");
        } else {
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            calcTextArea.appendText(" / ");
        }
    }

    @FXML private void powBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            calcTextArea.appendText(" ^ ");
        } else {
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            calcTextArea.appendText(" ^ ");
        }
    }

    @FXML private void rootBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            String number = currentText.substring(currentText.lastIndexOf(" ") + 1);
            try {
                Double.parseDouble(number);
            } catch (Exception ex) {
                // This can only mean that we incorrectly parsed the number
                return;
            }
            calcTextArea.deleteText(currentText.lastIndexOf(" ") + 1, currentText.length());
            calcTextArea.appendText("2 // " + number);
        } else {
            String number = currentText.substring(currentText.lastIndexOf(" "));
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            try {
                Double.parseDouble(number);
            } catch (Exception ex) {
                // This can only mean that we incorrectly parsed the number
                return;
            }
            calcTextArea.deleteText(currentText.lastIndexOf(" ") + 1, currentText.length());
            calcTextArea.appendText("2 // " + number);
        }
    }

    @FXML private void cubeRootBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            String number = currentText.substring(currentText.lastIndexOf(" ") + 1);
            try {
                Double.parseDouble(number);
            } catch (Exception ex) {
                // This can only mean that we incorrectly parsed the number
                return;
            }
            calcTextArea.deleteText(currentText.lastIndexOf(" ") + 1, currentText.length());
            calcTextArea.appendText("3 // " + number);
        } else {
            String number = currentText.substring(currentText.lastIndexOf(" "));
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            try {
                Double.parseDouble(number);
            } catch (Exception ex) {
                // This can only mean that we incorrectly parsed the number
                return;
            }
            calcTextArea.deleteText(currentText.lastIndexOf(" ") + 1, currentText.length());
            calcTextArea.appendText("3 // " + number);
        }
    }

    @FXML private void quadRootBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (
                !currentText.endsWith(" + ") && !currentText.endsWith(" - ")
                && !currentText.endsWith(" * ") && !currentText.endsWith(" / ")
                && !currentText.endsWith(" ^ ") && !currentText.endsWith(" // ")
            ) {
            String number = currentText.substring(currentText.lastIndexOf(" ") + 1);
            try {
                Double.parseDouble(number);
            } catch (Exception ex) {
                // This can only mean that we incorrectly parsed the number
                return;
            }
            calcTextArea.deleteText(currentText.lastIndexOf(" ") + 1, currentText.length());
            calcTextArea.appendText("4 // " + number);
        } else {
            String number = currentText.substring(currentText.lastIndexOf(" "));
            calcTextArea.deleteText(currentText.length() - 3, currentText.length());
            try {
                Double.parseDouble(number);
            } catch (Exception ex) {
                // This can only mean that we incorrectly parsed the number
                return;
            }
            calcTextArea.deleteText(currentText.lastIndexOf(" ") + 1, currentText.length());
            calcTextArea.appendText("4 // " + number);
        }
    }

    @FXML private void sinBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (!currentText.equals("")) {
            try {
                double num = Double.parseDouble(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " sin"));
            } catch (Exception ex) {
                double num = new ExpressionParser().startParse(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " sin"));
            }
        }
    }

    @FXML private void cosBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (!currentText.equals("")) {
            try {
                double num = Double.parseDouble(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " cos"));
            } catch (Exception ex) {
                double num = new ExpressionParser().startParse(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " cos"));
            }
        }
    }

    @FXML private void tgBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (!currentText.equals("")) {
            try {
                double num = Double.parseDouble(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " tg"));
            } catch (Exception ex) {
                double num = new ExpressionParser().startParse(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " tg"));
            }
        }
    }

    @FXML private void ctgBtnActionPerformed(ActionEvent evt) {
        String currentText = calcTextArea.getText();
        if (currentText.equals("")) return;
        
        if (!currentText.equals("")) {
            try {
                double num = Double.parseDouble(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " ctg"));
            } catch (Exception ex) {
                double num = new ExpressionParser().startParse(currentText);
                calcTextArea.setText("" + new ExpressionParser().startParse(num + " ctg"));
            }
        }
    }
    // </editor-fold>
    
}
