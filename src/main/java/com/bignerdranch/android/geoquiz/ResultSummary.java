package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import org.w3c.dom.Text;

public class ResultSummary extends AppCompatActivity {

    private static final String EXTRA_TOTAL_QUESTION_ANSWERED = "com.bignerdranch.android.geoquiz.totalquestionanswered";
    private static final String EXTRA_TOTAL_SCORE = "com.bignerdranch.adnroid.geoquiz.totalscore";
    private static final String EXTRA_TOTAL_CHEAT_ATTEMPTS = "com.bignerdranch.android.geoquiz.totalcheatattempts";

    private int mTotalQuestionAnswered;
    private int mTotalScore;
    private int mTotalCheatAttempts;
    private TextView mScore;
    private TextView mTotalAnswered;
    private TextView mTotalCheat;


    public static Intent newIntent (Context packageContext, int totalquestionanswered, int totalscore, int totalcheatattempts){
        Intent intent = new Intent (packageContext, ResultSummary.class);
        intent.putExtra(EXTRA_TOTAL_QUESTION_ANSWERED, totalquestionanswered);
        intent.putExtra(EXTRA_TOTAL_SCORE, totalscore);
        intent.putExtra(EXTRA_TOTAL_CHEAT_ATTEMPTS, totalcheatattempts);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_summary);

        mTotalQuestionAnswered = getIntent().getIntExtra(EXTRA_TOTAL_QUESTION_ANSWERED, 0);
        mTotalScore = getIntent().getIntExtra(EXTRA_TOTAL_SCORE, 0);
        mTotalCheatAttempts = getIntent().getIntExtra(EXTRA_TOTAL_CHEAT_ATTEMPTS, 0);


        mTotalAnswered = (TextView)findViewById(R.id.TotalQuestionAnswered);
        mTotalAnswered.setText("Total Question Answered: " + mTotalQuestionAnswered);

        mScore=(TextView)findViewById(R.id.TotalScore);
        mScore.setText("Total Score: " + mTotalScore);

        mTotalCheat = (TextView) findViewById(R.id.TotalCheatAttempts);
        mTotalCheat.setText("Total Cheat Attempts: " + mTotalCheatAttempts);
    }
}
