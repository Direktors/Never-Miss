import javax.swing.*;
import java.io.IOException;

public class GameWindow extends JFrame {

    public GameWindow() throws IOException {
        super();
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("NeverMiss");
        this.setResizable(false);
        this.add(new GamePanel());
        this.setVisible(true);
    }

}
