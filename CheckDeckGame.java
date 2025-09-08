package com.example;

	
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.*;

public class CheckDeckGame {

    @Test
    public void testCardGameBlackjack() {
        // 1. Confirm site is up and get a new deck
        Response rootResp = RestAssured.get("https://deckofcardsapi.com/api/deck/new/");
        Assert.assertEquals(rootResp.statusCode(), 200);

        // 2. Get deck_id
        String deckId = rootResp.jsonPath().getString("deck_id");
        Assert.assertNotNull(deckId);

        // 3. Shuffle the deck
        Response shuffleResp = RestAssured.get("https://deckofcardsapi.com/api/deck/" + deckId + "/shuffle/");
        Assert.assertEquals(shuffleResp.statusCode(), 200);

        // 4. Draw 6 cards
        Response drawResp = RestAssured.get("https://deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=6");
        Assert.assertEquals(drawResp.statusCode(), 200);
        List<Map<String, String>> cards = drawResp.jsonPath().getList("cards");

        // 5. Assign 3 cards to each player
        List<Map<String, String>> player1 = cards.subList(0, 3);
        List<Map<String, String>> player2 = cards.subList(3, 6);

        // 6. Check for blackjack
        boolean p1Blackjack = hasBlackjack(player1);
        boolean p2Blackjack = hasBlackjack(player2);

        // 7. Output result
        if (p1Blackjack) {
            System.out.println("Player 1 has blackjack: " + player1);
        }
        if (p2Blackjack) {
            System.out.println("Player 2 has blackjack: " + player2);
        }
        if (!p1Blackjack && !p2Blackjack) {
            System.out.println("No player has blackjack.");
        }
    }

    private boolean hasBlackjack(List<Map<String, String>> hand) {
        boolean hasAce = false;
        boolean hasTenValue = false;
        for (Map<String, String> card : hand) {
            String value = card.get("value");
            if ("ACE".equals(value)) hasAce = true;
            if (Arrays.asList("10", "JACK", "QUEEN", "KING").contains(value)) hasTenValue = true;
        }
        return hasAce && hasTenValue;
    }
}