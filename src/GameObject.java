import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Иришка
 * Date: 20.11.12
 */
public abstract class GameObject {
    protected int x;
    protected int y;

    protected Action action = Action.DO_NOTHING;
    protected double actionCompletedPercent = 0;

    protected char[][] map;
    protected int width;
    protected int height;

    protected Direction direction;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public char[][] getMap() {
        return map;
    }

    public void setMap(char[][] map) {
        this.map = map;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public double getActionCompletedPercent() {
        return actionCompletedPercent;
    }

    public void setActionCompletedPercent(double actionCompletedPercent) {
        this.actionCompletedPercent = actionCompletedPercent;
    }

    public abstract void draw(Graphics g);

    public abstract void makeStep();
}
