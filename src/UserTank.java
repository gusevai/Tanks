/**
 * Created with IntelliJ IDEA.
 * User: Иришка
 * Date: 18.11.12
 */
public class UserTank extends Tank {
    boolean rightPressed;
    boolean leftPressed;
    boolean upPressed;
    boolean downPressed;
    boolean firePressed;

    public UserTank() {
        super();

        tankEast = ResourceHelper.TANK_EAST_ORANGE;
        tankWest = ResourceHelper.TANK_WEST_ORANGE;
        tankNorth = ResourceHelper.TANK_NORTH_ORANGE;
        tankSouth = ResourceHelper.TANK_SOUTH_ORANGE;

        health = GameHelper.USER_HEALTH;
    }

    @Override
    public void setInjured(boolean injured) {
        super.setInjured(injured);

        if (health == 0) getTankDeathListener().userTankDead();
    }

    public void makeStep() {

        if (firePressed) {
            fire = true;
            action = Action.FIRE;
            return;

        } else {
            fire = false;
        }

        if (rightPressed) {
            if (direction.equals(Direction.RIGHT)) {
                if (isCellPassable(x + 1, y)) {
                    x++;
                    action = Action.MOVE_RIGHT;

                } else {
                    action = Action.DO_NOTHING;
                }

            } else {
                direction = Direction.RIGHT;
                action = Action.ROTATE;
            }

        } else if (leftPressed) {
            if (direction.equals(Direction.LEFT)) {
                if (isCellPassable(x - 1, y)) {
                    x--;
                    action = Action.MOVE_LEFT;

                } else {
                    action = Action.DO_NOTHING;
                }

            } else {
                direction = Direction.LEFT;
                action = Action.ROTATE;
            }

        } else if (upPressed) {
            if (direction.equals(Direction.UP)) {
                if (isCellPassable(x, y - 1)) {
                    y--;
                    action = Action.MOVE_UP;

                } else {
                    action = Action.DO_NOTHING;
                }

            } else {
                direction = Direction.UP;
                action = Action.ROTATE;
            }

        } else if (downPressed) {
            if (direction.equals(Direction.DOWN)) {
                if (isCellPassable(x, y + 1)) {
                    y++;
                    action = Action.MOVE_DOWN;

                } else {
                    action = Action.DO_NOTHING;
                }

            } else {
                direction = Direction.DOWN;
                action = Action.ROTATE;
            }

        } else {
            action = Action.DO_NOTHING;
        }
    }

    public void setUserInput(boolean rightPressed,
                             boolean leftPressed,
                             boolean upPressed,
                             boolean downPressed,
                             boolean firePressed) {

        this.rightPressed = rightPressed;
        this.leftPressed = leftPressed;
        this.upPressed = upPressed;
        this.downPressed = downPressed;
        this.firePressed = firePressed;
    }
}
