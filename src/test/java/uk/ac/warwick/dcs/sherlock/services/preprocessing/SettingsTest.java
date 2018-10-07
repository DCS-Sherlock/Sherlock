package uk.ac.warwick.dcs.sherlock.services.preprocessing;
import static junit.framework.Assert.assertEquals;

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
		assertEquals(0, FileTypes.ORI.getValue());
	}
	
	@Test
	void testNoWSProfileIndex() {
		assertEquals(1, FileTypes.NWS.getValue());
	}
	
	@Test
	void testNoCommentsProfileIndex() {
		assertEquals(3, FileTypes.NOC.getValue());
	}
	
	@Test
	void testNoCommentsWSProfileIndex() {
		assertEquals(2, FileTypes.NCW.getValue());
	}
	
	@Test
	void testCommentsProfileIndex() {
		assertEquals(4, FileTypes.COM.getValue());
	}
	
	@Test
	void testTokenisedProfileIndex() {
		assertEquals(5, FileTypes.TOK.getValue());
	}
	
	@Test
	void testWSPatternProfileIndex() {
		assertEquals(6, FileTypes.WSP.getValue());
	}
}
