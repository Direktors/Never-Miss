import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.JFrame;

public class GamePanel extends JPanel {

    private final ArrayList<Block> blocks = new ArrayList<>();
    private final ArrayList<Power> powers = new ArrayList<>();
    public static final Color DARK_RED = new Color(153,0,0);
    private int score = 0;
    private boolean gameActive = false;
    private int timeLeft = 30;
    private final Random rand = new Random();
    private final Timer timer;
    private final JButton startButton = new JButton("START GAME & SHOOOT");
    private static final String SOUND_INTRO = "C:\\Users\\andri\\Desktop\\shootergame-master\\AUDIO_FILES\\audio_intro_2.wav";
    private static final String SOUND_SHOT = "C:\\Users\\andri\\Desktop\\shootergame-master\\AUDIO_FILES\\audio_shot_2.wav";
    private static final String SOUND_RELOAD = "C:\\Users\\andri\\Desktop\\shootergame-master\\AUDIO_FILES\\audio_reload.wav";
    private static final String SOUND_START = "C:\\Users\\andri\\Desktop\\shootergame-master\\AUDIO_FILES\\audio_western_shorter.wav";
    private int clickCount = 0;
    private int bulletsFired = 0;
    private boolean isReloadSoundPlaying = false;
    private final BufferedImage bulletImage;
    private long lastPowerBlockTime = System.currentTimeMillis();



    public GamePanel() throws IOException {
        setLayout(null);
        setFocusable(true);

        startButton.setBounds(100, 140, 300, 50);
        startButton.setBackground(new Color(118, 48, 48));
        startButton.setForeground(Color.WHITE);
        add(startButton);
        playIntroSound();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                } else {
                    gameActive = false;
                    timer.stop();
                    startButton.setVisible(true);
                    stopStartSound();
                    playIntroSound();
                }
                repaint();
            }
        });

        startButton.addActionListener(e -> {
            if (!gameActive) {
                gameActive = true;
                score = 0;
                timeLeft = 30;
                blocks.clear();
                powers.clear();
                startButton.setVisible(false);
                stopIntroSound();
                playStartSound();
                timer.start();
                clickCount = 0;
                bulletsFired = 0;

                generateBlock();
                generatePower();

                repaint();
            }
        });

        //starta poga + reseti

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameActive || isReloadSoundPlaying) return;

                int cursorX = e.getX();
                int cursorY = e.getY();

                Iterator<Block> iterator = blocks.iterator();
                while (iterator.hasNext()) {
                    Block block = iterator.next();
                    if (block.rect.contains(cursorX, cursorY)) {
                        iterator.remove();
                        score++;
                        generateBlock();
                        playShotSound();
                        generatePower();
                        clickCount++;
                        bulletsFired++;
                        if (clickCount == 8) {
                            playReloadSound();
                            clickCount = 0;
                            isReloadSoundPlaying = true;
                            new Timer(2400, e1 -> isReloadSoundPlaying = false).start();
                            bulletsFired = 0;
                        }

                        repaint();

                        return;
                    }
                }

                Iterator<Power> powerIterator = powers.iterator();
                while (powerIterator.hasNext()) {
                    Power power = powerIterator.next();
                    if (power.rect.contains(cursorX, cursorY)) {
                        powerIterator.remove();
                        timeLeft += 5;
                        generatePower();
                        playShotSound();
                        clickCount++;
                        bulletsFired++;
                        if (clickCount == 8) {
                            playReloadSound();
                            clickCount = 0;
                            isReloadSoundPlaying = true;
                            new Timer(2400, e1 -> isReloadSoundPlaying = false).start();
                            bulletsFired = 0;
                        }
                        repaint();

                        return;
                    }
                }
            }
        });

            BufferedImage cursorImg = loadImage("C:\\Users\\andri\\Desktop\\shootergame-master\\VISUAL_MATERIAL\\cursor_small.png");
                if (cursorImg != null) {
                int centerX = cursorImg.getWidth() / 2;
                int centerY = cursorImg.getHeight() / 2;
                Point hotspot = new Point(centerX, centerY);
                Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, hotspot, "Custom Cursor");
                setCursor(customCursor);
            }

    // custom cursor ar offsetu uz centru click hotspotam

            bulletImage = loadImage("C:\\Users\\andri\\Desktop\\shootergame-master\\VISUAL_MATERIAL\\Bullets\\bullet_v.png");
            }


    private BufferedImage loadImage(String path) throws IOException {
            return ImageIO.read(new File(path));
            }

    //ielādē bildes

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameActive) {

            Image logo = Toolkit.getDefaultToolkit().getImage("C:\\Users\\andri\\Desktop\\shootergame-master\\VISUAL_MATERIAL\\LOGO_3.jpg");
            g.drawImage(logo, 0, 0, getWidth(), getHeight(), this);

            if (!gameActive && timeLeft == 0) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setFont(new Font("Arial", Font.BOLD, 18));
                String line1 = "Time is up! You never miss...";
                String line2 = "Yet you need to learn to be slow in a hurry!";
                String line3 = "Score: " + score;
                int x = getWidth() / 2 - 110;
                int y = getHeight() / 2 - 30;

                g.drawString(line1, x, y);
                g.drawString(line2, x - 80, y + 20);
                g.drawString(line3, x + 76, y + 40);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setFont(new Font("Arial", Font.BOLD, 18));
            }

            return;
        }

        Image background = Toolkit.getDefaultToolkit().getImage("C:\\Users\\andri\\Desktop\\shootergame-master\\VISUAL_MATERIAL\\BACKGROUND_1.jpg");
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        if (gameActive) {
            for (Block block : blocks) {
                g.setColor(block.color);
                g.fillRect(block.rect.x, block.rect.y, block.rect.width, block.rect.height);
            }

            for (Power power : powers) {
                g.setColor(DARK_RED);
                g.fillRect(power.rect.x, power.rect.y, power.rect.width, power.rect.height);
            }
        }

        if (bulletImage != null) {
            int startingX = getWidth() - bulletImage.getWidth() - 740;
            int startingY = getHeight() - 90;
            int remainingBullets = Math.max(0, 8 - bulletsFired);

            for (int i = 0; i < remainingBullets; i++) {
                int bulletX = startingX + i * (bulletImage.getWidth() + 10);
                g.drawImage(bulletImage, bulletX, startingY, null);
            }
        }
        //8 ložu kopijas un sākuma pozīcija + x pa labi

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Rezultāts: " + score, 10, 35);
        g.drawString("Atlikušais laiks: " + timeLeft, 10, 65);


    }

    private void generateBlock() {
        if (!gameActive) return;
        int x = rand.nextInt(Math.max(1, this.getWidth() - 50));
        int y = rand.nextInt(Math.max(1, this.getHeight() - 50));
        int width = 20 + rand.nextInt(81);
        int height = 20 + rand.nextInt(81);
        Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        blocks.add(new Block(new Rectangle(x, y, width, height), color));
    }


    private static class Block {
        Rectangle rect;
        Color color;


        Block(Rectangle rect, Color color) {
            this.rect = rect;
            this.color = color;
        }
    }


    private void generatePower() {
        if (!gameActive) return;

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPowerBlockTime > 10000) {
            lastPowerBlockTime = currentTime;

            //1x 10sek parādās power block

            int x = rand.nextInt(Math.max(1, this.getWidth() - 50));
            int y = rand.nextInt(Math.max(1, this.getHeight() - 50));
            int width = 20 + rand.nextInt(20);
            int height = 20 + rand.nextInt(20);
            powers.add(new Power(new Rectangle(x, y, width, height), DARK_RED));
        }
    }

    private static class Power {
        Rectangle rect;
        Color color;
        Power(Rectangle rect, Color color) {
            this.rect = rect;
            this.color= color;
        }

    }

            //power block beigas


    private void playShotSound() {
        playSound(SOUND_SHOT);
    }

    private void playReloadSound() {
        playSound(SOUND_RELOAD);
    }

    private void playSound(String soundPath) {
        File soundFile = new File(soundPath);
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception ignored) {}
    }
    private Clip startSoundClip;
    private void playStartSound() {
        try {
            File soundFile = new File(SOUND_START);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            startSoundClip = AudioSystem.getClip();
            startSoundClip.open(audioIn);
            startSoundClip.start();
        } catch (Exception ignored) {}
    }

    private void stopStartSound() {

        if (startSoundClip != null && startSoundClip.isOpen()) {
                startSoundClip.stop();
                startSoundClip.close();
        }
    }

    private Clip introSoundClip;
    private void playIntroSound() {
        try {
            File soundFile = new File(SOUND_INTRO);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            introSoundClip = AudioSystem.getClip();
            introSoundClip.open(audioIn);
            introSoundClip.loop(Clip.LOOP_CONTINUOUSLY);
            introSoundClip.start();
        } catch (Exception ignored) {}
    }

    private void stopIntroSound() {

        if (introSoundClip != null && introSoundClip.isOpen()) {
            introSoundClip.stop();
            introSoundClip.close();
        }
    }


            //audio beigas

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new GamePanel());
        frame.setVisible(true);
    }
}