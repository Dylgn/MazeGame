package dylan;
/*
Put header here


 */

import java.lang.reflect.Array;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class FXMLController implements Initializable {

    @FXML private Button start, stop;
    @FXML private Label timer, score;
    @FXML private AnchorPane scene;

    Polygon[][] maze = new Polygon[11][26];

    Timeline time = new Timeline(new KeyFrame(Duration.millis(100), ae -> timer()));

    Double t = 2.5;
    int s = 0;

    private void timer() {
        t -= 0.1;
        timer.setText(new DecimalFormat("#.#").format(Math.abs(t)) + "s");

        if (t <= 0) {
            resetGame();
        }
    }

    @FXML
    void start(ActionEvent event) {
        Button source = (Button) event.getSource();
        reset();

        if (source.getText().equals("Start") || source.getText().equals("Next")) {
            // Starts game
            int[] path = new int[2];

            // Random starting spot
            path[0] = ThreadLocalRandom.current().nextInt(0, 10 + 1);

            int dir = 1;

            // Starts at left or right of screen depending on source
            if (source.getLayoutX() != 0) {
                path[1] = 25;
                dir = -1;
            }

            newSpot(path[0], path[1]);

            // Goes forward for loop to not stop instantly
            path[1] += dir;
            newSpot(path[0], path[1]);

            ArrayList<int[]> paths = new ArrayList();

            // Creates path
            do {
                // Chooses random direction
                int r = ThreadLocalRandom.current().nextInt(0, 2 + 1);
                switch (r) {
                    case 0:
                        // Above
                        try {
                            if (maze[path[0] - 1][path[1]].getFill() == Color.BLACK && adjacents(path[0] - 1, path[1]) == 1) {
                                // Sets to open path
                                maze[path[0] - 1][path[1]].setFill(Color.WHITE);
                                maze[path[0] - 1][path[1]].setStroke(Color.WHITE);

                                // Changes current path end
                                --path[0];

                                paths.add(new int[]{path[0], path[1]});
                            } else {
                                // Continues if space is taken
                                continue;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            // Continues if space is out of array
                            continue;
                        }
                        break;
                    case 1:
                        // Forward
                        // Checks for adjacent spots
                        if (adjacents(path[0], path[1] + dir) == 1) {
                            maze[path[0]][path[1] + dir].setFill(Color.WHITE);
                            maze[path[0]][path[1] + dir].setStroke(Color.WHITE);

                            path[1] += dir;

                            paths.add(new int[]{path[0], path[1]});
                        }
                        break;
                    case 2:
                        // Down
                        try {
                            if (maze[path[0] + 1][path[1]].getFill() == Color.BLACK && adjacents(path[0] + 1, path[1]) == 1) {
                                // Sets to open path
                                maze[path[0] + 1][path[1]].setFill(Color.WHITE);
                                maze[path[0] + 1][path[1]].setStroke(Color.WHITE);

                                // Changes current path end
                                ++path[0];

                                paths.add(new int[]{path[0], path[1]});
                            } else {
                                // Continues if space is taken
                                continue;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            // Continues if space is out of array
                            continue;
                        }
                        break;
                }
            } while (path[1] % 25 != 0);

            if (time.getStatus() != Animation.Status.STOPPED) {
                ++s;
            }

            t += 7.5;
            time.play();

            visibility(true);
            source.setVisible(false);
            score.setVisible(false);
            source.setText("Next");
        }
    }

    @FXML
    void hover(MouseEvent event) {
        if (stop.isVisible()) {
            Polygon source = (Polygon) event.getSource();

            // Resets game if you enter black polygon
            if (source.getFill() == Color.BLACK) {
                resetGame();
            }
        }
    }

    @FXML
    void exit(MouseEvent event) {
        resetGame();
    }

    private int adjacents(int a, int b) {
        // Checks how many adjacent spots are already taken
        int c = 0;
        // Up
        try {
            if (maze[a - 1][b].getFill() == Color.WHITE) {
                ++c;
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        // Right
        try {
            if (maze[a][b + 1].getFill() == Color.WHITE) {
                ++c;
            }
            // Continues if out of bounds (at the very start/end)
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        // Down
        try {
            if (maze[a + 1][b].getFill() == Color.WHITE) {
                ++c;
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        //Left
        try {
            if (maze[a][b - 1].getFill() == Color.WHITE) {
                ++c;
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        // Returns amount of adjacents
        return c;
    }

    private void newSpot(int a, int b) {
        maze[a][b].setFill(Color.WHITE);
        maze[a][b].setStroke(Color.WHITE);
    }

    private void visibility(Boolean b) {
        start.setVisible(b);
        stop.setVisible(b);
    }

    private void resetGame() {
        stop.setVisible(false);
        start.setVisible(true);

        score.setVisible(true);
        score.setText("Score: " + s);

        start.setText("Start");
        stop.setText("Next");

        t = 2.5;
        s = 0;

        time.stop();

        reset();
    }

    private void reset() {
        for (int a = 0; a < 11; a++) {
            // Loops through 2d array to reset all polygons
            for (int b = 0; b < 26; b++) {
                maze[a][b].setFill(Color.BLACK);
                maze[a][b].setStroke(Color.BLACK);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        time.setCycleCount(-1);

        int a = 0;
        int b = 0;
        try {
            for (Node child : scene.getChildren()) {
                // Adds all maze polygons to list
                try {
                    maze[a][b] = (Polygon) child;
                    if (b != 25) {
                        b++;
                    } else {
                        a++;
                        b = 0;
                    }
                } catch (ClassCastException e) {

                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }    
}

 /*@FXML private Polygon gridA1, gridA2, gridA3, gridA4, gridA5, gridA6, gridA7, gridA8, gridA9, gridA10, gridA11, gridA12, gridA13;
    @FXML private Polygon gridA14, gridA15, gridA16, gridA17, gridA18, gridA19, gridA20, gridA21, gridA22, gridA23, gridA24, gridA25, gridA26;
    @FXML private Polygon gridB1, gridB2, gridB3, gridB4, gridB5, gridB6, gridB7, gridB8, gridB9, gridB10, gridB11, gridB12, gridB13;
    @FXML private Polygon gridB14, gridB15, gridB16, gridB17, gridB18, gridB19, gridB20, gridB21, gridB22, gridB23, gridB24, gridB25, gridB26;
    @FXML private Polygon gridC1, gridC2, gridC3, gridC4, gridC5, gridC6, gridC7, gridC8, gridC9, gridC10, gridC11, gridC12, gridC13;
    @FXML private Polygon gridC14, gridC15, gridC16, gridC17, gridC18, gridC19, gridC20, gridC21, gridC22, gridC23, gridC24, gridC25, gridC26;
    @FXML private Polygon gridD1, gridD2, gridD3, gridD4, gridD5, gridD6, gridD7, gridD8, gridD9, gridD10, gridD11, gridD12, gridD13;
    @FXML private Polygon gridD14, gridD15, gridD16, gridD17, gridD18, gridD19, gridD20, gridD21, gridD22, gridD23, gridD24, gridD25, gridD26;
    @FXML private Polygon gridE1, gridE2, gridE3, gridE4, gridE5, gridE6, gridE7, gridE8, gridE9, gridE10, gridE11, gridE12, gridE13;
    @FXML private Polygon gridE14, gridE15, gridE16, gridE17, gridE18, gridE19, gridE20, gridE21, gridE22, gridE23, gridE24, gridE25, gridE26;
    @FXML private Polygon gridF1, gridF2, gridF3, gridF4, gridF5, gridF6, gridF7, gridF8, gridF9, gridF10, gridF11, gridF12, gridF13;
    @FXML private Polygon gridF14, gridF15, gridF16, gridF17, gridF18, gridF19, gridF20, gridF21, gridF22, gridF23, gridF24, gridF25, gridF26;
    @FXML private Polygon gridG1, gridG2, gridG3, gridG4, gridG5, gridG6, gridG7, gridG8, gridG9, gridG10, gridG11, gridG12, gridG13;
    @FXML private Polygon gridG14, gridG15, gridG16, gridG17, gridG18, gridG19, gridG20, gridG21, gridG22, gridG23, gridG24, gridG25, gridG26;
    @FXML private Polygon gridH1, gridH2, gridH3, gridH4, gridH5, gridH6, gridH7, gridH8, gridH9, gridH10, gridH11, gridH12, gridH13;
    @FXML private Polygon gridH14, gridH15, gridH116, gridH17, gridH18, gridH19, gridH20, gridH21, gridH22, gridH23, gridH24, gridH25, gridH26;
    @FXML private Polygon gridI1, gridI2, gridI3, gridI4, gridI5, gridI6, gridI7, gridI8, gridI9, gridI10, gridI11, gridI12, gridI13;
    @FXML private Polygon gridI14, gridI15, gridI16, gridI17, gridI18, gridI19, gridI20, gridI21, gridI22, gridI23, gridI24, gridI25, gridI26;
    @FXML private Polygon gridJ1, gridJ2, gridJ3, gridJ4, gridJ5, gridJ6, gridJ7, gridJ8, gridJ9, gridJ10, gridJ11, gridJ12, gridJ13;
    @FXML private Polygon gridJ14, gridJ15, gridJ16, gridJ17, gridJ18, gridJ19, gridJ20, gridJ21, gridJ22, gridJ23, gridJ24, gridJ25, gridJ26;
    @FXML private Polygon gridK1, gridK2, gridK3, gridK4, gridK5, gridK6, gridK7, gridK8, gridK9, gridK10, gridK11, gridK12, gridK13;
    @FXML private Polygon gridK14, gridK15, gridK16, gridK17, gridK18, gridK19, gridK20, gridK21, gridK22, gridK23, gridK24, gridK25, gridK26;*/
