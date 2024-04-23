package com.saninnovationslab.authws.userdetail;

import org.springframework.web.bind.annotation.RestController;
import com.saninnovationslab.authws.auth.AuthConstant;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserDetailController {

    @Autowired
    private UserDetailService userDetailService;

    @PostMapping("create/user")
    public UserBasicDetailModel addUser(@RequestBody UserDetail userDetail) {
        return userDetailService.create(userDetail);
    }

    @GetMapping("user/detail")
    public UserBasicDetailModel getUser(HttpServletRequest httpServletRequest) {
        final UserBasicDetailModel userBasicDetailModel = (UserBasicDetailModel) httpServletRequest
                .getAttribute(AuthConstant.CONTEXT_USER);
        return userDetailService.retrive(userBasicDetailModel.getId());
    }

    @PutMapping("update/user/role/{role}")
    public UserBasicDetailModel updateUserRole(@PathVariable UserRole role, HttpServletRequest httpServletRequest) {
        final UserBasicDetailModel userBasicDetailModel = (UserBasicDetailModel) httpServletRequest
                .getAttribute(AuthConstant.CONTEXT_USER);
        return userDetailService.updateRole(userBasicDetailModel.getId(), role);
    }

    @PutMapping("update/user/status/{status}")
    public void disableUser(@PathVariable Integer id, @PathVariable UserStatus status) {
        userDetailService.updateStatus(id, status);
    }

}
