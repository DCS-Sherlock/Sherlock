package uk.ac.warwick.dcs.sherlock.module.web.configuration;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Additional config settings for the thymeleaf template engine
 */
@Configuration
public class ThymeLeafConfig {

	/**
	 * Enables layouts by adding an additional dialect to the template engine
	 *
	 * @return the layout dialect
	 */
	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}
}