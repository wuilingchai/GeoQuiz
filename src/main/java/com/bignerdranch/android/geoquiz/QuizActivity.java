package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String KEY_CORRECT = "correct";
    private static final String KEY_INCORRECT = "correct";
    private static final String KEY_CHEATER = "cheater";
    private static final String KEY_TOKENS = "tokens";
    private static final String KEY_QUESTION_ANSWERED = "questionanswered";



    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mRemainingTokensTextView;
    private ImageButton mResetButton;
    private Button mResultSummaryButton;

    private int correct = 0;
    private int incorrect = 0;
    private TextView mQuestionNumberTextView;


    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };


    private boolean[] mQuestionAnswered = new boolean[]{false,false,false,false,false,false};
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private int mCurrentScore = 0;
    private int mRemainingCheatTokens = 3;
    private int mTotalQuestionAnswered = 0;
    private int mTotalScore = 0;
    private int mTotalCheatAttempts = 0;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle)called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            correct = savedInstanceState.getInt(KEY_CORRECT, 0);
            incorrect = savedInstanceState.getInt(KEY_INCORRECT, 0);
            mRemainingCheatTokens = savedInstanceState.getInt(KEY_TOKENS, 3);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER);
            mQuestionAnswered = savedInstanceState.getBooleanArray(KEY_QUESTION_ANSWERED);
        }


        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });


        mQuestionNumberTextView = findViewById(R.id.question_number);
        int mIndex = mCurrentIndex+1;
        mQuestionNumberTextView.setText("Question: " + mIndex + "/6");


        mRemainingTokensTextView = findViewById(R.id.cheat_tokens_text_view);
        mRemainingTokensTextView.setText("Remaining Cheat Tokens: " + mRemainingCheatTokens);


        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });


        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });


        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        if (mCurrentIndex <= 0){
            mPrevButton.setVisibility(View.INVISIBLE);
        }
        else {
            mPrevButton.setVisibility(View.VISIBLE);
        }
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                    updateQuestion();
            }
        });


        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                mIsCheater = false;
            }
        });


        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);

                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });


        mResetButton = (ImageButton) findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButton();
                updateQuestion();
            }
        });


        mResultSummaryButton = (Button) findViewById(R.id.resultsummary_button);
        mResultSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int TotalQuestionAnswered =  (mCurrentIndex+1);
                int TotalScore = (correct);
                int TotalCheatAttempts = (3 - mRemainingCheatTokens);

               Intent intent =  ResultSummary.newIntent(QuizActivity.this,TotalQuestionAnswered, TotalScore, TotalCheatAttempts);
               intent.putExtra("Total Question Answered: ", TotalQuestionAnswered);
               intent.putExtra("Total Score: ", TotalScore );
               intent.putExtra("Total Cheat Attempts: ", TotalCheatAttempts);

               startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnwerShown(data);
            if (mIsCheater == true){
                //challenge limited to 3 cheats
                mRemainingCheatTokens--;
                mRemainingTokensTextView.setText("Remaining Cheat Tokens: " + mRemainingCheatTokens);
                if (mRemainingCheatTokens == 0){
                    mCheatButton.setEnabled(false);
             }
            }}
        }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_CORRECT, correct);
        savedInstanceState.putInt(KEY_INCORRECT, incorrect);
        savedInstanceState.putInt(KEY_TOKENS, mRemainingCheatTokens);
        savedInstanceState.putBoolean(KEY_CHEATER, mIsCheater);
        savedInstanceState.putBooleanArray(KEY_QUESTION_ANSWERED, mQuestionAnswered);
        }


        @Override
        public void onBackPressed(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Exit GeoQuiz?");
            builder.setMessage("Are you sure you want to exit GeoQuiz?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    QuizActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    QuizActivity.super.onResume();
                }
            });
            builder.show();
        }


        @Override
        public void onStop () {
            super.onStop();
            Log.d(TAG, "onStop() called");
        }

        @Override
        public void onDestroy () {
            super.onDestroy();
            Log.d(TAG, "onDestroy() called");
        }


        private void updateQuestion () {
            int question = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question);
            enableButton();
            int mIndex = (mCurrentIndex + 1);
            mQuestionNumberTextView.setText("Question: " + mIndex + "/6");

            if (mCurrentIndex <= 0){
                mPrevButton.setVisibility(View.INVISIBLE);
            }
            else {
                mPrevButton.setVisibility(View.VISIBLE);
            }

            if (mCurrentIndex != 5){
                mNextButton.setVisibility(View.VISIBLE);
            }
            else{
                mNextButton.setVisibility(View.INVISIBLE);
            }
        }


        private void checkAnswer ( boolean userPressedTrue){
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            mQuestionAnswered[mCurrentIndex] = true;
            enableButton();

            int messageResId = 0;

            if(mIsCheater){
                messageResId = R.string.judgement_toast;
            }else {
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.correct_toast;
                    correct += 1;
                    Log.d(TAG, mCurrentScore + "");
                } else {
                    messageResId = R.string.incorrect_toast;
                    incorrect -= 1;
                }

            }


            Toast toast = Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 20);
            toast.show();

            if(mCurrentIndex == mQuestionBank.length - 1){
                showScore();
            }
        }


        private void enableButton(){
            if (mQuestionAnswered[mCurrentIndex]==true){
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }else{
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
        }


        private void resetButton(){
            mQuestionAnswered = new boolean[]{false,false,false,false,false,false};
            enableButton();
            mCurrentIndex = 0;
            correct = 0;
            incorrect = 0;
            mCheatButton.setEnabled(true);
            mRemainingCheatTokens = 3;
            mRemainingTokensTextView.setText("Remaining Cheat Tokens: " + mRemainingCheatTokens);
            updateQuestion();
    }


        private void showScore(){
        int percentage = (correct*100)/mQuestionBank.length;
        String stringScore = "You got " + percentage + "%correct answers";
        Toast.makeText(this, stringScore, Toast.LENGTH_SHORT).show();
        }

}