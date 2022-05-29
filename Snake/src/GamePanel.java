import java.awt.*;
import java.awt.event.*;
import java.nio.file.DirectoryIteratorException;

import javax.swing.*;
import java.util.Random;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25; // controls the size of each item
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; // calculating how many units fits in the
																				// total area
	static final int DELAY = 200;
	// the 2 arrays are going to hold all of the coordinates for all the body parts
	// of the snake, including the head of the snake
	final int coordinatesX[] = new int[GAME_UNITS];
	final int coordinatesY[] = new int[GAME_UNITS];
	int bodyParts = 2;
	int applesEaten;
	int appleX; // x coordinate of where the apple it's going to appear randomly
	int appleY; // y coordinate of where the apple it's going to appear randomly
	char direction = 'R'; // R for right | L for left | U for up and D for down
	boolean isGameRunning = false;
	Timer timer;
	Random random;

	GamePanel() {
		// this are the parameter we need to show the panel and then star the game
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());

		startGame();
	}

	public void startGame() {
		newApple();
		isGameRunning = true; // changing the parameter to true, then the game can start
		timer = new Timer(DELAY, this); // how fast the game is running
		timer.start();
	}

	public void paintComponent(Graphics graphic) {
		super.paintComponent(graphic);
		draw(graphic);
	}

	public void draw(Graphics graphic) {
		if (isGameRunning) {
			// this for turns the panel into a grid so it's easier to see the size of any
			// unit
			for (int lineGrid = 0; lineGrid < SCREEN_HEIGHT / UNIT_SIZE; lineGrid++) {
				graphic.drawLine(lineGrid * UNIT_SIZE, 0, lineGrid * UNIT_SIZE, SCREEN_HEIGHT);
				graphic.drawLine(0, lineGrid * UNIT_SIZE, SCREEN_WIDTH, lineGrid * UNIT_SIZE);
			}
			// drawing the apple
			graphic.setColor(Color.red);
			graphic.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			// creating snnake's body
			for (int littleBodyPiece = 0; littleBodyPiece < bodyParts; littleBodyPiece++) {
				if (littleBodyPiece == 0) {
					graphic.setColor(Color.green);
					graphic.fillRect(coordinatesX[littleBodyPiece], coordinatesY[littleBodyPiece], UNIT_SIZE,
							UNIT_SIZE);
				} else {
					graphic.setColor(new Color(45, 180, 0));
					// graphic.setColor(new Color(random.nextInt(255), random.nextInt(255),
					// random.nextInt(255))); // snake with random body color
					graphic.fillRect(coordinatesX[littleBodyPiece], coordinatesY[littleBodyPiece], UNIT_SIZE,
							UNIT_SIZE);
				}
			}
			// drawing the current score
			graphic.setColor(Color.red);
			graphic.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(graphic.getFont());
			graphic.drawString("Score: " + applesEaten,
					(SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, graphic.getFont().getSize());

		} else {
			gameOver(graphic);
		}
	}

	public void newApple() {
		// generate the coordinates of a new apple whenever this method is called
		appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
	}

	public void move() {
		for (int indexMoveBody = bodyParts; indexMoveBody > 0; indexMoveBody--) {
			// shifting all the coordinates in this array over by one spot
			coordinatesX[indexMoveBody] = coordinatesX[indexMoveBody - 1];
			coordinatesY[indexMoveBody] = coordinatesY[indexMoveBody - 1];
		}

		// this switch will change the direciton of where the snake is headed
		switch (direction) {
		case 'U':
			coordinatesY[0] = coordinatesY[0] - UNIT_SIZE;
			break;

		case 'D':
			coordinatesY[0] = coordinatesY[0] + UNIT_SIZE;
			break;

		case 'L':
			coordinatesX[0] = coordinatesX[0] - UNIT_SIZE;
			break;

		case 'R':
			coordinatesX[0] = coordinatesX[0] + UNIT_SIZE;
			break;
		}

	}

	public void checkApple() {
		// checks if the coordinates of the snake's head matches with the apple's
		// coordinates
		if ((coordinatesX[0] == appleX) && (coordinatesY[0] == appleY)) {
			bodyParts++; // if matches, it'll increase the amount of body parts in one
			applesEaten++; // then will increase the amount of apples eaten
			newApple();

		}
	}

	public void checkCollisions() {
		// it'll check if head collides with body
		for (int checkBodyCollision = bodyParts; checkBodyCollision > 0; checkBodyCollision--) {
			// coordinateX[0] is the snake's head
			if ((coordinatesX[0] == coordinatesX[checkBodyCollision])
					&& (coordinatesY[0] == coordinatesY[checkBodyCollision])) {
				isGameRunning = false;
			}
		}
		// check if head touches left border
		if (coordinatesX[0] < 0) {
			isGameRunning = false;
		}
		// check if the head touches right border
		if (coordinatesX[0] > SCREEN_WIDTH) {
			isGameRunning = false;
		}
		// check if the head touches top border
		if (coordinatesY[0] < 0) {
			isGameRunning = false;
		}
		// check if the head touches bottom border
		if (coordinatesY[0] > SCREEN_HEIGHT) {
			isGameRunning = false;
		}

		if (!isGameRunning) {
			timer.stop();
		}
	}

	public void gameOver(Graphics graphic) {
		// display the current score
		graphic.setColor(Color.red);
		graphic.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metricsScore = getFontMetrics(graphic.getFont());
		graphic.drawString("Score: " + applesEaten,
				(SCREEN_WIDTH - metricsScore.stringWidth("Score: " + applesEaten)) / 2, graphic.getFont().getSize());

		// GameOver text
		graphic.setColor(Color.red);
		graphic.setFont(new Font("Ink Free", Font.BOLD, 60));
		FontMetrics metrics = getFontMetrics(graphic.getFont());
		// this will put right in the middle of the screen " G A M E O V E R"
		graphic.drawString(" G A M E  O V E R", (SCREEN_WIDTH - metrics.stringWidth("\" G A M E  O V E R")) / 2,
				SCREEN_HEIGHT / 2);

		startGame();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isGameRunning) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();

	}

	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()) {

			// triggered when the left arrow is pressed
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;

			// triggered when the right arrow is pressed
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;

			// triggered when the up arrow is pressed
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;

			// triggered when the down arrow is pressed
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}

}
