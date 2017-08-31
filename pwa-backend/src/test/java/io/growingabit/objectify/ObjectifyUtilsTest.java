package io.growingabit.objectify;

import org.junit.Test;

import io.growingabit.app.model.BaseModel;

public class ObjectifyUtilsTest {

    @Test
    public void checkRequiredFieldsTest() throws IllegalArgumentException, IllegalAccessException {
        BaseModel baseModel = new BaseModel() {};
        ObjectifyUtils.checkRequiredFields(baseModel);
    }

}
