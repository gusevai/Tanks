import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Иришка
 * Date: 15.11.12
 */
public class Game extends Canvas implements Runnable, TankDeathListener {
    private int width;
    public int height;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean firePressed = false;

    private java.util.List<ComputerTank> computerTanks = new ArrayList<ComputerTank>();
    private UserTank userTank;

    boolean userTankDead = false;
    boolean computerTanksDead = false;

    private boolean running;

    private char[][] map;

    public Game() {
        setBackground(Color.BLACK);
    }

    public static void main(String[] args) {
        Game game = new Game();

        game.start();
    }

    @Override
    public void run() {
        try {
            init();

            while (running) {
                update();

                for (int i = 0; i < GameHelper.STEP_DELAY / GameHelper.DRAW_DELAY; i++) {
                    updateActionCompletedPercent((i + 1) * ((double) GameHelper.DRAW_DELAY / GameHelper.STEP_DELAY));

                    render();

                    Thread.sleep(GameHelper.DRAW_DELAY);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateActionCompletedPercent(double percent) {
        userTank.setActionCompletedPercent(percent);

        for (Tank tank : computerTanks) {
            tank.setActionCompletedPercent(percent);
        }
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public void update() {
        makeSteps();

        updateInjuredStatuses();

        resetUserInput();
    }

    private void makeSteps() {
        userTank.setUserInput(rightPressed, leftPressed, upPressed, downPressed, firePressed);
        userTank.makeStep();

        for (ComputerTank computerTank : computerTanks) {
            computerTank.setTargetX(userTank.getX());
            computerTank.setTargetY(userTank.getY());

            computerTank.makeStep();
        }
    }

    private void updateInjuredStatuses() {
        userTank.setInjured(false);

        for (ComputerTank computerTank : new ArrayList<ComputerTank>(computerTanks)) {
            userTank.setInjured(userTank.isInjured() || computerTank.isFire());

            computerTank.setInjured(userTank.isFire() && wasComputerTankInjured(computerTank));
        }
    }

    private void resetUserInput() {
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;
        firePressed = false;
    }

    public void init() {
        try {
            initMap(ResourceHelper.MAP);

            for (ComputerTank computerTank : computerTanks) {
                computerTank.setMap(map);

                computerTank.setWidth(width);
                computerTank.setHeight(height);

                computerTank.setTargetX(userTank.getX());
                computerTank.setTargetY(userTank.getY());

                computerTank.setTankDeathListener(this);
            }

            userTank.setMap(map);
            userTank.setWidth(width);
            userTank.setHeight(height);
            userTank.setTankDeathListener(this);

            JFrame frame = new JFrame(GameHelper.GAME_NAME);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(this, BorderLayout.CENTER);
            frame.setPreferredSize(
                    new Dimension(
                            width * GameHelper.SCALE + GameHelper.GAME_SUMMARY_WIDTH,
                            height * GameHelper.SCALE + 35));
            frame.pack();
            frame.setResizable(false);
            frame.setVisible(true);

            addKeyListener(new KeyInputHandler());

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();

        if (bs == null) {
            createBufferStrategy(2);
            requestFocus();
            return;
        }

        Graphics g = bs.getDrawGraphics();

        drawGameSummary(g);

        if (userTankDead) {
            drawCenteredString(
                    g,
                    GameHelper.GAME_OVER,
                    width * GameHelper.SCALE,
                    height * GameHelper.SCALE,
                    0,
                    0,
                    70,
                    Color.RED);

            running = false;

        } else if (computerTanksDead) {
            drawCenteredString(
                    g,
                    GameHelper.YOU_WIN,
                    width * GameHelper.SCALE,
                    height * GameHelper.SCALE,
                    0,
                    0,
                    70,
                    Color.ORANGE);

            running = false;

        } else {
            drawGameField(g);
            drawGameSummary(g);
        }

        g.dispose();
        bs.show();
    }

    private void drawGameField(Graphics g) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                switch (map[i][j]) {
                    case GameHelper.BRICK:
                        g.setColor(Color.GRAY);
                        g.drawImage(
                                ResourceHelper.BRICK,
                                j * GameHelper.SCALE,
                                i * GameHelper.SCALE,
                                GameHelper.SCALE,
                                GameHelper.SCALE,
                                null);
                        break;

                    case GameHelper.WATER:
                        g.setColor(Color.BLUE);
                        g.drawImage(
                                ResourceHelper.WATER,
                                j * GameHelper.SCALE,
                                i * GameHelper.SCALE,
                                GameHelper.SCALE,
                                GameHelper.SCALE,
                                null);
                        break;

                    case GameHelper.GROUND:
                        g.setColor(Color.BLACK);
                        g.fillRect(j * GameHelper.SCALE, i * GameHelper.SCALE, GameHelper.SCALE, GameHelper.SCALE);
                        break;

                    default:
                        g.setColor(Color.BLACK);
                        g.fillRect(j * GameHelper.SCALE, i * GameHelper.SCALE, GameHelper.SCALE, GameHelper.SCALE);
                }

/*                if (computerTank.getWay().contains(new Cell(j, i, null, 0))) {
                    g.setColor(Color.MAGENTA);
                    g.fillRect(j * GameHelper.SCALE, i * GameHelper.SCALE, GameHelper.SCALE, GameHelper.SCALE);
                }*/
            }
        }

        for (ComputerTank computerTank : computerTanks) {
            computerTank.draw(g);
        }
        userTank.draw(g);
    }

    private void drawGameSummary(Graphics g) {
        int startX = width * GameHelper.SCALE;
        int startTankX = startX + 8;
        int startHealthBoxX = startX + GameHelper.SCALE + 18;

        int tankOffsetY = 22;
        int healthBoxOffsetY = 28;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 15; j++) {
                Image im = ResourceHelper.WALL;

                if ((j < 2) || (j == 4) || (j == 14) || (j == 13) || (i == 5)) {
                    im = ResourceHelper.BRICK;
                }

                g.drawImage(
                        im,
                        startX + i * GameHelper.SCALE,
                        j * GameHelper.SCALE,
                        GameHelper.SCALE,
                        GameHelper.SCALE,
                        null);
            }
        }

        g.drawImage(
                ResourceHelper.TANK_EAST_ORANGE,
                startTankX,
                GameHelper.SCALE * 2 + tankOffsetY,
                GameHelper.SCALE,
                GameHelper.SCALE,
                null);

        drawHealthBox(
                g,
                startHealthBoxX,
                GameHelper.SCALE * 2 + healthBoxOffsetY,
                userTank.getHealth(),
                GameHelper.USER_HEALTH);

        for (int i = 0; i < computerTanks.size(); i++) {
            Tank computerTank = computerTanks.get(i);

            g.drawImage(
                    ResourceHelper.TANK_EAST,
                    startTankX,
                    GameHelper.SCALE * (5 + 2 * i) + tankOffsetY,
                    GameHelper.SCALE,
                    GameHelper.SCALE,
                    null);

            drawHealthBox(
                    g,
                    startHealthBoxX,
                    GameHelper.SCALE * (5 + 2 * i) + healthBoxOffsetY,
                    computerTank.getHealth(),
                    GameHelper.COMPUTER_HEALTH);
        }
    }

    private void drawCenteredString(
            Graphics g,
            String s,
            int width,
            int height,
            int XPos,
            int YPos,
            int fontSize,
            Color color) {

        g.setColor(color);
        g.setFont(new Font(s, Font.CENTER_BASELINE, fontSize));

        int stringLen = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        int stringHeight = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();

        int startX = width / 2 - stringLen / 2;
        int startY = height / 2 + stringHeight / 2;

        g.drawString(s, startX + XPos, YPos + startY);
    }

    private void drawHealthBox(Graphics g, int x, int y, int health, int maxHealth) {
        g.setColor(Color.WHITE);
        g.fillRect(x - 2, y - 2, GameHelper.HEALTH_BOX_WIDTH + 4, GameHelper.HEALTH_BOX_HEIGHT + 4);

        g.setColor(Color.BLACK);
        g.fillRect(x, y, GameHelper.HEALTH_BOX_WIDTH, GameHelper.HEALTH_BOX_HEIGHT);

        double healthPercent = (health < 0) ? 0 : ((double) health / maxHealth);
        int healthWidth = (int) (healthPercent * GameHelper.HEALTH_BOX_WIDTH);

        g.setColor(new Color((int) ((1 - healthPercent) * 255), (int) (healthPercent * 255), 0));

        g.fillRect(x + GameHelper.HEALTH_BOX_WIDTH - healthWidth, y, healthWidth, GameHelper.HEALTH_BOX_HEIGHT);
    }

    @Override
    public void userTankDead() {
        userTankDead = true;
    }

    @Override
    public void computerTankDead(Tank tank) {
        computerTanks.remove(tank);

        if (computerTanks.isEmpty()) {
            computerTanksDead = true;
        }
    }

    public class KeyInputHandler extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = true;
                rightPressed = false;
                upPressed = false;
                downPressed = false;
                firePressed = false;
            }

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
                leftPressed = false;
                upPressed = false;
                downPressed = false;
                firePressed = false;
            }

            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upPressed = true;
                firePressed = false;
                leftPressed = false;
                rightPressed = false;
                downPressed = false;
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downPressed = true;
                firePressed = false;
                leftPressed = false;
                rightPressed = false;
                upPressed = false;
            }

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                firePressed = true;
                upPressed = false;
                leftPressed = false;
                rightPressed = false;
                downPressed = false;
            }
        }
    }

    private void initMap(String fileName) throws IOException {
        Scanner scanner = null;
        try {
            File f = new File(fileName);
            scanner = new Scanner(f);

            width = scanner.nextInt();
            height = scanner.nextInt();
            scanner.nextLine();

            map = new char[height][width];

            for (int i = 0; i < height; i++) {
                String line = scanner.nextLine();

                for (int j = 0; j < width; j++) {
                    map[i][j] = line.charAt(j);

                    if (map[i][j] == GameHelper.COMPUTER_TANK) {
                        ComputerTank computerTank = new ComputerTank();
                        computerTank.setX(j);
                        computerTank.setY(i);
                        computerTanks.add(computerTank);

                    } else if (map[i][j] == GameHelper.USER_TANK) {
                        userTank = new UserTank();
                        userTank.setX(j);
                        userTank.setY(i);
                    }
                }
            }

            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Ошибка при чтении карты из файла!");
        }
    }

    private boolean wasComputerTankInjured(Tank computerTank) {

        return userTank.canOvertakeTarget(
                userTank.getX(),
                userTank.getY(),
                computerTank.getX(),
                computerTank.getY(),
                userTank.getDirection());
    }
}
