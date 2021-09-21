package ca.uwaterloo.cs349;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.GestureViewHolder> {
    public static class GestureViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView gestureName;
        ImageView gesturePreview;
        ImageButton gestureDelete;
        ImageButton gestureModify;

        GestureViewHolder (View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_view);
            gestureName = (TextView) itemView.findViewById(R.id.title);
            gesturePreview = (ImageView) itemView.findViewById(R.id.preview);
            gestureDelete = (ImageButton) itemView.findViewById(R.id.delete);
            gestureModify = (ImageButton) itemView.findViewById(R.id.modify);
        }
    }

    ArrayList<Gesture> gestures;
    private Context context;
    private SharedViewModel mViewModel;
    private Fragment fragment;

    RVAdapter (Context c, ArrayList<Gesture> g, SharedViewModel mvm, Fragment f) {
        context = c;
        gestures = g;
        mViewModel = mvm;
        fragment = f;
    }

    @Override
    public int getItemCount() {
        return gestures.size();
    }

    @Override
    public GestureViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_library, viewGroup, false);
        GestureViewHolder gvh = new GestureViewHolder(v);
        return gvh;
    }

    @Override
    public void onBindViewHolder (GestureViewHolder gvh, int i) {
        final int index = i;
        final GestureViewHolder gvh1 = gvh;
        gvh.gestureName.setText(gestures.get(i).getName());
        gvh.gesturePreview.setImageBitmap(gestures.get(i).getImageBitmap());
        gvh.gestureDelete.setImageResource(R.drawable.delete);
        gvh.gestureModify.setImageResource(R.drawable.modify);
        gvh.gestureName.setVisibility(View.VISIBLE);
        gvh.gesturePreview.setVisibility(View.VISIBLE);
        gvh.gestureDelete.setVisibility(View.VISIBLE);
        gvh.gestureModify.setVisibility(View.VISIBLE);
        gvh.cv.setVisibility(View.VISIBLE);

        gvh.gestureDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deleteGesture(gestures.get(index), context);
                gvh1.cv.setVisibility(View.INVISIBLE);
            }
        });

        gvh.gestureModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdditionFragment addition = new AdditionFragment();
                FragmentTransaction transaction = fragment.getFragmentManager().beginTransaction();
                transaction.replace(fragment.getId(), addition);
                addition.setNameText(gestures.get(index).getName());
                transaction.commit();
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
