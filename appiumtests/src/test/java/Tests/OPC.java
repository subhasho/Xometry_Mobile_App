package Tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OPC extends BaseClass {

    private static final By START_CONVERSATION =
        By.xpath("//*[contains(@content-desc,'Start Conversation')]");

    private static final By DROPDOWN_SECOND =
        By.xpath("//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[4]");
    private static final By FIRST_DROPDOWN_OPTION =
        By.xpath("//android.view.ViewGroup[@content-desc=\"Print/CAD discrepancy\"]/android.view.ViewGroup");

    private static final By ADDITIONAL_INFO_EDIT = By.xpath(
        "//android.widget.EditText["
            + "contains(@text,'Provide additional') or "
            + "contains(@content-desc,'Provide additional') or "
            + "contains(@hint,'Provide additional')"
            + "]"
    );

    /** Pause after description text so the form can settle before consent (Tests 10–11). */
    private static final int WAIT_AFTER_DESCRIPTION_FIELD_MS = 3000;

    private static final String APP_PACKAGE = "com.xometry.workcenter.preview.stage";

    /**
     * Consent checkbox: parent row often has a long content-desc; target inner ViewGroup (not the full exact string).
     */
    private static final By CONSENT_CHECKBOX =
        By.xpath(
            "//*[contains(@content-desc,'you consent to the monitoring')]/android.view.ViewGroup"
                + " | //*[contains(@content-desc,'By using chat')]/android.view.ViewGroup[last()]"
        );

    /**
     * Inbox thread row (exact content-desc from UI). If relative time changes (e.g. {@code 2m ago} → {@code 5m ago}),
     * update this string or rely on {@link #INBOX_CONVERSATION_ROW_FALLBACK}.
     */
    private static final By INBOX_CONVERSATION_ROW_EXACT =
        By.xpath(
            "//android.view.ViewGroup[@content-desc=\"GS, Print/CAD discrepancy, 2m ago, Genis Sage, Hi team, "
                + "I need some help with TestJob., Issue: Print/CAD discrepancy, Test, JOB ID:, TestJob, "
                + "Waiting on Xomer\"]"
        );

    /** Job id substring shown on the Inbox thread row (OPC Test 12 scroll + xpath). */
    private static final String OPC12_INBOX_THREAD_JOB_ID = "J004CD16";

    /** When the exact content-desc differs (time text, etc.), match stable substrings (OPC Test 12). */
    private static final By INBOX_CONVERSATION_ROW_FALLBACK =
        By.xpath(
            "(//*[contains(@content-desc,'" + OPC12_INBOX_THREAD_JOB_ID + "')]"
                + "[contains(translate(@content-desc,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'print')])[1]"
                + " | (//android.widget.TextView[@text='Print/CAD discrepancy'])[1]"
        );

    /**
     * After opening a thread from Inbox, the conversation detail screen should show one of these
     * (message area, send control, or explicit Conversation semantics). Adjust if your build differs.
     */
    private static final By CONVERSATION_PAGE_MARKER =
        By.xpath(
            "//*[contains(@content-desc,'Conversation')]"
                + " | //*[contains(@content-desc,'Type a message')]"
                + " | //*[contains(@content-desc,'Send')]"
                + " | //*[contains(@content-desc,'Write something')]"
                + " | //*[contains(@hint,'Write something')]"
                + " | //android.widget.EditText[contains(@hint,'Message') or contains(@hint,'message')]"
                + " | //android.widget.EditText[contains(@hint,'essage')]"
                + " | //android.view.ViewGroup[@content-desc=\"View files\"]"
                + " | //*[contains(@content-desc,'View files')]"
                + " | //android.widget.ImageView[@content-desc=\"view\"]"
        );

    /** Conversation screen — opens file list / attachments (OPC Test 13). */
    private static final By VIEW_FILES_ICON = By.xpath("//android.widget.ImageView[@content-desc=\"view\"]");

    /** After tapping View files, wait for the files UI to open before BACK (OPC Test 13). */
    private static final int WAIT_AFTER_VIEW_FILES_OPEN_MS = 5000;

    /** "Job ID" label on conversation / thread UI (OPC Test 14). */
    private static final By JOB_ID_LABEL =
        By.xpath("//android.widget.TextView[@text=\"Job ID\"]");

    /** After tapping Job ID, allow the next screen to settle before BACK (OPC Test 14). */
    private static final int WAIT_AFTER_JOB_ID_PAGE_MS = 3500;

    /** OPC Test 27 — pause before the final success log (UI settle after Test 26). */
    private static final int WAIT_BEFORE_OPC27_SUCCESS_LOG_MS = 2000;

    /** Plus control (OPC Test 15). */
    private static final By PLUS_ICON =
        By.xpath("//android.widget.ImageView[@content-desc=\"plus\"]");

    /** Opens file picker (OPC Test 16). */
    private static final By ADD_FILES_TEXT =
        By.xpath("//android.widget.TextView[@text=\"Add Files\"]");

    /**
     * After upload, the thread UI should show an attachment / file chip / preview (tune for your build).
     */
    private static final By UPLOAD_SUCCESS_MARKERS =
        By.xpath(
            "//*[contains(@content-desc,'Attached') or contains(@text,'Attached')]"
                + " | //*[contains(@content-desc,'attachment') or contains(@text,'attachment')]"
                + " | //*[contains(@content-desc,'file')]"
                + " | //android.widget.ImageView[contains(@content-desc,'file')]"
                + " | //*[contains(@text,'.pdf') or contains(@text,'.jpg') or contains(@text,'.png')]"
                + " | //*[contains(@content-desc,'Upload') and (contains(@content-desc,'complete') or contains(@content-desc,'success'))]"
        );

    /**
     * Add Participants (OPC Test 17) — apps expose either {@code ViewGroup} or {@code TextView}; union avoids
     * timeouts when only one variant exists.
     */
    private static final By ADD_PARTICIPANTS_ANY =
        By.xpath(
            "//android.view.ViewGroup[@content-desc=\"Add Participants\"]"
                + " | //android.widget.TextView[@text=\"Add Participants\"]");

    /** Add Participants button (OPC Test 19) — explicit ViewGroup tap after participant row. */
    private static final By ADD_PARTICIPANTS_VIEWGROUP =
        By.xpath("//android.view.ViewGroup[@content-desc=\"Add Participants\"]");

    /**
     * Checkbox cell for the first participant row (OPC Test 18). Inner {@code ViewGroup[1]} of the row
     * identified by {@code content-desc}. Update the parent row if your list shows a different user.
     */
    private static final By PARTICIPANT_ROW_CHECKBOX =
        By.xpath(
            "//android.view.ViewGroup[@content-desc=\"BR, Brendan Hamilton, brendan.hamilton@xombuilder.com, Partner\"]"
                + "/android.view.ViewGroup[1]");

    /**
     * Shown after adding participants (OPC Test 20). Tune using Appium Inspector if your app uses different copy.
     */
    private static final By PARTICIPANT_ADD_SUCCESS_MARKERS =
        By.xpath(
            "//*[contains(@text,'added') or contains(@text,'Added') or contains(@text,'success')]"
                + " | //*[contains(@text,'Successfully') or contains(@text,'successfully')]"
                + " | //*[contains(@content-desc,'added') or contains(@content-desc,'Added')]"
                + " | //*[contains(@text,'participant') and (contains(@text,'add') or contains(@text,'Add'))]"
                + " | //*[contains(@content-desc,'Brendan Hamilton')]"
                + " | //android.widget.TextView[contains(@text,'Participant')]"
                + " | //*[contains(@content-desc,'Partner') and contains(@content-desc,'@')]"
        );

    /** Rich-text bold toggle (OPC Test 21). */
    private static final By BOLD_ICON_B = By.xpath("//android.widget.TextView[@text=\"B\"]");

    /** Send / advance after composing (OPC Tests 21–24). Primary match; see {@link #ENTER_CARET_SEND_CANDIDATES}. */
    private static final By ENTER_CARET_RIGHT =
        By.xpath("//android.widget.ImageView[@content-desc=\"caretRight\"]");

    /**
     * Ordered fallbacks: multiline / numbered-list composer sometimes uses a non-ImageView node or a slightly
     * different content-desc; {@link #clickCaretSend} walks these before failing.
     */
    private static final By[] ENTER_CARET_SEND_CANDIDATES =
        new By[] {
            ENTER_CARET_RIGHT,
            By.xpath("//*[@content-desc=\"caretRight\"]"),
            By.xpath("//android.widget.ImageView[contains(@content-desc,\"caret\")]")
        };


    /** Rich-text italic toggle (OPC Test 22). */
    private static final By ITALIC_ICON_I = By.xpath("//android.widget.TextView[@text=\"I\"]");

    /** Rich-text strikethrough / S toggle (OPC Test 23). */
    private static final By STRIKETHROUGH_ICON_S = By.xpath("//android.widget.TextView[@text=\"S\"]");

    /** OPC Test 24 — numbered list toolbar control. */
    private static final By OPC24_NUMBERED_LIST_BUTTON =
        By.xpath("//android.widget.Button[@content-desc=\"Numbered list\"]");

    /**
     * OPC Test 24 — composer showing “Write something…”. Union covers {@code @text}, {@code @hint}, and Unicode
     * ellipsis ({@code \u2026}).
     */
    private static final By OPC24_WRITE_SOMETHING_EDITTEXT =
        By.xpath(
            "//android.widget.EditText[@text='Write something \u2026']"
                + " | //android.widget.EditText[contains(@hint,'Write something')]"
                + " | //android.widget.EditText[contains(@text,'Write something')]");

    /** OPC Test 26 — open Insert link from composer toolbar. */
    private static final By INSERT_LINK_TOOLBAR_BUTTON =
        By.xpath("//android.widget.Button[@content-desc=\"Insert link\"]");

    /** OPC Test 26 — link label field. */
    private static final By INSERT_LINK_LABEL_EDITTEXT =
        By.xpath("//android.widget.EditText[@text=\"Enter link label\"]");

    /** OPC Test 26 — URL field (default text often {@code https://}). */
    private static final By INSERT_LINK_URL_EDITTEXT =
        By.xpath("//android.widget.EditText[@text=\"https://\"]");

    /** OPC Test 26 — confirm insert. */
    private static final By INSERT_LINK_CONFIRM_VIEWGROUP =
        By.xpath("//android.view.ViewGroup[@content-desc=\"Insert Link\"]");

    /**
     * OPC Test 26 — after {@link #ENTER_CARET_RIGHT} send, conversation shows the link (case-insensitive
     * {@code test.com} in {@code @text} or {@code @content-desc}).
     */
    private static final By OPC26_LINK_SENT_MARKERS =
        By.xpath(
            "//*[contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'test.com')]"
                + " | //*[contains(translate(@content-desc,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'test.com')]"
                + " | //*[contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'https://test.com')]"
        );

    /**
     * Message composer — must be focused before {@link #typeLatinViaIme} or keys go nowhere on screen (OPC 21–26).
     * {@code [last()]} prefers the bottom field on chat screens with multiple {@code EditText}s.
     */
    private static final By MESSAGE_COMPOSER_EDITTEXT =
        By.xpath(
            "(//android.widget.EditText["
                + "contains(@hint,'Message') or contains(@hint,'message') or contains(@hint,'essage') or contains(@hint,'Write something')"
                + " or contains(@content-desc,'Type a message') or contains(@content-desc,'message')"
                + "])[last()]"
        );

    private static final By MESSAGE_COMPOSER_EDITTEXT_FALLBACK = By.xpath("(//android.widget.EditText)[last()]");

    /** Any visible {@code EditText} (last match often = bottom composer). */
    private static final By ALL_EDITTEXTS =
        MobileBy.AndroidUIAutomator("new UiSelector().className(\"android.widget.EditText\")");

    @Test(priority = 1, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_JobManagement() {

        System.out.println("🚀 OPC Test 1 STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);

        MobileElement jobManagement = (MobileElement) wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//android.view.View[@content-desc='Job Management']")
            )
        );

        jobManagement.click();

        System.out.println("✅ OPC Test 1 PASSED");
    }

    @Test(priority = 2, dependsOnMethods = "opc_JobManagement")
    public void opc_SearchJob() {

        System.out.println("🚀 OPC Test 2 STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);

        MobileElement searchBox = (MobileElement) wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.widget.EditText")
            )
        );

        searchBox.sendKeys("J004");

        System.out.println("✅ OPC Test 2 PASSED");
    }

    @Test(priority = 3, dependsOnMethods = "opc_SearchJob")
    public void opc_ViewDetails() throws InterruptedException {

        System.out.println("🚀 OPC Test 3 STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        int maxScrolls = 8;
        boolean found = false;

        for (int i = 0; i < maxScrolls; i++) {

            Thread.sleep(1500);

            if (driver.findElements(
                    By.xpath("//*[contains(@content-desc,'View Details')]")
            ).size() > 0) {

                MobileElement viewDetails = driver.findElement(
                    By.xpath("//*[contains(@content-desc,'View Details')]")
                );

                viewDetails.click();

                System.out.println("✅ Clicked View Details");

                boolean isNextPageLoaded = driver.findElements(
                    By.xpath("//*[contains(@content-desc,'Job Overview')]")
                ).size() > 0;

                Assert.assertTrue(isNextPageLoaded, "❌ Navigation failed");

                System.out.println("✅ OPC Test 3 PASSED");

                found = true;
                break;
            }

            swipeUpSlow(driver);
            System.out.println("🔄 Swiping... Attempt: " + (i + 1));
        }

        if (!found) {
            Assert.fail("❌ View Details not found");
        }
    }

    @Test(priority = 4, dependsOnMethods = "opc_ViewDetails")
    public void opc_ContactUs() throws InterruptedException {

        System.out.println("🚀 OPC Test 4 STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        /* OLD: strict ViewGroup only — often misses when node is View or label differs slightly
        MobileElement contactUsButton = (MobileElement) wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//android.view.ViewGroup[@content-desc='Contact Us']")
            )
        );
        contactUsButton.click();
        */

        By contactUsFlexible = By.xpath("//*[contains(@content-desc,'Contact Us')]");
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\").scrollable(true))"
                    + ".scrollIntoView(new UiSelector().descriptionContains(\"Contact Us\"))"));
        } catch (Exception ignored) {
        }
        Thread.sleep(600);

        boolean clicked = false;
        for (int i = 0; i < 12; i++) {
            if (!driver.findElements(contactUsFlexible).isEmpty()) {
                try {
                    MobileElement btn = (MobileElement) wait.until(
                        ExpectedConditions.elementToBeClickable(contactUsFlexible));
                    btn.click();
                    clicked = true;
                    break;
                } catch (Exception e) {
                    System.out.println("Contact Us not clickable yet, scrolling... " + (i + 1));
                }
            }
            swipeUpSlow(driver);
            Thread.sleep(500);
        }
        Assert.assertTrue(clicked, "❌ Contact Us not found or not clickable after scrolling");

        boolean isNextScreen = driver.findElements(
            By.xpath("//*[contains(@content-desc,'Contact')]")
        ).size() > 0;

        Assert.assertTrue(isNextScreen, "❌ Contact page not opened");

        System.out.println("✅ OPC Test 4 PASSED");
    }

    @Test(priority = 5, dependsOnMethods = "opc_ContactUs")
    public void opc_ClickDropdown() {

        System.out.println("🚀 OPC Test 5 STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);

        MobileElement dropdown = (MobileElement) wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[1]")
            )
        );

        dropdown.click();

        System.out.println("✅ OPC Test 5 PASSED");
    }

    @Test(priority = 6, dependsOnMethods = "opc_ClickDropdown")
    public void opc_ClickCheckbox() throws InterruptedException {

        System.out.println("🚀 OPC Test 6 STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);

        try {
            /* Prior: Part 1 row — now step file row (OPC Test 6). */
            MobileElement checkboxRow = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("(//android.view.ViewGroup[@content-desc=\"01CEF4B_model_r_0.step\"])[1]/android.view.ViewGroup[2]")
                )
            );

            checkboxRow.click();
            System.out.println("✅ Checkbox selected");

            Thread.sleep(1000);

            TouchAction<?> action = new TouchAction<>(driver);
            action.tap(PointOption.point(500, 300)).perform();

            System.out.println("✅ Dropdown closed");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 6 FAILED");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 7, dependsOnMethods = "opc_ClickCheckbox")
    public void opc_SelectFirstOptionFromDropdown() throws InterruptedException {

        System.out.println("🚀 OPC Test 7 (Dropdown + Select First Option) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);

        try {
            try {
                driver.hideKeyboard();
            } catch (Exception ignored) {
            }
            Thread.sleep(500);

            scrollUntilElementDisplayed(driver, DROPDOWN_SECOND, 10);

            MobileElement dropdown = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(DROPDOWN_SECOND)
            );

            wait.until(ExpectedConditions.visibilityOf(dropdown));
            dropdown.click();

            System.out.println("👉 Dropdown clicked");
            Thread.sleep(1500);

            scrollUntilElementDisplayed(driver, FIRST_DROPDOWN_OPTION, 10);

            MobileElement firstOption = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(FIRST_DROPDOWN_OPTION)
            );

            wait.until(ExpectedConditions.visibilityOf(firstOption));
            firstOption.click();

            try {
                driver.hideKeyboard();
            } catch (Exception ignored) {
            }
            Thread.sleep(500);

            System.out.println("✅ OPC Test 7 PASSED - First option selected");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 7 FAILED - Unable to select first option");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 8, dependsOnMethods = "opc_SelectFirstOptionFromDropdown")
    public void opc_ScrollToStartConversation() throws InterruptedException {

        System.out.println("🚀 OPC Test 8 (scroll to Start Conversation) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            driver.hideKeyboard();
        } catch (Exception ignored) {
        }
        Thread.sleep(400);

        uiScrollIntoViewStartConversation(driver);
        Thread.sleep(600);

        if (isDisplayed(driver, START_CONVERSATION)) {
            System.out.println("✅ OPC Test 8 PASSED — Start Conversation visible (UiScrollable)");
            return;
        }

        for (int i = 0; i < 14; i++) {
            Thread.sleep(500);
            if (isDisplayed(driver, START_CONVERSATION)) {
                System.out.println("✅ OPC Test 8 PASSED — after swipe up");
                return;
            }
            swipeUpSlow(driver);
            System.out.println("🔄 Swipe up (reveal below)... " + (i + 1));
        }

        for (int i = 0; i < 14; i++) {
            Thread.sleep(500);
            if (isDisplayed(driver, START_CONVERSATION)) {
                System.out.println("✅ OPC Test 8 PASSED — after swipe down");
                return;
            }
            swipeDownSlow(driver);
            System.out.println("🔄 Swipe down (reveal above)... " + (i + 1));
        }

        Assert.fail("❌ Start Conversation not found after scrolling");
    }

    @Test(priority = 9, dependsOnMethods = "opc_ScrollToStartConversation")
    public void opc_EnterTextInField() throws InterruptedException {

        System.out.println("🚀 OPC Test 9 (Enter Text) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 25);

        try {
            try {
                driver.hideKeyboard();
            } catch (Exception ignored) {
            }
            Thread.sleep(400);

            scrollUntilElementDisplayed(driver, ADDITIONAL_INFO_EDIT, 12);

            MobileElement inputField = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(ADDITIONAL_INFO_EDIT)
            );

            inputField.click();
            Thread.sleep(500);

            enterTextInFlutterField(driver, inputField, "Test");

            /*
             * IMPORTANT: Do not call hideKeyboard() after typing here. On many Flutter/Android IMEs it issues
             * BACK / dismiss actions that pop the Contact flow and the app appears "closed" before Tests 10–11.
             */
            try {
                tapOutsideKeyboard(driver);
                System.out.println("⌨️ Tapped outside field to dismiss keyboard (no hideKeyboard)");
            } catch (Exception e) {
                System.out.println("Keyboard dismiss tap skipped: " + e.getMessage());
            }

            Thread.sleep(WAIT_AFTER_DESCRIPTION_FIELD_MS);
            System.out.println(
                "⏳ Wait after description field (" + WAIT_AFTER_DESCRIPTION_FIELD_MS + " ms) before next test case");

            System.out.println("✅ OPC Test 9 PASSED");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 9 FAILED");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 10, dependsOnMethods = "opc_EnterTextInField")
    public void opc_ClickConsentCheckbox() throws InterruptedException {

        System.out.println("🚀 OPC Test 10 (Consent Checkbox Click) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 35);

        try {
            bringAppToForeground(driver);

            try {
                driver.hideKeyboard();
            } catch (Exception ignored) {
            }
            Thread.sleep(500);

            try {
                driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\").scrollable(true))"
                        + ".scrollIntoView(new UiSelector().descriptionContains(\"you consent\"))"));
            } catch (Exception ignored) {
            }
            Thread.sleep(600);

            scrollUntilElementDisplayed(driver, CONSENT_CHECKBOX, 10);

            /* OLD: exact full content-desc on parent ViewGroup — brittle and often off-screen
            MobileElement consentCheckbox = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.ViewGroup[@content-desc=\"By using chat, you consent...\"]/android.view.ViewGroup")
                )
            );
            */

            MobileElement consentCheckbox = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(CONSENT_CHECKBOX)
            );

            wait.until(ExpectedConditions.visibilityOf(consentCheckbox));
            consentCheckbox.click();

            System.out.println("✅ OPC Test 10 PASSED - Consent checkbox clicked successfully");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 10 FAILED - Unable to click consent checkbox");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 11, dependsOnMethods = "opc_ClickConsentCheckbox")
    public void opc_ClickStartConversationAndVerifyInbox() throws InterruptedException {

        System.out.println("🚀 OPC Test 11 (Start Conversation + Verify Inbox) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        try {
            bringAppToForeground(driver);

            try {
                driver.hideKeyboard();
            } catch (Exception ignored) {
            }
            Thread.sleep(400);

            if (!isDisplayed(driver, START_CONVERSATION)) {
                uiScrollIntoViewStartConversation(driver);
                Thread.sleep(400);
            }
            scrollUntilElementDisplayed(driver, START_CONVERSATION, 8);

            MobileElement startConversationBtn = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.ViewGroup[@content-desc='Start Conversation']")
                )
            );

            wait.until(ExpectedConditions.visibilityOf(startConversationBtn));
            startConversationBtn.click();

            System.out.println("👉 Start Conversation button clicked");

            MobileElement inboxElement = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(@content-desc,'Inbox')]")
                )
            );

            Assert.assertTrue(inboxElement.isDisplayed(), "Inbox page is not displayed");

            System.out.println("✅ OPC Test 11 PASSED - Redirected to Inbox page successfully");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 11 FAILED - Navigation to Inbox failed");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Runs after login when {@code Tests.Tests} is in the suite; with {@code testng-opc-only.xml} the {@code login}
     * group may be absent — {@code ignoreMissingDependencies} allows OPC-only runs (session must already be logged in).
     */
    @Test(priority = 12, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_ClickInboxConversationRow() throws InterruptedException {

        System.out.println("🚀 OPC Test 12 (Inbox — click conversation thread row) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        try {
            bringAppToForeground(driver);
            Thread.sleep(800);

            // Home: tap Inbox in bottom navigation (icon / tab) to open the Inbox list
            final By inboxIconHome =
                By.xpath(
                    "//android.widget.BottomNavigationView//android.widget.ImageView[contains(@content-desc,'Inbox')]"
                        + " | //android.widget.BottomNavigationView//android.view.View[contains(@content-desc,'Inbox')]"
                        + " | //android.widget.ImageView[contains(@content-desc,'Inbox')]"
                        + " | //android.view.View[@content-desc='Inbox' and (@clickable='true' or @focusable='true')]"
                );
            MobileElement inboxOnHome =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(inboxIconHome));
            inboxOnHome.click();
            Thread.sleep(800);

            MobileElement inboxTag =
                (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("(//*[contains(@content-desc,'Inbox')])[1]")));
            inboxTag.click();
            Thread.sleep(600);

            try {
                driver.findElement(
                    MobileBy.AndroidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true))"
                            + ".scrollIntoView(new UiSelector().descriptionContains(\""
                            + OPC12_INBOX_THREAD_JOB_ID
                            + "\"))"));
            } catch (Exception ignored) {
            }
            Thread.sleep(400);

            MobileElement threadLabel =
                (MobileElement)
                    new WebDriverWait(driver, 45)
                        .until(ExpectedConditions.presenceOfElementLocated(INBOX_CONVERSATION_ROW_FALLBACK));
            try {
                threadLabel.click();
            } catch (Exception e) {
                int cx = threadLabel.getLocation().getX() + threadLabel.getSize().getWidth() / 2;
                int cy = threadLabel.getLocation().getY() + threadLabel.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }
            Thread.sleep(1200);

            new WebDriverWait(driver, 40).until(
                ExpectedConditions.presenceOfElementLocated(CONVERSATION_PAGE_MARKER));

            System.out.println("✅ OPC Test 12 PASSED — Clicked thread label; conversation page visible");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 12 FAILED — Inbox row click or conversation page verification failed");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 13, dependsOnMethods = "opc_ClickInboxConversationRow")
    public void opc_ClickLinkIcon() throws InterruptedException {

        System.out.println("🚀 OPC Test 13 (Conversation — View files) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 60);

        try {
            bringAppToForeground(driver);
            Thread.sleep(800);

            try {
                driver.findElement(
                    MobileBy.AndroidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true))"
                            + ".scrollIntoView(new UiSelector().description(\"View files\"))"));
            } catch (Exception ignored) {
            }

            MobileElement viewFiles =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(VIEW_FILES_ICON));
            try {
                viewFiles.click();
            } catch (Exception ignored) {
                int cx = viewFiles.getLocation().getX() + viewFiles.getSize().getWidth() / 2;
                int cy = viewFiles.getLocation().getY() + viewFiles.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }

            Thread.sleep(WAIT_AFTER_VIEW_FILES_OPEN_MS);

            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            Thread.sleep(800);

            System.out.println("✅ OPC Test 13 PASSED — View files clicked; waited for files UI; BACK once");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 13 FAILED — View files icon not found or not clickable");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Resolves the job ID value TextView shown with the "Job ID" label (any ID text).
     * Prefers a sibling TextView; otherwise the first following TextView whose text is not the label.
     */
    private MobileElement findJobIdValueElement(AndroidDriver<MobileElement> driver) {
        List<MobileElement> siblings = driver.findElements(By.xpath(
            "//android.widget.TextView[@text=\"Job ID\"]/following-sibling::android.widget.TextView"));
        if (!siblings.isEmpty()) {
            return siblings.get(0);
        }
        List<MobileElement> following = driver.findElements(By.xpath(
            "//android.widget.TextView[@text=\"Job ID\"]/following::android.widget.TextView"));
        for (MobileElement el : following) {
            try {
                String t = el.getText();
                if (t != null) {
                    String trimmed = t.trim();
                    if (!trimmed.isEmpty() && !"Job ID".equals(trimmed)) {
                        return el;
                    }
                }
            } catch (StaleElementReferenceException e) {
                // keep scanning
            }
        }
        return null;
    }

    @Test(priority = 14, dependsOnMethods = "opc_ClickLinkIcon")
    public void opc_ClickJobIdTestJob() throws InterruptedException {

        System.out.println("🚀 OPC Test 14 (Job ID — click value after \"Job ID\" label) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        try {
            bringAppToForeground(driver);
            Thread.sleep(600);

            wait.until(ExpectedConditions.visibilityOfElementLocated(JOB_ID_LABEL));

            MobileElement jobIdValue = null;
            long deadline = System.currentTimeMillis() + 30000;
            while (System.currentTimeMillis() < deadline) {
                jobIdValue = findJobIdValueElement(driver);
                if (jobIdValue != null) {
                    try {
                        if (jobIdValue.isDisplayed()) {
                            break;
                        }
                    } catch (StaleElementReferenceException e) {
                        jobIdValue = null;
                    }
                }
                Thread.sleep(200);
            }
            if (jobIdValue == null) {
                throw new RuntimeException("No job ID value TextView found after \"Job ID\" label");
            }

            try {
                jobIdValue.click();
            } catch (StaleElementReferenceException e) {
                jobIdValue = findJobIdValueElement(driver);
                if (jobIdValue == null) {
                    throw e;
                }
                jobIdValue.click();
            } catch (Exception clickEx) {
                MobileElement tapTarget = findJobIdValueElement(driver);
                if (tapTarget == null) {
                    throw clickEx;
                }
                int cx = tapTarget.getLocation().getX() + tapTarget.getSize().getWidth() / 2;
                int cy = tapTarget.getLocation().getY() + tapTarget.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }

            Thread.sleep(WAIT_AFTER_JOB_ID_PAGE_MS);

            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            Thread.sleep(800);

            System.out.println("✅ OPC Test 14 PASSED — Job ID value clicked; page settled; BACK once");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 14 FAILED — \"Job ID\" label or value not found / navigation failed");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 15, dependsOnMethods = "opc_ClickJobIdTestJob")
    public void opc_ClickPlusIcon() throws InterruptedException {

        System.out.println("🚀 OPC Test 15 (Plus icon) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        try {
            bringAppToForeground(driver);
            Thread.sleep(600);

            MobileElement plus =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(PLUS_ICON));
            try {
                plus.click();
            } catch (Exception ignored) {
                int cx = plus.getLocation().getX() + plus.getSize().getWidth() / 2;
                int cy = plus.getLocation().getY() + plus.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }

            System.out.println("✅ OPC Test 15 PASSED — Plus icon clicked");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 15 FAILED — Plus icon not found or not clickable");
            e.printStackTrace();
            throw e;
        }
    }

    /*
    @Test(priority = 16, dependsOnMethods = "opc_ClickPlusIcon")
    public void opc_AddFilesUploadFirstAndVerify() throws InterruptedException {

        System.out.println("🚀 OPC Test 16 (Add Files — pick first file on device, verify upload) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 40);

        try {
            bringAppToForeground(driver);
            Thread.sleep(600);

            MobileElement addFiles =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(ADD_FILES_TEXT));
            addFiles.click();
            Thread.sleep(2000);

            pickFirstFileFromSystemPicker(driver);
            confirmPickerIfNeeded(driver);

            new WebDriverWait(driver, 45).until(
                ExpectedConditions.visibilityOfElementLocated(UPLOAD_SUCCESS_MARKERS));

            System.out.println("✅ OPC Test 16 PASSED — First file selected; upload success UI visible");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 16 FAILED — Add Files or upload verification");
            e.printStackTrace();
            throw e;
        }
    }
    */

    @Test(priority = 17, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_ClickAddParticipants() throws InterruptedException {

        System.out.println("🚀 OPC Test 17 (Add Participants) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 45);

        try {
            bringAppToForeground(driver);
            Thread.sleep(600);

            try {
                driver.findElement(
                    MobileBy.AndroidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true))"
                            + ".scrollIntoView(new UiSelector().descriptionContains(\"Add Participants\"))"));
            } catch (Exception ignored) {
            }
            try {
                driver.findElement(
                    MobileBy.AndroidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true))"
                            + ".scrollIntoView(new UiSelector().text(\"Add Participants\"))"));
            } catch (Exception ignored) {
            }

            MobileElement addParticipants =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(ADD_PARTICIPANTS_ANY));
            try {
                addParticipants.click();
            } catch (Exception ignored) {
                int cx = addParticipants.getLocation().getX() + addParticipants.getSize().getWidth() / 2;
                int cy = addParticipants.getLocation().getY() + addParticipants.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }

            System.out.println("✅ OPC Test 17 PASSED — Add Participants clicked");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 17 FAILED — Add Participants not found or not clickable");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 18, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_ClickFirstParticipantCheckbox() throws InterruptedException {

        System.out.println("🚀 OPC Test 18 (Participant row — first checkbox) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        try {
            bringAppToForeground(driver);
            Thread.sleep(800);

            MobileElement checkbox =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(PARTICIPANT_ROW_CHECKBOX));
            try {
                checkbox.click();
            } catch (Exception ignored) {
                int cx = checkbox.getLocation().getX() + checkbox.getSize().getWidth() / 2;
                int cy = checkbox.getLocation().getY() + checkbox.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }

            System.out.println("✅ OPC Test 18 PASSED — Participant row checkbox clicked");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 18 FAILED — Participant checkbox not found or not clickable");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 19, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_ClickAddParticipantsViewGroup() throws InterruptedException {

        System.out.println("🚀 OPC Test 19 (Add Participants — ViewGroup button) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        try {
            bringAppToForeground(driver);
            Thread.sleep(600);

            MobileElement btn =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(ADD_PARTICIPANTS_VIEWGROUP));
            try {
                btn.click();
            } catch (Exception ignored) {
                int cx = btn.getLocation().getX() + btn.getSize().getWidth() / 2;
                int cy = btn.getLocation().getY() + btn.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }

            System.out.println("✅ OPC Test 19 PASSED — Add Participants ViewGroup clicked");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 19 FAILED — Add Participants ViewGroup not found or not clickable");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 20, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_VerifyNewParticipantsAddedSuccessfully() throws InterruptedException {

        System.out.println("🚀 OPC Test 20 (Verify new participants added) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 40);

        try {
            bringAppToForeground(driver);
            Thread.sleep(800);

            wait.until(ExpectedConditions.visibilityOfElementLocated(PARTICIPANT_ADD_SUCCESS_MARKERS));

            System.out.println("✅ OPC Test 20 PASSED — New participant(s) added; success UI visible");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 20 FAILED — Could not verify participants added");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 21, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_BoldTypeTestAndSendCaret() throws InterruptedException {

        System.out.println("🚀 OPC Test 21 (Bold — type TEST via keypad, caret send) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 22);

        try {
            bringAppToForeground(driver);
            Thread.sleep(400);

            tapMessageComposerForFocus(driver);

            MobileElement bold =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(BOLD_ICON_B));
            try {
                bold.click();
            } catch (Exception ignored) {
                int bx = bold.getLocation().getX() + bold.getSize().getWidth() / 2;
                int by = bold.getLocation().getY() + bold.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(bx, by)).perform();
            }

            tapMessageComposerForFocus(driver);

            typeLatinViaImeFast(driver, "TEST");

            clickCaretSend(driver, wait);

            System.out.println("✅ OPC Test 21 PASSED — Bold; TEST via keypad; caret send clicked");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 21 FAILED — Bold, typing, or caret send");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 22, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_ItalicTypeTestAndSendCaret() throws InterruptedException {

        System.out.println("🚀 OPC Test 22 (Italic — type TEST via keypad, caret send) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 22);

        try {
            bringAppToForeground(driver);
            Thread.sleep(400);

            tapMessageComposerForFocus(driver);

            MobileElement italic =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(ITALIC_ICON_I));
            try {
                italic.click();
            } catch (Exception ignored) {
                int ix = italic.getLocation().getX() + italic.getSize().getWidth() / 2;
                int iy = italic.getLocation().getY() + italic.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(ix, iy)).perform();
            }

            tapMessageComposerForFocus(driver);

            typeLatinViaImeFast(driver, "TEST");

            clickCaretSend(driver, wait);

            System.out.println("✅ OPC Test 22 PASSED — Italic; TEST via keypad; caret send clicked");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 22 FAILED — Italic, typing, or caret send");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 23, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_StrikethroughSTypeTest() throws InterruptedException {

        System.out.println("🚀 OPC Test 23 (Strikethrough S — type TEST via keypad, caret send) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 22);

        try {
            bringAppToForeground(driver);
            Thread.sleep(400);

            tapMessageComposerForFocus(driver);

            MobileElement strikethrough =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(STRIKETHROUGH_ICON_S));
            try {
                strikethrough.click();
            } catch (Exception ignored) {
                int sx = strikethrough.getLocation().getX() + strikethrough.getSize().getWidth() / 2;
                int sy = strikethrough.getLocation().getY() + strikethrough.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(sx, sy)).perform();
            }

            tapMessageComposerForFocus(driver);

            typeLatinViaImeFast(driver, "TEST");

            clickCaretSend(driver, wait);

            System.out.println("✅ OPC Test 23 PASSED — S; TEST via keypad; caret send clicked");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 23 FAILED — S, typing, or caret send");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 24, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_PuaTextViewThreeLineTextTest() throws InterruptedException {

        System.out.println("🚀 OPC Test 24 (Numbered list — Write something… multiline, ENTER send) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 22);

        try {
            bringAppToForeground(driver);
            Thread.sleep(400);

            tapMessageComposerForFocus(driver);

            MobileElement numberedListBtn;
            try {
                numberedListBtn =
                    (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(OPC24_NUMBERED_LIST_BUTTON));
            } catch (TimeoutException e) {
                numberedListBtn =
                    (MobileElement)
                        wait.until(ExpectedConditions.visibilityOfElementLocated(OPC24_NUMBERED_LIST_BUTTON));
            }
            try {
                numberedListBtn.click();
            } catch (Exception ignored) {
                int ix = numberedListBtn.getLocation().getX() + numberedListBtn.getSize().getWidth() / 2;
                int iy = numberedListBtn.getLocation().getY() + numberedListBtn.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(ix, iy)).perform();
            }

            Thread.sleep(350);

            MobileElement writeField =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(OPC24_WRITE_SOMETHING_EDITTEXT));
            try {
                writeField.click();
            } catch (Exception ignored) {
                int wx = writeField.getLocation().getX() + writeField.getSize().getWidth() / 2;
                int wy = writeField.getLocation().getY() + writeField.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(wx, wy)).perform();
            }
            Thread.sleep(200);

            typeMultilineIntoEditText(
                driver,
                writeField,
                "First Text TEST S\nSecond text Test2\nEnter text test3");

            Thread.sleep(400);
            /* Hardware ENTER often inserts another newline in multiline “Write something…” — use caret send like 21–23. */
            clickCaretSendAfterMultiline(driver, 35, OPC24_WRITE_SOMETHING_EDITTEXT);
            Thread.sleep(400);

            System.out.println("✅ OPC Test 24 PASSED — Numbered list; Write something…; multiline; caret send");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 24 FAILED — Numbered list, Write something field, typing, or caret send");
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 26, dependsOnGroups = "login", ignoreMissingDependencies = true)
    public void opc_InsertLinkFillAndConfirm() throws InterruptedException {

        System.out.println("🚀 OPC Test 26 (Insert link — label, URL, confirm) STARTED");

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        WebDriverWait wait = new WebDriverWait(driver, 22);

        try {
            bringAppToForeground(driver);
            Thread.sleep(400);

            tapMessageComposerForFocus(driver);

            MobileElement insertLinkBtn =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(INSERT_LINK_TOOLBAR_BUTTON));
            try {
                insertLinkBtn.click();
            } catch (Exception ignored) {
                int bx = insertLinkBtn.getLocation().getX() + insertLinkBtn.getSize().getWidth() / 2;
                int by = insertLinkBtn.getLocation().getY() + insertLinkBtn.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(bx, by)).perform();
            }

            Thread.sleep(500);

            MobileElement labelField =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(INSERT_LINK_LABEL_EDITTEXT));
            labelField.click();
            Thread.sleep(200);
            labelField.sendKeys("TEST");

            MobileElement urlField =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(INSERT_LINK_URL_EDITTEXT));
            urlField.click();
            Thread.sleep(250);
            // Field already shows "https://" — append host only (avoids "https://https://..." and broken links).
            urlField.sendKeys("https://TEST.COM");

            MobileElement insertConfirm =
                (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(INSERT_LINK_CONFIRM_VIEWGROUP));
            try {
                insertConfirm.click();
            } catch (Exception ignored) {
                int cx = insertConfirm.getLocation().getX() + insertConfirm.getSize().getWidth() / 2;
                int cy = insertConfirm.getLocation().getY() + insertConfirm.getSize().getHeight() / 2;
                new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
            }

            Thread.sleep(3500);

            driver.pressKey(new KeyEvent(AndroidKey.ENTER));
            Thread.sleep(600);

            new WebDriverWait(driver, 35).until(
                ExpectedConditions.presenceOfElementLocated(OPC26_LINK_SENT_MARKERS));

            System.out.println("✅ OPC Test 26 PASSED — Insert link; ENTER to send; link visible in conversation");

        } catch (Exception e) {
            System.out.println("❌ OPC Test 26 FAILED — Insert link flow");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Final OPC conversation suite marker — logs success after conversation flows (runs after Test 26 when present).
     */
    @Test(
        priority = 27,
        dependsOnMethods = "opc_InsertLinkFillAndConfirm",
        ignoreMissingDependencies = true)
    public void opc_SuccessfulConversationOpc() throws InterruptedException {

        Thread.sleep(WAIT_BEFORE_OPC27_SUCCESS_LOG_MS);
        System.out.println("✅ OPC Test 27 PASSED — Successful conversation OPC.");
    }

    /**
     * Chooses the first usable item in the system Documents / Files / Gallery sheet (varies by OEM).
     */
    private static void pickFirstFileFromSystemPicker(AndroidDriver<MobileElement> driver)
        throws InterruptedException {

        Thread.sleep(2000);

        By[] quickEntries =
            new By[] {
                By.xpath("(//*[contains(@content-desc,'Gallery')])[1]"),
                By.xpath("(//*[contains(@text,'Gallery') and (@clickable='true' or @focusable='true')])[1]"),
                By.xpath("(//*[contains(@content-desc,'Photos')])[1]"),
                By.xpath("(//*[contains(@content-desc,'Files') or contains(@text,'Files')])[1]"),
                By.xpath("(//*[contains(@content-desc,'Documents')])[1]"),
            };
        for (By by : quickEntries) {
            try {
                List<MobileElement> found = driver.findElements(by);
                if (!found.isEmpty()) {
                    MobileElement e = found.get(0);
                    if (e.isDisplayed() && e.getSize().getHeight() > 20) {
                        e.click();
                        Thread.sleep(1500);
                        break;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        By[] tryFirst =
            new By[] {
                By.xpath("//android.widget.GridView//android.widget.ImageView[1]"),
                By.xpath("(//android.widget.ImageView[@clickable='true'])[3]"),
                By.xpath("(//android.widget.ImageView[@clickable='true'])[2]"),
                By.xpath("(//android.widget.ImageView[@clickable='true'])[4]"),
                By.xpath("(//android.widget.LinearLayout[@clickable='true'])[1]"),
                By.xpath("(//android.widget.LinearLayout[@clickable='true'])[2]"),
                By.xpath("(//android.view.ViewGroup[@clickable='true'])[2]"),
                By.xpath("(//android.view.ViewGroup[@clickable='true'])[3]"),
                By.xpath("(//android.widget.ImageView)[3]"),
                By.xpath("(//android.widget.ImageView)[4]"),
                By.xpath("(//android.widget.ImageView)[5]"),
                By.xpath("(//*[contains(@class,'RecyclerView') or contains(@class,'recyclerview')]//*[@clickable='true'])[1]"),
                By.xpath("(//*[contains(@resource-id,'thumbnail') or contains(@resource-id,'icon')])[1]"),
            };

        for (By by : tryFirst) {
            try {
                MobileElement el =
                    (MobileElement)
                        new WebDriverWait(driver, 8).until(ExpectedConditions.elementToBeClickable(by));
                el.click();
                Thread.sleep(1200);
                return;
            } catch (Exception ignored) {
            }
        }

        for (int inst = 2; inst <= 12; inst++) {
            try {
                MobileElement el =
                    (MobileElement)
                        driver.findElement(
                            MobileBy.AndroidUIAutomator(
                                "new UiSelector().className(\"android.widget.ImageView\").instance("
                                    + inst
                                    + ")"));
                el.click();
                Thread.sleep(1200);
                return;
            } catch (Exception ignored) {
            }
        }

        throw new RuntimeException("Could not tap a first file in the system picker (OEM layout differs)");
    }

    /** Many pickers need Done / Open / Select after choosing an item. */
    private static void confirmPickerIfNeeded(AndroidDriver<MobileElement> driver) throws InterruptedException {
        By confirm =
            By.xpath(
                "//android.widget.Button[@text='Done' or @text='Open' or @text='OK' or @text='SELECT' or @text='Select']"
                    + " | //*[@content-desc='Done' or @content-desc='Open']");
        try {
            MobileElement btn =
                (MobileElement)
                    new WebDriverWait(driver, 8).until(ExpectedConditions.elementToBeClickable(confirm));
            btn.click();
            Thread.sleep(1000);
        } catch (Exception ignored) {
        }
    }

    private static void enterTextInFlutterField(
        AndroidDriver<MobileElement> driver,
        MobileElement inputField,
        String text
    ) throws InterruptedException {
        try {
            inputField.sendKeys(text);
            return;
        } catch (Exception ignored) {
        }
        try {
            inputField.click();
            Thread.sleep(400);
            inputField.sendKeys(text);
            return;
        } catch (Exception ignored) {
        }
        try {
            mobileType(driver, inputField, text);
            return;
        } catch (Exception ignored) {
        }
        try {
            inputField.clear();
            inputField.sendKeys(text);
            return;
        } catch (Exception ignored) {
        }
        typeLatinViaIme(driver, text);
    }

    /** Tap neutral area (not system BACK) so IME closes without popping Flutter routes. */
    private static void tapOutsideKeyboard(AndroidDriver<MobileElement> driver) throws InterruptedException {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        int y = (int) (size.height * 0.33);
        new TouchAction<>(driver).tap(PointOption.point(x, y)).perform();
        Thread.sleep(450);
    }

    private static void clickCaretElementWithTapFallback(AndroidDriver<MobileElement> driver, MobileElement caret) {
        try {
            caret.click();
        } catch (Exception ignored) {
            int cx = caret.getLocation().getX() + caret.getSize().getWidth() / 2;
            int cy = caret.getLocation().getY() + caret.getSize().getHeight() / 2;
            new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
        }
    }

    /**
     * Sends the message via the composer caret ({@code caretRight}). Tries {@link #ENTER_CARET_SEND_CANDIDATES}
     * in order (multiline / OEM differences).
     */
    private static void clickCaretSend(AndroidDriver<MobileElement> driver, WebDriverWait wait)
        throws InterruptedException {

        TimeoutException lastTimeout = null;
        for (By by : ENTER_CARET_SEND_CANDIDATES) {
            for (int staleRetry = 0; staleRetry < 2; staleRetry++) {
                try {
                    MobileElement caret =
                        (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(by));
                    clickCaretElementWithTapFallback(driver, caret);
                    return;
                } catch (TimeoutException e) {
                    lastTimeout = e;
                    break;
                } catch (StaleElementReferenceException e) {
                    Thread.sleep(200);
                }
            }
        }
        if (lastTimeout != null) {
            throw lastTimeout;
        }
        throw new TimeoutException("Caret send control not found for any locator");
    }

    /**
     * After multiline typing, the IME or numbered-list toolbar can hide {@code caretRight} until the field is
     * refocused — retry once with {@link #tapOutsideKeyboard} + tap on {@code refocusComposerBy}.
     */
    private static void clickCaretSendAfterMultiline(
        AndroidDriver<MobileElement> driver,
        int timeoutSeconds,
        By refocusComposerBy) throws InterruptedException {

        WebDriverWait first = new WebDriverWait(driver, timeoutSeconds);
        try {
            clickCaretSend(driver, first);
            return;
        } catch (TimeoutException e) {
            // Retry path: dismiss IME overlay, refocus composer, tap caret again.
        }
        try {
            tapOutsideKeyboard(driver);
        } catch (Exception ignored) {
        }
        MobileElement again =
            (MobileElement)
                new WebDriverWait(driver, 18)
                    .until(ExpectedConditions.elementToBeClickable(refocusComposerBy));
        try {
            again.click();
        } catch (Exception ignored) {
            int ax = again.getLocation().getX() + again.getSize().getWidth() / 2;
            int ay = again.getLocation().getY() + again.getSize().getHeight() / 2;
            new TouchAction<>(driver).tap(PointOption.point(ax, ay)).perform();
        }
        Thread.sleep(450);
        clickCaretSend(driver, new WebDriverWait(driver, timeoutSeconds));
    }

    /** If the app was backgrounded or the activity lost focus, bring it back before Tests 10–11. */
    private void bringAppToForeground(AndroidDriver<MobileElement> driver) throws InterruptedException {
        try {
            driver.activateApp(APP_PACKAGE);
            Thread.sleep(1200);
            System.out.println("↪️ activateApp(" + APP_PACKAGE + ") — ready for next OPC step");
        } catch (Exception e) {
            System.out.println("⚠️ activateApp: " + e.getMessage());
        }
    }

    private static void mobileType(AndroidDriver<MobileElement> driver, MobileElement el, String text) {
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", el.getId());
        args.put("text", text);
        driver.executeScript("mobile: type", args);
    }

    /**
     * Focuses the chat message field so {@link #typeLatinViaIme} reaches the composer, not the toolbar / system.
     * Waits for the conversation shell, then polls — after Test 21 / {@code activateApp}, {@code EditText} can
     * appear late or not be "clickable" until visible.
     */
    private static void tapMessageComposerForFocus(AndroidDriver<MobileElement> driver)
        throws InterruptedException {

        WebDriverWait ready = new WebDriverWait(driver, 18);
        try {
            ready.until(ExpectedConditions.visibilityOfElementLocated(CONVERSATION_PAGE_MARKER));
        } catch (TimeoutException ignored) {
        }
        Thread.sleep(350);

        MobileElement composer = pollForMessageComposer(driver, 28_000);
        try {
            composer.click();
        } catch (Exception ignored) {
            int cx = composer.getLocation().getX() + composer.getSize().getWidth() / 2;
            int cy = composer.getLocation().getY() + composer.getSize().getHeight() / 2;
            new TouchAction<>(driver).tap(PointOption.point(cx, cy)).perform();
        }
        Thread.sleep(200);
    }

    /**
     * Resolves the composer by trying several locators until any displayed {@code EditText} is found.
     */
    private static MobileElement pollForMessageComposer(AndroidDriver<MobileElement> driver, int maxWaitMs)
        throws InterruptedException {

        long deadline = System.currentTimeMillis() + maxWaitMs;
        By[] locators =
            new By[] {
                MESSAGE_COMPOSER_EDITTEXT,
                MESSAGE_COMPOSER_EDITTEXT_FALLBACK,
                By.xpath("//android.widget.EditText[@focused='true']"),
                ALL_EDITTEXTS
            };

        while (System.currentTimeMillis() < deadline) {
            for (By by : locators) {
                try {
                    List<MobileElement> list = driver.findElements(by);
                    for (int i = list.size() - 1; i >= 0; i--) {
                        MobileElement el = list.get(i);
                        try {
                            if (el.isDisplayed()) {
                                return el;
                            }
                        } catch (StaleElementReferenceException e) {
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            Thread.sleep(320);
        }
        throw new TimeoutException(
            "Message composer (EditText) not visible in time — conversation may not be ready or OEM hides the field.");
    }

    /** Multiline body for OPC Test 24 ({@code \n} between lines). */
    private static void typeMultilineIntoEditText(AndroidDriver<MobileElement> driver, MobileElement el, String text)
        throws InterruptedException {

        try {
            Map<String, Object> args = new HashMap<>();
            args.put("elementId", el.getId());
            args.put("text", text);
            driver.executeScript("mobile: type", args);
            return;
        } catch (Exception ignored) {
        }
        try {
            el.sendKeys(text);
            return;
        } catch (Exception ignored) {
        }
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                driver.pressKey(new KeyEvent(AndroidKey.ENTER));
                Thread.sleep(120);
            }
            el.sendKeys(lines[i]);
        }
    }

    /** OPC 21–24: minimal delay between key events for faster runs. */
    private static void typeLatinViaImeFast(AndroidDriver<MobileElement> driver, String text)
        throws InterruptedException {
        typeLatinViaIme(driver, text, 8);
    }

    private static void typeLatinViaIme(AndroidDriver<MobileElement> driver, String text)
        throws InterruptedException {
        typeLatinViaIme(driver, text, 45);
    }

    private static void typeLatinViaIme(AndroidDriver<MobileElement> driver, String text, int delayMsPerKey)
        throws InterruptedException {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '0' && c <= '9') {
                driver.pressKey(new KeyEvent(AndroidKey.valueOf("DIGIT_" + c)));
            } else if (Character.isLetter(c)) {
                driver.pressKey(new KeyEvent(AndroidKey.valueOf(String.valueOf(Character.toUpperCase(c)))));
            } else {
                throw new IllegalArgumentException("Unsupported char for IME typing: " + c);
            }
            if (delayMsPerKey > 0) {
                Thread.sleep(delayMsPerKey);
            }
        }
    }

    public void swipeUpSlow(AndroidDriver<MobileElement> driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.78);
        int endY = (int) (size.height * 0.32);
        swipe(driver, startX, startY, startX, endY, 900);
        pause(600);
    }

    public void swipeDownSlow(AndroidDriver<MobileElement> driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.28);
        int endY = (int) (size.height * 0.78);
        swipe(driver, startX, startY, startX, endY, 900);
        pause(600);
    }

    private static void swipe(
        AndroidDriver<MobileElement> driver,
        int x1, int y1, int x2, int y2,
        int waitMs
    ) {
        new TouchAction<>(driver)
            .press(PointOption.point(x1, y1))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitMs)))
            .moveTo(PointOption.point(x2, y2))
            .release()
            .perform();
    }

    private static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean isDisplayed(AndroidDriver<MobileElement> driver, By locator) {
        if (driver.findElements(locator).isEmpty()) {
            return false;
        }
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void scrollUntilElementDisplayed(
        AndroidDriver<MobileElement> driver,
        By locator,
        int maxPerDirection
    ) throws InterruptedException {
        if (isDisplayed(driver, locator)) {
            return;
        }
        for (int i = 0; i < maxPerDirection; i++) {
            Thread.sleep(350);
            if (isDisplayed(driver, locator)) {
                return;
            }
            swipeUpSlow(driver);
            System.out.println("🔄 Scroll (reveal below) " + (i + 1));
        }
        for (int i = 0; i < maxPerDirection; i++) {
            Thread.sleep(350);
            if (isDisplayed(driver, locator)) {
                return;
            }
            swipeDownSlow(driver);
            System.out.println("🔄 Scroll (reveal above) " + (i + 1));
        }
    }

    private void uiScrollIntoViewStartConversation(AndroidDriver<MobileElement> driver) {
        String[] uiScroll =
            new String[] {
                "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\").scrollable(true))"
                    + ".scrollIntoView(new UiSelector().descriptionContains(\"Start Conversation\"))",
                "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\"))"
                    + ".scrollIntoView(new UiSelector().descriptionContains(\"Start Conversation\"))",
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                    + ".scrollIntoView(new UiSelector().descriptionContains(\"Start Conversation\"))",
            };
        for (String expr : uiScroll) {
            try {
                driver.findElement(MobileBy.AndroidUIAutomator(expr));
                return;
            } catch (Exception ignored) {
            }
        }
    }
}
