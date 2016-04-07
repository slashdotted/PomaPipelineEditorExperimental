package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Marco on 03/03/2016.
 */
public class Value<T> {

    private boolean isMandatory;
    private boolean defaultValue;
    private String name;


    T value;

    public Value(String name, T val, boolean defaultValue, boolean isMandatory) {
        this.name = name;
        this.value = val;
        this.defaultValue = defaultValue;
        this.isMandatory = isMandatory;
    }

    public Value(String name, T val) {
        this.name = name;
        this.value = val;
        this.defaultValue = false;
    }

    public Value(Value value) {
        this.name = value.getName();
        this.defaultValue = value.isDefaultValue();
        this.isMandatory = value.isMandatory();
        Object val = null;
        Constructor ctor = null;
        try {
            //System.out.println(value.getType()); //TODO remove this
            ctor = Class.forName(value.getType()).getConstructor(String.class);
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

    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getType() {
        return value.getClass().getName();
    }

    public boolean isMandatory() {
        return isMandatory;
    }

}
