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
                    .load(question.getThumb())
                    .into(imageIv);

        questionTv.setText(question.getQuestion());
        dateTv.setText(question.getCreatedAt());
        resultTv.setText(question.getResult());
    }

}