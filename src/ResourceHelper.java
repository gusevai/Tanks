import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Иришка
 * Date: 02.03.13
 */
public class ResourceHelper {
    public static Image BRICK;
    public static Image WALL;

    public static Image WATER;

    public static Image TANK_SOUTH;
    public static Image TANK_NORTH;
    public static Image TANK_WEST;
    public static Image TANK_EAST;

    public static Image TANK_SOUTH_ORANGE;
    public static Image TANK_NORTH_ORANGE;
    public static Image TANK_WEST_ORANGE;
    public static Image TANK_EAST_ORANGE;

    public static final String MAP = "src\\resources\\maps\\map.txt";

    static {
        try {
            BRICK = ImageIO.read(new File("src\\resources\\icons\\brick.jpg"));
            WALL = ImageIO.read(new File("src\\resources\\icons\\wall.jpg"));

            WATER = ImageIO.read(new File("src\\resources\\icons\\water.jpg"));

            TANK_SOUTH = ImageIO.read(new File("src\\resources\\icons\\tankSouth.png"));
            TANK_WEST = ImageIO.read(new File("src\\resources\\icons\\tankWest.png"));
            TANK_NORTH = ImageIO.read(new File("src\\resources\\icons\\tankNorth.png"));
            TANK_EAST = ImageIO.read(new File("src\\resources\\icons\\tankEast.png"));

            TANK_SOUTH_ORANGE = ImageIO.read(new File("src\\resources\\icons\\tankSouthOrange.png"));
            TANK_WEST_ORANGE = ImageIO.read(new File("src\\resources\\icons\\tankWestOrange.png"));
            TANK_NORTH_ORANGE = ImageIO.read(new File("src\\resources\\icons\\tankNorthOrange.png"));
            TANK_EAST_ORANGE = ImageIO.read(new File("src\\resources\\icons\\tankEastOrange.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
