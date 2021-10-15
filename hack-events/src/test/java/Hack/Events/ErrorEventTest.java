package Hack.Events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorEventTest {

	public static final String TEST_MESSAGE = "Test Message";
	private ErrorEvent subject;

	@BeforeEach
	void setUp() {
		subject = new ErrorEvent(new Object(), TEST_MESSAGE);
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getErrorMessage() {
		assertEquals(TEST_MESSAGE, subject.getErrorMessage());
	}

}