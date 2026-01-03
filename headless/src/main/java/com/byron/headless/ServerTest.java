package com.byron.headless;

import com.badlogic.gdx.Application;
import org.junit.After;
import org.junit.Before;

public class ServerTest {

    protected Application app;

    @Before
    public void setUp() {
        app = EmptyHeadlessLauncher.createApplication();
    }

    @After
    public void tearDown() {
        app.exit(); // Dispose of the headless app
    }

}
