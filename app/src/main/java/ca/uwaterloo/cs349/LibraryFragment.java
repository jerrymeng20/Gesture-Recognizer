package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LibraryFragment extends Fragment{

    private SharedViewModel mViewModel;
    ArrayList<Gesture> gestures;
    private CardView cv;
    private RecyclerView rv;
    private RVAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        try {
            gestures = new ArrayList<>();
            cv = (CardView) root.findViewById(R.id.card_view);
            cv.getBackground().setAlpha(5);
            rv = (RecyclerView) root.findViewById(R.id.recycler_view);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);

            adapter = new RVAdapter (getActivity(), gestures, mViewModel, this);
            rv.setAdapter(adapter);

            mViewModel.getGestures().observe(getViewLifecycleOwner(), new Observer<ArrayList<Gesture>>() {
                @Override
                public void onChanged(@Nullable ArrayList<Gesture> g) {
                    if (gestures != null) gestures.clear();
                    for (int i = 0; i < g.size(); i++) {
                        gestures.add(g.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            });

        /*final Button number_button = root.findViewById(R.id.test);
        final TextView num = root.findViewById(R.id.number);

        number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                num.setText("gesture size: " + gestures.size());
                System.out.println("adapterItemSize: " + adapter.getItemCount());
                for (int i = 0; i < gestures.size(); i++) {
                    gestures.get(i).print();
                }
            }
        });*/

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

            return root;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }
}