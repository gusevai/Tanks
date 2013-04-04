/**
 * Created with IntelliJ IDEA.
 * User: Иришка
 * Date: 18.11.12
 */
public class Cell {
    private int x;
    private int y;

    private Cell parent;
    private int weight;

    public Cell() {
    }

    public Cell(int x, int y, Cell parent, int weight) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.weight = weight;
    }

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

    public Cell getParent() {
        return parent;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Cell) {
            Cell cell = (Cell) obj;
            return ((x == cell.getX()) && (y == cell.getY()));
        }

        return false;
    }
}
