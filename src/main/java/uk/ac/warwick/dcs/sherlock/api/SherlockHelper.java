package uk.ac.warwick.dcs.sherlock.api;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFileHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class SherlockHelper {

	private static ISourceFileHelper sourceFileHelper;

	private static Class<? extends ICodeBlockGroup> codeBlockGroupClass;

	public static String buildFieldReference(Field field) {
		return field.getDeclaringClass().getName() + ":" + field.getName();
	}

	public static ICodeBlockGroup getInstanceOfCodeBlockGroup() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		return codeBlockGroupClass.getConstructor().newInstance();
	}

	public static ISourceFile getSourceFile(long persistentId) {
		return sourceFileHelper.getSourceFile(persistentId);
	}
}
