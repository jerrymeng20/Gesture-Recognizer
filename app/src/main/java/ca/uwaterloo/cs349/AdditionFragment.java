package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class AdditionFragment extends Fragment {

    private SharedViewModel mViewModel;
    private RelativeLayout bg = null;

    private ArrayList<CoordinatePoint> path;
    private DrawingView dv;
    private Button bconfirm;
    private Button bclear;

    private TextView nameTitle;
    private EditText nameText;

    private String presetName = "";
    boolean modifyMode = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addition, container, false);

        path = new ArrayList<>();
        nameText = (EditText) root.findViewById(R.id.name);
        nameTitle = (TextView) root.findViewById(R.id.name_title);
        if (presetName != "") {
            nameText.setText(presetName);
            TextView modify_text = root.findViewById(R.id.text_modify);
            modify_text.setText("Modifying: " + presetName);
            modify_text.setVisibility(View.VISIBLE);
            modifyMode = true;

            Button cancel = root.findViewById(R.id.cancel);
            cancel.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LibraryFragment library = new LibraryFragment();
                    assert getFragmentManager() != null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(getId(), library);
                    transaction.commit();
                }
            });
        }

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
                        touch_start();
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

        // click OK add the gesture path, initialize the settings
        bconfirm = root.findViewById(R.id.confirm);
        bconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String name = nameText.getText().toString();
                // check if name has been populated
                if (name.equals("")) {
                    // notify the user to input the name
                    Toast.makeText(getActivity(), "Please enter a name for the gesture", Toast.LENGTH_LONG).show();
                }
                else {
                    Gesture dup_name = mViewModel.isNameOccupied(name);
                    if (dup_name != null) {
                        // first delete the gesture with this name
                        mViewModel.deleteGesture(dup_name, getActivity());
                    }
                    Gesture g = new Gesture(path, name);
                    mViewModel.addGesture(g, getActivity());
                    path.clear();
                    // the button "ok" and "clear" disappears
                    bconfirm.setVisibility(View.INVISIBLE);
                    bclear.setVisibility(View.INVISIBLE);

                    // save gesture to internal storage
                    dv.saveToInternalStorage(name);

                    // clear text and set text button to invisible
                    nameText.setText("");
                    nameTitle.setVisibility(View.INVISIBLE);
                    nameText.setVisibility(View.INVISIBLE);

                    dv.clearDrawing();

                    // if is in modify mode, go back to previous fragment
                    if (modifyMode) {
                        LibraryFragment library = new LibraryFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(getId(), library);
                        transaction.commit();
                    }
                }
            }
        });

        // click CLEAR clears the drawing, do not add the gesture path
        bclear = root.findViewById(R.id.clear);
        bclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                path.clear();
                dv.clearDrawing();
                // the button "ok" and "clear" disappears
                bconfirm.setVisibility(View.INVISIBLE);
                bclear.setVisibility(View.INVISIBLE);
            }
        });

        return root;
    }

    private void touch_start() {
        path.clear();
        dv.clearDrawing();
        // the button "ok" and "clear" disappears
        bconfirm.setVisibility(View.INVISIBLE);
        bclear.setVisibility(View.INVISIBLE);

        // the text for name input appears
        nameTitle.setVisibility(View.INVISIBLE);
        nameText.setVisibility(View.INVISIBLE);
    }

    private void touch_move(float x, float y) {
        CoordinatePoint cp = new CoordinatePoint(x, y);
        path.add(cp);
    }

    private void touch_up() {
        // the button "ok" and "clear" appears
        bconfirm.setVisibility(View.VISIBLE);
        bclear.setVisibility(View.VISIBLE);

        // the text for name input appears
        if (!modifyMode) {
            nameTitle.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.VISIBLE);
        }
    }

    public void setNameText (String name) {
        presetName = name;
    }
}