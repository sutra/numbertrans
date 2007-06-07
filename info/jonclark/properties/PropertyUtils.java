/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package info.jonclark.properties;

import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.util.*;
import java.io.*;

/**
 * Utilities for working with the java.util.Properties class.
 * 
 * @author Jonathan
 */
public class PropertyUtils {

    private static final String IMPORT_KEYWORD = "@import ";
    private static final String VARIABLE_PREFIX = "${";
    private static final String VARIABLE_SUFFIX = "}";

    public static Properties getProperties(String path) throws FileNotFoundException, IOException,
	    PropertiesException {
	return getProperties(new File(path));
    }

    /**
         * Open a text file in the standard Java Properties format, but also
         * resolve import statements. If the import path is not an absolute
         * path, it will be assumed to be relative.
         * 
         * @param path
         *                The path to the properties file to be opened.
         * @return A Properties object representing the file with all imports
         *         resolved.
         * @throws FileNotFoundException
         *                 If the path to the base Properties file or any of its
         *                 included file cannot be found.
         * @throws IOException
         *                 If there was an error in reading the base Properties
         *                 file or any of its included files.
         * @throws PropertiesException
         *                 If a variable in the properties file is not defined.
         */
    public static Properties getProperties(File file) throws FileNotFoundException, IOException,
	    PropertiesException {
	// First, resolve imports in the properties file
	final StringBuilder tempBuilder = new StringBuilder();
	replaceImports(file, tempBuilder);

	// Now load the properties from our temp file,
	// which is in the format that Properties expects.
	final Properties prop = new Properties();
	final String tempPropertiesString = tempBuilder.toString();
	final byte[] tempByteArray = tempPropertiesString.getBytes();
	prop.load(new ByteArrayInputStream(tempByteArray));

	replaceVariables(prop);

	return prop;
    }

    /**
         * Checks to see if a Properties object contains all mandatory values
         * (so that you don't end up with any null Strings)
         * 
         * @param props
         *                The Properties object to be validated
         * @param mandatoryValues
         *                A String array with all of the keys that must be
         *                contained within the Properties object.
         * @return True if Properties contian all mandatory values False if any
         *         value is missing
         */
    public static void validateProperties(Properties props, String[] mandatoryValues)
	    throws PropertiesException {
	assert props != null : "props parameter cannot be null";
	for (String value : mandatoryValues)
	    if (!props.containsKey(value))
		throw new PropertiesException("Property not defined: " + value);
    }

    /**
         * Checks to see if a Properties object contains all mandatory values
         * (so that you don't end up with any null Strings)
         * 
         * @param props
         *                The Properties object to be validated
         * @param mandatoryValues
         *                An array of objects whose toString() values must have
         *                matching keys contained within the Properties object.
         * @return True if Properties contian all mandatory values False if any
         *         value is missing
         */
    public static <T> void validateProperties(Properties props, T[] mandatoryValues)
	    throws PropertiesException {
	for (final T element : mandatoryValues) {
	    final String value = element.toString();
	    if (!props.containsKey(value))
		throw new PropertiesException("Property not defined: " + value);
	}
    }

    /**
         * Save properties. In the future, this method will support the
         * preservation of comments.
         * 
         * @param path
         *                The target path to save the file.
         * @param prop
         *                The Properties object from which to draw the variable
         *                data.
         * @throws FileNotFoundException
         * @throws IOException
         */
    public static void saveProperties(String path, Properties prop) throws FileNotFoundException,
	    IOException {
	prop.store(new FileOutputStream(new File(path)), "");
    }

    /**
         * Replaced import statements in an input properties file with the
         * contents of the imported file. The results are written to the
         * <code>out</code> file.
         * 
         * @param in
         * @param out
         * @throws IOException
         */
    private static void replaceImports(final File inFile, final StringBuilder out)
	    throws IOException {
	final BufferedReader in = new BufferedReader(new FileReader(inFile));

	String line = null;
	while ((line = in.readLine()) != null) {
	    if (line.startsWith(IMPORT_KEYWORD)) {
		final String importedFileStr = StringUtils.removeLeadingString(line, IMPORT_KEYWORD);
		File importedFile = new File(importedFileStr);
		if (!importedFile.isAbsolute()) {
		    // resolve this relative import against the base path
		    // of the properties file from which it was included
		    importedFile = new File(inFile.getParent(), importedFileStr);
		}
		FileUtils.insertFile(importedFile, out);
	    } else {
		out.append(line + '\n');
	    }
	}
    }

    /**
         * Get a list of all property files referenced by this property files
         * via import statements. (Including the property file passed in).
         * 
         * @param file
         *                The properties file to be parsed.
         * @return A list of files imported by this property file plus the file
         *         itself.
         * @throws IOException
         *                 If there was an error in reading the base Properties
         *                 file or any of its included files.
         */
    public static ArrayList<File> listImports(File file) throws IOException {
	final ArrayList<File> list = new ArrayList<File>();
	listImportsRecursive(file, list);
	return list;
    }

    private static void listImportsRecursive(File file, ArrayList<File> list) throws IOException {
	list.add(file);

	final BufferedReader in = new BufferedReader(new FileReader(file));
	String line = null;
	while ((line = in.readLine()) != null) {
	    if (line.startsWith(IMPORT_KEYWORD)) {
		final String importedFileStr = StringUtils.removeLeadingString(line, IMPORT_KEYWORD);
		File importedFile = new File(importedFileStr);
		if (!importedFile.isAbsolute()) {
		    // resolve this relative import against the base path
		    // of the properties file from which it was included
		    importedFile = new File(file.getParent(), importedFileStr);
		}

		// recursively find imports in this file
		listImportsRecursive(importedFile, list);
	    }
	}
    }

    /**
         * Replace variables within an existing properties file. Variables of
         * the form ${X} will be converted into literals using substitutions
         * from within the properties file.
         * 
         * @param props
         *                The properties file in which all variables will be
         *                replaced with their literal values.
         * @throws PropertiesException
         */
    private static void replaceVariables(final Properties props) throws PropertiesException {
	// iterate through values until all variables have been replaced.
	Collection<Object> keys = props.keySet();

	for (Object obj : keys) {
	    final String key = (String) obj;
	    String value = props.getProperty(key);
	    while (StringUtils.hasSubstringBetween(value, VARIABLE_PREFIX, VARIABLE_SUFFIX)) {
		final String variableName = StringUtils.substringBetween(value, VARIABLE_PREFIX,
			VARIABLE_SUFFIX);
		final String placeholder = VARIABLE_PREFIX + variableName + VARIABLE_SUFFIX;
		final String replacement = props.getProperty(variableName);
		if (replacement == null)
		    throw new PropertiesException("Property variable not defined: " + variableName);
		value = StringUtils.replaceFast(value, placeholder, replacement);
	    }
	    props.setProperty(key, value);
	}
    }

    /**
         * @param props
         * @param namespace
         * @param rightMostKey
         * @param desiredValue
         * @return null if the property was not found
         * @throws PropertiesException
         *                 If more than one match is found
         */
    public static String getPropertyWithValue(Properties props, String namespace,
	    String rightMostKey, String desiredValue) throws PropertiesException {

	String matchingKey = null;
	for (final Object p : props.keySet()) {
	    String propName = (String) p;
	    if (propName.startsWith(namespace) && propName.endsWith(rightMostKey)) {
		String value = props.getProperty(propName);
		if (value.equals(desiredValue)) {
		    if (matchingKey != null) {
			throw new PropertiesException("Non-unique match for namespace " + namespace
				+ " while searching for key " + rightMostKey + " with value "
				+ value);
		    } else {
			matchingKey = value;
		    }
		}
	    }
	}

	return matchingKey;
    }
    
    public static String[] getPropertiesInNamespace(Properties props, String namespace) {
	ArrayList<String> list = new ArrayList<String>();
	
	for (final Object p : props.keySet()) {
	    String propName = (String) p;
	    if (propName.startsWith(namespace))
		list.add(propName);
	}
	
	return list.toArray(new String[list.size()]);
    }
}
