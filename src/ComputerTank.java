import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Иришка
 * Date: 18.11.12
 */
public class ComputerTank extends Tank {
    private int targetX;
    private int targetY;

    List<Cell> way = new LinkedList<Cell>();

    public ComputerTank() {
        super();

        tankEast = ResourceHelper.TANK_EAST;
        tankWest = ResourceHelper.TANK_WEST;
        tankNorth = ResourceHelper.TANK_NORTH;
        tankSouth = ResourceHelper.TANK_SOUTH;

        health = GameHelper.COMPUTER_HEALTH;
    }

    @Override
    public void setInjured(boolean injured) {
        super.setInjured(injured);

        if (health == 0) getTankDeathListener().computerTankDead(this);
    }

    public List<Cell> getWay() {
        return way;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    public int getTargetX() {
        return targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public void findWayToTheTarget() {
        way.clear();

        if (canOvertakeTarget(x, y, targetX, targetY, null)) {
            return;
        }

        Cell targetCell = new Cell(targetX, targetY, null, 0);
        Cell startCell = new Cell(x, y, null, 1);

        LinkedList<Cell> front = new LinkedList<Cell>();
        LinkedList<Cell> closedList = new LinkedList<Cell>();
        LinkedList<Cell> tmpList = new LinkedList<Cell>();

        front.add(startCell);

        boolean found = false;
        boolean deadlock = false;

        while (!found && !deadlock) {
            for (Cell cell : new LinkedList<Cell>(front)) {
                front.remove(cell);
                closedList.add(cell);

                tmpList.clear();
                tmpList.add(new Cell(cell.getX() + 1, cell.getY(), cell, cell.getWeight() + 1));
                tmpList.add(new Cell(cell.getX(), cell.getY() + 1, cell, cell.getWeight() + 1));
                tmpList.add(new Cell(cell.getX(), cell.getY() - 1, cell, cell.getWeight() + 1));
                tmpList.add(new Cell(cell.getX() - 1, cell.getY(), cell, cell.getWeight() + 1));

                for (Cell neighbour : tmpList) {
                    if (!closedList.contains(neighbour)
                            && !front.contains(neighbour)
                            && isCellPassable(neighbour.getX(), neighbour.getY())) {
                        front.add(neighbour);

                        if (canOvertakeTarget(
                                neighbour.getX(),
                                neighbour.getY(),
                                targetX,
                                targetY,
                                null
                        )) {
                            targetCell = neighbour;
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (front.isEmpty()) {
                deadlock = true;
            }
        }

        if (!deadlock) {
            Cell current = targetCell;
            way.add(current);

            while (!current.getParent().equals(startCell)) {
                way.add(current.getParent());
                current = current.getParent();
            }
        }

        Collections.reverse(way);
    }

    @Override
    public void makeStep() {
        findWayToTheTarget();

        fire = canOvertakeTarget(x, y, targetX, targetY, direction);
        if (fire) {
            action = Action.FIRE;
            return;
        }

        if (!way.isEmpty()) {
            int desiredX = way.get(0).getX();
            int desiredY = way.get(0).getY();

            boolean rotate = rotate(desiredX, desiredY);

            if (!rotate) {
                if (x > desiredX) {
                    action = Action.MOVE_LEFT;
                } else if (x < desiredX) {
                    action = Action.MOVE_RIGHT;
                } else if (y > desiredY) {
                    action = Action.MOVE_UP;
                } else if (y < desiredY) {
                    action = Action.MOVE_DOWN;
                }

                x = desiredX;
                y = desiredY;

            } else {
                action = Action.ROTATE;
            }
        } else {
            boolean rotate = rotate(targetX, targetY);

            if (rotate) {
                action = Action.ROTATE;
            } else {
                action = Action.DO_NOTHING;
            }
        }
    }

    private boolean rotate(int desiredX, int desiredY) {
        if (x != desiredX) {
            if (x > desiredX) {
                if (direction.equals(Direction.LEFT)) {
                    return false;
                }
                if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
                    direction = Direction.LEFT;
                    return true;
                }
                if (direction.equals(Direction.RIGHT)) {
                    direction = Direction.DOWN;
                    return true;
                }
            }
            if (x < desiredX) {
                if (direction.equals(Direction.RIGHT)) {
                    return false;
                }
                if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
                    direction = Direction.RIGHT;
                    return true;
                }
                if (direction.equals(Direction.LEFT)) {
                    direction = Direction.DOWN;
                    return true;
                }
            }
        }
        if (y != desiredY) {
            if (y < desiredY) {
                if (direction.equals(Direction.DOWN)) {
                    return false;
                }
                if (direction.equals(Direction.LEFT) || direction.equals(Direction.RIGHT)) {
                    direction = Direction.DOWN;
                    return true;
                }
                if (direction.equals(Direction.UP)) {
                    direction = Direction.LEFT;
                    return true;
                }
            }
            if (y > desiredY) {
                if (direction.equals(Direction.UP)) {
                    return false;
                }
                if (direction.equals(Direction.LEFT) || direction.equals(Direction.RIGHT)) {
                    direction = Direction.UP;
                    return true;
                }
                if (direction.equals(Direction.DOWN)) {
                    direction = Direction.LEFT;
                    return true;
                }
            }
        }

        return false;
    }
}
