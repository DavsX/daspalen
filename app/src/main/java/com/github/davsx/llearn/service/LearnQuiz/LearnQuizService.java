package com.github.davsx.llearn.service.LearnQuiz;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.*;

// Rules:
// * Max 5 new cards
// * Use 1 new card if possible
// * Max score is 8
// * Cards with score 0-3 use 3x
// * Cards with score 4-5 use 2x
// * Cards with score 6-7 use 1x
// * Max 20 rounds (calculated from card usage above)
// * For cards with !score 0! || !lastLearned > 1 week! use "show card" first
// *** That said, some cards will be blocked by "show card" before first real use

// Question is: how to schedule cards with score: 0 0 2 4 6 7
// For 0 - do the first real card 1-2 cards after the show card - space out the rest

// Do a weight system, where we would use the cards with the lowest weight
// After using a card, it's weight could be recalculated, so that it would be used before or after a card with score 7

public class LearnQuizService {
    private CardRepository cardRepository;
    private LearnQuizSchedule learnQuizSchedule;
    private List<LearnQuizCard> cards;
    private List<CardEntity> randomCards;

    public LearnQuizService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean startSession() {
        this.cards = prepareCards();

        ArrayList<LearnQuizCard> cardQueue = new ArrayList<>(this.cards);
        this.learnQuizSchedule = new LearnQuizSchedule(cardQueue);

        if (this.cards == null) {
            return false;
        }

        this.randomCards = cardRepository.getRandomCards(LLearnConstants.LEARN_SESSION_RANDOM_CARDS_COUNT);

        setUpNextCard();

        return true;
    }

    private void setUpNextCard() {
        LearnQuizCard schedulableCard = learnQuizSchedule.nextCard();
    }

    private List<LearnQuizCard> prepareCards() {
        List<CardEntity> learnCandidates = cardRepository.getLearnCandidates();
        if (learnCandidates.isEmpty()) {
            return null;
        }

        Collections.shuffle(learnCandidates);

        List<LearnQuizCard> chosenCards = new ArrayList<>();

        int roundCounter = 0;
        int newCardCounter = 0;
        for (CardEntity card : learnCandidates) {
            if (card.getLearnScore() == 0) {
                if (newCardCounter >= LLearnConstants.LEARN_SESSION_MAX_NEW_CARDS) {
                    continue;
                }
                newCardCounter++;
            }

            LearnQuizCard learnQuizCard = new LearnQuizCard(cardRepository, card);
            chosenCards.add(learnQuizCard);
            roundCounter += learnQuizCard.getPlannedRounds();

            if (roundCounter >= LLearnConstants.LEARN_SESSION_MAX_ROUNDS) {
                break;
            }
            if (chosenCards.size() >= LLearnConstants.LEARN_SESSION_MAX_CARDS) {
                break;
            }
        }

        Collections.sort(chosenCards, new LearnQuizCard.LearnQuizCardComparator());

        return chosenCards;
    }

    public void processAnswer(String answer) {
    }
}
