package com.github.davsx.daspalen.activities.BaseQuiz;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.davsx.daspalen.R;

import java.util.*;

public class FragmentChoice1of4 extends BaseQuizFragment implements View.OnClickListener {
    private ArrayList<Button> choiceButtons;
    private TextView textViewQuiz;
    private TextView textViewCardScore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_choice_1of4, container, false);

        textViewCardScore = view.findViewById(R.id.textview_card_score);
        textViewQuiz = view.findViewById(R.id.textview_quiz);
        choiceButtons = new ArrayList<>();

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        textViewQuiz.setText(quizData.getFrontText());
        textViewCardScore.setText(quizData.getCardScore().toString());

        List<String> choices = quizData.getChoices();
        for (int i = 0; i < 4; i++) {
            Button btn = choiceButtons.get(i);
            btn.setText(choices.get(i));
        }
    }

    private void initView(View view) {
        LinearLayout choicesLayout = view.findViewById(R.id.choices_layout);

        ArrayList<Integer> choiceRowResources = new ArrayList<>(Arrays.asList(
                R.id.choice_1_row,
                R.id.choice_2_row,
                R.id.choice_3_row,
                R.id.choice_4_row
        ));

        for (Integer choiceRowResource : choiceRowResources) {
            LinearLayout choiceRow = choicesLayout.findViewById(choiceRowResource);

            final Button choiceButton = choiceRow.findViewById(R.id.button_choice);
            ImageView ttsButton = choiceRow.findViewById(R.id.button_tts);

            choiceButtons.add(choiceButton);
            choiceButton.setOnClickListener(this);
            ttsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakerService.speak(choiceButton.getText().toString());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        final String answer = btn.getText().toString();

        String correctAnswer = quizData.getBackText();

        int timer_delay = 2000;
        if (answer.equals(correctAnswer)) {
            timer_delay = 1000;
            btn.setBackgroundResource(R.drawable.button_choice_correct);
        } else {
            btn.setBackgroundResource(R.drawable.button_choice_incorrect);

            for (Button button : choiceButtons) {
                if (button.getText().toString().equals(correctAnswer)) {
                    button.setBackgroundResource(R.drawable.button_choice_correct);
                    button.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                }
            }
        }
        btn.setTextColor(Color.parseColor("#FFFFFF"));

        for (Button button : choiceButtons) {
            button.setOnClickListener(null);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                answerReceiver.onAnswer(answer);
            }
        }, timer_delay);
    }
}
