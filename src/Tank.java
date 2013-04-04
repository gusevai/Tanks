import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Иришка
 * Date: 18.11.12
 */
public abstract class Tank extends GameObject {
    protected boolean fire;
    protected int health;

    protected Image tankSouth;
    protected Image tankNorth;
    protected Image tankWest;
    protected Image tankEast;

    protected boolean injured;

    private TankDeathListener tankDeathListener;

    public Tank() {
        direction = Direction.LEFT;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }

    public boolean isInjured() {
        return injured;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;

        if (injured) {
            health -= GameHelper.DAMAGE;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void draw(Graphics g) {
        Image tankImage = null;

        switch (direction) {
            case UP:
                tankImage = tankNorth;
                break;
            case DOWN:
                tankImage = tankSouth;
                break;
            case LEFT:
                tankImage = tankWest;
                break;
            case RIGHT:
                tankImage = tankEast;
                break;
        }

        int scaledY = y * GameHelper.SCALE;
        int scaledX = x * GameHelper.SCALE;

        switch (action) {
            case MOVE_DOWN:
                scaledY = (int) ((y - 1 + actionCompletedPercent) * GameHelper.SCALE);
                break;

            case MOVE_UP:
                scaledY = (int) ((y + 1 - actionCompletedPercent) * GameHelper.SCALE);
                break;

            case MOVE_LEFT:
                scaledX = (int) ((x + 1 - actionCompletedPercent) * GameHelper.SCALE);
                break;

            case MOVE_RIGHT:
                scaledX = (int) ((x - 1 + actionCompletedPercent) * GameHelper.SCALE);
                break;
        }

        if (injured) {
            g.setColor(Color.RED);
            g.fillRect(scaledX, scaledY, GameHelper.SCALE, GameHelper.SCALE);
        }

        g.drawImage(tankImage, scaledX, scaledY, GameHelper.SCALE, GameHelper.SCALE, null);
    }

    protected boolean isCellPassable(int desiredX, int desiredY) {
        if ((desiredX == width) || (desiredX < 0)) {
            return false;
        }

        if ((desiredY == height) || (desiredY < 0)) {
            return false;
        }

        if ((map[desiredY][desiredX] == GameHelper.BRICK)
                || (map[desiredY][desiredX] == GameHelper.WATER)) {
            return false;
        }

        return true;
    }

    public TankDeathListener getTankDeathListener() {
        return tankDeathListener;
    }

    public void setTankDeathListener(TankDeathListener tankDeathListener) {
        this.tankDeathListener = tankDeathListener;
    }

    public boolean canOvertakeTarget(int x, int y, int targetX, int targetY, Direction direction) {
        if (((direction == null) || direction.equals(Direction.UP)) && (x == targetX) && (y > targetY)) {
            for (int i = targetY; i < y; i++) {
                if (map[i][x] == GameHelper.BRICK) {
                    return false;
                }
            }
            return true;

        } else if (((direction == null) || direction.equals(Direction.DOWN)) && (x == targetX) && (y < targetY)) {
            for (int i = y; i < targetY; i++) {
                if (map[i][x] == GameHelper.BRICK) {
                    return false;
                }
            }
            return true;

        } else if (((direction == null) || direction.equals(Direction.LEFT)) && (x > targetX) && (y == targetY)) {
            for (int i = targetX; i < x; i++) {
                if (map[y][i] == GameHelper.BRICK) {
                    return false;
                }
            }
            return true;

        } else if (((direction == null) || direction.equals(Direction.RIGHT)) && (x < targetX) && (y == targetY)) {
            for (int i = x; i < targetX; i++) {
                if (map[y][i] == GameHelper.BRICK) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
