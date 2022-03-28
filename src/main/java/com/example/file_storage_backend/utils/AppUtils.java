package com.example.file_storage_backend.utils;

import com.example.file_storage_backend.dto.FileInformationDto;
import com.example.file_storage_backend.entity.FileInformation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public class AppUtils {
    public static void setStatus(HttpServletResponse resp, int status) {
        resp.setStatus(status);
    }

    public static FileInformationDto fileEntityToDto(FileInformation entity){
        return new FileInformationDto(entity.getFilename(), entity.getSize(), entity.getOwner(), entity.getPub());
    }

    public static String extractUser(HttpServletRequest req){
        Principal userPrincipal = req.getUserPrincipal();
        if(userPrincipal==null) return null;
        return userPrincipal.getName();
    }
}
