package com.saninnovationslab.authws.userdetail;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserDetailService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserDetail findById(Integer id) {
        return userDetailRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private UserBasicDetailModel transform(UserDetail userDetail) {
        UserBasicDetailModel userBasicDetailModel = new UserBasicDetailModel();
        userBasicDetailModel.setId(userDetail.getId());
        userBasicDetailModel.setRole(userDetail.getRole());
        userBasicDetailModel.setStatus(userDetail.getStatus());
        userBasicDetailModel.setUsername(userDetail.getUsername());
        return userBasicDetailModel;
    }

    public UserBasicDetailModel create(UserDetail userDetail) {
        userDetail.setPassword(bCryptPasswordEncoder.encode(userDetail.getPassword()));
        userDetail.setStatus(UserStatus.ACTIVE);
        userDetail.setCreated(new Timestamp(System.currentTimeMillis()));
        userDetail.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        userDetailRepository.save(userDetail);
        return transform(userDetail);
    }

    public UserBasicDetailModel retrive(Integer id) {
        UserDetail userDetail = findById(id);
        return transform(userDetail);
    }

    public UserBasicDetailModel updateRole(Integer id, UserRole role) {
        UserDetail userDetail = findById(id);
        userDetail.setRole(role);
        userDetail.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        userDetailRepository.save(userDetail);
        return transform(userDetail);
    }

    public UserBasicDetailModel updateStatus(Integer id, UserStatus status) {
        UserDetail userDetail = findById(id);
        userDetail.setStatus(status);
        userDetail.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        userDetailRepository.save(userDetail);
        return transform(userDetail);
    }

}
