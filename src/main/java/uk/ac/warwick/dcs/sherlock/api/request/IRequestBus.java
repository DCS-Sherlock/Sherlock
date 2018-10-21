package uk.ac.warwick.dcs.sherlock.api.request;

import javax.validation.constraints.NotNull;

public interface IRequestBus {

	boolean post(@NotNull IRequestReference reference, @NotNull Object source, Object payload);

}
