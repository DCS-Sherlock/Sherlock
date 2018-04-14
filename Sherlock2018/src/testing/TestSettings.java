package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sherlock.model.analysis.FileTypes;
import sherlock.model.analysis.Settings;

class TestSettings {
	Settings s = new Settings();
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testOriginalProfileIndex() {
		assertTrue(FileTypes.ORI.getValue() == 0);
	}
	
	@Test
	void testNoWSProfileIndex() {
		assertTrue(FileTypes.NWS.getValue() == 1);
	}
	
	@Test
	void testNoCommentsProfileIndex() {
		assertTrue(FileTypes.NOC.getValue() == 2);
	}
	
	@Test
	void testNoCommentsWSProfileIndex() {
		assertTrue(FileTypes.NCW.getValue() == 3);
	}
	
	@Test
	void testCommentsProfileIndex() {
		assertTrue(FileTypes.COM.getValue() == 4);
	}
	
	@Test
	void testTokenisedProfileIndex() {
		assertTrue(FileTypes.TOK.getValue() == 5);
	}
	
	@Test
	void testWSPatternProfileIndex() {
		assertTrue(FileTypes.WSP.getValue() == 6);
	}
}
