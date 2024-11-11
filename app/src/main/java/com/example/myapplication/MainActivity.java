// Name: Alpha Romer N. Coma
// Student Number: 202211383
// Section: TN32
// Instructor: Sir Abraham Magpantay
// Course: Mobile Programming
// Activity: Android Memory Game
// Done: 2024/10/28

package com.example.myapplication;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView timerTextView;
    private Button replayButton;
    private List<CardView> cards;
    private CountDownTimer timer;

    private final List<String> symbols = Arrays.asList("★", "♠", "♣", "♥", "♦");
    private List<String> gameSymbols;
    private CardView firstCard;
    private CardView secondCard;
    private boolean isProcessing;
    private int matchedPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        timerTextView = findViewById(R.id.timerTextView);
        replayButton = findViewById(R.id.replayButton);

        // Set grid properties
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(3);
        gridLayout.setUseDefaultMargins(true);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);

        // Set up replay button
        replayButton.setVisibility(View.GONE);
        replayButton.setOnClickListener(v -> resetGame());

        initializeGame();
        startTimer();
    }

    private void resetGame() {
        matchedPairs = 0;
        isProcessing = false;
        firstCard = null;
        secondCard = null;

        gridLayout.removeAllViews();
        if (cards != null) {
            cards.clear();
        }

        replayButton.setVisibility(View.GONE);
        timerTextView.setVisibility(View.VISIBLE);

        initializeGame();
        startTimer();
    }

    private void initializeGame() {
        gameSymbols = new ArrayList<>();
        // Add 4 pairs
        for (int i = 0; i < 4; i++) {
            gameSymbols.add(symbols.get(i));
            gameSymbols.add(symbols.get(i));
        }
        gameSymbols.add(symbols.get(4));
        Collections.shuffle(gameSymbols);

        cards = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            cards.add(createCard(i));
        }

        // Add cards to grid layout
        for (int i = 0; i < cards.size(); i++) {
            CardView card = cards.get(i);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = getResources().getDimensionPixelSize(R.dimen.card_size);
            params.height = getResources().getDimensionPixelSize(R.dimen.card_size);
            params.setMargins(8, 8, 8, 8);
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            params.rowSpec = GridLayout.spec(i / 3, 1f);
            card.setLayoutParams(params);
            gridLayout.addView(card);
        }
    }

    private CardView createCard(final int index) {
        CardView card = new CardView(this);

        card.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
        card.setCardElevation(getResources().getDimension(R.dimen.card_elevation));
        card.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        card.setAlpha(1.0f);

        TextView textView = new TextView(this);
        textView.setText(gameSymbols.get(index));
        textView.setTextSize(24);
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(View.INVISIBLE);
        card.addView(textView);

        card.setOnClickListener(v -> {
            if (!isProcessing) {
                flipCard((CardView) v);
            }
        });

        return card;
    }

    private void flipCard(CardView card) {
        if (card == firstCard || card == secondCard) return;

        AnimatorSet flipAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(
                this,
                R.animator.card_flip
        );

        flipAnimation.setTarget(card);
        flipAnimation.start();

        card.getChildAt(0).setVisibility(View.VISIBLE);

        if (firstCard == null) {
            firstCard = card;
        } else if (secondCard == null) {
            secondCard = card;
            checkMatch();
        }
    }

    private void checkMatch() {
        isProcessing = true;

        String firstSymbol = ((TextView) firstCard.getChildAt(0)).getText().toString();
        String secondSymbol = ((TextView) secondCard.getChildAt(0)).getText().toString();

        if (firstSymbol.equals(secondSymbol)) {
            matchedPairs++;

            // Fade out matched cards
            firstCard.animate().alpha(0f).setDuration(300);
            secondCard.animate().alpha(0f).setDuration(300);

            // Game is won when all 4 pairs are matched
            if (matchedPairs == 4) {
                gameWon();
            }

            new Handler().postDelayed(() -> {
                firstCard = null;
                secondCard = null;
                isProcessing = false;
            }, 300);
        } else {
            // Flip cards back
            new Handler().postDelayed(() -> {
                flipCardBack(firstCard);
                flipCardBack(secondCard);
                firstCard = null;
                secondCard = null;
                isProcessing = false;
            }, 1000);
        }
    }

    private void flipCardBack(CardView card) {
        AnimatorSet flipBackAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(
                this,
                R.animator.card_flip_back
        );

        flipBackAnimation.setTarget(card);
        flipBackAnimation.start();

        card.getChildAt(0).setVisibility(View.INVISIBLE);
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                gameOver();
            }
        }.start();
    }

    private void gameOver() {
        isProcessing = true;
        timer.cancel();
        timerTextView.setText("Game Over!");
        showReplayButton();
    }

    private void gameWon() {
        timer.cancel();
        timerTextView.setText("You Won!");
        showReplayButton();
    }

    private void showReplayButton() {
        replayButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}