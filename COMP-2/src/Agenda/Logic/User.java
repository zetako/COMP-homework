package Agenda.Logic;

import java.util.*;

/**
 * Representing a single user
 * @author zetako
 * @version 0.0.1
 */
public class User {
    public String userName;
    public String passwd;
    public List<Integer> meetings;

    public User(String _userName, String _passwd)
    {
        userName = _userName;
        passwd = _passwd;
    }
}
