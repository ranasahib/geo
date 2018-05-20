package com.rd.security;

import com.rd.dto.UserDetailDTO;
import com.rd.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private IndexService indexService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities = AuthorityUtils.createAuthorityList("User");
        UserDetailDTO userDetailDTO = new UserDetailDTO(login, "Password", authorities);
        indexService.generateToken(userDetailDTO);
        return userDetailDTO;

    }

    

}
