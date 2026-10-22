package com.example.connective

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.connective.ui.screens.HomeScreen
import com.example.connective.ui.theme.CONNECTIVETheme
import org.junit.Rule
import org.junit.Test

class HomeScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHomeScreenRendersAppTitleAndSignInButtonForGuest() {
        composeTestRule.setContent {
            CONNECTIVETheme {
                HomeScreen(
                    isLoggedIn = false,
                    onSignInClick = {},
                    onJobClick = {}
                )
            }
        }

        // Assert app title is displayed
        composeTestRule.onNodeWithText("Connective").assertIsDisplayed()

        // Assert Sign In button is present for guest user
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()

        // Assert Search input placeholder is rendered
        composeTestRule.onNodeWithText("Search jobs, qualifications, companies...").assertIsDisplayed()
    }

    @Test
    fun testHomeScreenRendersFilterChips() {
        composeTestRule.setContent {
            CONNECTIVETheme {
                HomeScreen(
                    isLoggedIn = true,
                    onSignInClick = {},
                    onJobClick = {}
                )
            }
        }

        // Assert qualification filter chips are present
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("My Qualification").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bachelor's").assertIsDisplayed()
    }

    @Test
    fun testHomeScreenRendersFeaturedJobCard() {
        composeTestRule.setContent {
            CONNECTIVETheme {
                HomeScreen(
                    isLoggedIn = true,
                    onSignInClick = {},
                    onJobClick = {}
                )
            }
        }

        // Assert featured job title is visible
        composeTestRule.onNodeWithText("Software Developer").assertIsDisplayed()
        composeTestRule.onNodeWithText("TechWave Ltd").assertIsDisplayed()
    }
}