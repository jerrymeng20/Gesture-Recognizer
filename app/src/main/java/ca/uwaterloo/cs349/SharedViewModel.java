package ca.uwaterloo.cs349;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static java.lang.Float.valueOf;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Gesture>> gestures;

    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shared model");

        ArrayList<Gesture> g = new ArrayList<>();
        gestures = new MutableLiveData<ArrayList<Gesture>>();
        gestures.setValue(g);

        File file = new File("/data/user/0/ca.uwaterloo.cs349/app_gestureDir/gestures.gst");
        if (file.exists()) {
            populateGestures(file);
        }
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String s) { mText.setValue(s);}

    public LiveData<ArrayList<Gesture>> getGestures() { return gestures;}

    public void addGesture (Gesture g, Context c) {
        gestures.getValue().add(g);
        saveToInternalStorage(c);
        System.out.println("added gesture: " + g.getName());
    }

    public void deleteGesture (Gesture g, Context c) {
        gestures.getValue().remove(g);
        saveToInternalStorage(c);
    }

    public void printGestures () {
        ArrayList<Gesture> g = gestures.getValue();
        for (int i = 0; i < g.size(); i++) {
            g.get(i).print();
        }
    }

    // save the gestures to internal storage
    public String saveToInternalStorage(Context c){
        ContextWrapper cw = new ContextWrapper(c);
        // path to /data/user/0/ca.uwaterloo.cs349/app_gestureDir
        File directory = cw.getDir("gestureDir", Context.MODE_PRIVATE);
        // Create gestureDir
        File mypath=new File(directory, "gestures.gst");

        try {
            PrintWriter writer = new PrintWriter(mypath);

            // print curve contents
            ArrayList<Gesture> g = gestures.getValue();
            int length = g.size();
            writer.println(length);

            for (int i = 0; i < length; i++) {
                writer.print(modify_name(g.get(i).getName()));
                writer.print(" ");
                for (int j = 0; j < Gesture.N; j++) {
                    writer.print(g.get(i).getCoordinateAt(j).getX() + " " + g.get(i).getCoordinateAt(j).getY());
                    writer.print(" ");
                }
                writer.println();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public void populateGestures (File file) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            int len = Integer.parseInt(line);

            for (int i = 0; i < len; i++) {
                line = reader.readLine();
                if (line == null) return;
                String[] splited = line.split("\\s+");
                String name = recover_name(splited[0]);

                ArrayList<CoordinatePoint> p = new ArrayList<>();
                for (int j = 0; j < Gesture.N; j++) {
                    // p_j = (splited[2j+1], splited[2j+2])
                    CoordinatePoint cp = new CoordinatePoint(valueOf(splited[2*j+1]), valueOf(splited[2*j+2]));
                    p.add(cp);
                }

                Gesture g = new Gesture(p, name, true);
                gestures.getValue().add(g);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // modify name to legal format
    public String modify_name (String name) {
        String dest = "";
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ') {
                dest += '_';
            }
            else {
                dest += name.charAt(i);
            }
        }
        return dest;
    }

    // recover name to original format
    public String recover_name (String name) {
        String dest = "";
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '_') {
                dest += ' ';
            }
            else {
                dest += name.charAt(i);
            }
        }
        return dest;
    }

    // is the name is occupied return the gesture with corresponding name
    public Gesture isNameOccupied (String name) {
        for (int i = 0; i < gestures.getValue().size(); i++) {
            if (gestures.getValue().get(i).getName().equals(name)) {
                return gestures.getValue().get(i);
            }
        }
        return null;
    }
}