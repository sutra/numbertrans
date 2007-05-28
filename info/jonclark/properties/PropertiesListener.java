/*
 * Created on Nov 30, 2006
 */
package info.jonclark.properties;

public interface PropertiesListener {
    /**
         * Fired for each property that changed.
         * 
         * @param props
         *                The SmartProperties object that changed.
         * @param name
         *                The name (key) of the property that changed.
         */
    public void propertyChanged(SmartProperties props, String name);

    /**
         * Fired for each time the properties file is changed. Implementors that
         * need to regenerate regardless of which property changed will find
         * this method useful.
         * 
         * @param props
         *                The SmartProperties object that changed.
         * @param name
         *                The name (key) of the property that changed.
         */
    public void propertyFileChanged(SmartProperties props);
}
