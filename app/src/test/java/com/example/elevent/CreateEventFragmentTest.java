package com.example.elevent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateEventFragmentTest {

    @Mock
    CreateEventFragment.CreateEventListener listener;

    private CreateEventFragment createEventFragment;

    @Before
    public void setUp() {
        createEventFragment = new CreateEventFragment();
        createEventFragment.setListetListener(listener); // Assuming you can set the listener through a method
    }

    @Test
    public void eventCreationWithValidDataCallsListener() {
        // Assuming there's a method in CreateEventFragment to validate and create the event that we can call directly
        Event event = new Event("Test Event", null, null, 0, "2024-01-01", "10:00", "Description", "Location", null, null);
        createEventFragment.createEvent(event);

        // Verify that the listener's onPositiveClick is called with the correct event
        verify(listener, times(1)).onPositiveClick(event);
    }
}
