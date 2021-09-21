package ca.uwaterloo.cs349;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;

public class Gesture {
    public static final int N = 128;
    private ArrayList<CoordinatePoint> gesturePath;
    private String name;

    Gesture (ArrayList<CoordinatePoint> path, String _name, boolean absolute) {
        if (absolute) {
            // simply add the gesture, do not make modification to the path
            gesturePath = path;
            name = _name;
        }
    }

    Gesture (ArrayList<CoordinatePoint> path, String _name) {
        name = _name;

        // convert raw gesture to a stroke path
        gesturePath = new ArrayList<>();

        // special case: there is no path
        if (path.size() == 0) {
            for (int i = 0; i < N; i++) {
                gesturePath.add(new CoordinatePoint(0, 0));
            }
            return;
        }

        // calculate the overall length of the path
        float length = pathLength(path);
        // the sub interval between each points
        float interval = length / N;
        // divide the path into a gesture path by splitting into equal sub intervals
        toGesture(path, interval);

        // find the centroid of gesture points
        CoordinatePoint centroid = new CoordinatePoint(getCentroidX(), getCentroidY());
        // compute the rotate angle
        // theta = arcsin[(y_p-y_c)/d(p,c)], where c = centroid, p = start of the path
        // if x_c > x_p and y_c > y_p, we need to change theta = PI - theta
        // if x_c > x_p and y_c < y_p, we need to change theta = PI - theta
        // if x_c < x_p and y_c < y_p, we need to change theta = 2PI - theta
        CoordinatePoint start = gesturePath.get(0);
        float theta = (float) Math.asin(((centroid.getY() - start.getY()) / getDistance(start.getX(), start.getY(), centroid.getX(), centroid.getY())));
        if (centroid.getX() > start.getX()) theta = (float) Math.PI - theta;
        else if (centroid.getX() < start.getX() && centroid.getY() < start.getY()) theta = 2 * (float) Math.PI + theta;

        // rotate all points about the centroid based on the angle computed above
        for (int i = 0; i < N; i++) {
            rotate(gesturePath.get(i), centroid, theta);
        }

        // Translate all points so that the centroid is at origin
        for (int i = 0; i < N; i++) {
            translate(gesturePath.get(i), centroid);
        }
        centroid.setX(0);
        centroid.setY(0);

        // Scale all points so that they fit in a 100px box
        float ratio = getScaleRatio();
        for (int i = 0; i < N; i++) {
            scale(gesturePath.get(i), ratio);
        }
    }

    public CoordinatePoint getCoordinateAt (int i) {
        if (i < 0 || i >= N) return new CoordinatePoint(0,0);
        else return gesturePath.get(i);
    }

    public String getName () {
        return name;
    }

    public float pathLength (ArrayList<CoordinatePoint> path) {
        int len = path.size();
        float result = 0;
        for (int i = 0; i < len - 1; i++) {
            // distance between adjacent 2 points
            result += getDistance(path.get(i).getX(), path.get(i).getY(), path.get(i+1).getX(), path.get(i+1).getY());
        }
        return result;
    }

    public void toGesture (ArrayList<CoordinatePoint> path, float interval) {
        // if path has only 1 index
        if (path.size() == 1) {
             CoordinatePoint p = path.get(0);
             for (int i = 0; i < N; i++) {
                 gesturePath.add(new CoordinatePoint(p.getX(), p.getY()));
             }
             return;
        }

        int currIndex = 0;
        float currX = path.get(currIndex).getX();
        float currY = path.get(currIndex).getY();
        int counter = 1;

        gesturePath.add(new CoordinatePoint(currX, currY));

        while (counter < N - 1 && currIndex < path.size() - 1) {
            float currInterval = interval;
            float nextX = path.get(currIndex + 1).getX();
            float nextY = path.get(currIndex + 1).getY();
            // first check if the distance between current point and next point is greater than interval
            float d = getDistance(currX, currY, nextX, nextY);
            if (d > currInterval) {
                // we are still moving towards next index
                float ratio = currInterval / d;
                currX = currX + (nextX - currX) * ratio;
                currY = currY + (nextY - currY) * ratio;
            }
            else {
                // we have reached the next interval
                while (d <= currInterval) {
                    currIndex++;
                    currX = path.get(currIndex).getX();
                    currY = path.get(currIndex).getY();
                    nextX = path.get(currIndex + 1).getX();
                    nextY = path.get(currIndex + 1).getY();
                    // decrease the interval and check from next point
                    currInterval -= d;
                    d = getDistance(currX, currY, nextX, nextY);
                }
                float ratio = currInterval / d;
                currX = currX + (nextX - currX) * ratio;
                currY = currY + (nextY - currY) * ratio;
            }
            counter++;
            gesturePath.add(new CoordinatePoint(currX, currY));
        }

        currIndex = path.size() - 1;
        currX = path.get(currIndex).getX();
        currY = path.get(currIndex).getY();
        gesturePath.add(new CoordinatePoint(currX, currY));
    }

    public float getDistance (float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void print () {
        System.out.print("[");
        for (int i = 0; i < gesturePath.size(); i++) {
            gesturePath.get(i).print();
            if (i != gesturePath.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.print("]");
        System.out.println();
    }

    public float getCentroidX () {
        float sum = 0;
        for (int i = 0; i < N; i++) {
            sum += gesturePath.get(i).getX();
        }
        return sum / N;
    }

    public float getCentroidY () {
        float sum = 0;
        for (int i = 0; i < N; i++) {
            sum += gesturePath.get(i).getY();
        }
        return sum / N;
    }

    public void rotate (CoordinatePoint p, CoordinatePoint c, float theta) {
        float sin = (float) Math.sin(theta);
        float cos = (float) Math.cos(theta);

        float cx = c.getX();
        float cy = c.getY();
        float px = p.getX();
        float py = p.getY();

        // translate point back to origin:
        px -= cx;
        py -= cy;

        // rotate point
        float xnew = px * cos - py * sin;
        float ynew = px * sin + py * cos;

        // translate point back:
        px = xnew + cx;
        py = ynew + cy;

        p.setX(px);
        p.setY(py);
    }

    public void translate (CoordinatePoint p, CoordinatePoint c) {
        p.setX(p.getX() - c.getX());
        p.setY(p.getY() - c.getY());
    }

    public void scale (CoordinatePoint p, float ratio) {
        p.setX(p.getX() * ratio);
        p.setY(p.getY() * ratio);
    }

    public float getScaleRatio () {
        // get max/min x & y
        float minX = 0;
        float maxX = 0;
        float minY = 0;
        float maxY = 0;

        for (int i = 0; i < N; i++) {
            float px = gesturePath.get(i).getX();
            float py = gesturePath.get(i).getY();
            if (px > maxX) maxX = px;
            else if (px < minX) minX = px;
            if (py > maxY) maxY = py;
            else if (py < minY) minY = py;
        }

        // calculate the max vertical/horizontal distance
        float d = Math.max(maxX - minX, maxY - minY);

        // return the ratio as 100 / d
        return 100 / d;
    }

    // return the match distance between 2 gestures
    // lower match distance means higher match
    public float getMatchDistance (Gesture g) {
        float sum = 0;
        for (int i = 0; i < N; i++) {
            CoordinatePoint s = gesturePath.get(i);
            CoordinatePoint t = g.getCoordinateAt(i);
            sum += getDistance(s.getX(), s.getY(), t.getX(), t.getY());
        }
        return sum / N;
    }

    // get image bitmap
    public Bitmap getImageBitmap () {
        @SuppressLint("SdCardPath") File imgFile = new File("/data/user/0/ca.uwaterloo.cs349/app_imageDir/" + name + ".png");
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        else {
            System.out.println("Failed to get image for " + name);
            return null;
        }
    }
}
