package com.moobasoft.yezna.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.moobasoft.yezna.ui.MyQuestionsAdapter.SummaryClickListener;

public final class MyQuestionView extends CardView {
    @Bind(R.id.image)       ImageView imageIv;
    @Bind(R.id.question)    TextView  questionTv;
    @Bind(R.id.result)      TextView  resultTv;
    @Bind(R.id.date)        TextView  dateTv;

    public MyQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(Question question, SummaryClickListener listener) {
        if (question.getImage() != null)
            Glide.with(getContext())
                    .load(question.getImage())
                    .into(imageIv);

        questionTv.setText(question.getQuestion());
        dateTv.setText(question.getCreatedAt());
        setMessage(question);
    }

    private void setMessage(Question question) {
        int total = question.getYeses() + question.getNoes();

        String peeps = (total == 1) ? "person" : "people";
        String all   = (total == 1) ? "" : "All ";

        if (total == 0) {
            resultTv.setText(getResources().getString(R.string.no_answers));
        } else if (total == question.getYeses()) {
            resultTv.setText(all + total + " " + peeps + " said yes");
        } else if (total == question.getNoes()) {
            resultTv.setText(all + total + " " + peeps + " said no");
        } else if (question.getYeses() >= question.getNoes()) {
            int percentage = (int) ((float) question.getYeses() / total * 100);
            resultTv.setText(percentage + "% of " + total + " " + peeps + " said yes");
        } else {
            int percentage = (int) ((float) question.getNoes() / total * 100);
            resultTv.setText(percentage + "% of " + total + " " + peeps + " said no");
        }
    }
}