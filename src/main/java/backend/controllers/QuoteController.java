package backend.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteController {
    private List<String> quotes;

    public QuoteController() {
        quotes = new ArrayList<>();
        quotes.add("“All our dreams can come true, if we have the courage to pursue them.” —Walt Disney");
        quotes.add("“The secret of getting ahead is getting started.” —Mark Twain");
        quotes.add("“The best time to plant a tree was 20 years ago. The second best time is now.” ―Chinese proverb");
        quotes.add("“It’s hard to beat a person who never gives up.” —Babe Ruth");
        quotes.add("“I wake up every morning and think to myself, ‘How far can I push this company in the next 24 hours?’” —Leah Busque");
        quotes.add("“Write it. Shoot it. Publish it. Crochet it. Sauté it. Whatever. MAKE.” —Joss Whedon");
        quotes.add("“If people are doubting how far you can go, go so far that you can’t hear them anymore.” —Michele Ruiz");
        quotes.add("“Happiness is not something ready made. It comes from your own actions.” ―Dalai Lama XIV");
        quotes.add("“Whatever you are, be a good one.” ―Abraham Lincoln");
        quotes.add("“Imagination is everything. It is the preview of life's coming attractions.” —Albert Einstein");
        quotes.add("“Impossible is just an opinion.” —Paulo Coelho");
        quotes.add("“Hold the vision, trust the process.” —Unknown");
        quotes.add("“If we have the attitude that it’s going to be a great day, it usually is.” —Catherine Pulsifier");
        quotes.add("“You can either experience the pain of discipline or the pain of regret. The choice is yours.” —Unknown");
        quotes.add("“Oh! It’s Friday again. Share the love that was missing during the week. In a worthy moment of peace and bliss.” —S. O’Sade");
    }

    public String getRandomQuote() {
        Random random = new Random();
        int randomIndex = random.nextInt(quotes.size());
        return quotes.get(randomIndex);
    }
}
