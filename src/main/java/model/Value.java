package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Value Class
 */
public class Value<T> {

    private boolean mandatory = false;
    private final T defaultValue;
    private boolean valid = true;
    private String name;
    private String description;

    T value;

    public Value(String name, String description, T val, T defaultValue, boolean mandatory) {
        this.name = name;
        this.value = val;
        this.defaultValue = defaultValue;
        this.mandatory = mandatory;
        this.valid = (defaultValue != null);
        this.description = description;

    }

    public Value(Value value) {
        this.name = value.getName();
        this.mandatory = value.isMandatory();
        Object val = null;
        Constructor ctor = null;
        try {
            ctor = Class.forName(value.getClassName()).getConstructor(String.class);
            val = ctor.newInstance("0");

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        this.value = (T) val;
        this.defaultValue = (T) value.getDefaultValue();
        this.valid = value.isValid();
        this.description = value.description;

    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
    
    public void validate() {
        if (value instanceof String) {
            if (mandatory && ((String) value).isEmpty()) {
                setValid(false);
                return;
            }
        }
        setValid(true);
    }

    public String updateValue(String newValue) {
        if (mandatory && newValue.isEmpty()) {
                return ""+value;
        }
        if (value instanceof String) {
            value = (T) newValue;
            return newValue;
        } else if (value instanceof Long) {
            try {
                value = (T) Long.valueOf(newValue);
                return newValue;
            } catch(NumberFormatException e) {
                return ""+value;
            }
        } else if (value instanceof Double) {
            try {
                value = (T) Double.valueOf(newValue);
                return newValue;
            } catch(NumberFormatException e) {
                return ""+value;
            }
        } else if (value instanceof Integer) {
            try {
                value = (T) Integer.valueOf(newValue);
                return newValue;
            } catch(NumberFormatException e) {
                return ""+value;
            }
        } else if (value instanceof Boolean) {
            if (newValue.equalsIgnoreCase("true") || newValue.equalsIgnoreCase("false")) {
                    value = (T) Boolean.valueOf(newValue);
                    return newValue;
                } else if (newValue.equals("0")) {
                    value = (T) Boolean.valueOf("false");
                    return newValue;
                } else if (newValue.equals("1")) {
                    value = (T) Boolean.valueOf("true");
                    return newValue;
                } else {
                    return value.toString();
                }
        } else {
            return newValue;
        }
    }

    @Override
    public String toString() {
        return "Value{"
                + "name='" + name + '\''
                + " description='" + description + '\''
                + " type= '" + getType() + '\''
                + " value= '" + getValue() + '\''
                + " defaultValue = '" + getDefaultValue() + '\''
                + " mandatory = '" + isMandatory() + '\''
                + " valid = '" + isValid() + '\''
                + '}';
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getType() {
        return value.getClass().getSimpleName();
    }

    public String getClassName() {
        return value.getClass().getName();
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public boolean isValid() {
        validate();
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
