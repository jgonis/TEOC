/**
 * 
 */
package textCompare;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.*;

/**
 * @author jeffg_000
 *
 */
public class TextComparerTest {

	private BufferedReader m_br1;
	private BufferedReader m_br2;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		String input1 = "Hello World!";
		String input2 = "Hello world!";
		m_br1 = new BufferedReader(new StringReader(input1));
		m_br2 = new BufferedReader(new StringReader(input2));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link textCompare.TextComparer#compareText(java.io.BufferedReader, java.io.BufferedReader)}.
	 */
	@Test
	public final void testCompareTextReturnsFalseWithNullInput() {
		try {
			assertFalse(TextComparer.compareText(null, null));
			assertFalse(TextComparer.compareText(m_br1, null));
			assertFalse(TextComparer.compareText(null, m_br2));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
