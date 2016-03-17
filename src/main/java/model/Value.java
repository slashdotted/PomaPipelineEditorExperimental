package model;

/**
 * Created by Marco on 03/03/2016.
 */
public class Value <T>{

    boolean hasDefault;
    String name;

    T value;
    public Value (String name,T val, boolean hasDefault){
        this.name=name;
        this.value=val;
        this.hasDefault = hasDefault;
    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public boolean isHasDefault() {
        return hasDefault;
    }

    public void setValue(T value){
        this.value = value;
    }

    public String getType(){
       return String.valueOf(value.getClass());
    }


}
