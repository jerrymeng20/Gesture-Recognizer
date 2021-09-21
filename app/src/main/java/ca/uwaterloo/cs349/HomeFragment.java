package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SharedViewModel mViewModel;
    private RelativeLayout bg = null;

    private ArrayList<CoordinatePoint> path;
    private DrawingView dv;
    private ArrayList<Gesture> gestures;

    private ImageButton match_1;
    private ImageButton match_2;
    private ImageButton match_3;

    private TextView name_1;
    private TextView name_2;
    private TextView name_3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        textView.setText("Recognize your gesture here");

        mViewModel.getGestures().observe(getViewLifecycleOwner(), new Observer<ArrayList<Gesture>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Gesture> g) {
                gestures = g;
            }
        });

        path = new ArrayList<CoordinatePoint>();
        match_1 = root.findViewById(R.id.match_1);
        match_2 = root.findViewById(R.id.match_2);
        match_3 = root.findViewById(R.id.match_3);
        name_1 = root.findViewById(R.id.name_1);
        name_2 = root.findViewById(R.id.name_2);
        name_3 = root.findViewById(R.id.name_3);

        bg = root.findViewById(R.id.bg);
        dv = new DrawingView(this.getContext());
        bg.addView(dv);

        bg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touch_start(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touch_move(x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        touch_up();
                        break;
                }
                dv.onTouchEvent(event);
                return true;
            }
        });

        return root;
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        // make all buttons invisible
        match_1.setVisibility(View.INVISIBLE);
        match_2.setVisibility(View.INVISIBLE);
        match_3.setVisibility(View.INVISIBLE);
        name_1.setVisibility(View.INVISIBLE);
        name_2.setVisibility(View.INVISIBLE);
        name_3.setVisibility(View.INVISIBLE);

        path.clear();
        dv.clearDrawing();
    }

    private void touch_move(float x, float y) {
        CoordinatePoint cp = new CoordinatePoint(x, y);
        path.add(cp);
    }

    private void touch_up() {
        Gesture g = new Gesture(path, "");

        // make all buttons visible
        match_1.setVisibility(View.VISIBLE);
        match_2.setVisibility(View.VISIBLE);
        match_3.setVisibility(View.VISIBLE);
        name_1.setVisibility(View.VISIBLE);
        name_2.setVisibility(View.VISIBLE);
        name_3.setVisibility(View.VISIBLE);

        // set blank for all matches
        match_1.setImageDrawable(null);
        match_1.setImageDrawable(null);
        match_1.setImageDrawable(null);

        name_1.setText("");
        name_2.setText("");
        name_3.setText("");

        // compare g with all existing gestures
        switch (gestures.size()) {
            case 0:
                name_1.setVisibility(View.INVISIBLE);
                name_2.setVisibility(View.INVISIBLE);
                name_3.setVisibility(View.INVISIBLE);
                return;
            case 1:
                // add the only gesture
                match_1.setImageBitmap(gestures.get(0).getImageBitmap());
                name_1.setText(gestures.get(0).getName());
                name_2.setVisibility(View.INVISIBLE);
                name_3.setVisibility(View.INVISIBLE);
                return;
            case 2:
                // compare 2 gestures
                float d1 = gestures.get(0).getMatchDistance(g);
                float d2 = gestures.get(1).getMatchDistance(g);
                if (d1 <= d2) {
                    match_1.setImageBitmap(gestures.get(0).getImageBitmap());
                    match_2.setImageBitmap(gestures.get(1).getImageBitmap());
                    name_1.setText(gestures.get(0).getName());
                    name_2.setText(gestures.get(1).getName());
                }
                else {
                    match_1.setImageBitmap(gestures.get(1).getImageBitmap());
                    match_2.setImageBitmap(gestures.get(0).getImageBitmap());
                    name_1.setText(gestures.get(1).getName());
                    name_2.setText(gestures.get(0).getName());
                }
                name_3.setVisibility(View.INVISIBLE);
                return;
            default:
                ArrayList<Float> distances = new ArrayList<>();
                for (int i = 0; i < gestures.size(); i++) {
                    distances.add(gestures.get(i).getMatchDistance(g));
                }

                // get 3 smallest distances
                int ind1 = 0;
                int ind2 = 0;
                int ind3 = 0;
                float dist1 = Float.MAX_VALUE;
                float dist2 = Float.MAX_VALUE;
                float dist3 = Float.MAX_VALUE;
                for (int i = 0; i < distances.size(); i++) {
                    if (distances.get(i) < dist1) {
                        dist3 = dist2; ind3 = ind2;
                        dist2 = dist1; ind2 = ind1;
                        dist1 = distances.get(i);
                        ind1 = i;
                    }
                    else if (distances.get(i) < dist2) {
                        dist3 = dist2; ind3 = ind2;
                        dist2 = distances.get(i);
                        ind2 = i;
                    }
                    else if (distances.get(i) < dist3) {
                        dist3 = distances.get(i);
                        ind3 = i;
                    }
                }

                // set button and image
                match_1.setImageBitmap(gestures.get(ind1).getImageBitmap());
                name_1.setText(gestures.get(ind1).getName());
                match_2.setImageBitmap(gestures.get(ind2).getImageBitmap());
                name_2.setText(gestures.get(ind2).getName());
                match_3.setImageBitmap(gestures.get(ind3).getImageBitmap());
                name_3.setText(gestures.get(ind3).getName());
        }
    }
}