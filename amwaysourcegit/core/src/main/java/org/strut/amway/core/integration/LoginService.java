package org.strut.amway.core.integration;

import org.strut.amway.core.exception.UserCredentialsException;
import org.strut.amway.core.model.Account;

public interface LoginService {

    public Account login(String userName, String passWord, String ip) throws UserCredentialsException;

}
