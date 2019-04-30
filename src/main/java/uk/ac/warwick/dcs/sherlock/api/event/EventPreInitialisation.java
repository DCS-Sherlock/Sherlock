package uk.ac.warwick.dcs.sherlock.api.event;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IAdvancedPreProcessorGroup;

/**
 * Event for the pre initialisation step of startup.
 * <br><br>
 * Used by modules this should be when languages and  {@link IAdvancedPreProcessorGroup} are registered
 */
public class EventPreInitialisation implements IEventModule {

}
