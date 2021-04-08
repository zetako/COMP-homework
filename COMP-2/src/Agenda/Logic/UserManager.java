package Agenda.Logic;

import java.util.*;

public class UserManager {
    private Map<String, User> userList;

    public UserManager()
    {
        userList = new HashMap<String, User>();
    }

    public enum Status {
        NORMAL,
        ERROR,
        USER_EXISTED,
        NO_USER_BY_NAME,
        PASSWORD_INVALID
    }
    
    enum GetUserReturn {
        status, user
    }
    public EnumMap<GetUserReturn, Object> getUser(String userName, String passwd)
    {
        EnumMap<GetUserReturn, Object> ret = new EnumMap<GetUserReturn, Object>(GetUserReturn.class);
        
        if (!userList.containsKey(userName)) {
            ret.put(GetUserReturn.status, Status.NO_USER_BY_NAME);
            ret.put(GetUserReturn.user, null);
            return ret;
        }

        User curUser = userList.get(userName);
        if (curUser.passwd != passwd) {
            ret.put(GetUserReturn.status, Status.PASSWORD_INVALID);
            ret.put(GetUserReturn.user, null);
            return ret;
        }

        ret.put(GetUserReturn.status, Status.NORMAL);
        ret.put(GetUserReturn.user, curUser);
        return ret;
    }

    enum AddUserReturn {
        status,user
    }
    public EnumMap<AddUserReturn, Object> addUser(String userName, String passwd)
    {
        EnumMap<AddUserReturn, Object> ret = new EnumMap<AddUserReturn, Object>(AddUserReturn.class);
        if (userList.containsKey(userName)) {
            ret.put(AddUserReturn.status, Status.USER_EXISTED);
            ret.put(AddUserReturn.user, null);
            return ret;
        }

        User curUser = new User(userName,passwd);
        userList.put(userName, curUser);

        ret.put(AddUserReturn.status, Status.NORMAL);
        ret.put(AddUserReturn.user, curUser);

        return ret;
    }

    public Status delUser(String userName, String passwd)
    {
        if (!userList.containsKey(userName)) {
            return Status.NO_USER_BY_NAME;
        }

        User curUser = userList.get(userName);
        if (curUser.passwd != passwd) {
            return Status.PASSWORD_INVALID;
        }

        userList.remove(userName);
        return Status.NORMAL;
    }
}
