import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class SnakeGame extends Application {

    private ArrayList<int[]> snake;             // This is snake long as an arrayList.

    private int[] food;                    // Food coordinates as an integer list.

    private Color foodColor;                // Color of food.

    private Text foodText;                  // Information about food at the end of the game.
    private Text newGameText;                  // Information about the new game.

    private int gameWidth = 800;                // Witdth of the game.
    private int gameHeight = 600;               // Height of the game.
    private int blockSize = 50;                 // Block size of the game.
    private int eatenFood = 0;                  // Eaten food count is start with zero.

    private String direction = "RIGHT";             // The snake direction is going to be right at the beginning of the game.

    private boolean running = false;               // Since the snake is not moving at the beginning of the game, running must be false.
    private boolean gameOver = false;               // gameOver must be false at the beginning of course.

    private AudioClip eatSound;                    // Audio of the game for eat sound.
    private AudioClip gameOverSound;                   // Audio of the game for game over.


    /**
     * The start method in a JavaFX application is the entry point for your application's main UI setup.
     * This setup initializes the game, arranges the start screen, updates the game state periodically
     * while drawing the necessary graphics on the screen according to input as keys, and also the sound effects are loaded here.
     */
    public void start(Stage primaryStage)  {
        Canvas canvas = new Canvas(gameWidth, gameHeight);         // Creates canvas object with specific height and width.
        GraphicsContext gc = canvas.getGraphicsContext2D();         // GraphicsContext is used for drawing shapes, text, and images on the canvas.

        Group root = new Group();             // Group is a container component that does not apply any speacial layout for the children.
        root.getChildren().add(canvas);            // Adds the canvas to the group.
        Scene scene = new Scene(root, gameWidth, gameHeight, Color.BLACK);          // Creates a Scene with the root group, width, height, and background color as a black.

        snake = new ArrayList<>();              // Initialize the snake an ArrayList.
        snake.add(new int[]{gameWidth/2, gameHeight/2});        // Adds the initial position of the snake to the list. Here initial position is center of the game.

        generateFood();         // Calls generateFood method to place the initial food item on the screen.

        // Arranges the color, position, and font of the foodText also gives information about count of the eaten foods.
        foodText = new Text("Your score: " + eatenFood); 
        foodText.setFill(Color.WHITE);
        foodText.setX(20);
        foodText.setY(40);
        foodText.setFont(new Font("Arial", 18));
        root.getChildren().add(foodText);           // Adds the foodText to the group.

        newGameText = new Text("Press space to replay the game.");           // Initialize the newGameText for information of the new game.
        root.getChildren().add(newGameText);          // Adds the newGameText to the group.

        URL eatSoundUrl = getClass().getResource("eat_sound.wav");          // Loads sound file "eat_sound.wav" is wav file in the same folder.
        eatSound = new AudioClip(eatSoundUrl.toString());                   // Converts the URLs to AudioClip objects for playing the sound.

        URL gameOverSoundUrl = getClass().getResource("gameOver_sound.wav");          // Loads sound file "gameOver_sound.wav" is wav file in the same folder.
        gameOverSound = new AudioClip(gameOverSoundUrl.toString());            // Converts the URLs to AudioClip objects for playing the sound.

        scene.setOnKeyPressed(this:: handleKeyPressed);                // When a key is pressed, the handleKeyPressed method is called to handle the event.

        primaryStage.setScene(scene);           // Sets the scene for the primaty stage (main window).
        primaryStage.setTitle("Snake Game");            //Sets the title of the window to "Snake Game".
        primaryStage.show();               // Shows the primary stage, making it visible on the screen.

        AnimationTimer timer = new AnimationTimer() {        // Creates an AnimationTimer objects, which is used to create a game loop.
            private long lastUpdate = 0;        // Initialize the lastUpdate as 0 at the beginning of the game.             

            /**
             * The handle method is called periodically ( approximately every 200 ms.).
             */
            public void handle(long now)   {                    
                if (now - lastUpdate >=200_000_000)    {  

                    if (running)   {            // If the game is running,
                        update();              // calls update method and updates the game.
                        draw(gc);               // calls draw method and redraws the game state.
                    
                    } else if(gameOver) {            // If the game is over or in other words gameOver is true anymore,
                        drawGameOverScreen(gc);         // calls the drawGameOverScreen and draw game over screen.
                    
                    }  else  {              // If the game is not running and not over,
                        drawStartScreen(gc);           // calls the drawStartScreen method and initializes the start screen.
                    }

                    lastUpdate = now;       // Updates the lastUpdate to ensure the timer runs correctly.
                }
            }
        };
        timer.start();
    }


    /**
     * The handleKeyPressed method allows the player to control the direction of the snake using the arrow keys 
     * and to start, pause, or reset the game with the space key. It ensures that the snake cannot reverse direction, 
     * handles pausing and resuming the game, and resets the game state when the game is over.
     * @param event the KeyEvent representing the key press event.
     */
    private void handleKeyPressed(KeyEvent event)   {
        KeyCode code = event.getCode();               // This line retrieves the code of the key that was pressed.

        // The switch statement checks the "code" determine which key was pressed and executes the corresponding block of code.
        // Checks with the if statements is in the switch because of the checks the snake cannot turn directly opposite direction.
        switch(code)  {

            case UP:             // If code is UP
                if (!direction.equals("DOWN"))  {          // And also if direction is not DOWN,
                    direction = "UP";               // the direction can be UP.
                    break;
                }

            case DOWN:          // If the code is DOWN
                if(!direction.equals("UP"))    {           // And also if the direction is not UP,
                    direction = "DOWN";             // the direction can be DOWN.        
                    break;
                }

            case LEFT:          // If the code is LEFT
                if(!direction.equals("RIGHT"))     {       // And also if the direction is not RIGHT,
                    direction = "LEFT";             // the direction can be LEFT.
                    break;
                }

            case RIGHT:         // If the code is RIGHT
                if(!direction.equals("LEFT"))    {        // And also if the direction is not LEFT,
                    direction = "RIGHT";            // the direction can be RIGHT.
                    break;
                }

            case SPACE:         // If the code is SPACE,

                if(gameOver)   {             // If gameOver is true in other words game is over,
                    
                    gameOver = false;           // resets gameOver as false for prepare for playing again to new game.
                    running = true;             // resets running as false for prepare for playing again to new game.
                    
                    eatenFood = 0;                // resets eatenFood in other words count of the eaten food. 

                    // Arranges and updates the current foodText.
                    foodText.setX(20);
                    foodText.setY(40);
                    foodText.setFont(new Font("Arial", 18));
                    foodText.setText("Your score: " + eatenFood);

                    // Arranges the newGameText as a unvisiable for the new game.
                    newGameText.setX(gameWidth + 50);
                    newGameText.setY(gameHeight + 50);

                    snake.clear();              // Resets the snake.
                    snake.add(new int[]{gameWidth/2, gameHeight/2});          // Inıtialize the new snake for next game.

                    generateFood();               // Calls generateFood method and initializes the new food.

                    direction = "RIGHT";          // Resets the direction as RIGHT again.

                } else  {               // If the game is not over yet,
                    running =! running;        // pressing space will pause or resume the game by toggling the running variable.  
                } 

                break;

            default:              // The default case is required for compeleteness but doesn't do anything in this case.
                break;
            
        }      
    }


    /**
     * The generateFood method creates a new food item at a random position on the game grid and assigns it a random color.
     */
    private void generateFood()  {
        Random random = new Random();           // A random object is created to generate random numbers.
        food = new int[]{random.nextInt(gameWidth/50)*50, random.nextInt(gameHeight/50)*50};            // A new position for the food is generated.

        foodColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));           // Takes random integer and determines color for the initialize food color.
    }


    /**
     * The update method moves the snake in the current direction, checks for collisions, handles eating food, and updates the snake's position accordingly.
     * The method ensures that the game stops and triggers the game over state if the snake collides with the walls or itself.
     */
    private void update()   {
        int[] head = snake.get(0);          // The current position of the snake's head is retrieved from the snake list.
        int[] newHead = new int[2];             // newHead array is creatd to store the new position of the snake's head after it moves.

        // Depending on the current direction of movement ("UP", "DOWN", "LEFT", "RIGHT"), the new position of the head is calculated.
        switch(direction)   {
            case "UP":
                newHead[0] = head[0];
                newHead[1] = head[1] - blockSize;
                break;
            case "DOWN":
                newHead[0] = head[0];
                newHead[1] = head[1] + blockSize;
                break;
            case "RIGHT":
                newHead[0] = head[0] + blockSize;
                newHead[1] = head[1];
                break;
            case "LEFT":
                newHead[0] = head[0] - blockSize;
                newHead[1] = head[1];
                break;
            default:
                break;
        }

        // This part checks for collisions. 
        // The first condition checks if the snake's head has collided with any part of its body.
        // The second codition checks if the snake's head has collided with any part of its body.
        // If either condition is true, the game is stopped ("running = false") and the game over state set ("gameOver" = true). The method then returns early.
        if(newHead[0] < 0 || newHead[0] >= gameWidth || newHead[1] < 0 || newHead[1] >= gameHeight ||
                snake.stream().anyMatch(block -> block[0] == newHead[0] && block[1] == newHead[1]))  {
            running =  false;
            gameOver = true;
            gameOverSound.play();
            return;
        }

        snake.add(0, newHead);          // The new head position is added to the begging of the snake list.

        // If the snake ate the food, the "snake.remove" call is skipped, effecively increasing the snake's length by one segment.
        if(newHead[0] == food[0] && newHead[1] == food[1])   {           // If the snake's haed is at the same position as the food,
            eatenFood++;            // Add one to eatenFood.
            eatSound.play();            // Play the eating sound.
            foodText.setText(" Your score: " + eatenFood);      // Update the score.
            generateFood();             // A new food item is generated.
        
        // If the snake didn't eat the food, the last segment of the snake is removed, keeping the snake's length constant.
        } else {
            snake.remove(snake.size()- 1);
        }
    }  


    /**
     * The draw method uses the "GraphicsContext" to draw the game elements on the "Canvas". 
     * It handles drawing the background, the food, and each segment of the snake.
     * @param gc
     */
    private void draw(GraphicsContext gc)  {
        // The entire canvas is filled with black to clear any previous drawings, thanks to that the screen is updated correctly with each frame.
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,gameWidth, gameHeight);

        // The food is drawn as a circle at the coordinates specified by "food[0]" and "food[1]".
        gc.setFill(foodColor);
        gc.fillOval(food[0], food[1], blockSize, blockSize);

        // A loop iterates over each segment of the snake (eaach segment is an "int[]" containing the x and y coordinates).
        gc.setFill(Color.GREEN);                // The GraphicsContext is set to fill with green color for snake.
        for(int[] block: snake)  {
            gc.fillRect(block[0], block[1], blockSize, blockSize);
        }
    }


    /**
     * The drawStartScreen method uses GraphicsContext to draw a start screen message on the "canvas".
     * @param gc   The GraphicsContext used to draw on the canvas.
     */
    private void drawStartScreen(GraphicsContext gc)   {
        // The entire canvas is filled with black to clear any previous drawings, thanks to that the screen is updated correctly with each frame.
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // Draws the start screen with the message "Press Space to Start".
        gc.setFont(new Font("Arial", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("Press SPACE to Start", gameWidth*3/8, gameHeight/2);
    }
    

    /**
     * Draws the game over screen with the message "Game Over!", total score information as "Total score is ", and necessary direction to start the neew game as "Press space to replay the game.".  
     * @param gc    The GraphicsContext used to draw on the canvas.
     */
    private void drawGameOverScreen(GraphicsContext gc)  {
        // Fill the entire canvas with a red color to indicate that game is over.
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // Set the fond to Arial with a size of 25 and set the fill color fır the text to white.
        gc.setFont(new Font("Arial", 30));
        gc.setFill(Color.WHITE);
        gc.fillText("Game Over!", gameWidth*3/8 + 15, gameHeight/2);

        // Give information about score in other word eaten food count at the end of the game.
        foodText.setX(335);
        foodText.setY(530);
        foodText.setFont(new Font("Arial", 18));
        foodText.setText("Total score is " + eatenFood);

        // Give information about the what should you do to start a new game.
        newGameText.setFill(Color.WHITE);
        newGameText.setX(275);
        newGameText.setY(555);
        newGameText.setFont(new Font("Arial", 18));

    }


    /**
     * The main entry point for the application.
     * @param args   The command line arguments passed to the application.
     */
    public static void main(String[] args)   {
        launch(args);
    }
}
