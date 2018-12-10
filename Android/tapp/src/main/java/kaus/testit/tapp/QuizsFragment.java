package kaus.testit.tapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuizsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuizsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView mLstQuizs;

    private OnFragmentInteractionListener mListener;

    public QuizsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizsFragment newInstance(String param1, String param2) {
        QuizsFragment fragment = new QuizsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        QuizsFragment.ContentAdapter adapter = new QuizsFragment.ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
/*
        View rootView =  inflater.inflate(R.layout.fragment_quizs, container, false);

        TappDataBaseHelper db = new TappDataBaseHelper(getActivity());

        final List<Quiz> quizs =  db.getQuizs();

        mLstQuizs = (ListView)rootView.findViewById(R.id.lstQuizs);

        String[] mQuizs = new String [quizs.size()];
        for(int i=0;i<mQuizs.length;i++){
            mQuizs[i] = quizs.get(i).getTitle() + " "+ quizs.get(i).getQuizNumber();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mQuizs);

        mLstQuizs.setAdapter(adapter);

        mLstQuizs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });

        return rootView;
        */
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView quiz;
        public TextView description;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.quizs_list, parent, false));

            //Intent intent = new Intent(RespondentsActivity.this, RespondentsActivity.class);

            //intent.putExtra("GroupId",i);

            //startActivity(intent);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ScanActivity.class);
                    context.startActivity(intent);
                }
            });

            quiz = (TextView) itemView.findViewById(R.id.list_quizs);
            description = (TextView) itemView.findViewById(R.id.list_quizDesc);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<QuizsFragment.ViewHolder> {

        private final String[] mQuizs;
        private final List<Quiz> quizs;

        //private final String[] mPlaceDesc;

        private static int length;

        public ContentAdapter(Context context) {

            TappDataBaseHelper db = new TappDataBaseHelper(context);

            quizs =  db.getQuizs();

            length = quizs.size();

            mQuizs = new String [quizs.size()];
            for(int i=0;i<mQuizs.length;i++){
                mQuizs[i] = quizs.get(i).getTitle();
            }
        }

        @Override
        public QuizsFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new QuizsFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.quiz.setText(quizs.get(position % mQuizs.length).getTitle());
            holder.description.setText("Пока здесь текста нет");
        }

        @Override
        public int getItemCount() {
            return length;
        }
    }
}
