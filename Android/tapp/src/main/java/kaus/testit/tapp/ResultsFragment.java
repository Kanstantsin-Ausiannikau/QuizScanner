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
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public ResultsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();

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
        ResultsFragment.ContentAdapter adapter = new ResultsFragment.ContentAdapter(recyclerView.getContext());
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

        public TextView results;
        public TextView resultsDetails;
        public  TextView resultsRespondents;

        public ViewHolder(final LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.results_list, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Context context = v.getContext();
                    Intent intent = new Intent(context, RespondentsActivity.class);
                    intent.putExtra("GroupId", Integer.parseInt(groupId.getText().toString()));
                    context.startActivity(intent);
                    */
                }
            });


            results = (TextView) itemView.findViewById(R.id.list_results);
            resultsDetails = (TextView) itemView.findViewById(R.id.list_resultsDetails);
            resultsRespondents = (TextView) itemView.findViewById(R.id.list_resultsRespondents);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ResultsFragment.ViewHolder> {

        private final String[] mResults;
        private final String[] mResultDetails;
        private final String[] mResultRespondents;
        //private final String[] mPlaceDesc;

        private static int length = 10;

        public ContentAdapter(Context context) {

            TappDataBaseHelper db = new TappDataBaseHelper(context);

            final List<Answer> answers =  db.getNewAnswers();

            length = answers.size();

            mResults = new String [answers.size()];
            mResultDetails = new String [answers.size()];
            mResultRespondents = new String [answers.size()];

            for(int i=0;i<mResults.length;i++){
                mResults[i] = "Тест "+answers.get(i).getQuizNumber()+". Результат - "+Answer.calculateAnswerPercentage(context, answers.get(i))*100+"%";
                mResultDetails[i] = "Время тестирования: "+answers.get(i).getDate();
                if (!answers.get(i).getRespondentNumber().equals("")){
                    mResultRespondents[i] = db.GetRespondentNameByRespondentNumber(answers.get(i).getRespondentNumber());
                }
            }
        }

        @Override
        public ResultsFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ResultsFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ResultsFragment.ViewHolder holder, int position) {
            holder.results.setText(mResults[position % mResults.length]);
            holder.resultsDetails.setText(mResultDetails[position%mResultDetails.length]);
            if (mResultRespondents[position%mResultRespondents.length]!=null) {
                holder.resultsRespondents.setText(mResultRespondents[position%mResultRespondents.length]);
            }
            else {
                holder.resultsRespondents.setText("-");
            }
        }

        @Override
        public int getItemCount() {
            return length;
        }
    }

}
