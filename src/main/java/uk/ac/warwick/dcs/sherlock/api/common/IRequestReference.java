package uk.ac.warwick.dcs.sherlock.api.common;

public interface IRequestReference {

	Class<?> getHandler();

	Class<?> getReturnType();

}
