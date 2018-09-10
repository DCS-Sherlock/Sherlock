package uk.ac.warwick.dcs.sherlock.services.preprocessing;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.warwick.dcs.sherlock.FileTypes;
import uk.ac.warwick.dcs.sherlock.Settings;

class SettingsTest {
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
		assertTrue(FileTypes.NOC.getValue() == 3);
	}
	
	@Test
	void testNoCommentsWSProfileIndex() {
		assertTrue(FileTypes.NCW.getValue() == 2);
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
