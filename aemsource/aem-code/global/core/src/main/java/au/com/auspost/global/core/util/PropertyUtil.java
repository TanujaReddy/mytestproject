package au.com.auspost.global.core.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;

public class PropertyUtil {

	private PropertyUtil(){
		//Don't allow instantiation.
	}

	/**
	 * Returns a list of String values in case the 
	 * @param node
	 * @param propertyName
	 * @return String list of the property name on the node.
	 * @throws RepositoryException
	 */
	public static List<String> getPropertyValueAsStringList(Node node, String propertyName) throws RepositoryException{
		List<String> propValues = new ArrayList<String>();
		if(null!=node && node.hasProperty(propertyName)){
			Property prop = node.getProperty(propertyName);
			for(Value value: prop.isMultiple()?Arrays.asList(prop.getValues()):Collections.singletonList(prop.getValue())){
				propValues.add(value.getString());
			}
		}
		return propValues;
	}

	/**
	 * In case you're confident that the property value will always be singular then this method can be used as well.
	 * @param node
	 * @param propertyName
	 * @return String value of the property
	 * @throws RepositoryException
	 */
	public static String returnSinglePropertyValue (Node node, String propertyName) throws RepositoryException{
		String propValue = StringUtils.EMPTY;
		if(StringUtils.isNotBlank(propertyName) && null!=node && node.hasProperty(propertyName) && !node.getProperty(propertyName).isMultiple() 
				&& node.getProperty(propertyName).getType()==PropertyType.STRING){
			propValue =node.getProperty(propertyName).getString();
		}
		return propValue;
	}

	/**
	 * In case you're confident that the property value(Binary) will always be singular then this method can be used as well.
	 * @param node
	 * @param propertyName
	 * @return String value of the property
	 * @throws RepositoryException
	 */
	public static String returnSingleBinaryPropertyValue (Node node, String propertyName) throws RepositoryException{
		String propValue = StringUtils.EMPTY;
		if(null!=node && node.hasProperty(propertyName) && !node.getProperty(propertyName).isMultiple()
				&& node.getProperty(propertyName).getType()==PropertyType.BINARY){
			propValue =node.getProperty(propertyName).getString();
		}
		return propValue;
	}

	/**
	 * Returns the date from the node. If things aren't relevant then return null.
	 * @param node
	 * @param propertyName
	 * @return Calendar instance or null.
	 * @throws RepositoryException
	 */
	public static Calendar getDateFromNode(Node node , String propertyName) throws RepositoryException{
		if(null!=node && node.hasProperty(propertyName) && node.getProperty(propertyName).getType()==PropertyType.DATE){
			return node.getProperty(propertyName).getDate();
		}
		return null;
	}

	/**
	 * This method can be used to fetch the first available value from 
	 * @param node
	 * @param propertyNames
	 * @return first available value in the properties
	 * @throws RepositoryException
	 */

	public static String getPropValueFromFirstProperty (Node node, String ...propertyNames) throws RepositoryException{
		String propValue = StringUtils.EMPTY;
		if(null!=node){
			for(String property: propertyNames){
				if(node.hasProperty(property) && !node.getProperty(property).isMultiple() && node.getProperty(property).getType()==PropertyType.STRING){
					propValue = node.getProperty(property).getString();
					break;
				}
			}
		}
		return propValue;
	}

	/**
	 * Get the values of any node as String array using the above list method first.
	 * @param node
	 * @param propName
	 * @return
	 * @throws RepositoryException
	 */
	public static String [] getValuesAsStringArray(Node node, String propName) throws RepositoryException{
		List<String> list = getPropertyValueAsStringList(node,propName);
		String[] array = new String[list.size()];
		array = list.toArray(array);
		return array;
	}
}
