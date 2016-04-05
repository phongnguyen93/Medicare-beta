package com.namlongsolutions.medicare.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Phong Nguyen on 14-Mar-16.
 */
public class TestUtils extends AndroidTestCase {

    //create sample doctor data
    static ContentValues createDummyDoctorData(){
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_ID,"bsphong");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_NAME,"Nguyen Thanh Phong");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_EMAIL, "phongnguyen93@gmail.com");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_ADDRESS,"846 Vo Van Kiet, p5, q5, tpHCM");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_PHONE,"01226129774");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_IMAGE,"empty");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_RATING,5);
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_SPECIALITY,"nhi khoa");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_WORKDAYS,"1,2,3,4,5");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_WORKTIME,"12,19");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_LICENSE,"CCHN");
        testValues.put(DataContract.DoctorEntry.COLUMN_DR_LOCATION,"10.3243345,10.4674434");
        testValues.put(DataContract.DoctorEntry.COLUMN_IS_FAV,0);
        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
