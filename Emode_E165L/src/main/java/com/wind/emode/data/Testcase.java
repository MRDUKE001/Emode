package com.wind.emode.data;

public class Testcase implements Cloneable{
    public String code;
    public int type;
    public String enName;
    public String cnName;
    public String activity;
    
    @Override
    public Testcase clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        Testcase tc = new Testcase();
        tc.code = code;
        tc.type = type;
        tc.enName = enName;
        tc.cnName = cnName;
        tc.activity = activity;
        return tc;
    }
    

}
