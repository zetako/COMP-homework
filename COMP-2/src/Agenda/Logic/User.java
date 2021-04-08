package Agenda.Logic;

import java.util.*;

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
