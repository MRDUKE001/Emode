package com.wind.emode.utils;

public class Constants {
    public static final String ACTION_EMODE = "com.wind.intent.action.EMODE";
    public static final String ACTION_EMODE_PARSE = "com.wind.intent.action.EMODE_PARSE";  
    public static final String ACTION_EMODE_MENU = "com.wind.intent.action.EMODE_MENU";
    public static final String ACTION_EMODE_RESTORE_WIFI = "com.wind.intent.action.EMODE_RESTORE_WIFI";
    public static final String ACTION_EMODE_RESTORE_BT = "com.wind.intent.action.EMODE_RESTORE_BT";
    public static final String ACTION_EMODE_RESTORE_GPS = "com.wind.intent.action.EMODE_RESTORE_GPS";
    public static final String ACTION_EMODE_OPEN_WIFI_BT_GPS = "com.wind.intent.action.EMODE_OPEN_WIFI_BT_GPS";
    public static final String ACTION_EMODE_CLOSE_WIFI_BT_GPS = "com.wind.intent.action.EMODE_CLOSE_WIFI_BT_GPS";   
    public static final String ACTION_EMODE_STOP = "com.wind.intent.action.EMODE_STOP";
    public static final String ACTION_EMODE_INIT_RESULT_FILE = "com.wind.intent.action.EMODE_INIT_RESULT_FILE";
    public static final String ACTION_EMODE_UPDATE_RESULT_FILE = "com.wind.intent.action.EMODE_UPDATE_RESULT_FILE";
    
    public static final String EXTRA_EMODE_CODE = "com.wind.intent.extra.EMODE_CODE";
    public static final String EXTRA_GPS_ORIG_STATE = "com.wind.intent.extra.GPS_ORIG_STATE";
    
    public static final String PREF_NAME_TEST_RESULTS = "emode_results";
    public static final String PREF_KEY_TEST_RESULTS_BOARD = "emode_board_results";
    public static final String PREF_KEY_TEST_RESULTS_PHONE = "emode_phone_results";
    public static final String PREF_KEY_TEST_RESULT_FILE_INITIALIZED = "test_result_file_initialized";
    
    public static final int TESTCASE_TYPE_PHONE = 0x01;
    public static final int TESTCASE_TYPE_BOARD = 0x02;
    public static final int TESTCASE_TYPE_OTHER = 0x04;
    public static final int TESTCASE_TYPE_CUSTOM = 0x08;
    
    public static final String TEST_RESULT_FILE_NAME = "SMMI_TestResult.txt";
    
    public static final int NOT_TEST = -1;
    public static final int TEST_PASS = 0;
    public static final int TEST_FAIL = 1;
    
    public static final int BATTERY_TEST_ERROR = 1601;
    public static final int DISPLAY_TEST_ERROR = 1602;
    public static final int VIBRATOR_AND_BACKLIGHT_TEST_ERROR = 1603;
    public static final int LED_TEST_ERROR = 1604;
    public static final int SD_CARD_AND_RING_TEST_ERROR = 1605;        
    public static final int AUDIO_LOOP_TEST_ERROR = 1606;
    public static final int MEMORY_TEST_ERROR = 1607;        
    public static final int KEY_TEST_ERROR = 1608;
    public static final int BT_TEST_ERROR = 1609;
    public static final int GPS_TEST_ERROR = 1610;
    public static final int TOUCH_SCREEN_TEST_ERROR = 1611;
    public static final int FM_TEST_ERROR = 1612;
    public static final int WIFI_TEST_ERROR = 1613;
    public static final int FLASH_TEST_ERROR = 1614;
    public static final int FRONT_CAMERA_TEST_ERROR = 1615;
    public static final int BACK_CAMERA_TEST_ERROR = 1616;
    public static final int GSENSOR_TEST_ERROR = 1617;
    public static final int MSENSOR_TEST_ERROR = 1618;
    public static final int LIGHT_AND_PROXIMITY_TEST_ERROR = 1619;

}
