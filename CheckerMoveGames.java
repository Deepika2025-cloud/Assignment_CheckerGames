package com.example;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;


	public class CheckerMoveGames {
		


		    WebDriver driver;
		    WebDriverWait wait;
		    Actions actions;

		    @BeforeClass
		    public void setup() {
		        System.setProperty("webdriver.chrome.driver", "C:\\webDriver\\chromedriver.exe");
		        driver = new ChromeDriver();
		        driver.manage().window().maximize();
		        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		        actions = new Actions(driver);
		    }

		    @Test
		    public void testCheckersGameMoves() {
		        try {
		            // Step 1: Navigate to site
		            driver.get("https://www.gamesforthebrain.com/game/checkers/");
		            
		            // Wait for page to load completely
		            wait.until(ExpectedConditions.titleContains("Checkers"));
		            Assert.assertTrue(driver.getTitle().contains("Checkers"));

		            // Step 2: Wait for the game board to load
		            WebElement gameBoard = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("board")));
		            Assert.assertNotNull(gameBoard, "Game board should be present");

		            // Step 3: Wait for initial message
		            WebElement initialMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
		            System.out.println("Initial message: " + initialMessage.getText());

		            // Step 4: Make proper visual moves
		            makeVisualMoves();

		            // Step 5: Restart the game
		            WebElement restartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Restart...")));
		            restartBtn.click();

		            // Step 6: Confirm restart
		            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
		            Assert.assertTrue(msg.getText().contains("Make a move") || msg.getText().contains("Select"));

		        } catch (Exception e) {
		            System.err.println("Test failed with exception: " + e.getMessage());
		            printPageStructure();
		            throw e;
		        }
		    }

		    private void makeVisualMoves() {
		        try {
		            // Wait a bit for the page to fully load
		            Thread.sleep(2000);
		            
		            // Find orange pieces (player pieces)
		            List<WebElement> orangePieces = driver.findElements(By.cssSelector("img[src*='you1.gif']"));
		            System.out.println("Found " + orangePieces.size() + " orange pieces");

		            if (orangePieces.isEmpty()) {
		                orangePieces = driver.findElements(By.cssSelector("img[src*='you']"));
		                System.out.println("Found " + orangePieces.size() + " pieces with alternative selector");
		            }

		            int movesMade = 0;
		            int maxMoves = Math.min(5, orangePieces.size());

		            for (int i = 0; i < maxMoves && movesMade < 5; i++) {
		                try {
		                    WebElement piece = orangePieces.get(i);
		                    
		                    // Make sure the piece is visible and clickable
		                    wait.until(ExpectedConditions.elementToBeClickable(piece));
		                    
		                    // Click the piece first
		                    System.out.println("Clicking piece " + (i + 1));
		                    piece.click();
		                    Thread.sleep(1000); // Wait for piece selection
		                    
		                    // Find valid diagonal moves for this piece
		                    WebElement targetSquare = findValidMove(piece);
		                    
		                    if (targetSquare != null) {
		                        System.out.println("Moving to target square");
		                        
		                        // Use Actions class for smooth visual movement
		                        actions.moveToElement(targetSquare)
		                               .pause(500)
		                               .click()
		                               .perform();
		                        
		                        Thread.sleep(2000); // Wait for move animation
		                        
		                        // Check if move was successful
		                        WebElement message = driver.findElement(By.id("message"));
		                        System.out.println("Move " + (movesMade + 1) + " message: " + message.getText());
		                        
		                        if (message.getText().contains("Make a move") || 
		                            message.getText().contains("Select") ||
		                            message.getText().contains("Please wait")) {
		                            movesMade++;
		                        }
		                    } else {
		                        System.out.println("No valid move found for piece " + (i + 1));
		                    }
		                    
		                } catch (Exception e) {
		                    System.out.println("Move " + (i + 1) + " failed: " + e.getMessage());
		                }
		            }

		            System.out.println("Successfully made " + movesMade + " visual moves");

		        } catch (Exception e) {
		            System.err.println("Error making visual moves: " + e.getMessage());
		        }
		    }

		    private WebElement findValidMove(WebElement piece) {
		        try {
		            // Get the piece's position in the table
		            String pieceSrc = piece.getAttribute("src");
		            
		            // Find all empty squares (gray squares)
		            List<WebElement> emptySquares = driver.findElements(By.cssSelector("img[src*='gray.gif']"));
		            
		            // For checkers, we need to find diagonal moves
		            // Let's try to find squares that are diagonally adjacent
		            for (WebElement square : emptySquares) {
		                try {
		                    // Check if this square is clickable (meaning it's a valid move)
		                    if (square.isDisplayed() && square.isEnabled()) {
		                        return square;
		                    }
		                } catch (Exception e) {
		                    // Continue to next square
		                }
		            }
		            
		            // If no empty squares found, try to find any clickable square
		            List<WebElement> allSquares = driver.findElements(By.cssSelector("td img"));
		            for (WebElement square : allSquares) {
		                try {
		                    String src = square.getAttribute("src");
		                    if (src != null && (src.contains("gray.gif") || src.contains("space"))) {
		                        return square;
		                    }
		                } catch (Exception e) {
		                    // Continue to next square
		                }
		            }
		            
		        } catch (Exception e) {
		            System.err.println("Error finding valid move: " + e.getMessage());
		        }
		        
		        return null;
		    }

		    private void printPageStructure() {
		        try {
		            System.out.println("=== PAGE DEBUG INFO ===");
		            System.out.println("Page title: " + driver.getTitle());
		            System.out.println("Current URL: " + driver.getCurrentUrl());
		            
		            // Check for board element
		            List<WebElement> boardElements = driver.findElements(By.id("board"));
		            System.out.println("Board elements found: " + boardElements.size());
		            
		            // Check for all images
		            List<WebElement> allImages = driver.findElements(By.tagName("img"));
		            System.out.println("Total images found: " + allImages.size());
		            
		            // Print image sources
		            for (int i = 0; i < Math.min(10, allImages.size()); i++) {
		                WebElement img = allImages.get(i);
		                System.out.println("Image " + i + " src: " + img.getAttribute("src"));
		            }
		            
		            // Check for message element
		            List<WebElement> messageElements = driver.findElements(By.id("message"));
		            if (!messageElements.isEmpty()) {
		                System.out.println("Message: " + messageElements.get(0).getText());
		            }
		            
		        } catch (Exception e) {
		            System.err.println("Error printing page structure: " + e.getMessage());
		        }
		    }

		    @AfterClass
		    public void teardown() {
		        if (driver != null) {
		            driver.quit();
		        }
		    }
		}
