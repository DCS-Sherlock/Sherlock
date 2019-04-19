package uk.ac.warwick.dcs.sherlock.api;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFileHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Static helper functions and access to various engine functions which do not fit elsewhere in the api
 */
public class SherlockHelper {

	/**
	 * Implementation of ISourceFileHelper in use by SherlockEngine, used to get {@link ISourceFile} instances from  an ID
	 */
	private static ISourceFileHelper sourceFileHelper;

	/**
	 * Implementation of ICodeBlockGroup interface in use by SherlockEngine
	 */
	private static Class<? extends ICodeBlockGroup> codeBlockGroupClass;

	/**
	 * Builds the reference string of a field by prepending the declaring class name
	 * @param field field to build reference for
	 * @return string reference for field
	 */
	public static String buildFieldReference(Field field) {
		return field.getDeclaringClass().getName() + ":" + field.getName();
	}

	/**
	 * Constructs a new instance of the current ICodeBlockGroup implementation and returns it
	 * @return New instance
	 * @throws IllegalAccessException the implementation is incorrect
	 * @throws InstantiationException issue creating new instance
	 * @throws NoSuchMethodException the implementation is incorrect
	 * @throws InvocationTargetException inuse code block group class is null, ensure SherlockEngine is running correctly
	 */
	public static ICodeBlockGroup getInstanceOfCodeBlockGroup() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		return codeBlockGroupClass.getConstructor().newInstance();
	}

	/**
	 * Fetches the instance of ISourceFile for the corresponding unique id
	 * @param persistentId unique id to fetch
	 * @return instance of ISourceFile with passed id
	 */
	public static ISourceFile getSourceFile(long persistentId) {
		return sourceFileHelper.getSourceFile(persistentId);
	}
}
