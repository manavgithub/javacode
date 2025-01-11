import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JFrame {

    private static final int GRID_SIZE = 20;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final Color SNAKE_COLOR = Color.GREEN;
    private static final Color FOOD_COLOR = Color.RED;
    private static final Color BG_COLOR = Color.BLACK;
    private static final int FPS = 10;
    private static final int DELAY = 1000 / FPS;

    private LinkedList<Point> snake;
    private Point direction;
    private Point food;
    private boolean gameOver;
    private GamePanel gamePanel;
    private Timer timer;
    private boolean godMode = false;

    public SnakeGame() {
        setTitle("Simple Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(GRID_WIDTH * GRID_SIZE, GRID_HEIGHT * GRID_SIZE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);
        initGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        setFocusable(true);
        setVisible(true);
        startGameLoop();
    }

    private void initGame() {
        snake = new LinkedList<>();
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        direction = new Point(1, 0); // Initial direction: right
        food = generateFood();
        gameOver = false;
    }

    private void startGameLoop() {
        timer = new Timer(DELAY, e -> {
            if (!gameOver) {
                moveSnake();
                // Modified check collision conditional
                if(!godMode) {
                    checkCollision();
                }

            } else {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over");
                System.exit(0);
            }
            gamePanel.repaint();
        });
        timer.start();
    }

    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = new Point(head.x + direction.x, head.y + direction.y);
        //Modified move method so it wraps around walls
        if (newHead.x >= GRID_WIDTH) {
            newHead.x = 0;
        } else if (newHead.x < 0) {
            newHead.x = GRID_WIDTH - 1;
        }
        if (newHead.y >= GRID_HEIGHT) {
            newHead.y = 0;
        } else if (newHead.y < 0) {
            newHead.y = GRID_HEIGHT - 1;
        }

        snake.addFirst(newHead);
        if (newHead.equals(food)) {
            food = generateFood();
        } else {
            snake.removeLast();
        }
    }

    private Point generateFood() {
        Random random = new Random();
        while (true) {
            Point position = new Point(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT));
            if (!snake.contains(position)) {
                return position;
            }
        }
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP && direction.y != 1) {
            direction = new Point(0, -1);
        } else if (key == KeyEvent.VK_DOWN && direction.y != -1) {
            direction = new Point(0, 1);
        } else if (key == KeyEvent.VK_LEFT && direction.x != 1) {
            direction = new Point(-1, 0);
        } else if (key == KeyEvent.VK_RIGHT && direction.x != -1) {
            direction = new Point(1, 0);
        } else if (key == KeyEvent.VK_G) {
            godMode = !godMode;
            if (godMode) {
                System.out.println("God mode ON");
            } else {
                System.out.println("God mode OFF");
            }
        }
    }

    private void checkCollision() {
        Point head = snake.getFirst();
        // Check walls
        if (!(0 <= head.x && head.x < GRID_WIDTH && 0 <= head.y && head.y < GRID_HEIGHT)) {
            gameOver = true;
            return;
        }
        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                return;
            }
        }
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGame(g);
        }

        private void drawGame(Graphics g) {
            g.setColor(BG_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draw snake
            g.setColor(SNAKE_COLOR);
            for (Point p : snake) {
                g.fillRect(p.x * GRID_SIZE, p.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            }

            // Draw food by the chitresh
            g.setColor(FOOD_COLOR);
            g.fillRect(food.x * GRID_SIZE, food.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            // Draw Score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Score: " + (snake.size() - 1), 10, 20);
            if (godMode) {
                g.drawString("God Mode: ON", 10, 40);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }
}