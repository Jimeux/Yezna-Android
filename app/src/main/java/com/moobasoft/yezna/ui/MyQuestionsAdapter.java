package com.moobasoft.yezna.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.views.MyQuestionView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.ViewHolder;

public class MyQuestionsAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int ID_PREFIX     = 12345678;
    public static final int TYPE_NORMAL   = 1;
    public static final int TYPE_FOOTER   = 2;
    private boolean hideFooter = true;

    private SummaryClickListener summaryClickListener;
    private ArrayList<Question> questionList;

    public MyQuestionsAdapter(SummaryClickListener summaryClickListener) {
        this.summaryClickListener = summaryClickListener;
        this.questionList = new ArrayList<>();
    }

    public ArrayList<Question> getQuestionsList() {
        return questionList;
    }

    public void loadQuestion(Question question) {
        questionList.add(0, question);
        notifyDataSetChanged();
    }

    public void loadQuestions(List<Question> questions) {
        questionList.addAll(questions);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.element_list_footer, parent, false);
            return new FooterHolder(view);
        } else {
            MyQuestionView view = (MyQuestionView) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.view_my_question, parent, false);
            return new SummaryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof SummaryViewHolder )
            ((SummaryViewHolder)holder).bindTo(questionList.get(position), summaryClickListener);
        else if (holder instanceof FooterHolder) {
            ((StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams())
                    .setFullSpan(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == questionList.size())
            return TYPE_FOOTER;
        else
            return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int size = questionList.size();
        return hideFooter ? size : size + 1;
    }

    public void clear() {
        questionList.clear();
        hideFooter = true;
        notifyDataSetChanged();
    }

    public void setFinished() {
        hideFooter = false;
        notifyDataSetChanged();
    }

    public boolean isEmpty() { return questionList.isEmpty(); }

    public interface SummaryClickListener {
        void onSummaryClicked(Question question);
    }

    class SummaryViewHolder extends ViewHolder {

        private final MyQuestionView itemView;

        public SummaryViewHolder(MyQuestionView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void bindTo(Question question, SummaryClickListener listener) {
            itemView.setId(ID_PREFIX + question.getId());
            itemView.bindTo(question, listener);
        }
    }

    static class FooterHolder extends ViewHolder {
        //private final View view;

        public FooterHolder(View itemView) {
            super(itemView);
            //this.view = itemView;
        }
    }
}