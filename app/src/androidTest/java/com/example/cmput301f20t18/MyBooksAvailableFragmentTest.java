package com.example.cmput301f20t18;

public class MyBooksAvailableFragmentTest {
//    private Solo solo;
//
//    private static final String USERNAME = "OtherBot";
//    private static final String EMAIL = "OtherBot@OtherBot.botnet";
//    private static final String PASSWORD = "BotPass";
//
//    @Rule
//    public ActivityTestRule<Login> rule =
//            new ActivityTestRule<>(Login.class, true, true);
//    @Before
//    public void setUp() throws Exception{
//        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
//        RobotiumLoginManager.loginOwner(solo);
//        RobotiumUserBookManager.add(solo);
//        solo.clickOnView(solo.getView(R.id.tab_mybooks));
//    }
//    @After
//    public void deleteBooks(){
//        RobotiumUserBookManager.deleteAll(solo);
//    }
//    @Test
//    public void start(){
//        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
//    }
//    @Test
//    public void bookAdded(){
//        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_TITLE,
//                1, 2000));
//        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_AUTHOR,
//                1, 2000));
//        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_YEAR,
//                1, 2000));
//        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_ISBN,
//                1, 2000));
//        assertTrue(solo.waitForText("No requests", 1, 2000));
//    }
//    @Test
//    public void bookRequested(){
//        RobotiumLoginManager.signOut(solo);
//        RobotiumLoginManager.loginBorrower(solo);
//        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
//        solo.clickOnView(solo.getView(R.id.tab_search));
//        RobotiumSearchManager.searchUser(solo, RobotiumUserBookManager.DEFAULT_BOOK_TITLE,
//                RobotiumUserBookManager.DEFAULT_BOOK_TITLE);
//        RobotiumLoginManager.signOut(solo);
//
//        RobotiumLoginManager.loginOwner(solo);
//        solo.clickOnView(solo.getView(R.id.tab_mybooks));
//        assertTrue(solo.waitForText("View requests", 1, 2000));
//    }
//    @Test
//    public void declineRequest(){
//        RobotiumLoginManager.signOut(solo);
//        RobotiumLoginManager.loginBorrower(solo);
//        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
//        solo.clickOnView(solo.getView(R.id.tab_search));
//        RobotiumTransactionManager.requestBook(solo, RobotiumUserBookManager.DEFAULT_BOOK_TITLE,
//                RobotiumUserBookManager.DEFAULT_BOOK_TITLE);
//        RobotiumLoginManager.signOut(solo);
//
//        RobotiumLoginManager.loginOwner(solo);
//        solo.clickOnView(solo.getView(R.id.tab_mybooks));
//        solo.clickOnButton("View requests");
//        assertTrue(solo.waitForText(USERNAME, 1, 2000));
//
//        solo.clickOnView(solo.getView(R.id.button_delete_request));
//        solo.goBack();
//        assertTrue(solo.waitForText("No requests", 1, 2000));
//    }
//    @Test
//    public void acceptRequestCancel(){
//        RobotiumLoginManager.signOut(solo);
//        RobotiumLoginManager.loginBorrower(solo);
//        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
//        solo.clickOnView(solo.getView(R.id.tab_search));
//        RobotiumTransactionManager.requestBook(solo, RobotiumUserBookManager.DEFAULT_BOOK_TITLE,
//                RobotiumUserBookManager.DEFAULT_BOOK_TITLE);
//        RobotiumLoginManager.signOut(solo);
//
//        RobotiumLoginManager.loginOwner(solo);
//        solo.clickOnView(solo.getView(R.id.tab_mybooks));
//        solo.clickOnButton("View requests");
//        assertTrue(solo.waitForText(USERNAME, 1, 2000));
//        solo.clickOnView(solo.getView(R.id.button_accept_request));
//
//        solo.assertCurrentActivity("Wrong Activity - NOT CHOOSELOCATIONACTIVITY",
//                ChooseLocationActivity.class);
//        solo.clickOnView(solo.getView(R.id.button_back));
//        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
//    }
//    @Test
//    public void acceptRequestAccept(){
//        RobotiumLoginManager.signOut(solo);
//        RobotiumLoginManager.loginBorrower(solo);
//        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
//        solo.clickOnView(solo.getView(R.id.tab_search));
//        RobotiumTransactionManager.requestBook(solo, RobotiumUserBookManager.DEFAULT_BOOK_TITLE,
//                RobotiumUserBookManager.DEFAULT_BOOK_TITLE);
//        RobotiumLoginManager.signOut(solo);
//
//        RobotiumLoginManager.loginOwner(solo);
//        solo.clickOnView(solo.getView(R.id.tab_mybooks));
//        solo.clickOnButton("View requests");
//        solo.assertCurrentActivity("Wrong Activity - NOT VIEWREQUESTSACTIVITY",
//                ViewRequestsActivity.class);
//        assertTrue(solo.waitForText(USERNAME, 1, 2000));
//        solo.clickOnView(solo.getView(R.id.button_accept_request));
//
//        solo.assertCurrentActivity("Wrong Activity - NOT CHOOSELOCATIONACTIVITY",
//                ChooseLocationActivity.class);
//        solo.clickOnButton("Add New Location");
//
//        solo.assertCurrentActivity("Wrong Activity - NOT SELECTLOCATIONACTIVITY",
//                SelectLocationActivity.class);
//        solo.clickOnScreen(100, 100);
//        solo.sleep(1000);
//        solo.clickOnView(solo.getView(R.id.confirm_location_selected_button));
//
//        solo.assertCurrentActivity("Wrong Activity - NOT CHOOSELOCATIONACTIVITY",
//                ChooseLocationActivity.class);
//        solo.clickOnButton("Select");
//
//        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
//        assertFalse(solo.waitForText(USERNAME, 1, 2000));
//    }
}
