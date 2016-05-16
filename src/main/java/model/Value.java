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


    T value;

    public Value(String name, T val, T defaultValue, boolean mandatory) {
        this.name = name;
        this.value = val;
        this.defaultValue = defaultValue;
        this.mandatory = mandatory;
        this.valid = (defaultValue != null);

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

    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean updateValue(String newValue) {

        boolean success = false;
        if (value instanceof String) {
            value = (T) newValue;
            success = true;
        }
        try {
            if (!success && value instanceof Long) {
                value = (T) Long.valueOf(newValue);
                success = true;
            }
            if (!success && value instanceof Double) {
                value = (T) Double.valueOf(newValue);
                success = true;
            }
            if (!success && value instanceof Integer) {
                value = (T) Integer.valueOf(newValue);
                success = true;
            }
        } catch (NumberFormatException e) {

            success = false;
        }
        if (!success && value instanceof Boolean) {
            if (newValue.equalsIgnoreCase("true") || newValue.equalsIgnoreCase("false")) {
                value = (T) Boolean.valueOf(newValue);
                success = true;
            } else if (newValue.equals("0")) {
                value = (T) Boolean.valueOf("false");
                success = true;
            } else if (newValue.equals("1")) {
                value = (T) Boolean.valueOf("true");
                success = true;
            } else {
                success = false;
            }
        }

        if (!success && defaultValue != null) {
            value = defaultValue;

        }

        valid = success;
        return success;
    }


    @Override
    public String toString() {
        return "Value{" +
                "name='" + name + '\'' +
                " type= '" + getType() + '\''+
                " value= '" + getValue() + '\''+
                " defaultValue = '" + getDefaultValue() + '\''+
                " mandatory = '" + isMandatory() + '\''+
                " valid = '" + isValid() + '\''+
                '}';
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
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
