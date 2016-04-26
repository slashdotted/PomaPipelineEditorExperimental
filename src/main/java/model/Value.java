package model;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Value<T> {

    private boolean isMandatory;
    private boolean defaultValue;
    private String name;
    private Property<T> valueProperty;


    T value;

    public Value(String name, T val, boolean defaultValue, boolean isMandatory) {
        this.name = name;
        this.value = val;
        this.valueProperty = initializeValueProperty();


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

    private Property<T> initializeValueProperty() {

        return new Property<T>() {
            @Override
            public void bind(ObservableValue<? extends T> observable) {

            }

            @Override
            public void unbind() {

            }

            @Override
            public boolean isBound() {
                return false;
            }

            @Override
            public void bindBidirectional(Property<T> other) {

            }

            @Override
            public void unbindBidirectional(Property<T> other) {

            }

            @Override
            public Object getBean() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void addListener(ChangeListener<? super T> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super T> listener) {

            }

            @Override
            public T getValue() {
                return null;
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }

            @Override
            public void setValue(T value) {

            }
        };

    }

}
