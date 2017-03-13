package com.jabarasca.financial_app.test;

import android.content.res.Resources;
import android.support.test.espresso.AmbiguousViewMatcherException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.ImageButton;

import com.jabarasca.financial_app.MainActivity;
import com.jabarasca.financial_app.R;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class MainActivityTest {

    //****************** IMPORTANT (BEFORE RUN THE TEST) **************************
    //OBS: Animations on device needs to be turned off.
    //OBS2: All months on the Year before the actual Year where the test is
    //executed needs to have one amount.
    //OBS3: The database needs to be on default state (Clear data if necessary).
    //*****************************************************************************

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    private int datePickerYearId;
    private Matcher<View> actBarHomeIconMatcher;

    @Before
    public void setUp() {
        datePickerYearId = Resources.getSystem().getIdentifier("year", "id", "android");
        actBarHomeIconMatcher = Matchers.allOf(ViewMatchers
                .withParent(ViewMatchers.withClassName(is(Toolbar.class.getName()))),
                ViewMatchers.withClassName(is(ImageButton.class.getName()))
        );
        //If is the first time that the app launch on device.
        try {
            onView(ViewMatchers.withId(R.id.helpOkButton)).perform(ViewActions.click());
        } catch (NoMatchingViewException e){}
    }

    @Test
    public void accessAnnualAnalysis() {
        //Open ActBar Drawer Toggle.
        onView(actBarHomeIconMatcher).perform(ViewActions.click());
        //Click on menu options.
        onView(ViewMatchers.withText(R.string.actbardrawtoggle_menu_option_1))
                .perform(ViewActions.click());
        //Swipe datePicker month top-bottom.
        onView(ViewMatchers.withId(datePickerYearId)).perform(ViewActions.swipeDown());
        onView(ViewMatchers.withId(R.id.datePickerButton)).perform(ViewActions.click());
    }

    @Test
    public void changeAmountsMonth() {
        onView(ViewMatchers.withId(R.id.actionBarTextView)).perform(ViewActions.click());
        onView(ViewMatchers.withId(datePickerYearId)).perform(ViewActions.swipeDown());
        onView(ViewMatchers.withId(R.id.datePickerButton)).perform(ViewActions.click());
    }

    @Test
    public void addIncomeAmount() {
        performSwipeRemoveAmount();

        String incomeValue = "125.57";
        String titleValue = "My Title";
        onView(ViewMatchers.withId(R.id.addButton)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.add_menu_option_2)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.amountEditText))
                .perform(ViewActions.typeText(incomeValue), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.titleYesRadioBtn)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.titleEditText))
                .perform(ViewActions.typeText(titleValue), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.amountDialogButtonOk)).perform(ViewActions.click());
        performShowAmountDetail();
        performSwipeRemoveAmount();
    }

    @Test
    public void addExpenseAmount() {
        performSwipeRemoveAmount();

        String incomeValue = "225.23";
        String titleValue = "My Title";
        onView(ViewMatchers.withId(R.id.addButton)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.add_menu_option_1)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.amountEditText))
                .perform(ViewActions.typeText(incomeValue), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.titleYesRadioBtn)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.titleEditText))
                .perform(ViewActions.typeText(titleValue), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.amountDialogButtonOk)).perform(ViewActions.click());
        performShowAmountDetail();
        performSwipeRemoveAmount();
    }

    @Test
    public void showInfiniteAmountConfig() {
        onView(ViewMatchers.withId(R.id.actionBarTextView)).perform(ViewActions.click());
        onView(ViewMatchers.withId(datePickerYearId)).perform(ViewActions.swipeDown());
        onView(ViewMatchers.withId(R.id.datePickerButton)).perform(ViewActions.click());
        performShowAmountDetail();
        performSwipeRemoveAmount();

        boolean showAmountDetail = true;
        String incomeValue = "999999";
        try {
            while (true) {
                onView(ViewMatchers.withId(R.id.addButton)).perform(ViewActions.click());
                onView(ViewMatchers.withText(R.string.add_menu_option_1)).perform(ViewActions.click());
                onView(ViewMatchers.withId(R.id.amountEditText))
                        .perform(ViewActions.typeText(incomeValue), ViewActions.closeSoftKeyboard());
                onView(ViewMatchers.withId(R.id.amountDialogButtonOk)).perform(ViewActions.click());
                //Show detail only on the first amount added.
                if (showAmountDetail) {
                    showAmountDetail = false;
                    performShowAmountDetail();
                }
            }
        } catch (NoMatchingViewException e) {
            onView(ViewMatchers.withId(R.id.amountDialogButtonCancel)).perform(ViewActions.click());
        }
    }

    @Test
    public void showHelp() {
        onView(actBarHomeIconMatcher).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.actbardrawtoggle_menu_option_2))
                .perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.helpImgSwitcher)).perform(ViewActions.swipeLeft());
        onView(ViewMatchers.withId(R.id.helpImgSwitcher)).perform(ViewActions.swipeLeft());
        onView(ViewMatchers.withId(R.id.helpImgSwitcher)).perform(ViewActions.swipeLeft());
        onView(ViewMatchers.withId(R.id.helpImgSwitcher)).perform(ViewActions.swipeRight());
        onView(ViewMatchers.withId(R.id.helpImgSwitcher)).perform(ViewActions.swipeRight());
        onView(ViewMatchers.withId(R.id.helpImgSwitcher)).perform(ViewActions.swipeRight());
        onView(ViewMatchers.withId(R.id.helpOkButton)).perform(ViewActions.click());
    }

    private void performSwipeRemoveAmount() {
        try {
            GeneralSwipeAction swipeDismissAction = new GeneralSwipeAction(Swipe.FAST,
                    GeneralLocation.CENTER, GeneralLocation.CENTER_RIGHT, Press.PINPOINT);
            onView(ViewMatchers.withId(R.id.amountItemRelativeLay)).perform(swipeDismissAction);
        }catch (AmbiguousViewMatcherException e){}
        catch (NoMatchingViewException e){}
    }

    private void performShowAmountDetail() {
        try {
            onView(ViewMatchers.withId(R.id.amountItemTextView)).perform(ViewActions.longClick());
            onView(ViewMatchers.withId(R.id.amountDetailButton)).perform(ViewActions.click());
        } catch (AmbiguousViewMatcherException e){}
        catch (NoMatchingViewException e){}
    }

    @After
    public void tearDown() {
        actBarHomeIconMatcher = null;
    }
}
