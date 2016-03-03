package model;

/**
 * Created by Marco on 03/03/2016.
 */
public class Value <T>{

    String name;
    T value;

    Value (String name,T val){

        this.name=name;
        this.value=val;
    }

    String getType(){

       return String.valueOf(value.getClass());
    }

}
