package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Marco on 03/03/2016.
 */
public class Value<T> {

    private boolean mandatory = false;
    private final T defaultValue;
    private boolean valid = true;
    private String name;


    T value;

    public Value(String name, T val, T defaultValue, boolean mandatory) {
        this.name = name;
        this.value = val;//(val.toString().equals("0")) ? val : updateValue("") ;
        this.defaultValue = defaultValue;
        this.mandatory = mandatory;
        this.valid = (defaultValue!=null) ? true : false;
        //initialize();
    }

//    private void initialize() {
//        System.out.println("Value: " + name + ", Type: " + getType() + ", Default: " + defaultValue);
//        System.out.println("Before initialize: " + value.toString());
//        if(value.toString().equals("0")){
//            System.out.println("\tCalling update!: "+ updateValue(""));
//
//        }
//        System.out.println("After initialize: " + value.toString());
//        System.out.println();
//    }

//    public Value(String name, T val) {
//        this.name = name;
//        this.value = val;
//        this.defaultValue = null;
//        //initialize();
//
//    }

    public Value(Value value) {
        this.name = value.getName();
        this.mandatory = value.isMandatory();
        Object val = null;
        Constructor ctor = null;
        try {
            //System.out.println(value.getType()); //TODO remove this
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
        //initialize();
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
        System.out.println("Updating value of type: " + getType() + " with " + newValue);
        boolean success = false;
        if (value instanceof String) {
            value = (T) newValue;
            success = true;
            System.out.println("setting with string");
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
            }else {
                success = false;
            }
        }

        if(!success && defaultValue!=null){
            value = defaultValue;

        }

        System.out.println("New value: " + value.toString() + ", success? " + success);
        valid = success;
        return success;
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
