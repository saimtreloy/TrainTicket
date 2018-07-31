package saim.com.trainticket.Utils;

/**
 * Created by sam on 8/5/17.
 */

public class ApiURL {

    public static String header = "http://demo.resourcespoints.com/Train/";

    public static String getLogin = header + "login.php";                       //user_email, user_pass
    public static String getRegistration = header + "registration.php";         //user_name, user_email, user_mobile, user_pass
    public static String getFromLocation = header + "from_list.php";            //user_id
    public static String getToLocation = header + "to_list.php";                //user_id, from
    public static String getTrainList = header + "train_list.php";                //user_id, from, to

}
