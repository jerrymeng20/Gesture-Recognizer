package ca.uwaterloo.cs349;

public class CoordinatePoint {
    private float x;
    private float y;

    CoordinatePoint(float _x, float _y) {
        x = _x;
        y = _y;
    }

    public float getX () {return x;}
    public float getY () {return y;}

    public void setX (float _x) {x = _x;}
    public void setY (float _y) {y = _y;}

    public void print () {
        System.out.print("(" + x + ", " + y + ")");
    }
}
