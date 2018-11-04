package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.ReviewQuiz.ReviewQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        CardRepositoryModule.class,
        JournalRepositoryModule.class,
        CardImageModule.class
})
public class ReviewQuizModule {
    @Provides
    ReviewQuizService provide(CardRepository cardRepository,
                                               JournalRepository journalRepository,
                                               CardImageService cardImageService) {
        return new ReviewQuizService(cardRepository, journalRepository, cardImageService);
    }
}