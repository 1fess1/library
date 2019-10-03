package jsfiu.controller;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.User;
import org.springframework.stereotype.Component;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.ResourceBundle;

@ManagedBean
@ViewScoped
@Component
@Getter @Setter
public class UserController {

    private String username;
    private String password;

    public User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean hasRole(String role){
        return Arrays.stream(getCurrentUser().getAuthorities())
                .anyMatch(x -> x.getAuthority().equals("ROLE_"+role));
    }

    public String getLoginFailedMessage(){

        Object obj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loginFailed");

        if (obj==null) return "";

        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");

        String message = bundle.getString("login_failed");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("loginFailed");
        if (Strings.isNullOrEmpty(message)){
            return "";
        }else{
            return message;
        }

    }


}
