package org.alaeri.cityvibe.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.alaeri.cityvibe.model.test", appContext.getPackageName());
    }


    @Test
    public void testRefreshPopular(){
        DataManager dataManager = new DataManagerImpl();
        RefreshResults refreshResults = dataManager.refreshPopular().test().awaitDone(3, TimeUnit.SECONDS).values().get(0);
        Log.d("DataManagerImpl","refreshResults: "+refreshResults.getSongs().size() + refreshResults);
        RefreshResults refreshResults2 = dataManager.refreshPopular().test().awaitDone(3, TimeUnit.SECONDS).values().get(0);
        Log.d("DataManagerImpl","refreshResults2: "+refreshResults2.getSongs().size() + refreshResults);


    }
}
