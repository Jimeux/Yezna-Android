package com.moobasoft.yezna.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.moobasoft.yezna.ui.SummaryAdapter.SummaryClickListener;

public final class SummaryView extends CardView {
    @Bind(R.id.image)       ImageView imageIv;
    @Bind(R.id.avatar)      ImageView avatarIv;
    @Bind(R.id.username)    TextView  usernameTv;
    @Bind(R.id.question)    TextView  questionTv;
    @Bind(R.id.result)      TextView  resultTv;
    @Bind(R.id.date)        TextView  dateTv;
    @Bind(R.id.answer_btns) ViewGroup answerBtnsLayout;

    public SummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void toggleAnswerBtns() {
        boolean visible = answerBtnsLayout.getVisibility() == View.VISIBLE;
        answerBtnsLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    public void bindTo(Question question, SummaryClickListener listener) {
        //answerBtnsLayout.setVisibility(View.GONE);

        if (question.getUser() != null) {
            usernameTv.setText(question.getUser().getUsername());
            if (question.getUser().getAvatar() != null)
                Glide.with(getContext())
                        .load(question.getUser().getAvatar())
                        .into(avatarIv);
        }

        if (question.getImage() != null)
            Glide.with(getContext())
                    .load(question.getImage())
                    .into(imageIv);

        imageIv.setVisibility(GONE);

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