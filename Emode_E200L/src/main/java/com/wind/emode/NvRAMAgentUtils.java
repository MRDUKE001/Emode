package com.wind.emode;
import android.os.ServiceManager;
import com.mediatek.hdmi.NvRAMAgent;
import android.os.IBinder;

public class NvRAMAgentUtils {
    /*
     * vendor/mediatek/proprietary/custom/k35v1_64_op01q_pre/cgen/inc/Custom_NvRam_LID.h  ----AP_CFG_REEB_PRODUCT_INFO_LID
     * vendor/mediatek/proprietary/custom/common/cgen/inc/CFG_file_lid.h  ----AP_CFG_CUSTOM_BEGIN_LID
     */
    private static int AP_CFG_REEB_PRODUCT_INFO_LID = 59;
    public static byte[] readFile(){
        try {
            IBinder binder = ServiceManager.getService("NvRAMAgent");
            NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
            return agent.readFile(AP_CFG_REEB_PRODUCT_INFO_LID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int writeFile(byte[] buff){
        try {
            IBinder binder = ServiceManager.getService("NvRAMAgent");
            NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
            return agent.writeFile(AP_CFG_REEB_PRODUCT_INFO_LID, buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
