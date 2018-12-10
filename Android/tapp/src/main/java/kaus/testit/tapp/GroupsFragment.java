package kaus.testit.tapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupName;
        public TextView description;
        public  TextView groupId;
        public ViewHolder(final LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.groups_list, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, RespondentsActivity.class);
                    intent.putExtra("GroupId", Integer.parseInt(groupId.getText().toString()));
                    context.startActivity(intent);
                }
            });


            groupName = (TextView) itemView.findViewById(R.id.list_groupTitles);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            groupId = (TextView) itemView.findViewById(R.id.list_groupId);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final String[] mGroupTitles;
        private final String[] mGroupIds;
        //private final String[] mPlaceDesc;

        private static int length = 10;

        public ContentAdapter(Context context) {

            TappDataBaseHelper db = new TappDataBaseHelper(context);

            final List<Group> groups =  db.getGroups();

            length = groups.size();

            mGroupTitles = new String [groups.size()];
            mGroupIds = new String [groups.size()];
            for(int i=0;i<mGroupTitles.length;i++){
                mGroupTitles[i] = groups.get(i).getGroupName();
                mGroupIds[i] = String.valueOf(groups.get(i).getId());
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.groupName.setText(mGroupTitles[position % mGroupTitles.length]);
            holder.description.setText("Пока здесь текста нет");
            holder.groupId.setText(mGroupIds[position%mGroupIds.length]);
        }

        @Override
        public int getItemCount() {
            return length;
        }
    }
}


